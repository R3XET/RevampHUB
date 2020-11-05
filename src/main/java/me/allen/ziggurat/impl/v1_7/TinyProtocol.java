package me.allen.ziggurat.impl.v1_7;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import me.allen.ziggurat.util.tinyprotocol.api.AbstractTinyProtocol;
import me.allen.ziggurat.util.tinyprotocol.reflection.FieldAccessor;
import me.allen.ziggurat.util.tinyprotocol.reflection.MethodInvoker;
import me.allen.ziggurat.util.tinyprotocol.reflection.Reflection;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.io.netty.channel.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public abstract class TinyProtocol implements AbstractTinyProtocol
{
    private static AtomicInteger ID = new AtomicInteger(0);
    private static MethodInvoker getPlayerHandle = Reflection.getMethod("{obc}.entity.CraftPlayer", "getHandle");
    private static FieldAccessor<Object> getConnection = Reflection.getField("{nms}.EntityPlayer", "playerConnection", Object.class);
    private static FieldAccessor<Object> getManager = Reflection.getField("{nms}.PlayerConnection", "networkManager", Object.class);
    private static FieldAccessor<Channel> getChannel = Reflection.getField("{nms}.NetworkManager", Channel.class, 0);
    private static Class<Object> minecraftServerClass = Reflection.getUntypedClass("{nms}.MinecraftServer");
    private static Class<Object> serverConnectionClass = Reflection.getUntypedClass("{nms}.ServerConnection");
    private static FieldAccessor<Object> getMinecraftServer = Reflection.getField("{obc}.CraftServer", TinyProtocol.minecraftServerClass, 0);
    private static FieldAccessor<Object> getServerConnection = Reflection.getField(TinyProtocol.minecraftServerClass, TinyProtocol.serverConnectionClass, 0);
    private static MethodInvoker getNetworkMarkers = Reflection.getTypedMethod(TinyProtocol.serverConnectionClass, null, List.class, TinyProtocol.serverConnectionClass);
    private static Class<?> PACKET_SET_PROTOCOL = Reflection.getMinecraftClass("PacketHandshakingInSetProtocol");
    private static Class<?> PACKET_LOGIN_IN_START = Reflection.getMinecraftClass("PacketLoginInStart");
    private static FieldAccessor<GameProfile> getGameProfile = Reflection.getField(TinyProtocol.PACKET_LOGIN_IN_START, GameProfile.class, 0);
    private static FieldAccessor<Integer> protocolId = Reflection.getField(TinyProtocol.PACKET_SET_PROTOCOL, "a", Integer.TYPE);
    private static FieldAccessor<Enum> protocolType = Reflection.getField(TinyProtocol.PACKET_SET_PROTOCOL, Enum.class, 0);


    protected volatile boolean closed;
    protected Plugin plugin;
    private Map<String, Channel> channelLookup;
    private Map<Channel, Integer> protocolLookup;
    private Listener listener;
    private Set<Channel> uninjectedChannels;
    private List<Object> networkManagers;
    private List<Channel> serverChannels;
    private ChannelInboundHandlerAdapter serverChannelHandler;
    private ChannelInitializer<Channel> beginInitProtocol;
    private ChannelInitializer<Channel> endInitProtocol;
    private String handlerName;
    
    public TinyProtocol(Plugin plugin) {
        this.channelLookup = new MapMaker().weakValues().makeMap();
        this.protocolLookup = new MapMaker().weakKeys().makeMap();
        this.uninjectedChannels = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());
        this.serverChannels = Lists.newArrayList();
        this.plugin = plugin;
        this.handlerName = this.getHandlerName();
        this.registerBukkitEvents();
        try {
            System.out.println("Attempting to inject into netty");
            this.registerChannelHandler();
            this.registerPlayers(plugin);
        }
        catch (IllegalArgumentException ex) {
            plugin.getLogger().info("Attempting to delay injection.");
            new BukkitRunnable() {
                public void run() {
                    TinyProtocol.this.registerChannelHandler();
                    TinyProtocol.this.registerPlayers(plugin);
                    plugin.getLogger().info("Injection complete.");
                }
            }.runTask(plugin);
        }
    }
    
    private void createServerChannelHandler() {
        this.endInitProtocol = new ChannelInitializer<Channel>() {
            protected void initChannel(Channel channel) throws Exception {
                try {
                    synchronized (TinyProtocol.this.networkManagers) {
                        if (!TinyProtocol.this.closed) {
                            channel.eventLoop().submit(() -> TinyProtocol.this.injectChannelInternal(channel));
                        }
                    }
                }
                catch (Exception e) {
                    TinyProtocol.this.plugin.getLogger().log(Level.SEVERE, "Cannot inject incomming channel " + channel, e);
                }
            }
        };
        this.beginInitProtocol = new ChannelInitializer<Channel>() {
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline().addLast(TinyProtocol.this.endInitProtocol);
            }
        };
        this.serverChannelHandler = new ChannelInboundHandlerAdapter() {
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                Channel channel = (Channel)msg;
                channel.pipeline().addFirst(TinyProtocol.this.beginInitProtocol);
                ctx.fireChannelRead(msg);
            }
        };
    }
    
    private void registerBukkitEvents() {
        this.listener = new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onPlayerLogin(PlayerJoinEvent e) {
                if (TinyProtocol.this.closed) {
                    return;
                }
                Channel channel = TinyProtocol.this.getChannel(e.getPlayer());
                if (!TinyProtocol.this.uninjectedChannels.contains(channel)) {
                    TinyProtocol.this.injectPlayer(e.getPlayer());
                }
            }
            
            @EventHandler
            public void onPluginDisable(PluginDisableEvent e) {
                if (e.getPlugin().equals(TinyProtocol.this.plugin)) {
                    TinyProtocol.this.close();
                }
            }
        };
        this.plugin.getServer().getPluginManager().registerEvents(this.listener, this.plugin);
    }
    
    private void registerChannelHandler() {
        Object mcServer = TinyProtocol.getMinecraftServer.get(Bukkit.getServer());
        Object serverConnection = TinyProtocol.getServerConnection.get(mcServer);
        boolean looking = true;
        this.networkManagers = (List<Object>) TinyProtocol.getNetworkMarkers.invoke(null, serverConnection);
        this.createServerChannelHandler();
        int i = 0;
        while (looking) {
            List<Object> list = Reflection.getField(serverConnection.getClass(), List.class, i).get(serverConnection);
            for (Object item : list) {
                Channel serverChannel = ((ChannelFuture)item).channel();
                this.serverChannels.add(serverChannel);
                serverChannel.pipeline().addFirst(this.serverChannelHandler);
                System.out.println("Server channel handler injected (" + serverChannel + ")");
                looking = false;
            }
            ++i;
        }
    }

    private void unregisterChannelHandler() {
        if (this.serverChannelHandler != null) {
            for (Channel serverChannel : this.serverChannels) {
                ChannelPipeline pipeline = serverChannel.pipeline();
                serverChannel.eventLoop().execute(() -> {
                    try {
                        pipeline.remove(this.serverChannelHandler);
                    } catch (NoSuchElementException ignored) {
                    }
                });
            }
        }
    }

    
    private void registerPlayers(Plugin plugin) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            this.injectPlayer(player);
        }
    }
    
    public Object onPacketOutAsync(Player receiver, Object packet) {
        return packet;
    }
    
    public Object onPacketInAsync(Player sender, Object packet) {
        return packet;
    }
    
    @Override
    public void sendPacket(Player player, Object packet) {
        this.sendPacket(this.getChannel(player), packet);
    }
    
    public void sendPacket(Channel channel, Object packet) {
        channel.pipeline().writeAndFlush(packet);
    }
    
    @Override
    public void receivePacket(Player player, Object packet) {
        this.receivePacket(this.getChannel(player), packet);
    }
    
    public void receivePacket(Channel channel, Object packet) {
        channel.pipeline().context("encoder").fireChannelRead(packet);
    }
    
    protected String getHandlerName() {
        return "tiny-" + this.plugin.getName() + "-" + TinyProtocol.ID.incrementAndGet();
    }
    
    @Override
    public void injectPlayer(Player player) {
        this.injectChannelInternal(this.getChannel(player)).player = player;
    }
    
    public void injectChannel(Channel channel) {
        this.injectChannelInternal(channel);
    }
    
    private PacketInterceptor injectChannelInternal(Channel channel) {
        try {
            PacketInterceptor interceptor = (PacketInterceptor)channel.pipeline().get(this.handlerName);
            if (interceptor == null) {
                interceptor = new PacketInterceptor();
                channel.pipeline().addBefore("packet_handler", this.handlerName, interceptor);
                this.uninjectedChannels.remove(channel);
            }
            return interceptor;
        }
        catch (IllegalArgumentException e) {
            return (PacketInterceptor)channel.pipeline().get(this.handlerName);
        }
    }
    
    public Channel getChannel(Player player) {
        Channel channel = this.channelLookup.get(player.getName());
        if (channel == null) {
            Object connection = TinyProtocol.getConnection.get(TinyProtocol.getPlayerHandle.invoke(player));
            Object manager = TinyProtocol.getManager.get(connection);
            this.channelLookup.put(player.getName(), channel = TinyProtocol.getChannel.get(manager));
        }
        return channel;
    }
    
    @Override
    public int getProtocolVersion(Player player) {
        Channel channel = this.channelLookup.get(player.getName());
        if (channel == null) {
            Object connection = TinyProtocol.getConnection.get(TinyProtocol.getPlayerHandle.invoke(player));
            Object manager = TinyProtocol.getManager.get(connection);
            this.channelLookup.put(player.getName(), channel = TinyProtocol.getChannel.get(manager));
        }
        return this.protocolLookup.get(channel);
    }
    
    @Override
    public void uninjectPlayer(Player player) {
        this.uninjectChannel(this.getChannel(player));
    }
    
    public void uninjectChannel(Channel channel) {
        if (!this.closed) {
            this.uninjectedChannels.add(channel);
        }
        channel.eventLoop().execute(() -> channel.pipeline().remove(this.handlerName));
    }
    
    @Override
    public boolean hasInjected(Player player) {
        return this.hasInjected(this.getChannel(player));
    }
    
    public boolean hasInjected(Channel channel) {
        return channel.pipeline().get(this.handlerName) != null;
    }
    
    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                this.uninjectPlayer(player);
            }
            HandlerList.unregisterAll(this.listener);
            this.unregisterChannelHandler();
        }
    }
    
    private class PacketInterceptor extends ChannelDuplexHandler
    {
        public volatile Player player;
        
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Channel channel = ctx.channel();
            if (TinyProtocol.PACKET_LOGIN_IN_START.isInstance(msg)) {
                GameProfile profile = TinyProtocol.getGameProfile.get(msg);
                TinyProtocol.this.channelLookup.put(profile.getName(), channel);
            }
            else if (TinyProtocol.PACKET_SET_PROTOCOL.isInstance(msg)) {
                String protocol = TinyProtocol.protocolType.get(msg).name();
                if (protocol.equalsIgnoreCase("LOGIN")) {
                    TinyProtocol.this.protocolLookup.put(channel, TinyProtocol.protocolId.get(msg));
                }
            }
            try {
                msg = TinyProtocol.this.onPacketInAsync(this.player, msg);
            }
            catch (Exception e) {
                TinyProtocol.this.plugin.getLogger().log(Level.SEVERE, "Error in onPacketInAsync().", e);
            }
            if (msg != null) {
                super.channelRead(ctx, msg);
            }
        }
        
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            try {
                msg = TinyProtocol.this.onPacketOutAsync(this.player, msg);
            }
            catch (Exception e) {
                TinyProtocol.this.plugin.getLogger().log(Level.SEVERE, "Error in onPacketOutAsync().", e);
            }
            if (msg != null) {
                super.write(ctx, msg, promise);
            }
        }
    }
}

package me.allen.ziggurat;

import com.google.common.collect.Lists;
import eu.revamp.spigot.utils.chat.color.CC;
import lombok.Getter;
import lombok.Setter;
import me.allen.ziggurat.objects.IZigguratHelper;
import me.allen.ziggurat.thread.ZigguratThread;
import me.allen.ziggurat.util.player.PlayerUtil;
import me.allen.ziggurat.util.version.ServerVersion;
import me.allen.ziggurat.util.version.protocol.ProtocolCheck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class Ziggurat
{
    private static String PREFIX = "&6[Tab] > ";
    @Getter
    @Setter
    private static Ziggurat instance;
    private ZigguratInit init;
    private ProtocolCheck protocolCheck;
    private ServerVersion version;
    private JavaPlugin plugin;
    private ZigguratAdapter adapter;
    private ZigguratThread thread;
    private IZigguratHelper implementation;
    private List<String> scoreboardPlugins;
    private long ticks;
    private Map<UUID, ZigguratTablist> tablists;
    
    public Ziggurat(JavaPlugin plugin, ZigguratAdapter adapter) {
        this.ticks = 30L;
        if (Ziggurat.instance != null) {
            throw new RuntimeException("Ziggurat has already been instantiated!");
        }
        if (plugin == null) {
            throw new RuntimeException("Ziggurat can not be instantiated without a plugin instance!");
        }
        if (!plugin.getServer().getPluginManager().isPluginEnabled("ProtocolLib") && !plugin.getServer().getPluginManager().isPluginEnabled("ProtocolSupport") && !plugin.getServer().getPluginManager().isPluginEnabled("ViaVersion")) {
            this.log("&eIf you want to use Custom Tab, please install ProtocolLib, ProtocolSupport or ViaVersion");
            return;
        }
        Ziggurat.instance = this;
        this.plugin = plugin;
        this.adapter = adapter;
        this.scoreboardPlugins = Lists.newArrayList();
        (this.init = new ZigguratInit()).init(this, plugin, adapter);
        this.implementation = this.init.getImplementation();
        this.thread = this.init.getThread();
        this.version = this.init.getVersion();
        this.tablists = this.init.getTablists();
        this.protocolCheck = this.init.getProtocolCheck();
        PlayerUtil.init();
    }
    
    private void log(String msg) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(Ziggurat.PREFIX + msg));
    }
    
    public ZigguratTablist getTablist(Player player) {
        return this.init.getTablists().get(player.getUniqueId());
    }
    
    public boolean hasTablist(Player player) {
        return this.init.getTablists().containsKey(player.getUniqueId());
    }
    
    public ZigguratTablist removeTablist(Player player) {
        return this.init.getTablists().remove(player.getUniqueId());
    }
    
    public ZigguratTablist create(Player player) {
        if (this.hasTablist(player)) {
            return this.getTablist(player);
        }
        ZigguratTablist zigguratTablist = new ZigguratTablist(player);
        this.init.getTablists().put(zigguratTablist.getPlayer().getUniqueId(), zigguratTablist);
        return zigguratTablist;
    }
    
    public void addScoreboardPlugin(String plugin) {
        if (!this.scoreboardPlugins.contains(plugin)) {
            this.scoreboardPlugins.add(plugin);
        }
    }
    
    public void addScoreboardPlugin(Plugin plugin) {
        if (!this.scoreboardPlugins.contains(plugin.getName())) {
            this.scoreboardPlugins.add(plugin.getName());
        }
    }
}

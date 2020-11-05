package me.allen.ziggurat;

import eu.revamp.spigot.utils.chat.color.CC;
import lombok.Getter;
import lombok.Setter;
import me.allen.ziggurat.impl.protocollib.ProtocolLibTabImpl;
import me.allen.ziggurat.impl.universal.UniversalTabImpl;
import me.allen.ziggurat.listener.ZigguratListeners;
import me.allen.ziggurat.objects.IZigguratHelper;
import me.allen.ziggurat.thread.ZigguratThread;
import me.allen.ziggurat.util.reflection.Reflection;
import me.allen.ziggurat.util.tinyprotocol.api.ProtocolVersion;
import me.allen.ziggurat.util.version.ServerVersion;
import me.allen.ziggurat.util.version.protocol.ProtocolCheck;
import me.allen.ziggurat.util.version.protocol.ProtocolLibProtocolCheck;
import me.allen.ziggurat.util.version.protocol.ProtocolSupportProtocolCheck;
import me.allen.ziggurat.util.version.protocol.ViaVersionProtocolCheck;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class ZigguratInit
{
    private static String PREFIX = "&6[Ziggurat] > ";
    private Ziggurat ziggurat;
    private ProtocolCheck protocolCheck;
    private Map<UUID, ZigguratTablist> tablists;
    private ServerVersion version;
    private JavaPlugin plugin;
    private ZigguratAdapter adapter;
    private ZigguratThread thread;
    private IZigguratHelper implementation;
    
    public void init(Ziggurat ziggurat, JavaPlugin plugin, ZigguratAdapter adapter) {
        this.ziggurat = ziggurat;
        this.plugin = plugin;
        Reflection.init();
        this.version = Reflection.ver;
        this.adapter = adapter;
        this.tablists = new ConcurrentHashMap<>();
        this.registerImplementation();
        this.initProtocolCheck();
        this.setup();
    }
    
    private void registerImplementation() {
        Bukkit.getConsoleSender().sendMessage("Detected Server Version: " + ProtocolVersion.getGameVersion());
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.implementation = new ProtocolLibTabImpl();
            this.log("&eNow Ziggurat Uses &6ProtocolLib As the Implementation to Work!");
            return;
        }
        this.implementation = new UniversalTabImpl();
        this.log("&eNow Ziggurat Uses &6Universal &7(Built-In) &eProtocol the Implementation to Work!");
    }
    
    private void setup() {
        if (!Bukkit.getServer().getOnlineMode()) {
            this.log("&eOnline Mode is Not Enabled, Custom Skin Functionality in Ziggurat Will Not Work Until You Re-Enabled It.");
        }
        this.plugin.getServer().getPluginManager().registerEvents(new ZigguratListeners(), this.plugin);
        if (this.thread != null) {
            this.thread.getExecutorService().shutdownNow();
            this.thread = null;
        }
        this.thread = new ZigguratThread(this.ziggurat);
    }
    
    private void initProtocolCheck() {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport")) {
            this.protocolCheck = new ProtocolSupportProtocolCheck();
            this.log("&eNow TabAPI Uses &6ProtocolSupport&e to Detect Players' Client Versions!");
            return;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
            this.protocolCheck = new ViaVersionProtocolCheck();
            this.log("&eNow TabAPI Uses &6ViaVersion&e to Detect Players' Client Versions!");
            return;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.protocolCheck = new ProtocolLibProtocolCheck();
            this.log("&eNow TabAPI Uses &6ProtocolLib&e to Detect Players' Client Versions!");
            return;
        }
        this.version.isBelow(ServerVersion.v1_8);
        this.protocolCheck = null;
    }
    
    private void log(String msg) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(ZigguratInit.PREFIX + msg));
    }
}

package eu.revamp.hub.handlers.server;

import java.util.HashMap;
import java.util.Map;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.messages.Message;
import eu.revamp.hub.queue.implement.DefaultQueue;
import eu.revamp.hub.queue.implement.PortalQueue;
import eu.revamp.spigot.utils.chat.color.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

@Getter @Setter
public class ServerHandler extends Handler {
    private Map<String, Server> data = new HashMap<>();
    private boolean defaultQueueSystem = true;

    public ServerHandler(RevampHub plugin) {
        super(plugin);
    }

    public void setupServers() {
        HashMap<String, Server> tempData = new HashMap<>();
        ConfigurationSection section = this.plugin.getConfiguration().getConfigurationSection("servers");
        if (section == null) {
            return;
        }
        section.getKeys(false).forEach(key -> {
            String name = this.plugin.getConfiguration().getString("servers." + key + ".name");
            tempData.put(name, new Server(name, this.plugin.getConfiguration().getString("servers." + key + ".bungee-name"), this.plugin.getConfiguration().getString("servers." + key + ".address"), this.plugin.getConfiguration().getInt("servers." + key + ".port")));
        });
        this.data = tempData;
        this.data.values().forEach(server -> {
            if (this.plugin.getServer().getPluginManager().getPlugin("Portal") != null) {
                this.defaultQueueSystem = false;
                server.setQueue(new PortalQueue(this.plugin, server.getName()));
                Bukkit.getConsoleSender().sendMessage(CC.translate(Message.PREFIX.get() + " &aRegistered queue for &e" + server.getBungeeName() + "&a! &7(PortalQueue)"));
                return;
            }
            this.defaultQueueSystem = true;
            server.setQueue(new DefaultQueue(this.plugin, server));
            Bukkit.getConsoleSender().sendMessage(CC.translate(Message.PREFIX.get() + " &aRegistered queue for &e" + server.getBungeeName() + "&a!"));
        });
    }
}


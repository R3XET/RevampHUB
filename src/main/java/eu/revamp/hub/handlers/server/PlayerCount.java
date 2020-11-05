package eu.revamp.hub.handlers.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.spigot.utils.generic.Tasks;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

@Getter
public class PlayerCount extends Handler implements PluginMessageListener {
    private int globalCount = 0;
    private Map<String, Integer> servers = new HashMap<>();

    public PlayerCount(RevampHub plugin) {
        super(plugin);
        Tasks.runAsyncTimer(plugin, () -> {
            this.sendToBungeeCord("PlayerCount", "ALL");
            this.servers.keySet().forEach(key -> this.sendToBungeeCord("PlayerCount", key));
        }, 2L, 2L);
    }

    public void setup() {
        this.servers.clear();
        this.plugin.getServerHandler().getData().values().forEach(server -> this.servers.put(server.getBungeeName(), 0));
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        try {
            if (channel.equals("BungeeCord")) {
                ByteArrayDataInput input = ByteStreams.newDataInput(message);
                String subchannel = input.readUTF();
                if (subchannel.equals("PlayerCount")) {
                    String serverName = input.readUTF();
                    int playerCount = input.readInt();
                    if (serverName.equalsIgnoreCase("ALL"))
                        this.globalCount = playerCount;
                    this.servers.keySet().forEach(name -> {
                        if (serverName.equalsIgnoreCase(name))
                            this.servers.put(name, playerCount);
                    });
                }
            }
        } catch (Exception exception) {
            // remove errors
        }
    }

    private void sendToBungeeCord(String channel, String sub) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(channel);
            out.writeUTF(sub);
        } catch (IOException iOException) {}
        try {
            Bukkit.getServer().sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Can't get player count from BungeeCord! Are you using a fork?");
            return;
        }
    }

    public int getCount(String server) {
        if (this.servers.containsKey(server))
            return this.servers.get(server);
        return -1;
    }
}


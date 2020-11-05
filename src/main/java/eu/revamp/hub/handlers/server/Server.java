package eu.revamp.hub.handlers.server;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.queue.Queue;
import lombok.Getter;
import lombok.Setter;
import me.joeleoli.portal.shared.server.ServerData;
import org.bukkit.ChatColor;

@Getter @Setter
public class Server {
    private final String name;

    private final String bungeeName;

    private final String address;

    private final int port;

    private Queue queue;

    private boolean online;

    @ConstructorProperties({"name", "bungeeName", "address", "port"})
    public Server(String name, String bungeeName, String address, int port) {
        this.name = name;
        this.bungeeName = bungeeName;
        this.address = address;
        this.port = port;
    }

    public int getOnlinePlayers(RevampHub plugin) {
        return plugin.getCoreHandler().getPlayerCount().getCount(this.bungeeName);
    }

    public String getStatus() {
        if (!isOnline())
            return ChatColor.RED + "Offline";
        if (RevampHub.INSTANCE.isPortalQueueSupport()) {
            ServerData serverData = ServerData.getByName(this.name);
            if (serverData != null && serverData.isOnline() && serverData.isWhitelisted())
                return ChatColor.YELLOW + "Whitelisted";
            if (serverData != null && serverData.isOnline())
                return ChatColor.GREEN + "Online";
        } else if (isOnline()) {
            return ChatColor.GREEN + "Online";
        }
        return ChatColor.RED + "Offline";
    }

    public void updateOnlineStatus() {
        if (RevampHub.INSTANCE.isPortalQueueSupport()) {
            ServerData serverData = ServerData.getByName(this.name);
            this.online = (serverData != null && serverData.isOnline());
            return;
        }
        Socket socket = new Socket();
        Exception exception = null;
        try {
            socket.connect(new InetSocketAddress(this.address, this.port), 2000);
        } catch (IOException e) {
            exception = e;
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.online = (exception == null);
    }
}

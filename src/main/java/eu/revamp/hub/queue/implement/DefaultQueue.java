package eu.revamp.hub.queue.implement;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.handlers.server.Server;
import eu.revamp.hub.messages.Message;
import eu.revamp.hub.queue.Queue;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Getter @Setter
public class DefaultQueue extends Handler implements Queue {
    private Server server;
    private List<Player> players = new ArrayList<>();
    private boolean paused;

    public DefaultQueue(RevampHub plugin, Server server) {
        super(plugin);
        this.server = server;
    }

    @Override
    public int getInQueue() {
        return this.players.size();
    }

    @Override
    public String getServer() {
        return this.server.getName();
    }

    @Override
    public boolean isDefaultQueueSystem() {
        return true;
    }

    @Override
    public int getPosition(Player player) {
        return this.players.indexOf(player) + 1;
    }

    @Override
    public void addEntry(Player player) {
        if (this.plugin.getQueueHandler().getPriority(player, this.server.getBungeeName()) == -1) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(this.server.getBungeeName());
            player.sendPluginMessage(RevampHub.INSTANCE, "BungeeCord", out.toByteArray());
            player.sendMessage(Message.SENDING_TO_SERVER.get(player).replace("<server>", this.server.getName()));
            return;
        }
        player.sendMessage(Message.QUEUE_JOINED.get().replace("<server>", this.server.getName()));
        this.players.add(player);
        this.players.forEach(queuePlayer -> {
            int pos = this.players.indexOf(queuePlayer);
            if (queuePlayer == player) return;
            if (this.plugin.getQueueHandler().getPriority(player, this.server.getBungeeName()) >= this.plugin.getQueueHandler().getPriority(queuePlayer, this.server.getBungeeName())) return;
            if (this.players.get(pos).isOnline()) {
                this.players.get(pos).sendMessage(Message.QUEUE_HIGHER_PRIORITY.get(this.players.get(pos)));
            }
            Collections.swap(this.players, pos, this.players.size() - 1);
        });
    }

    @Override
    public void removeEntry(Player player) {
        this.players.remove(player);
    }

    @Override
    public boolean isPaused() {
        return this.paused;
    }

    @Override
    public void setPaused(boolean is) {
        this.paused = is;
    }

    @Override
    public boolean isQueued(Player player) {
        return this.players.contains(player);
    }

    @Override
    public void check() {
        if (this.isPaused()) {
            return;
        }
        if (this.players.isEmpty()) return;
        Player player = this.players.get(0);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(this.server.getBungeeName());
        player.sendPluginMessage(RevampHub.INSTANCE, "BungeeCord", out.toByteArray());
        this.removeEntry(player);
        player.sendMessage(Message.SENDING_TO_SERVER.get(player).replace("<server>", this.server.getName()));
    }
}


package eu.revamp.hub.queue.implement;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.messages.Message;
import eu.revamp.hub.queue.Queue;
import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PortalQueue
extends Handler
implements Queue {
    private String server;

    public PortalQueue(RevampHub plugin, String server) {
        super(plugin);
        this.server = server;
    }

    @Override
    public int getInQueue() {
        me.joeleoli.portal.shared.queue.Queue queue = me.joeleoli.portal.shared.queue.Queue.getByName(this.server);
        if (queue != null) return queue.getPlayers().size();
        Bukkit.getConsoleSender().sendMessage(CC.translate(Message.PREFIX.get() + " &7[&cLog-1&7] &cQueue for &e" + this.server + " &ccan't be found. &7(Portal Queue server, queue is null)"));
        return 0;
    }

    @Override
    public int getPosition(Player player) {
        me.joeleoli.portal.shared.queue.Queue queue = me.joeleoli.portal.shared.queue.Queue.getByName(this.server);
        if (queue == null) {
            Bukkit.getConsoleSender().sendMessage(CC.translate(Message.PREFIX.get() + " &7[&cLog-2&7] &cQueue for &e" + this.server + " &ccan't be found. &7(Portal Queue server, queue is null)"));
            return 0;
        }
        if (queue.containsPlayer(player.getUniqueId())) return queue.getPosition(player.getUniqueId());
        return 0;
    }

    @Override
    public void addEntry(Player player) {
        Tasks.run(this.plugin, () -> player.performCommand("joinqueue " + this.server));
    }

    @Override
    public void removeEntry(Player player) {
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public void setPaused(boolean is) {
    }

    @Override
    public boolean isQueued(Player player) {
        me.joeleoli.portal.shared.queue.Queue queue = me.joeleoli.portal.shared.queue.Queue.getByPlayer(player.getUniqueId());
        return queue != null;
    }

    @Override
    public void check() {
    }

    @Override
    public String getServer() {
        return this.server;
    }

    @Override
    public boolean isDefaultQueueSystem() {
        return false;
    }
}


package eu.revamp.hub.queue;

import java.util.Iterator;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.messages.Message;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.handlers.server.Server;
import eu.revamp.spigot.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QueueHandler extends Handler {
    public QueueHandler(RevampHub plugin) {
        super(plugin);
        int per = plugin.getConfiguration().getInt("queue-send-every-seconds", 2);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Update(), 20L * (long)per, 20L * (long)per);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new QueueNotify(), 150L, 150L);
    }

    public Queue getQueueByPlayer(Player player) {
        Server server2;
        me.joeleoli.portal.shared.queue.Queue queue;
        if (!this.plugin.isPortalQueueSupport()) return this.plugin.getServerHandler().getData().values().stream().filter(server -> server.getQueue().isQueued(player)).map(Server::getQueue).findFirst().orElse(null);
        Iterator<Server> iterator = this.plugin.getServerHandler().getData().values().iterator();
        do {
            if (!iterator.hasNext()) return this.plugin.getServerHandler().getData().values().stream().filter(server -> server.getQueue().isQueued(player)).map(Server::getQueue).findFirst().orElse(null);
        } while ((queue = me.joeleoli.portal.shared.queue.Queue.getByName((server2 = iterator.next()).getBungeeName())) == null || !queue.containsPlayer(player.getUniqueId()));
        return server2.getQueue();
    }

    public int getPriority(Player player, String server) {
        if (player.hasPermission("queue.bypass")) {
            return -1;
        }
        if (player.hasPermission("queue.bypass." + server)) {
            return -1;
        }
        int i = 1;
        while (i < 50) {
            if (player.hasPermission("queue.priority." + i)) {
                return i;
            }
            ++i;
        }
        return 50;
    }

    private class Update
    implements Runnable {
        private Update() {
        }

        @Override
        public void run() {
            QueueHandler.this.plugin.getServerHandler().getData().values().forEach(server -> server.getQueue().check());
        }
    }

    private class QueueNotify
    implements Runnable {
        private QueueNotify() {
        }

        @Override
        public void run() {
            PlayerUtils.getOnlinePlayers().forEach(player -> {
                Queue queue = QueueHandler.this.getQueueByPlayer(player);
                if (queue == null) return;
                if (!queue.isDefaultQueueSystem()) return;
                if (queue.isPaused()) {
                    player.sendMessage(Message.QUEUE_PAUSED.get().replace("<server>", queue.getServer()));
                    return;
                }
                QueueHandler.this.plugin.getConfiguration().getStringList("queue.notify-message").forEach(message -> player.sendMessage(message.replace("<pos>", String.valueOf(queue.getPosition(player))).replace("<in_queue>", String.valueOf(queue.getInQueue())).replace("<server>", queue.getServer())));
            });
        }
    }

}


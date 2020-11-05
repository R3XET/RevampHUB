package eu.revamp.hub.commands.queue;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.handlers.server.Server;
import eu.revamp.hub.messages.Message;
import eu.revamp.hub.queue.Queue;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveQueueCommand extends Handler implements CommandExecutor {
    public LeaveQueueCommand(RevampHub plugin) {
        super(plugin);
        this.plugin.getCommand("leavequeue").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Tasks.runAsync(this.plugin, () -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You can't execute this command.");
                return;
            }
            Player player = (Player)sender;
            Queue queue = this.plugin.getQueueHandler().getQueueByPlayer(player);
            if (queue == null) {
                player.sendMessage(Message.QUEUE_NOT_QUEUED.get());
                return;
            }
            Server server = this.plugin.getServerHandler().getData().get(queue.getServer());
            if (server == null) {
                player.sendMessage(Message.QUEUE_INVALID_SERVER.get());
                return;
            }
            queue.removeEntry(player);
            player.sendMessage(Message.QUEUE_LEAVE.get().replace("<server>", server.getName()));
        });
        return false;
    }
}


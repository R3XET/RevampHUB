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

public class JoinQueueCommand extends Handler
implements CommandExecutor {
    public JoinQueueCommand(RevampHub plugin) {
        super(plugin);
        this.plugin.getCommand("joinqueue").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Tasks.runAsync(this.plugin, () -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You can't execute this command.");
                return;
            }
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /joinqueue <server>");
                return;
            }
            Player player = (Player)sender;
            Queue queue = this.plugin.getQueueHandler().getQueueByPlayer(player);
            if (queue != null) {
                player.sendMessage(Message.QUEUE_ALREADY_QUEUED.get());
                return;
            }
            Server server = this.plugin.getServerHandler().getData().get(args[0]);
            if (server == null) {
                player.sendMessage(Message.QUEUE_INVALID_SERVER.get());
                return;
            }
            server.getQueue().addEntry(player);
        });
        return false;
    }
}


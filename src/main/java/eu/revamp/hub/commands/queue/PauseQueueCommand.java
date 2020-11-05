package eu.revamp.hub.commands.queue;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.handlers.server.Server;
import eu.revamp.hub.messages.Message;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PauseQueueCommand extends Handler implements CommandExecutor {
    public PauseQueueCommand(RevampHub plugin) {
        super(plugin);
        this.plugin.getCommand("pausequeue").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Tasks.runAsync(this.plugin, () -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You can't execute this command.");
                return;
            }
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /pausequeue <server>");
                return;
            }
            Player player = (Player)sender;
            if (!player.hasPermission("revamphub.pausequeue")) {
                player.sendMessage(ChatColor.RED + "No permission.");
                return;
            }
            Server server = this.plugin.getServerHandler().getData().get(args[0]);
            if (server == null) {
                player.sendMessage(Message.QUEUE_INVALID_SERVER.get());
                return;
            }
            server.getQueue().setPaused(!server.getQueue().isPaused());
            player.sendMessage(Message.QUEUE_PAUSED_BY_PLAYER.get().replace("<option>", server.getQueue().isPaused() ? "paused" : "unpaused").replace("<server>", server.getName()));
        });
        return false;
    }
}


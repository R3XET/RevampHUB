package eu.revamp.hub.commands;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.messages.Message;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends Handler implements CommandExecutor {
    public SetSpawnCommand(RevampHub plugin) {
        super(plugin);
        plugin.getCommand("setspawn").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Tasks.runAsync(this.plugin, () -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You can't execute this command.");
                return;
            }
            Player player = (Player)sender;
            if (!player.hasPermission("revamphub.command.setspawn")) {
                player.sendMessage(ChatColor.RED + "No permission.");
                return;
            }
            this.plugin.getCoreHandler().getSpawn().save(player.getLocation());
            player.sendMessage(Message.SPAWN_SET.get());
        });
        return false;
    }
}


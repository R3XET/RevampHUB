package eu.revamp.hub.commands;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.messages.Message;
import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HubCoreCommand extends Handler implements CommandExecutor {
    public HubCoreCommand(RevampHub plugin) {
        super(plugin);
        plugin.getCommand("hubcore").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Tasks.runAsync(this.plugin, () -> {
            if (!sender.hasPermission("revamphub.reload")) {
                //this.sendMessage(sender);
                return;
            }
            if (args.length != 0 && args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(Message.PREFIX.get() + ChatColor.GREEN.toString() + " Reloading hub core..");
                double start = System.currentTimeMillis();
                this.plugin.setReloading(true);
                this.plugin.setupFiles();
                this.plugin.getServerHandler().setupServers();
                this.plugin.getCoreHandler().getPlayerCount().setup();
                this.plugin.getCoreHandler().setupLaunchPads();
                this.plugin.getCoreHandler().setupJoinItems();
                this.plugin.getCoreHandler().setupSpawn();
                this.plugin.getCoreHandler().setupArmor();
                this.plugin.getCoreHandler().setupPermissionSystem();
                this.plugin.getGadgetHandler().setupGadgets();
                this.plugin.getCoreHandler().setupCustomMenuData();
                double ms = (double)System.currentTimeMillis() - start;
                this.plugin.setReloading(false);
                sender.sendMessage(Message.PREFIX.get() + ChatColor.GREEN.toString() + " Reloaded hub core in " + ChatColor.DARK_GREEN.toString() + ms + "ms" + ChatColor.GREEN.toString() + ".");
                return;
            }
            //this.sendMessage(sender);
        });
        return false;
    }

    private void sendMessage(CommandSender sender) {
        sender.sendMessage(CC.translate("&7&m-----------------------------------"));
        sender.sendMessage(CC.translate("&bThis server is running &bRevampHub &3v" + this.plugin.getDescription().getVersion()));
        sender.sendMessage(CC.translate("&bDeveloped by R3XET."));
        sender.sendMessage(CC.translate("&7&m-----------------------------------"));
    }
}


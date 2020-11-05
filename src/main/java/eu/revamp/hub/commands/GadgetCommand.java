package eu.revamp.hub.commands;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.messages.Message;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.handlers.gadget.Gadget;
import eu.revamp.hub.handlers.player.PlayerData;
import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GadgetCommand extends Handler implements CommandExecutor {
    public GadgetCommand(RevampHub plugin) {
        super(plugin);
        this.plugin.getCommand("gadget").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Tasks.runAsync(this.plugin, () -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You can't execute this command.");
                return;
            }
            Player player = (Player)sender;
            PlayerData playerData = this.plugin.getPlayerHandler().getData().get(player.getUniqueId());
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /gadget <name>");
                player.sendMessage(CC.translate("&7&oUse 'none' for name to remove active gadget"));
                return;
            }
            if (args[0].equalsIgnoreCase("none")) {
                if (playerData.getActiveGadget() == null) {
                    player.sendMessage(Message.GADGET_NON_APPLIED.get());
                    return;
                }
                playerData.setActiveGadget(null);
                player.sendMessage(Message.GADGET_REMOVED.get());
                return;
            }
            Gadget gadget = this.plugin.getGadgetHandler().getGadgetData().get(args[0]);
            if (gadget == null) {
                player.sendMessage(Message.GADGET_INVALID.get().replace("<name>", args[0]));
                return;
            }
            if (!player.hasPermission(gadget.getPermission())) {
                player.sendMessage(Message.GADGET_NO_PERMISSION.get().replace("<gadget>", gadget.getName()));
                return;
            }
            playerData.setActiveGadget(gadget);
            player.sendMessage(Message.GADGET_APPLIED.get().replace("<gadget>", gadget.getName()));
        });
        return false;
    }
}


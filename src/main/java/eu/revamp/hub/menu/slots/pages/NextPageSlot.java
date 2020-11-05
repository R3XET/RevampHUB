package eu.revamp.hub.menu.slots.pages;

import eu.revamp.hub.menu.menu.SwitchableMenu;
import eu.revamp.hub.menu.slots.Slot;
import eu.revamp.hub.messages.Message;
import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.spigot.utils.player.PlayerUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class NextPageSlot extends Slot {
    private final SwitchableMenu switchableMenu;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder item = new ItemBuilder(Material.GOLD_NUGGET);
        item.setName("&bNext page");
        if (this.switchableMenu.getPage() < this.switchableMenu.getPages(player)) {
            item.addLoreLine(" ");
            item.addLoreLine("&3Click to head");
            item.addLoreLine("&3over to next page.");
            item.addLoreLine(" ");
        } else {
            item.addLoreLine(" ");
            item.addLoreLine("&cThere is no next page.");
            item.addLoreLine("&cYou're on the last page.");
            item.addLoreLine(" ");
        }
        return item.toItemStack();
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType) {
        if (this.switchableMenu.getPage() < this.switchableMenu.getPages(player)) {
            PlayerUtils.playSound(player, Sound.ORB_PICKUP);
        } else {
            player.sendMessage(CC.translate(Message.PREFIX + "&bYou're on the last page of the menu!"));
            return;
        }
        this.switchableMenu.changePage(player, 1);
    }

    @Override
    public int getSlot() {
        return 8;
    }

    @Override
    public int[] getSlots() {
        return new int[]{44};
    }
}

package eu.revamp.hub.menu.slots.pages;

import eu.revamp.hub.menu.menu.SwitchableMenu;
import eu.revamp.hub.menu.slots.Slot;
import eu.revamp.spigot.utils.item.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class PageSlot extends Slot {
    private final SwitchableMenu switchableMenu;
    private final int slot;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder item = new ItemBuilder(Material.PAPER);
        item.setName(switchableMenu.getPagesTitle(player));

        item.addLoreLine("");
        item.addLoreLine("&bCurrent page&7: &3" + switchableMenu.getPage());
        item.addLoreLine("&bMax pages&7: &3" + switchableMenu.getPages(player));
        item.addLoreLine("");

        return item.toItemStack();
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public int[] getSlots() {
        return new int[]{40};
    }
}

package eu.revamp.hub.listeners.items;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener extends Handler implements Listener {
    public InventoryListener(RevampHub plugin) {
        super(plugin);
    }

    private boolean isJoinItem(ItemStack item) {
        return this.plugin.getCoreHandler().getJoinItems().stream().filter(configItem -> configItem.toItemStack().equals(item)).findFirst().orElse(null) != null;
    }

    @EventHandler(ignoreCancelled=true)
    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (event.getClick() == ClickType.NUMBER_KEY) {
            event.setCancelled(true);
        }
        if (item == null) {
            return;
        }
        if (!this.isJoinItem(item)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled=true)
    public void onArmorClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player)event.getWhoClicked();
        if (item == null) {
            return;
        }
        if (this.plugin.getCoreHandler().canClickArmor(player, item.getType())) {
            return;
        }
        if (event.getSlotType() != InventoryType.SlotType.ARMOR) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled=true)
    public void onClick(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (!this.isJoinItem(item)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item == null) return;
        if (item.getType() == Material.AIR) {
            return;
        }
        if (!this.isJoinItem(item)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlace(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getType() == Material.AIR) {
            return;
        }
        if (!this.isJoinItem(item)) {
            return;
        }
        event.setCancelled(true);
    }
}


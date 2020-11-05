package eu.revamp.hub.listeners;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.messages.Message;
import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.GameMode;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class OptionsListener extends Handler implements Listener {
    public OptionsListener(RevampHub plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("revamphub.options.bypass.break") && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (this.plugin.getConfiguration().getOption("break-blocks")) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission("revamphub.options.bypass.place") && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (this.plugin.getConfiguration().getOption("place-blocks")) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().updateInventory();
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasPermission("revamphub.options.bypass.drop") && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (this.plugin.getConfiguration().getOption("item-drop")) {
            return;
        }
        event.setCancelled(true);
        Tasks.runLater(this.plugin, () -> event.getPlayer().updateInventory(), 2L);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (event.getPlayer().hasPermission("revamphub.options.bypass.pick") && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (this.plugin.getConfiguration().getOption("item-pickup")) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }
        if (this.plugin.getConfiguration().getOption("monster-spawn")) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onAnimalSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Animals)) {
            return;
        }
        if (this.plugin.getConfiguration().getOption("animal-spawn")) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
            event.getEntity().teleport(this.plugin.getCoreHandler().getSpawn().toLocation());
            return;
        }
        if (this.plugin.getConfiguration().getOption("damage")) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("revamphub.options.bypass.chat")) {
            this.setFormat(event);
            return;
        }
        if (!this.plugin.getConfiguration().getOption("chat-use")) {
            player.sendMessage(Message.CANT_USE_CHAT.get(player));
            event.setCancelled(true);
            return;
        }
        if (!this.plugin.getConfiguration().getBoolean("chat-format.enabled")) return;
        this.setFormat(event);
    }

    private void setFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!this.plugin.getConfiguration().getBoolean("chat-format.enabled")) return;
        Replacement format = new Replacement(this.plugin.getConfiguration().getString("chat-format.format"));
        format.add("<prefix>", this.plugin.getCoreHandler().getPermissionSystem().getPrefix(player));
        format.add("<player>", player.getName());
        format.add("<suffix>", this.plugin.getCoreHandler().getPermissionSystem().getSuffix(player));
        event.setFormat(format.toString().replace("<message>", event.getMessage()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onFood(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled=true)
    public void onWeather(WeatherChangeEvent event) {
        if (this.plugin.getConfiguration().getOption("weather-change")) {
            return;
        }
        event.setCancelled(event.toWeatherState());
    }
}


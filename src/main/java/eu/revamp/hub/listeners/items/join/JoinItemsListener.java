package eu.revamp.hub.listeners.items.join;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.handlers.core.enderbut.EnderButtType;
import eu.revamp.hub.messages.Message;
import eu.revamp.hub.handlers.gadget.Gadget;
import eu.revamp.hub.handlers.menu.CustomMenu;
import eu.revamp.hub.handlers.player.PlayerData;
import eu.revamp.hub.utilities.custom.ConfigItem;
import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.date.DateUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.spigot.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class JoinItemsListener extends Handler implements Listener {
    private List<UUID> enderButtData = new ArrayList<>();

    public JoinItemsListener(RevampHub plugin) {
        super(plugin);
    }

    private ConfigItem getJoinItem(ItemStack item) {
        return this.plugin.getCoreHandler().getJoinItems().stream().filter(configItem -> configItem.toItemStack().equals(item)).findFirst().orElse(null);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        PlayerData playerData = this.plugin.getPlayerHandler().getData().get(player.getUniqueId());
        if (stack == null) {
            return;
        }
        ConfigItem item = this.getJoinItem(stack);
        if (item == null) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (!item.getAction().toLowerCase().equals("enderbutt")) {
            event.setCancelled(true);
        }
        switch (item.getAction().toLowerCase()) {
            case "firework": {
                Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
                firework.setPassenger(player);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();
                fireworkMeta.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withFade(Color.FUCHSIA).build());
                fireworkMeta.setPower(2);
                firework.setFireworkMeta(fireworkMeta);
                break;
            }
            case "players_off": {
                if (playerData.getPlayerVisibilityTime() > System.currentTimeMillis()) {
                    player.sendMessage(Message.PLAYER_VISIBILITY_COOLDOWN.get().replace("<time>", DateUtils.formatTimeMillis(playerData.getPlayerVisibilityTime() - System.currentTimeMillis(), true, true)));
                    return;
                }
                this.plugin.getCoreHandler().getJoinItems().stream().filter(configItem -> configItem.getAction().equalsIgnoreCase("players_on")).findFirst().ifPresent(players_on -> player.getInventory().setItem(players_on.getSlot(), players_on.toItemStack()));
                for (Player online : PlayerUtils.getOnlinePlayers()) {
                    player.hidePlayer(online);
                }
                playerData.setHidingPlayers(true);
                playerData.setPlayerVisibilityTime(System.currentTimeMillis() + DateUtils.parseTime("5s"));
                break;
            }
            case "players_on": {
                if (playerData.getPlayerVisibilityTime() > System.currentTimeMillis()) {
                    player.sendMessage(Message.PLAYER_VISIBILITY_COOLDOWN.get().replace("<time>", DateUtils.formatTimeMillis(playerData.getPlayerVisibilityTime() - System.currentTimeMillis(), true, true)));
                    return;
                }
                this.plugin.getCoreHandler().getJoinItems().stream().filter(configItem -> configItem.getAction().equalsIgnoreCase("players_off")).findFirst().ifPresent(players_off -> player.getInventory().setItem(players_off.getSlot(), players_off.toItemStack()));
                for (Player online : PlayerUtils.getOnlinePlayers()) {
                    player.showPlayer(online);
                }
                playerData.setHidingPlayers(false);
                playerData.setPlayerVisibilityTime(System.currentTimeMillis() + DateUtils.parseTime("5s"));
                break;
            }
            case "enderbutt": {
                if (this.plugin.getCoreHandler().getEnderButtType() == EnderButtType.RIDING) {
                    if (player.isSneaking()) {
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        return;
                    }
                    Tasks.runAsyncLater(this.plugin, () -> player.getInventory().setItem(item.getSlot(), item.toItemStack()), 2L);
                    break;
                }
                event.setUseItemInHand(Event.Result.DENY);
                event.setUseInteractedBlock(Event.Result.DENY);
                player.setVelocity(player.getLocation().getDirection().multiply(3.0));
                player.updateInventory();
                PlayerUtils.playSound(player, this.plugin.getConfiguration().getString("ender-butt-sound"));
                break;
            }
            case "gadgets": {
                CustomMenu customMenu = this.plugin.getCoreHandler().getCustomMenuData().get("gadgets-menu");
                if (customMenu == null) {
                    return;
                }
                ArrayList<ConfigItem> items = new ArrayList<>();
                Tasks.runAsync(this.plugin, () -> {
                    customMenu.getItems().stream().filter(configItem -> configItem.getGadgetName() != null).forEach(configItem -> {
                        Gadget gadget = this.plugin.getGadgetHandler().getGadgetData().get(configItem.getGadgetName());
                        if (gadget != null) {
                            if (player.hasPermission(gadget.getPermission())) {
                                configItem.setLore(this.plugin.getGadgets().getStringList("menu.gadget-menu.items." + configItem.getItemKey() + ".lore-with-permission"));
                            } else {
                                configItem.setLore(this.plugin.getGadgets().getStringList("menu.gadget-menu.items." + configItem.getItemKey() + ".lore-with-no-permission"));
                            }
                        } else {
                            configItem.setLore(this.plugin.getGadgets().getStringList("menu.gadget-menu.items." + configItem.getItemKey() + ".lore"));
                        }
                        items.add(configItem);
                    });
                    customMenu.setItems(items);
                    Tasks.run(this.plugin, () -> customMenu.getMenu().open(player));
                });
                break;
            }
        }
        if (item.getAction().toLowerCase().startsWith("{openmenu:") && item.getAction().toLowerCase().endsWith("}")) {
            String menu = item.getAction().replace("{openmenu:", "").replace("}", "").toLowerCase();
            CustomMenu customMenu = this.plugin.getCoreHandler().getCustomMenuData().get(menu);
            if (customMenu != null) {
                Tasks.run(this.plugin, () -> {
                    player.closeInventory();
                    customMenu.getMenu().open(player);
                });
            } else {
                Bukkit.getConsoleSender().sendMessage(CC.translate("&c[MenuLog-1] &eThere is no menu with name &e&n" + menu + "&b &eto open for &b" + player.getName() + "&e. &c&oPlease check your configurations."));
            }
        }
        if (item.isCommandEnabled()) {
            player.performCommand(item.getCommand());
        }
        player.updateInventory();
    }

    @EventHandler
    public void onEnderPearl(ProjectileLaunchEvent event) {
        if (this.plugin.getCoreHandler().getEnderButtType() != EnderButtType.RIDING) {
            return;
        }
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof EnderPearl)) {
            return;
        }
        EnderPearl enderPearl = (EnderPearl)projectile;
        if (!(enderPearl.getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player)enderPearl.getShooter();
        if (player.isInsideVehicle()) {
            Entity vehicle = player.getVehicle();
            if (vehicle == null) {
                return;
            }
            vehicle.remove();
        }
        player.spigot().setCollidesWithEntities(false);
        this.enderButtData.add(player.getUniqueId());
        enderPearl.setPassenger(player);
        enderPearl.setMetadata("enderbutt", new FixedMetadataValue(this.plugin, true));
    }

    @EventHandler
    public void onEnderPearlLand(ProjectileHitEvent event) {
        if (this.plugin.getCoreHandler().getEnderButtType() != EnderButtType.RIDING) {
            return;
        }
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof EnderPearl)) {
            return;
        }
        EnderPearl enderPearl = (EnderPearl)projectile;
        if (!(enderPearl.getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player)enderPearl.getShooter();
        if (enderPearl.getLocation().distance(player.getLocation()) <= 2.0) {
            player.teleport(player.getLocation().add(0.0, 1.0, 0.0));
        }
        player.spigot().setCollidesWithEntities(true);
        player.setNoDamageTicks(5);
        enderPearl.remove();
        this.enderButtData.remove(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled=true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (this.plugin.getCoreHandler().getEnderButtType() != EnderButtType.RIDING) {
            return;
        }
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        Player player = event.getPlayer();
        if (!this.enderButtData.contains(player.getUniqueId())) {
            return;
        }
        player.spigot().setCollidesWithEntities(true);
        event.setCancelled(true);
        this.enderButtData.remove(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.enderButtData.remove(event.getPlayer().getUniqueId());
    }
}


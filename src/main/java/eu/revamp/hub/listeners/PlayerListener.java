package eu.revamp.hub.listeners;

import java.util.Iterator;
import java.util.UUID;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.handlers.gadget.Gadget;
import eu.revamp.hub.handlers.launchpads.LaunchPad;
import eu.revamp.hub.handlers.player.PlayerData;
import eu.revamp.hub.utilities.custom.ConfigItem;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.spigot.utils.player.PlayerUtils;
import me.allen.ziggurat.util.player.PlayerUtil;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener extends Handler implements Listener {
    public PlayerListener(RevampHub plugin) {
        super(plugin);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        String name = event.getName();
        UUID uuid = event.getUniqueId();
        PlayerData playerData = this.plugin.getPlayerHandler().getData().get(uuid);
        if (playerData != null) return;
        playerData = this.plugin.getPlayerHandler().getData().put(uuid, new PlayerData(uuid, name, this.plugin));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!this.plugin.getConfiguration().getBoolean("join.message.enabled")) {
            event.setJoinMessage(null);
        }
        Player player = event.getPlayer();
        Tasks.runAsync(this.plugin, () -> {
            if (this.plugin.getConfiguration().getBoolean("join.send-message.enabled")) {
                this.plugin.getConfiguration().getStringList("join.send-message.message").stream().map(message -> message.replace("<player>", player.getDisplayName())).forEach(((Player)player)::sendMessage);
            }
            player.setMaxHealth(20.0);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
            this.plugin.getCoreHandler().getJoinItems().stream().filter(configItem -> {
                if (configItem.getAction().equalsIgnoreCase("players_on")) return false;
                return !configItem.getAction().equalsIgnoreCase("players_off");
            }).forEach(configItem -> player.getInventory().setItem(configItem.getSlot(), configItem.toItemStack()));
            this.plugin.getCoreHandler().getJoinItems().stream().filter(configItem -> configItem.getAction().equalsIgnoreCase("players_off")).findFirst().ifPresent(players_off -> player.getInventory().setItem(players_off.getSlot(), players_off.toItemStack()));
            this.plugin.getCoreHandler().applyArmor(player);
            player.updateInventory();
        });
        Tasks.runLater(this.plugin, () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)), 20L  );
        player.setGameMode(GameMode.SURVIVAL);
        Iterator<Player> iterator = PlayerUtils.getOnlinePlayers().iterator();
        do {
            if (!iterator.hasNext()) {
                player.setFlying(false);
                player.setAllowFlight(true);
                player.teleport(this.plugin.getCoreHandler().getSpawn().toLocation());
                Tasks.runLater(this.plugin, () -> {
                    if (!this.plugin.getConfiguration().getBoolean("join.apply-firework")) return;
                    Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();
                    fireworkMeta.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.YELLOW).withFade(Color.AQUA).build());
                    fireworkMeta.setPower(2);
                    firework.setFireworkMeta(fireworkMeta);
                    Tasks.runLater(this.plugin, firework::detonate, 5L);
                }, 10L);
                return;
            }
            Player online = iterator.next();
            PlayerData playerData = this.plugin.getPlayerHandler().getData().get(online.getUniqueId());
            if (playerData == null || !playerData.isHidingPlayers()) continue;
            online.hidePlayer(player);
        } while (true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!this.plugin.getConfiguration().getBoolean("quit.message.enabled")) {
            event.setQuitMessage(null);
        }
        this.plugin.getPlayerHandler().getData().remove(player.getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (this.plugin.getConfiguration().getBoolean("quit.message.enabled")) return;
        event.setLeaveMessage(null);
    }

    @EventHandler
    public void onLaunchPad(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        LaunchPad launchPad = this.plugin.getCoreHandler().getLaunchPad();
        if (launchPad == null) return;
        if (!launchPad.isEnabled()) return;
        if (launchPad.getMaterial() == null) {
            return;
        }
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (block.getType() != launchPad.getMaterial()) {
            return;
        }
        Material material = block.getRelative(BlockFace.DOWN).getType();
        if (material != Material.REDSTONE_BLOCK) {
            return;
        }
        event.setCancelled(true);
        player.setVelocity(player.getLocation().getDirection().multiply(launchPad.getMultipy()).setY(launchPad.getVertical()));
        PlayerUtils.playSound(player, launchPad.getSound());
    }

    @EventHandler
    public void onFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (!this.plugin.getConfiguration().getBoolean("double-jump.enabled")) {
            event.setCancelled(true);
            player.setAllowFlight(false);
            return;
        }
        event.setCancelled(true);
        player.setVelocity(player.getLocation().getDirection().multiply(this.plugin.getConfiguration().getDouble("double-jump.velocity.multiply")).setY(this.plugin.getConfiguration().getDouble("double-jump.velocity.vertical")));
        PlayerUtils.playSound(player, this.plugin.getConfiguration().getString("double-jump.sound"));
    }

    @EventHandler
    public void handleGadget(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerHandler().getData().get(player.getUniqueId());
        if (playerData == null) return;
        if (playerData.getActiveGadget() == null) return;
        Gadget gadget = playerData.getActiveGadget();
        this.plugin.getCoreHandler().sendParticle(gadget.getEffect(), (float)gadget.getOffsetX(), (float)gadget.getOffsetY(), (float)gadget.getOffsetZ(), gadget.getSpeed(), gadget.getAmount(), player.getLocation().add(0.0, 1.0, 0.0), gadget.getRange());
    }
}


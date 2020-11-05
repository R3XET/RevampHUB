package me.allen.ziggurat;

import lombok.Getter;
import me.allen.ziggurat.events.type.ZigguratTablistCreateEvent;
import me.allen.ziggurat.events.type.ZigguratTablistDestroyEvent;
import me.allen.ziggurat.events.type.ZigguratTablistUpdateEvent;
import me.allen.ziggurat.impl.v1_7.v1_7TabImpl;
import me.allen.ziggurat.objects.BufferedTabObject;
import me.allen.ziggurat.objects.TabColumn;
import me.allen.ziggurat.objects.TabEntry;
import me.allen.ziggurat.util.ZigguratCommons;
import me.allen.ziggurat.util.player.LegacyClientUtils;
import me.allen.ziggurat.util.player.PlayerUtil;
import me.allen.ziggurat.util.version.PlayerVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class ZigguratTablist
{
    private Player player;
    private Set<TabEntry> currentEntries;

    public ZigguratTablist(Player player) {
        this.currentEntries = new HashSet<>();
        this.player = player;
        this.setup();
        Team team1 = player.getScoreboard().getTeam("\\u000181");
        if (team1 == null) {
            team1 = player.getScoreboard().registerNewTeam("\\u000181");
        }
        team1.addEntry(player.getName());
        for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
            Team team2 = loopPlayer.getScoreboard().getTeam("\\u000181");
            if (team2 == null) {
                team2 = loopPlayer.getScoreboard().registerNewTeam("\\u000181");
            }
            team2.addEntry(player.getName());
            team2.addEntry(loopPlayer.getName());
            team1.addEntry(loopPlayer.getName());
            team1.addEntry(player.getName());
        }
    }

    public static String[] splitStrings(String text, int rawSlot) {
        if (text.length() > 16) {
            String prefix = text.substring(0, 16);
            String suffix;
            if (prefix.charAt(15) == '§' || prefix.charAt(15) == '&') {
                prefix = prefix.substring(0, 15);
                suffix = text.substring(15);
            }
            else if (prefix.charAt(14) == '§' || prefix.charAt(14) == '&') {
                prefix = prefix.substring(0, 14);
                suffix = text.substring(14);
            }
            else {
                suffix = ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', prefix)) + text.substring(16);
            }
            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }
            return new String[] { prefix, (ChatColor.getLastColors(prefix).equalsIgnoreCase("") ? ChatColor.WHITE.toString() : ChatColor.getLastColors(prefix)) + suffix };
        }
        return new String[] { text };
    }

    private void setup() {
        ZigguratTablistCreateEvent event = new ZigguratTablistCreateEvent(this.player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        int possibleSlots = (PlayerUtil.getPlayerVersion(this.player) == PlayerVersion.v1_7) ? 60 : 80;
        for (int i = 1; i <= possibleSlots; ++i) {
            TabColumn tabColumn = TabColumn.getFromSlot(this.player, i);
            if (tabColumn != null) {
                Ziggurat.getInstance().getImplementation().removeSelf(this.player);
                TabEntry tabEntry = Ziggurat.getInstance().getImplementation().createFakePlayer(this, "0" + ((i > 9) ? Integer.valueOf(i) : ("0" + i)) + "|Tab", tabColumn, tabColumn.getNumb(this.player, i), i);
                if (PlayerUtil.getPlayerVersion(this.player) == PlayerVersion.v1_7 || Ziggurat.getInstance().getImplementation() instanceof v1_7TabImpl) {
                    Team team = this.player.getScoreboard().getTeam(LegacyClientUtils.teamNames.get(i - 1));
                    if (team != null) {
                        team.unregister();
                    }
                    team = this.player.getScoreboard().registerNewTeam(LegacyClientUtils.teamNames.get(i - 1));
                    team.setPrefix("");
                    team.setSuffix("");
                    team.addEntry(LegacyClientUtils.tabEntrys.get(i - 1));
                }
                this.currentEntries.add(tabEntry);
            }
        }
        this.update();
        boolean scoreboard = Ziggurat.getInstance().getScoreboardPlugins().stream().anyMatch(plugin -> Bukkit.getPluginManager().isPluginEnabled(plugin));
        if (scoreboard) {
            new BukkitRunnable() {
                public void run() {
                    for (int i = 1; i <= possibleSlots; ++i) {
                        if (PlayerUtil.getPlayerVersion(ZigguratTablist.this.player) == PlayerVersion.v1_7 || Ziggurat.getInstance().getImplementation() instanceof v1_7TabImpl) {
                            Team team = ZigguratTablist.this.player.getScoreboard().getTeam(LegacyClientUtils.teamNames.get(i - 1));
                            if (team != null) {
                                team.unregister();
                            }
                            team = ZigguratTablist.this.player.getScoreboard().registerNewTeam(LegacyClientUtils.teamNames.get(i - 1));
                            team.setPrefix("");
                            team.setSuffix("");
                            team.addEntry(LegacyClientUtils.tabEntrys.get(i - 1));
                        }
                    }
                }
            }.runTaskLater(Ziggurat.getInstance().getPlugin(), 40L);
        }
    }

    public void update() {
        ZigguratTablistUpdateEvent event = new ZigguratTablistUpdateEvent(this.player);
        AtomicBoolean cancelled = new AtomicBoolean(false);
        Bukkit.getScheduler().runTask(Ziggurat.getInstance().getPlugin(), () -> {
            Bukkit.getPluginManager().callEvent(event);
            cancelled.set(event.isCancelled());
        });
        if (cancelled.get())
            return;
        Set<TabEntry> previous = new HashSet<>(this.currentEntries);
        Ziggurat.getInstance().getImplementation().updateHeaderAndFooter(this, Ziggurat.getInstance().getAdapter().getHeader(), Ziggurat.getInstance().getAdapter().getFooter());
        Set<BufferedTabObject> processedObjects = Ziggurat.getInstance().getAdapter().getSlots(this.player);
        for (BufferedTabObject scoreObject : processedObjects) {
            TabEntry tabEntry = this.getEntry(scoreObject.getColumn(), scoreObject.getSlot());
            if (tabEntry != null) {
                previous.remove(tabEntry);
                Ziggurat.getInstance().getImplementation().updateFakeName(this, tabEntry, scoreObject.getText());
                Ziggurat.getInstance().getImplementation().updateFakeLatency(this, tabEntry, scoreObject.getPing());
                if (PlayerUtil.getPlayerVersion(this.player) == PlayerVersion.v1_7 || tabEntry.getTexture().toString().equals(scoreObject.getSkinTexture().toString())) {
                    continue;
                }
                Ziggurat.getInstance().getImplementation().updateFakeSkin(this, tabEntry, scoreObject.getSkinTexture());
            }
        }
        for (TabEntry tabEntry2 : previous) {
            Ziggurat.getInstance().getImplementation().updateFakeName(this, tabEntry2, "");
            Ziggurat.getInstance().getImplementation().updateFakeLatency(this, tabEntry2, 0);
            if (PlayerUtil.getPlayerVersion(this.player) != PlayerVersion.v1_7) {
                Ziggurat.getInstance().getImplementation().updateFakeSkin(this, tabEntry2, ZigguratCommons.defaultTexture);
            }
        }
        previous.clear();
    }

    public void destory() {
        ZigguratTablistDestroyEvent event = new ZigguratTablistDestroyEvent(this.player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Team team = this.player.getScoreboard().getTeam("\\u000181");
        if (team != null) {
            team.unregister();
        }
        Ziggurat.getInstance().removeTablist(this.player);
    }

    public TabEntry getEntry(TabColumn column, Integer slot) {
        for (TabEntry entry : this.currentEntries) {
            if (entry.getColumn().name().equalsIgnoreCase(column.name()) && entry.getSlot() == slot) {
                return entry;
            }
        }
        return null;
    }
}
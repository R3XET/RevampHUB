package me.allen.ziggurat.listener;

import me.allen.ziggurat.Ziggurat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class ZigguratListeners implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard())
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        Ziggurat.getInstance().create(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Team team = player.getScoreboard().getTeam("\\u000181");
        if (team != null)
            team.unregister();
        Ziggurat.getInstance().getTablist(player).destory();
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        Objects.requireNonNull(Ziggurat.getInstance().getThread()).getExecutorService().shutdownNow();
    }
}

package eu.revamp.hub.utilities.assemble;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.utilities.assemble.events.AssembleBoardCreateEvent;
import eu.revamp.hub.utilities.assemble.events.AssembleBoardDestroyEvent;
import eu.revamp.spigot.utils.generic.Tasks;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AssembleListener implements Listener {
  @Getter private Assemble assemble;
  
  public AssembleListener(Assemble paramAssemble) {
    this.assemble = paramAssemble;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Tasks.runLater(RevampHub.INSTANCE, () ->{
      AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(event.getPlayer());
      Bukkit.getPluginManager().callEvent(createEvent);
      if (createEvent.isCancelled())
        return;
      getAssemble().getBoards().put(event.getPlayer().getUniqueId(), new AssembleBoard(event.getPlayer(), getAssemble()));
    }, 30L);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    AssembleBoardDestroyEvent destroyEvent = new AssembleBoardDestroyEvent(event.getPlayer());
    Bukkit.getPluginManager().callEvent(destroyEvent);
    if (destroyEvent.isCancelled())
      return;
    getAssemble().getBoards().remove(event.getPlayer().getUniqueId());
    event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
  }
}


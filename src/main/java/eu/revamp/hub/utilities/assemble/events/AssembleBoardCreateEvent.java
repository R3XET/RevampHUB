package eu.revamp.hub.utilities.assemble.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AssembleBoardCreateEvent extends Event implements Cancellable {
  public static HandlerList handlerList = new HandlerList();
  
  private Player player;
  
  private boolean cancelled = false;
  
  public AssembleBoardCreateEvent(Player paramPlayer) {
    this.player = paramPlayer;
  }
  
  public void setCancelled(boolean paramBoolean) {
    this.cancelled = paramBoolean;
  }
  
  public HandlerList getHandlers() {
    return handlerList;
  }
  
  public Player getPlayer() {
    return this.player;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public static HandlerList getHandlerList() {
    return handlerList;
  }
}

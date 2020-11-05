package eu.revamp.hub.utilities.assemble.events;

import eu.revamp.hub.utilities.assemble.AssembleBoard;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AssembleBoardCreatedEvent extends Event {
  public static HandlerList handlerList = new HandlerList();
  
  private boolean cancelled = false;
  
  private final AssembleBoard board;
  
  public AssembleBoardCreatedEvent(AssembleBoard paramAssembleBoard) {
    this.board = paramAssembleBoard;
  }
  
  public HandlerList getHandlers() {
    return handlerList;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public AssembleBoard getBoard() {
    return this.board;
  }
  
  public void setCancelled(boolean paramBoolean) {
    this.cancelled = paramBoolean;
  }
  
  public static HandlerList getHandlerList() {
    return handlerList;
  }
}

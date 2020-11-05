package me.allen.ziggurat.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.beans.ConstructorProperties;

@Getter
@Setter
public abstract class ZigguratTablistEvent extends Event implements Cancellable
{
    public static HandlerList handlerList = new HandlerList();
    private Player player;
    private boolean cancelled;
    
    public HandlerList getHandlers() {
        return ZigguratTablistEvent.handlerList;
    }
    
    @ConstructorProperties({ "player" })
    public ZigguratTablistEvent(Player player) {
        this.player = player;
    }
    
    public static HandlerList getHandlerList() {
        return ZigguratTablistEvent.handlerList;
    }

}

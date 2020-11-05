package me.allen.ziggurat.events.type;

import me.allen.ziggurat.events.ZigguratTablistEvent;
import org.bukkit.entity.Player;

public class ZigguratTablistUpdateEvent extends ZigguratTablistEvent
{
    public ZigguratTablistUpdateEvent(Player player) {
        super(player);
    }
}

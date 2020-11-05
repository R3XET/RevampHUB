package me.allen.ziggurat.events.type;

import me.allen.ziggurat.events.ZigguratTablistEvent;
import org.bukkit.entity.Player;

public class ZigguratTablistCreateEvent extends ZigguratTablistEvent
{
    public ZigguratTablistCreateEvent(Player player) {
        super(player);
    }
}

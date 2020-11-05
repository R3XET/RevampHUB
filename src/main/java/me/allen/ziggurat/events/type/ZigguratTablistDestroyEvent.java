package me.allen.ziggurat.events.type;

import me.allen.ziggurat.events.ZigguratTablistEvent;
import org.bukkit.entity.Player;

public class ZigguratTablistDestroyEvent extends ZigguratTablistEvent
{
    public ZigguratTablistDestroyEvent(Player player) {
        super(player);
    }
}

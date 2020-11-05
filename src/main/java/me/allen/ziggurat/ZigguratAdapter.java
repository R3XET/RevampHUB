package me.allen.ziggurat;

import me.allen.ziggurat.objects.BufferedTabObject;
import org.bukkit.entity.Player;

import java.util.Set;

public interface ZigguratAdapter
{
    Set<BufferedTabObject> getSlots(Player p0);
    
    String getFooter();
    
    String getHeader();
}

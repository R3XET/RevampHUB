package me.allen.ziggurat.objects;

import me.allen.ziggurat.ZigguratTablist;
import org.bukkit.entity.Player;

public interface IZigguratHelper
{
    void removeSelf(Player p0);
    
    TabEntry createFakePlayer(ZigguratTablist p0, String p1, TabColumn p2, Integer p3, Integer p4);
    
    void updateFakeName(ZigguratTablist p0, TabEntry p1, String p2);
    
    void updateFakeLatency(ZigguratTablist p0, TabEntry p1, Integer p2);
    
    void updateFakeSkin(ZigguratTablist p0, TabEntry p1, SkinTexture p2);
    
    void updateHeaderAndFooter(ZigguratTablist p0, String p1, String p2);
}

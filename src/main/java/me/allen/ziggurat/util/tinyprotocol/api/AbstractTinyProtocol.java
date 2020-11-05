package me.allen.ziggurat.util.tinyprotocol.api;

import org.bukkit.entity.Player;

public interface AbstractTinyProtocol
{
    void sendPacket(Player p0, Object p1);
    
    void receivePacket(Player p0, Object p1);
    
    void injectPlayer(Player p0);
    
    int getProtocolVersion(Player p0);
    
    void uninjectPlayer(Player p0);
    
    boolean hasInjected(Player p0);
    
    void close();
}

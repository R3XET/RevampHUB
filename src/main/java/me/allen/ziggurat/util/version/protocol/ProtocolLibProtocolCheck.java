package me.allen.ziggurat.util.version.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.entity.Player;

public class ProtocolLibProtocolCheck implements ProtocolCheck
{
    @Override
    public int getVersion(Player player) {
        return ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
    }
}

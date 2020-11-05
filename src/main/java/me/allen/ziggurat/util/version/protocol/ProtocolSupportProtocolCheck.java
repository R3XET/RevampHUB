package me.allen.ziggurat.util.version.protocol;

import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;

public class ProtocolSupportProtocolCheck implements ProtocolCheck
{
    @Override
    public int getVersion(Player player) {
        return ProtocolSupportAPI.getProtocolVersion(player).getId();
    }
}

package me.allen.ziggurat.util.version.protocol;

import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

public class ViaVersionProtocolCheck implements ProtocolCheck
{
    @Override
    public int getVersion(Player player) {
        ViaAPI viaAPI = Via.getAPI();
        return viaAPI.getPlayerVersion(player.getUniqueId());
    }
}

package me.allen.ziggurat.util.player;

import lombok.Getter;
import me.allen.ziggurat.Ziggurat;
import me.allen.ziggurat.impl.universal.TinyProtocol;
import me.allen.ziggurat.util.tinyprotocol.api.AbstractTinyProtocol;
import me.allen.ziggurat.util.version.PlayerVersion;
import me.allen.ziggurat.util.version.ServerVersion;
import org.bukkit.entity.Player;

public class PlayerUtil
{
    @Getter
    private static AbstractTinyProtocol protocol;
    
    public static void init() {
        PlayerUtil.protocol = (Ziggurat.getInstance().getVersion().isAbove(ServerVersion.v1_7) ? new TinyProtocol(Ziggurat.getInstance().getPlugin()) {} : new me.allen.ziggurat.impl.v1_7.TinyProtocol(Ziggurat.getInstance().getPlugin()) {});
    }
    
    public static PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(Ziggurat.getInstance().getProtocolCheck().getVersion(player));
    }
}

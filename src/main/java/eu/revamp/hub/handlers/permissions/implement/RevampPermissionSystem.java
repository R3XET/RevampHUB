package eu.revamp.hub.handlers.permissions.implement;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.handlers.permissions.PermissionSystem;
import eu.revamp.system.plugin.RevampSystem;
import org.bukkit.entity.Player;

public class RevampPermissionSystem extends Handler implements PermissionSystem {
    public RevampPermissionSystem(RevampHub plugin) {
        super(plugin);
    }

    @Override
    public String getName(Player player) {
        eu.revamp.system.api.player.PlayerData targetProfile = RevampSystem.getINSTANCE().getPlayerManagement().getPlayerData(player.getUniqueId());
        return targetProfile != null ? targetProfile.getHighestRank().getDisplayName() : "Default";
    }

    @Override
    public String getPrefix(Player player) {
        eu.revamp.system.api.player.PlayerData targetProfile = RevampSystem.getINSTANCE().getPlayerManagement().getPlayerData(player.getUniqueId());
        return targetProfile != null ? targetProfile.getHighestRank().getPrefix() : "";
    }

    @Override
    public String getSuffix(Player player) {
        eu.revamp.system.api.player.PlayerData targetProfile = RevampSystem.getINSTANCE().getPlayerManagement().getPlayerData(player.getUniqueId());
        return targetProfile != null ? targetProfile.getHighestRank().getSuffix() : "";
    }
}


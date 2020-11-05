package eu.revamp.hub.handlers.permissions;

import org.bukkit.entity.Player;

public interface PermissionSystem {
    public String getName(Player var1);

    public String getPrefix(Player var1);

    public String getSuffix(Player var1);
}


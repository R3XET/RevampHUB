package me.allen.ziggurat.util.player;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class OfflinePlayerUtil
{

    public static OfflinePlayer createOfflinePlayer(String name) {
        return new OfflinePlayer() {
            private UUID uuid = UUID.randomUUID();
            
            public boolean isOnline() {
                return true;
            }
            
            public String getName() {
                return name;
            }
            
            public UUID getUniqueId() {
                return this.uuid;
            }
            
            public boolean isBanned() {
                return false;
            }

            public void setBanned(boolean b) {
            }
            
            public boolean isWhitelisted() {
                return false;
            }
            
            public void setWhitelisted(boolean b) {
            }
            
            public Player getPlayer() {
                return null;
            }
            
            public long getFirstPlayed() {
                return 0L;
            }
            
            public long getLastPlayed() {
                return 0L;
            }
            
            public boolean hasPlayedBefore() {
                return false;
            }
            
            public Location getBedSpawnLocation() {
                return null;
            }
            
            public Map<String, Object> serialize() {
                return null;
            }
            
            public boolean isOp() {
                return false;
            }
            
            public void setOp(boolean b) {
            }
        };
    }
}

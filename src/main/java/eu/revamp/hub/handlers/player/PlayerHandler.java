package eu.revamp.hub.handlers.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;

public class PlayerHandler extends Handler {
    private Map<UUID, PlayerData> data = new HashMap<>();

    public PlayerHandler(RevampHub plugin) {
        super(plugin);
    }

    public Map<UUID, PlayerData> getData() {
        return this.data;
    }

    public void setData(Map<UUID, PlayerData> data) {
        this.data = data;
    }
}


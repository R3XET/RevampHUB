package eu.revamp.hub.handlers.player;

import java.beans.ConstructorProperties;
import java.util.UUID;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.gadget.Gadget;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerData {
    private final UUID uniqueId;
    private final String name;
    private final RevampHub plugin;
    private Gadget activeGadget;
    private boolean hidingPlayers = false;
    private long playerVisibilityTime = -1L;

    @ConstructorProperties(value={"uniqueId", "name", "hub"})
    public PlayerData(UUID uniqueId, String name, RevampHub plugin) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.plugin = plugin;
    }
}


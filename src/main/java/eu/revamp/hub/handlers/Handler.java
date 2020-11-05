package eu.revamp.hub.handlers;

import java.beans.ConstructorProperties;
import eu.revamp.hub.RevampHub;

public class Handler {
    protected RevampHub plugin;

    @ConstructorProperties(value={"hub"})
    public Handler(RevampHub plugin) {
        this.plugin = plugin;
    }
}


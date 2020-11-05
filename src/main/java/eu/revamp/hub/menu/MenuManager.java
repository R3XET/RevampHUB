package eu.revamp.hub.menu;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.manager.Manager;
import eu.revamp.hub.menu.menu.AquaMenu;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager extends Manager {
    public Map<UUID, AquaMenu> openedMenus = new HashMap<>();
    public Map<UUID, AquaMenu> lastOpenedMenus = new HashMap<>();

    public MenuManager(RevampHub plugin) {
        super(plugin);
    }
}

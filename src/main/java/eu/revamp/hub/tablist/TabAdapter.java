package eu.revamp.hub.tablist;

import java.util.HashSet;
import java.util.Set;

import eu.revamp.hub.RevampHub;
import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.player.PlayerUtils;
import me.allen.ziggurat.ZigguratAdapter;
import me.allen.ziggurat.objects.BufferedTabObject;
import eu.revamp.hub.handlers.Handler;
import org.bukkit.entity.Player;

public class TabAdapter extends Handler implements ZigguratAdapter {
    public TabAdapter(RevampHub plugin) {
        super(plugin);
    }

    @Override
    public Set<BufferedTabObject> getSlots(Player player) {
        Set<BufferedTabObject> tabObjects = new HashSet<>();
        this.plugin.getTab().getConfigurationSection("LEFT").getKeys(false).forEach(key -> tabObjects.add(new BufferedTabObject().text(this.translatePlaceHolder(this.plugin.getTab().getString("LEFT." + key), player)).slot(Integer.parseInt(key))));
        this.plugin.getTab().getConfigurationSection("MIDDLE").getKeys(false).forEach(key -> tabObjects.add(new BufferedTabObject().text(this.translatePlaceHolder(this.plugin.getTab().getString("MIDDLE." + key), player)).slot(Integer.parseInt(key) + 20)));
        this.plugin.getTab().getConfigurationSection("RIGHT").getKeys(false).forEach(key -> tabObjects.add(new BufferedTabObject().text(this.translatePlaceHolder(this.plugin.getTab().getString("RIGHT." + key), player)).slot(Integer.parseInt(key) + 40)));
        this.plugin.getTab().getConfigurationSection("FAR-RIGHT").getKeys(false).forEach(key -> tabObjects.add(new BufferedTabObject().text(this.translatePlaceHolder(this.plugin.getTab().getString("FAR-RIGHT." + key), player)).slot(Integer.parseInt(key) + 60)));
        return tabObjects;
    }

    @Override
    public String getFooter() {
        return this.plugin.getCoreHandler().translate(this.plugin.getTab().getString("FOOTER"));
    }

    @Override
    public String getHeader() {
        return this.plugin.getCoreHandler().translate(this.plugin.getTab().getString("HEADER"));
    }

    private String translatePlaceHolder(String source, Player player) {
        Replacement replacement = new Replacement(source);
        replacement.add("{player}", player.getName());
        replacement.add("{rank}", this.plugin.getCoreHandler().getPermissionSystem().getName(player));
        replacement.add("{ping}", PlayerUtils.getPing(player));
        return this.plugin.getCoreHandler().translate(player, replacement.toString());
    }
}


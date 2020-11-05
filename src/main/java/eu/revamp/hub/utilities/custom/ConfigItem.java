package eu.revamp.hub.utilities.custom;

import java.util.ArrayList;
import java.util.List;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.utilities.config.ConfigurationFile;
import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.generic.GenericUtils;
import eu.revamp.spigot.utils.item.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter @Setter
public class ConfigItem {
    private ConfigurationFile configuration;
    private String path;
    private String name;
    private String action;
    private String command;
    private String gadgetName;
    private String itemKey;
    private Material material;
    private int durability;
    private int slot;
    private List<String> lore;
    private boolean commandEnabled;
    private boolean closeMenu;
    private boolean glow = false;

    public ConfigItem(ConfigurationFile configuration, String path) {
        this.configuration = configuration;
        this.path = path;
        this.closeMenu = true;
        this.setup();
    }

    public ConfigItem(ConfigurationFile configuration, String path, String gadgetName, String key) {
        this.configuration = configuration;
        this.path = path;
        this.closeMenu = true;
        this.gadgetName = gadgetName;
        this.itemKey = key;
        this.setup();
    }

    private void setup() {
        this.name = this.configuration.getString(this.path + ".name");
        this.material = GenericUtils.getMaterial(this.configuration.getString(this.path + ".material"));
        this.durability = this.configuration.getInt(this.path + ".durability");
        this.lore = this.configuration.getStringList(this.path + ".lore");
        this.slot = this.configuration.getInt(this.path + ".slot") - 1;
        this.action = this.configuration.getString(this.path + ".action");
        this.glow = this.configuration.getBoolean(this.path + ".glow");
        this.command = this.configuration.getString(this.path + ".command.execute");
        this.commandEnabled = this.configuration.getBoolean(this.path + ".command.enabled");
        if (this.configuration.contains(this.path + ".close-inventory")) {
            this.closeMenu = this.configuration.getBoolean(this.path + ".close-inventory", true);
            return;
        }
        this.closeMenu = true;
    }

    public void replaceLore(Replacement replacement) {
        ArrayList<String> lore = new ArrayList<>();
        this.lore.forEach(line -> replacement.getReplacements().keySet().forEach(o -> lore.add(line.replace(String.valueOf(o), String.valueOf(replacement.getReplacements().get(o))))));
        this.lore = CC.translate(lore);
    }

    public ItemStack toItemStack() {
        ItemBuilder item = new ItemBuilder(this.material);
        if (this.glow) {
            item.addEnchant(RevampHub.INSTANCE.getGlow(), 1);
        }
        item.setName(this.name);
        item.setLore(this.lore);
        item.setDurability((short) this.durability);
        return item.toItemStack();
    }
}


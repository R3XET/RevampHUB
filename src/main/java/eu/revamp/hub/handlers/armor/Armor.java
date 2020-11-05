package eu.revamp.hub.handlers.armor;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.stream.Collectors;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.utilities.config.ConfigurationFile;
import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.item.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter @Setter
public class Armor {
    private String permission;

    private boolean op;

    private Content helmet;

    private Content chestplate;

    private Content leggings;

    private Content boots;

    @ConstructorProperties({"permission", "op", "helmet", "chestplate", "leggings", "boots"})
    public Armor(String permission, boolean op, Content helmet, Content chestplate, Content leggings, Content boots) {
        this.permission = permission;
        this.op = op;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public void setup() {
        this.helmet.setup();
        this.chestplate.setup();
        this.leggings.setup();
        this.boots.setup();
    }

    @Getter @Setter
    public static class Content {
        private final RevampHub plugin;

        private final ConfigurationFile configuration;

        private final String key;

        private final String type;

        private Color color;

        private String name;

        private List<String> lore;

        private boolean enabled;

        @ConstructorProperties({"plugin", "configuration", "key", "type"})
        public Content(RevampHub plugin, ConfigurationFile configuration, String key, String type) {
            this.plugin = plugin;
            this.configuration = configuration;
            this.key = key;
            this.type = type;
        }


        public void setup() {
            try {
                this.color = CCUtils.getBukkitColor(this.configuration.getString("armor-cosmetics." + this.key + ".contents." + this.type + ".color"));
            } catch (Exception e) {
                this.color = Color.RED;
            }
            this.name = this.configuration.getString("armor-cosmetics." + this.key + ".contents." + this.type + ".name");
            this.enabled = this.configuration.getBoolean("armor-cosmetics." + this.key + ".contents." + this.type + ".enabled");
            this
                    .lore = this.configuration.getStringList("armor-cosmetics." + this.key + ".contents." + this.type + ".lore").stream().map(line -> line.replace("<rank>", "Default")).collect(Collectors.toList());
        }

        public void replaceContentFor(Player player) {
            this.name = this.name.replace("<rank>", this.plugin.getCoreHandler().getPermissionSystem().getName(player));
            this
                    .lore = this.configuration.getStringList("armor-cosmetics." + this.key + ".contents." + this.type + ".lore").stream().map(line -> line.replace("<rank>", this.plugin.getCoreHandler().getPermissionSystem().getName(player))).collect(Collectors.toList());
        }

        public ItemStack toItemStack() {
            return (new ItemBuilder(getType(this.type.toLowerCase()))).setName(this.name).setLore(this.lore).setLeatherArmorColor(this.color).toItemStack();
        }

        private Material getType(String type) {
            return type.equals("helmet") ? Material.LEATHER_HELMET : (type.equals("chestplate") ? Material.LEATHER_CHESTPLATE : (type.equals("leggings") ? Material.LEATHER_LEGGINGS : Material.LEATHER_BOOTS));
        }
    }
}

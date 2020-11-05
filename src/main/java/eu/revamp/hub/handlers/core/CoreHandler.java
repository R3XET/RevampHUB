package eu.revamp.hub.handlers.core;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.handlers.armor.Armor;
import eu.revamp.hub.handlers.core.enderbut.EnderButtType;
import eu.revamp.hub.handlers.launchpads.LaunchPad;
import eu.revamp.hub.handlers.menu.CustomMenu;
import eu.revamp.hub.handlers.permissions.PermissionSystem;
import eu.revamp.hub.handlers.permissions.implement.RevampPermissionSystem;
import eu.revamp.hub.handlers.server.PlayerCount;
import eu.revamp.hub.handlers.server.Server;
import eu.revamp.hub.utilities.config.ConfigurationFile;
import eu.revamp.hub.utilities.custom.ConfigItem;
import eu.revamp.hub.utilities.particles.ParticleEffect_Older_19;
import eu.revamp.hub.utilities.particles.ReflectionUtils;
import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.player.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;
import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
public class CoreHandler extends Handler {
    private PlayerCount playerCount = new PlayerCount(this.plugin);
    private LaunchPad launchPad;
    private Spawn spawn;
    private List<Armor> armorData = new ArrayList<>();
    private PermissionSystem permissionSystem;
    private List<ConfigItem> joinItems = new ArrayList<>();
    private Map<String, CustomMenu> customMenuData = new HashMap<>();
    private EnderButtType enderButtType = EnderButtType.RIDING;

    public CoreHandler(RevampHub plugin) {
        super(plugin);
        this.setup();
    }

    public void setup() {
        try {
            this.enderButtType = EnderButtType.valueOf(this.plugin.getConfiguration().getString("ender-butt-type").toUpperCase());
            return;
        }
        catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("&c[Log] Failed to set ender butt mode to " + this.plugin.getConfiguration().getString("ender-butt-type") + ", using RIDING mode for now."));
        }
    }

    public void setupLaunchPads() {
        this.launchPad = new LaunchPad(this.plugin.getConfiguration().getBoolean("launch-pads.enabled"), this.plugin.getConfiguration().getString("launch-pads.pressure-plate-type"), this.plugin.getConfiguration().getString("launch-pads.launch-sound"), this.plugin.getConfiguration().getDouble("launch-pads.velocity.multiply"), this.plugin.getConfiguration().getDouble("launch-pads.velocity.vertical"), null);
        this.launchPad.setupMaterial();
    }

    public void setupSpawn() {
        this.spawn = new Spawn(this.plugin.getConfiguration());
        this.spawn.setup();
        World world = Bukkit.getWorld(this.spawn.getWorld());
        if (world != null) return;
        Bukkit.createWorld(new WorldCreator(this.spawn.getWorld()));
    }

    public void setupArmor() {
        this.armorData.clear();
        ConfigurationSection section = this.plugin.getConfiguration().getConfigurationSection("armor-cosmetics");
        if (section == null) {
            return;
        }
        section.getKeys(false).forEach(key -> {
            this.armorData.add(new Armor(this.plugin.getConfiguration().getString("armor-cosmetics." + key + ".permission"), this.plugin.getConfiguration().getBoolean("armor-cosmetics." + key + ".op"), new Armor.Content(this.plugin, this.plugin.getConfiguration(), key, "helmet"), new Armor.Content(this.plugin, this.plugin.getConfiguration(), key, "chestplate"), new Armor.Content(this.plugin, this.plugin.getConfiguration(), key, "leggings"), new Armor.Content(this.plugin, this.plugin.getConfiguration(), key, "boots")));
            Bukkit.getConsoleSender().sendMessage(CC.translate("&eArmor for " + key + " has been loaded. &7(&bPermission: " + this.plugin.getConfiguration().getString("armor-cosmetics." + key + ".permission") + "&7, &bOP: " + this.plugin.getConfiguration().getBoolean("armor-cosmetics." + key + ".op")));
        });
        this.armorData.forEach(Armor::setup);
    }

    public boolean canClickArmor(Player player, Material material) {
        if (material == Material.LEATHER_HELMET) {
            return this.armorData.stream().filter(armor -> {
                if (!armor.getHelmet().isEnabled()) return false;
                return player.hasPermission(armor.getPermission());
            }).findFirst().orElse(null) == null;
        }
        if (material == Material.LEATHER_CHESTPLATE) {
            return this.armorData.stream().filter(armor -> {
                if (!armor.getChestplate().isEnabled()) return false;
                return player.hasPermission(armor.getPermission());
            }).findFirst().orElse(null) == null;
        }
        if (material == Material.LEATHER_LEGGINGS) {
            return this.armorData.stream().filter(armor -> {
                if (!armor.getLeggings().isEnabled()) return false;
                return player.hasPermission(armor.getPermission());
            }).findFirst().orElse(null) == null;
        }
        if (material != Material.LEATHER_BOOTS) return true;
        return this.armorData.stream().filter(armor -> {
            if (!armor.getBoots().isEnabled()) return false;
            return player.hasPermission(armor.getPermission());
        }).findFirst().orElse(null) == null;
    }

    public void applyArmor(Player player) {
        this.armorData.forEach(armor -> {
            Armor.Content content;
            if (armor.isOp() && player.isOp() && armor.getHelmet().isEnabled()) {
                content = armor.getHelmet();
                content.replaceContentFor(player);
                player.getInventory().setHelmet(content.toItemStack());
            } else if (!player.isOp() && player.hasPermission(armor.getPermission()) && !armor.isOp() && armor.getHelmet().isEnabled()) {
                content = armor.getHelmet();
                content.replaceContentFor(player);
                player.getInventory().setHelmet(content.toItemStack());
            }
            if (armor.isOp() && player.isOp() && armor.getChestplate().isEnabled()) {
                content = armor.getChestplate();
                content.replaceContentFor(player);
                player.getInventory().setChestplate(content.toItemStack());
            } else if (!player.isOp() && player.hasPermission(armor.getPermission()) && !armor.isOp() && armor.getChestplate().isEnabled()) {
                content = armor.getChestplate();
                content.replaceContentFor(player);
                player.getInventory().setChestplate(content.toItemStack());
            }
            if (armor.isOp() && player.isOp() && armor.getLeggings().isEnabled()) {
                content = armor.getLeggings();
                content.replaceContentFor(player);
                player.getInventory().setLeggings(content.toItemStack());
            } else if (!player.isOp() && player.hasPermission(armor.getPermission()) && !armor.isOp() && armor.getLeggings().isEnabled()) {
                content = armor.getLeggings();
                content.replaceContentFor(player);
                player.getInventory().setLeggings(content.toItemStack());
            }
            if (armor.isOp() && player.isOp() && armor.getBoots().isEnabled()) {
                content = armor.getBoots();
                content.replaceContentFor(player);
                player.getInventory().setBoots(content.toItemStack());
                return;
            }
            if (player.isOp()) return;
            if (!player.hasPermission(armor.getPermission())) return;
            if (armor.isOp()) return;
            if (!armor.getBoots().isEnabled()) return;
            content = armor.getBoots();
            content.replaceContentFor(player);
            player.getInventory().setBoots(content.toItemStack());
        });
        player.updateInventory();
    }

    public void setupPermissionSystem() {
        if (this.plugin.getServer().getPluginManager().getPlugin("RevampSystem") != null) {
            this.permissionSystem = new RevampPermissionSystem(this.plugin);
            return;
        }
        this.permissionSystem = new PermissionSystem(){

            @Override
            public String getName(Player player) {
                return "Default";
            }

            @Override
            public String getPrefix(Player player) {
                return "";
            }

            @Override
            public String getSuffix(Player player) {
                return "";
            }
        };
    }

    public void setupJoinItems() {
        this.joinItems.clear();
        ConfigurationSection section = this.plugin.getConfiguration().getConfigurationSection("join-items");
        if (section == null) {
            return;
        }
        section.getKeys(false).forEach(key -> this.joinItems.add(new ConfigItem(this.plugin.getConfiguration(), "join-items." + key)));
    }

    public void setupCustomMenuData() {
        this.customMenuData.clear();
        ConfigurationSection section = this.plugin.getMenus().getConfigurationSection("menus");
        if (section == null) {
            return;
        }
        section.getKeys(false).forEach(key -> {
            ArrayList<ConfigItem> items = new ArrayList<>();
            ConfigurationSection itemSection = this.plugin.getMenus().getConfigurationSection("menus." + key + ".items");
            if (itemSection != null) {
                itemSection.getKeys(false).forEach(item -> items.add(new ConfigItem(this.plugin.getMenus(), "menus." + key + ".items." + item)));
            }
            String name = this.plugin.getMenus().getString("menus." + key + ".name", "").toLowerCase();
            this.customMenuData.put(name, new CustomMenu(name, this.plugin.getMenus().getString("menus." + key + ".menu-title"), this.plugin.getMenus().getInt("menus." + key + ".menu-size"), items));
        });
        ArrayList<ConfigItem> items = new ArrayList<>();
        ConfigurationSection itemSection = this.plugin.getGadgets().getConfigurationSection("menu.gadget-menu.items");
        if (itemSection != null) {
            itemSection.getKeys(false).forEach(item -> items.add(new ConfigItem(this.plugin.getGadgets(), "menu.gadget-menu.items." + item, this.plugin.getGadgets().getString("menu.gadget-menu.items." + item + ".gadget-name"), (String)item)));
        }
        String name = "gadgets-menu";
        this.customMenuData.put(name, new CustomMenu(name, this.plugin.getGadgets().getString("menu.gadget-menu.menu-title"), this.plugin.getGadgets().getInt("menu.gadget-menu.menu-size"), items));
    }

    private String replaceServerStatus(String source) {
        Server server;
        Iterator<Server> iterator = this.plugin.getServerHandler().getData().values().iterator();
        do {
            if (!iterator.hasNext()) return source;
            server = iterator.next();
        } while (!source.toLowerCase().contains("{status-" + server.getBungeeName().toLowerCase() + "}"));
        return source.replace("{status-" + server.getBungeeName().toLowerCase() + "}", server.getStatus());
    }

    private String replaceServerOnlinePlayers(String source) {
        Server server;
        Iterator<Server> iterator = this.plugin.getServerHandler().getData().values().iterator();
        do {
            if (!iterator.hasNext()) return source;
            server = iterator.next();
        } while (!source.toLowerCase().contains("{online-" + server.getBungeeName().toLowerCase() + "}"));
        return source.replace("{online-" + server.getBungeeName().toLowerCase() + "}", String.valueOf(server.getOnlinePlayers(this.plugin)));
    }

    private String replaceServerInQueue(String source) {
        Server server;
        Iterator<Server> iterator = this.plugin.getServerHandler().getData().values().iterator();
        do {
            if (!iterator.hasNext()) return source;
            server = iterator.next();
        } while (!source.toLowerCase().contains("{queued-" + server.getBungeeName().toLowerCase() + "}"));
        return source.replace("{queued-" + server.getBungeeName().toLowerCase() + "}", String.valueOf(server.getQueue().getInQueue()));
    }

    private String replaceMoreCounts(String source) {
        int i;
        ArrayList<String> servers = new ArrayList<>();
        if (!source.toLowerCase().contains("{count:")) {
            return source;
        }
        String replaced = source.split("count:")[1].replace("{count:", "").replace("}", "");
        StringBuilder serversString = new StringBuilder();
        String[] splitted = replaced.split(":");
        for (i = 0; i < splitted.length; ++i) {
            servers.add(splitted[i]);
            if (i != splitted.length - 1) {
                serversString.append(splitted[i]).append(":");
                continue;
            }
            serversString.append(splitted[i]);
        }
        i = 0;
        for (String server : servers) {
            Server srw = this.plugin.getServerHandler().getData().get(server);
            if (srw == null) continue;
            i += srw.getOnlinePlayers(this.plugin);
        }
        return source.replace("{count:" + serversString.toString() + "}", String.valueOf(i));
    }

    public List<String> translate(List<String> source) {
        return CC.translate(source.stream().map(this::translate).collect(Collectors.toList()));
    }

    public String translate(String source) {
        source = source.replace("|", "\u2503");
        source = source.replace("{bungee-online}", String.valueOf(this.plugin.getCoreHandler().getPlayerCount().getGlobalCount()));
        source = source.replace("{in-queues}", String.valueOf((int) PlayerUtils.getOnlinePlayers().stream().filter(player -> this.plugin.getQueueHandler().getQueueByPlayer(player) != null).count()));
        source = this.replaceMoreCounts(source);
        source = this.replaceServerStatus(source);
        source = this.replaceServerOnlinePlayers(source);
        source = this.replaceServerInQueue(source);
        return CC.translate(source);
    }

    public String translate(Player player, String source) {
        source = this.translate(source);
        if (this.plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) return CC.translate(source);
        source = PlaceholderAPI.setPlaceholders(player, source);
        return CC.translate(source);
    }

    public List<String> translate(Player player, List<String> source) {
        return source.stream().map(l -> this.translate(player, l)).map(this::translate).collect(Collectors.toList());
    }

    public void sendParticle(String particle, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) {
        String version = ReflectionUtils.PackageType.getServerVersion();
        try {
            ParticleEffect_Older_19.valueOf(particle).display(offsetX, offsetY, offsetZ, speed, amount, center, range);
            return;
        }
        catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("&eCouldn't play particle &c" + particle + "&e, it might not support your server version."));
            return;
        }
    }

    public void sendParticle(String particle, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players) {
        String version = ReflectionUtils.PackageType.getServerVersion();
        try {
            ParticleEffect_Older_19.valueOf(particle).display(offsetX, offsetY, offsetZ, speed, amount, center, players);
            return;
        }
        catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("&eCouldn't play particle &c" + particle + "&e, it might not support your server version."));
            return;
        }
    }

    @Getter @Setter
    public class Spawn {
        private final ConfigurationFile configuration;
        private String world;
        private double x;
        private double y;
        private double z;
        private float yaw;
        private float pitch;

        public void setup() {
            this.world = this.configuration.getString("spawn.world");
            this.x = this.configuration.getDouble("spawn.x");
            this.y = this.configuration.getDouble("spawn.y");
            this.z = this.configuration.getDouble("spawn.z");
            this.yaw = (float)this.configuration.getInt("spawn.yaw") + 0.0f;
            this.pitch = this.configuration.getInt("spawn.pitch");
        }

        public void save(Location location) {
            this.world = location.getWorld().getName();
            this.x = location.getX();
            this.y = location.getY();
            this.z = location.getZ();
            this.yaw = location.getYaw();
            this.pitch = location.getPitch();
            this.configuration.set("spawn.world", this.world);
            this.configuration.set("spawn.x", this.x);
            this.configuration.set("spawn.y", this.y);
            this.configuration.set("spawn.z", this.z);
            this.configuration.set("spawn.yaw", this.yaw);
            this.configuration.set("spawn.pitch", this.pitch);
            this.configuration.save();
        }

        public Location toLocation() {
            return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, this.yaw, this.pitch);
        }

        @ConstructorProperties(value={"configuration"})
        public Spawn(ConfigurationFile configuration) {
            this.configuration = configuration;
        }

    }

}


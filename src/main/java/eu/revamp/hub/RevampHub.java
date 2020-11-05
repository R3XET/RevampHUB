package eu.revamp.hub;

import eu.revamp.hub.commands.GadgetCommand;
import eu.revamp.hub.commands.HubCoreCommand;
import eu.revamp.hub.commands.SetSpawnCommand;
import eu.revamp.hub.commands.queue.JoinQueueCommand;
import eu.revamp.hub.commands.queue.LeaveQueueCommand;
import eu.revamp.hub.commands.queue.PauseQueueCommand;
import eu.revamp.hub.handlers.core.CoreHandler;
import eu.revamp.hub.handlers.gadget.GadgetHandler;
import eu.revamp.hub.handlers.player.PlayerHandler;
import eu.revamp.hub.handlers.server.Server;
import eu.revamp.hub.handlers.server.ServerHandler;
import eu.revamp.hub.listeners.MenuListener;
import eu.revamp.hub.listeners.OptionsListener;
import eu.revamp.hub.listeners.PlayerListener;
import eu.revamp.hub.listeners.items.InventoryListener;
import eu.revamp.hub.listeners.items.join.JoinItemsListener;
import eu.revamp.hub.menu.MenuManager;
import eu.revamp.hub.menu.menu.AquaMenu;
import eu.revamp.hub.messages.Message;
import eu.revamp.hub.queue.QueueHandler;
import eu.revamp.hub.scoreboard.ScorebardAdapter;
import eu.revamp.hub.tablist.TabAdapter;
import eu.revamp.hub.utilities.assemble.Assemble;
import eu.revamp.hub.utilities.config.ConfigurationFile;
import eu.revamp.hub.utilities.particles.ReflectionUtils;
import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.enchant.GlowEnchantment;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.spigot.utils.player.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import me.allen.ziggurat.Ziggurat;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter @Setter
public class RevampHub extends JavaPlugin {

    @Getter public static RevampHub INSTANCE;
    private PlayerHandler playerHandler;
    private ServerHandler serverHandler;
    private CoreHandler coreHandler;
    private MenuManager menuManager;
    private GadgetHandler gadgetHandler;
    private QueueHandler queueHandler;
    private GlowEnchantment glow;
    private boolean portalQueueSupport;
    private boolean reloading;
    private ConfigurationFile configuration;
    private ConfigurationFile messages;
    private ConfigurationFile menus;
    private ConfigurationFile gadgets;
    private ConfigurationFile scoreboard;
    private ConfigurationFile tab;

    public void onEnable() {
        INSTANCE = this;
        this.reloading = false;
        this.setupFiles();
        Message.load(this);
        this.loadHandlers();
        this.loadListeners();
        this.serverHandler.setupServers();
        this.coreHandler.getPlayerCount().setup();
        this.coreHandler.setupLaunchPads();
        this.coreHandler.setupJoinItems();
        this.coreHandler.setupSpawn();
        this.coreHandler.setupArmor();
        this.coreHandler.setupPermissionSystem();
        this.gadgetHandler.setupGadgets();
        this.coreHandler.setupCustomMenuData();
        if (this.scoreboard.getBoolean("enabled")) {
            new Assemble(this, new ScorebardAdapter(this));
        }

        if (this.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            try {
                new Ziggurat(this, new TabAdapter(this));
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(CC.translate("&c[Log-1] &eIt seems like that the TabAPI doesn't support your version of server. &7(&b" + ReflectionUtils.PackageType.getServerVersion() + "&7) &7(&c" + e.getMessage() + "&7)"));
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(CC.translate("&c[Log-1] &eYou need ProtocolLib for TabAPI to work properly, disabling for now.&7(&b" + ReflectionUtils.PackageType.getServerVersion() + "&7)"));
        }

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this.coreHandler.getPlayerCount());
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new OnlineStatusUpdate(), 50L, 50L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new MenuUpdate(), 20L, 20L);
        this.loadCommands();
        Bukkit.getWorlds().forEach(world -> {
            world.setTime(0L);
            /*
            List<Entity> entityList = world.getEntities();
            List<LivingEntity> livingEntityList = world.getLivingEntities();
            entityList.forEach(entity -> {
                if (!(entity instanceof ItemFrame)){
                    entity.remove();
                }
            });
            livingEntityList.forEach(entity -> {
                if (!(entity instanceof ItemFrame)){
                    entity.remove();
                }
            });*/
            //world.getEntities().forEach(Entity::remove);
            //world.getEntities().clear();
            //world.getLivingEntities().forEach(Entity::remove);
            world.setGameRuleValue("doDaylightCycle", "false");
        });
        this.portalQueueSupport = this.getServer().getPluginManager().getPlugin("Portal") != null && this.getServer().getPluginManager().getPlugin("Portal").isEnabled();
        Bukkit.getConsoleSender().sendMessage(CC.translate("&c=====&7======================&c====="));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&bPlugin&7: &e" + this.getName()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&bVersion&7: &ev" + this.getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&bAuthor&7: &e" + this.getDescription().getAuthors().toString()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&c=====&7======================&c====="));


        //ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        //Tasks.runLater(this, () -> Bukkit.dispatchCommand(console, "bb reload"), 200L);

    }

    public void setupFiles() {
        this.configuration = new ConfigurationFile(this, "settings.yml");
        this.messages = new ConfigurationFile(this, "messages.yml");
        this.menus = new ConfigurationFile(this, "menus.yml");
        this.gadgets = new ConfigurationFile(this, "gadgets.yml");
        this.scoreboard = new ConfigurationFile(this, "scoreboard.yml");
        this.tab = new ConfigurationFile(this, "tab.yml");
    }

    private void loadHandlers() {
        this.playerHandler = new PlayerHandler(this);
        this.serverHandler = new ServerHandler(this);
        this.coreHandler = new CoreHandler(this);
        this.menuManager = new MenuManager(this);
        this.queueHandler = new QueueHandler(this);
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new OptionsListener(this), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        this.getServer().getPluginManager().registerEvents(new JoinItemsListener(this), this);
        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
        this.gadgetHandler = new GadgetHandler(this);
        this.getServer().getPluginManager().registerEvents(this.gadgetHandler, this);
    }

    private void loadCommands() {
        new SetSpawnCommand(this);
        new GadgetCommand(this);
        if (this.serverHandler.isDefaultQueueSystem()) {
            new JoinQueueCommand(this);
            new LeaveQueueCommand(this);
            new PauseQueueCommand(this);
        }
        new HubCoreCommand(this);
    }

    private class MenuUpdate implements Runnable {

        @Override
        public void run() {
            PlayerUtils.getOnlinePlayers().forEach(player -> {
                AquaMenu aquaMenu = RevampHub.this.menuManager.getOpenedMenus().get(player.getUniqueId());
                if (aquaMenu == null) return;
                if (!aquaMenu.isUpdateInTask()) return;
                aquaMenu.update(player);
            });
        }
    }

    private class OnlineStatusUpdate implements Runnable {

        @Override
        public void run() {
            if (RevampHub.this.isReloading()) return;
            RevampHub.this.serverHandler.getData().values().forEach(Server::updateOnlineStatus);
        }
    }
}


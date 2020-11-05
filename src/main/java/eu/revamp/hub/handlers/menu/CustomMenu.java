package eu.revamp.hub.handlers.menu;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.menu.menu.AquaMenu;
import eu.revamp.hub.menu.slots.Slot;
import eu.revamp.hub.utilities.custom.ConfigItem;
import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.generic.Tasks;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@Getter @Setter
public class CustomMenu {
    private String name;

    private String title;

    private int size;

    private List<ConfigItem> items;

    private RevampHub plugin = RevampHub.INSTANCE;

    @ConstructorProperties({"name", "title", "size", "items"})
    public CustomMenu(String name, String title, int size, List<ConfigItem> items) {
        this.name = name;
        this.title = title;
        this.size = size;
        this.items = items;
    }

    public AquaMenu getMenu() {
        return new AquaMenu() {
            public List<Slot> getSlots(Player player) {
                List<Slot> slots = new ArrayList<>();
                CustomMenu.this.items.forEach(configItem -> slots.add(new Slot() {
                    public ItemStack getItem(Player player) {
                        List<String> lore = configItem.getLore();
                        String name = configItem.getName();
                        configItem.setName(CustomMenu.this.plugin.getCoreHandler().translate(player, configItem.getName()));
                        configItem.setLore(CustomMenu.this.plugin.getCoreHandler().translate(player, configItem.getLore()));
                        ItemStack item = configItem.toItemStack();
                        configItem.setLore(lore);
                        configItem.setName(name);
                        return item;
                    }

                    public int getSlot() {
                        return configItem.getSlot();
                    }

                    public void onClick(Player player, int slot, ClickType clickType) {
                        if (configItem.isCloseMenu())
                            Tasks.run(CustomMenu.this.plugin, player::closeInventory);
                        if (configItem.getAction().toLowerCase().startsWith("{openmenu:") && configItem.getAction().toLowerCase().endsWith("}")) {
                            String menu = configItem.getAction().replace("{openmenu:", "").replace("}", "").toLowerCase();
                            CustomMenu customMenu = CustomMenu.this.plugin.getCoreHandler().getCustomMenuData().get(menu);
                            if (customMenu != null) {
                                Tasks.run(CustomMenu.this.plugin, () -> customMenu.getMenu().open(player));
                            } else {
                                Bukkit.getConsoleSender().sendMessage(CC.translate("&c[MenuLog-2] &eThere is no menu with name &e&n" + menu + "&b &eto open for &b" + player.getName() + "&e. &c&oPlease check your configurations."));
                            }
                        }
                        if (configItem.isCommandEnabled())
                            player.performCommand(configItem.getCommand());
                    }
                }));
                if (!Slot.hasSlot(slots, CustomMenu.this.size - 1))
                    slots.add(new Slot() {
                        public ItemStack getItem(Player player) {
                            return null;
                        }

                        public int getSlot() {
                            return CustomMenu.this.size - 1;
                        }
                    });
                return slots;
            }

            public String getName(Player player) {
                return CustomMenu.this.title;
            }
        };
    }
}
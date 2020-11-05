package me.allen.ziggurat.plugin;

import me.allen.ziggurat.Ziggurat;
import me.allen.ziggurat.ZigguratAdapter;
import me.allen.ziggurat.objects.BufferedTabObject;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class ZigguratPlugin extends JavaPlugin
{
    public void onEnable() {
        new Ziggurat(this, new ZigguratAdapter() {
            @Override
            public Set<BufferedTabObject> getSlots(Player player) {
                Set<BufferedTabObject> tabObjects = new HashSet<>();
                int[] array;
                int[] ipSlots = array = new int[] { 20, 40, 60 };
                for (int ipSlot : array) {
                    tabObjects.add(new BufferedTabObject().text("  &ewww.example.com").slot(ipSlot));
                }
                int[] array2;
                int[] emptySlots = array2 = new int[] { 1, 2, 3, 4, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 24, 27, 29, 32, 33, 34, 35, 36, 37, 38, 39, 41, 42, 43, 44, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 61, 62, 63, 64, 65, 66, 72, 73, 74, 75, 76, 77, 78, 79, 80 };
                for (int emptySlot : array2) {
                    tabObjects.add(BufferedTabObject.EMPTY_COLUMN.slot(emptySlot));
                }
                tabObjects.add(new BufferedTabObject().slot(5).text("&6Store"));
                tabObjects.add(new BufferedTabObject().slot(6).text("store.example.com"));
                tabObjects.add(new BufferedTabObject().slot(22).text("&6&lExample Network"));
                tabObjects.add(new BufferedTabObject().slot(23).text("&eOnline: &f" + String.format("%,d", 1000) + "/" + String.format("%,d", 10000)));
                tabObjects.add(new BufferedTabObject().slot(25).text("&6Player Info"));
                tabObjects.add(new BufferedTabObject().slot(26).text("Rank: Member"));
                tabObjects.add(new BufferedTabObject().slot(28).text("&a&lServers"));
                tabObjects.add(new BufferedTabObject().slot(30).text("&eFactions"));
                tabObjects.add(new BufferedTabObject().slot(31).text("&c- WIP"));
                tabObjects.add(new BufferedTabObject().slot(45).text("&6TeamSpeak"));
                tabObjects.add(new BufferedTabObject().slot(46).text("ts.example.com"));
                tabObjects.add(new BufferedTabObject().slot(67).text("&c&lWARNING!!!"));
                tabObjects.add(new BufferedTabObject().slot(68).text("&ePlease use"));
                tabObjects.add(new BufferedTabObject().slot(69).text("&e1.7 for the"));
                tabObjects.add(new BufferedTabObject().slot(70).text("&eoptimal playing"));
                tabObjects.add(new BufferedTabObject().slot(71).text("&eexperience"));
                return tabObjects;
            }
            
            @Override
            public String getFooter() {
                return "Example Footer";
            }
            
            @Override
            public String getHeader() {
                return "Example Header";
            }
        });
    }
    
    public void onDisable() {
    }
}

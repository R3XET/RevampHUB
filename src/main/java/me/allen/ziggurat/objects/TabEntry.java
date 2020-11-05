package me.allen.ziggurat.objects;

import lombok.Getter;
import lombok.Setter;
import me.allen.ziggurat.ZigguratTablist;
import org.bukkit.OfflinePlayer;

import java.beans.ConstructorProperties;

@Getter
@Setter
public class TabEntry
{
    private String id;
    private OfflinePlayer offlinePlayer;
    private String text;
    private ZigguratTablist tab;
    private SkinTexture texture;
    private TabColumn column;
    private int slot;
    private int rawSlot;
    private int latency;
    
    @ConstructorProperties({ "id", "offlinePlayer", "text", "tab", "texture", "column", "slot", "rawSlot", "latency" })
    public TabEntry(String id, OfflinePlayer offlinePlayer, String text, ZigguratTablist tab, SkinTexture texture, TabColumn column, int slot, int rawSlot, int latency) {
        this.id = id;
        this.offlinePlayer = offlinePlayer;
        this.text = text;
        this.tab = tab;
        this.texture = texture;
        this.column = column;
        this.slot = slot;
        this.rawSlot = rawSlot;
        this.latency = latency;
    }
}

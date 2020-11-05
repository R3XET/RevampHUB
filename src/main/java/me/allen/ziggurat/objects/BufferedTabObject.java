package me.allen.ziggurat.objects;

import lombok.Getter;
import me.allen.ziggurat.util.ZigguratCommons;
import org.jetbrains.annotations.NotNull;

@Getter
public class BufferedTabObject {
    public static final BufferedTabObject EMPTY_COLUMN = (new BufferedTabObject()).ping(0).text("").skin(ZigguratCommons.defaultTexture);

    private TabColumn column = TabColumn.LEFT;

    private int ping = 0;

    private int slot = 1;

    private String text = "";

    private SkinTexture skinTexture = ZigguratCommons.defaultTexture;

    public BufferedTabObject text(String text) {
        this.text = text;
        return this;
    }

    public BufferedTabObject skin(SkinTexture skinTexture) {
        this.skinTexture = skinTexture;
        return this;
    }

    public BufferedTabObject slot(Integer slot) {
        this.slot = slot;
        return getBufferedTabObject();
    }

    public BufferedTabObject ping(Integer ping) {
        this.ping = ping;
        return this;
    }

    public BufferedTabObject column(TabColumn tabColumn) {
        this.column = tabColumn;
        return getBufferedTabObject();
    }

    @NotNull
    private BufferedTabObject getBufferedTabObject() {
        if (this.slot > 20 && this.slot <= 40) {
            this.slot -= 20;
            this.column = TabColumn.MIDDLE;
        } else if (this.slot > 40 && this.slot <= 60) {
            this.slot -= 40;
            this.column = TabColumn.RIGHT;
        } else if (this.slot > 60 && this.slot <= 80) {
            this.slot -= 60;
            this.column = TabColumn.FAR_RIGHT;
        }
        return this;
    }
}
package me.allen.ziggurat.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import me.allen.ziggurat.util.player.PlayerUtil;
import me.allen.ziggurat.util.version.PlayerVersion;
import org.bukkit.entity.Player;

@Getter
public enum TabColumn {
    LEFT(0, "Left", -2, 1, 3),
    MIDDLE(1, "Middle", -1, 21, 3),
    RIGHT(2, "Right", 0, 41, 3),
    FAR_RIGHT(3, "Far-Right", 60, 61, 1);
    private List<Integer> numbers = new ArrayList<>();

    private int startNumber;

    private int incrementBy;

    private int rawStart;

    private String identifier;

    private int ordinal;


    TabColumn(int ordinal, String identifier, int rawStart, int startNumber, int incrementBy) {
        this.ordinal = ordinal;
        this.identifier = identifier;
        this.rawStart = rawStart;
        this.startNumber = startNumber;
        this.incrementBy = incrementBy;
        generate();
    }

    public static TabColumn getColumn(String identifier) {
        for (TabColumn tabColumn : values()) {
            if (tabColumn.getIdentifier().equalsIgnoreCase(identifier))
                return tabColumn;
        }
        return null;
    }

    public static TabColumn getFromSlot(Player player, Integer slot) {
        if (PlayerUtil.getPlayerVersion(player) == PlayerVersion.v1_7)
            return Arrays.<TabColumn>stream(values())
                    .filter(tabColumn -> tabColumn.getNumbers().contains(slot))
                    .findFirst().get();
        if (isBetween(slot, 1, 20))
            return LEFT;
        if (isBetween(slot, 21, 40))
            return MIDDLE;
        if (isBetween(slot, 41, 60))
            return RIGHT;
        if (isBetween(slot, 61, 80))
            return FAR_RIGHT;
        return null;
    }

    public static TabColumn getFromOrdinal(int ordinal) {
        for (TabColumn column : values()) {
            if (column.getOrdinal() == ordinal)
                return column;
        }
        return null;
    }

    public static Boolean isBetween(Integer input, Integer min, Integer max) {
        return (input >= min && input <= max);
    }

    private void generate() {
        for (int i = 1; i <= 20; i++) {
            Integer numb = this.rawStart + i * this.incrementBy;
            this.numbers.add(numb);
        }
    }

    public Integer getNumb(Player player, int raw) {
        if (PlayerUtil.getPlayerVersion(player) != PlayerVersion.v1_7)
            return raw - this.startNumber + 1;
        int number = 0;
        for (Integer value : this.numbers) {
            int integer = value;
            number++;
            if (integer == raw)
                return number;
        }
        return number;
    }
}

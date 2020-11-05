package eu.revamp.hub.handlers.gadget;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Getter @Setter
public class Gadget {
    private String name;
    private String permission;
    private String effect;
    private float speed;
    private int amount;
    private int range;
    private int offsetX;
    private int offsetY;
    private int offsetZ;

    @ConstructorProperties(value = {"name", "permission", "effect", "speed", "amount", "range", "offsetX", "offsetY", "offsetZ"})
    public Gadget(String name, String permission, String effect, float speed, int amount, int range, int offsetX, int offsetY, int offsetZ) {
        this.name = name;
        this.permission = permission;
        this.effect = effect;
        this.speed = speed;
        this.amount = amount;
        this.range = range;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }
}


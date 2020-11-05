package eu.revamp.hub.handlers.launchpads;

import java.beans.ConstructorProperties;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public class LaunchPad {

    private boolean enabled;
    private String type;
    private String sound;
    private double multipy;
    private double vertical;
    private Material material;

    public void setupMaterial() {
        this.material = this.getMaterialType();
    }

    public Material getMaterialType() {
        try {
            Material material;
            if (this.type.toLowerCase().equals("gold")) {
                material = Material.valueOf("LEGACY_GOLD_PLATE");
                return material;
            }
            if (this.type.toLowerCase().equals("iron")) {
                material = Material.valueOf("LEGACY_IRON_PLATE");
                return material;
            }
            material = Material.valueOf("LEGACY_STONE_PLATE");
            return material;
        }
        catch (Exception e) {
            try {
                Material material;
                if (this.type.toLowerCase().equals("gold")) {
                    material = Material.valueOf("GOLD_PLATE");
                    return material;
                }
                if (this.type.toLowerCase().equals("iron")) {
                    material = Material.valueOf("IRON_PLATE");
                    return material;
                }
                material = Material.valueOf("STONE_PLATE");
                return material;
            }
            catch (Exception e2) {
                return null;
            }
        }
    }

    @ConstructorProperties(value={"enabled", "type", "sound", "multipy", "vertical", "material"})
    public LaunchPad(boolean enabled, String type, String sound, double multipy, double vertical, Material material) {
        this.enabled = enabled;
        this.type = type;
        this.sound = sound;
        this.multipy = multipy;
        this.vertical = vertical;
        this.material = material;
    }

}


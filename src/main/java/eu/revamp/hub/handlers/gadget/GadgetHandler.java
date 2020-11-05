package eu.revamp.hub.handlers.gadget;

import java.util.HashMap;
import java.util.Map;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;


public class GadgetHandler extends Handler
implements Listener {
   @Getter private Map<String, Gadget> gadgetData = new HashMap<>();

    public GadgetHandler(RevampHub plugin) {
        super(plugin);
    }

    public void setupGadgets() {
        this.gadgetData.clear();
        ConfigurationSection section = this.plugin.getGadgets().getConfigurationSection("gadgets");
        if (section == null) {
            return;
        }
        section.getKeys(false).forEach(key -> {
            String name = this.plugin.getGadgets().getString("gadgets." + key + ".name");
            this.gadgetData.put(name, new Gadget(name, this.plugin.getGadgets().getString("gadgets." + key + ".permission"), this.plugin.getGadgets().getString("gadgets." + key + ".effect.name"), this.plugin.getGadgets().getInt("gadgets." + key + ".effect.speed"), this.plugin.getGadgets().getInt("gadgets." + key + ".effect.amount"), this.plugin.getGadgets().getInt("gadgets." + key + ".effect.range"), this.plugin.getGadgets().getInt("gadgets." + key + ".effect.offset.x"), this.plugin.getGadgets().getInt("gadgets." + key + ".effect.offset.y"), this.plugin.getGadgets().getInt("gadgets." + key + ".effect.offset.z")));
        });
    }
}


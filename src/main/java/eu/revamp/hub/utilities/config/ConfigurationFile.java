package eu.revamp.hub.utilities.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import eu.revamp.spigot.utils.chat.color.CC;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigurationFile
extends YamlConfiguration {
    private File file;
    private JavaPlugin plugin;
    private String name;

    public ConfigurationFile(JavaPlugin plugin, String name) {
        this.file = new File(plugin.getDataFolder(), name);
        this.plugin = plugin;
        this.name = name;
        if (!this.file.exists()) {
            plugin.saveResource(name, false);
        }
        try {
            this.load(this.file);
            return;
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        this.file = new File(this.plugin.getDataFolder(), this.name);
        if (!this.file.exists()) {
            this.plugin.saveResource(this.name, false);
        }
        try {
            this.load(this.file);
            return;
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.save(this.file);
            return;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getInt(String path) {
        return super.getInt(path, 0);
    }

    public double getDouble(String path) {
        return super.getDouble(path, 0.0);
    }

    public boolean getBoolean(String path) {
        return super.getBoolean(path, false);
    }

    public String getString(String path, boolean check) {
        return super.getString(path, null);
    }

    public String getString(String path) {
        return CC.translate(super.getString(path, "String at path '" + path + "' not found.")).replace("|", "\u2503");
    }

    public List<String> getStringList(String path) {
        return super.getStringList(path).stream().map(CC::translate).collect(Collectors.toList());
    }

    public List<String> getStringList(String path, boolean check) {
        if (super.contains(path)) return super.getStringList(path).stream().map(CC::translate).collect(Collectors.toList());
        return null;
    }

    public boolean getOption(String option) {
        return this.getBoolean("options." + option);
    }
}


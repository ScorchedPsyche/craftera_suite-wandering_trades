package com.github.scorchedpsyche.craftera_suite.wandering_trades;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin
{

    private File customConfigFile;
    private FileConfiguration customConfig;

    // Plugin startup logic
    @Override
    public void onEnable()
    {
        createCustomConfig();
    }

    // Plugin shutdown logic
    @Override
    public void onDisable()
    {
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "trades.yml");
        if (!customConfigFile.exists()) {
            boolean wasSuccessful = customConfigFile.getParentFile().mkdirs();

            if (!wasSuccessful) {
                System.out.println("Trade list file creation was not successful.");
            } else {
                saveResource("trades.yml", false);
            }
        }

        customConfig= new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}

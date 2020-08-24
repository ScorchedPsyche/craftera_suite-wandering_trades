package com.github.scorchedpsyche.craftera_suite.wandering_trades.files;

import com.github.scorchedpsyche.craftera_suite.wandering_trades.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class TradeListFile
{
    public TradeListFile (Main plugin)
    {
        this.plugin = plugin;
        setup();
    }

    private Main plugin;
    private File tradeListFile;
    private FileConfiguration tradeList;

    public void setup()
    {
        tradeListFile = new File( plugin.getDataFolder(), "trade_list.yml" );

        if (!tradeListFile.exists()) {
            boolean wasSuccessful = tradeListFile.getParentFile().mkdirs();

            plugin.saveResource("trade_list.yml", false);
            Bukkit.getConsoleSender().sendMessage("[CraftEra Suite - Wandering Trades] New trade config file was " +
                                                          "created");
        }

        tradeList = new YamlConfiguration();
        try {
            tradeList.load(tradeListFile);
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getConsoleSender().sendMessage("[CraftEra Suite - Wandering Trades] TRADE LIST FILE NOT FOUND");
            e.printStackTrace();
        }
    }
}

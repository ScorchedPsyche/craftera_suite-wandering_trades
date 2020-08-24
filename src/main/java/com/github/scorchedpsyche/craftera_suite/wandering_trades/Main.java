package com.github.scorchedpsyche.craftera_suite.wandering_trades;

import com.github.scorchedpsyche.craftera_suite.wandering_trades.files.TradeListFile;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.listeners.WanderingTraderSpawnListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin
{
//    public static Main plugin;
    public static TradeListFile tradeList;

    // Plugin startup logic
    @Override
    public void onEnable()
    {
        tradeList = new TradeListFile(this);
//        plugin = this;
        getServer().getPluginManager().registerEvents(new WanderingTraderSpawnListener(), this);
    }

    // Plugin shutdown logic
    @Override
    public void onDisable()
    {
    }

//    public FileConfiguration getCustomConfig() {
//        return this.customConfig;
//    }
//
//    private void createCustomConfig() {
//        customConfigFile = new File(getDataFolder(), "trade_list.yml");
//
//        if (!customConfigFile.exists()) {
//            boolean wasSuccessful = customConfigFile.getParentFile().mkdirs();
//
//            saveResource("trade_list.yml", false);
//            Bukkit.getConsoleSender().sendMessage("[CraftEra Suite - Wandering Trades] New trade config file was " +
//                                                          "created");
//        }
//
//        customConfig = new YamlConfiguration();
//        try {
//            customConfig.load(customConfigFile);
//        } catch (IOException | InvalidConfigurationException e) {
//            Bukkit.getConsoleSender().sendMessage("[CraftEra Suite - Wandering Trades] TRADE LIST FILE NOT FOUND");
//            e.printStackTrace();
//        }
//    }
}

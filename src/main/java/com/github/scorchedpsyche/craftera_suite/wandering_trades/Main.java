package com.github.scorchedpsyche.craftera_suite.wandering_trades;

import com.github.scorchedpsyche.craftera_suite.wandering_trades.core.tradeListManager;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.listeners.WanderingTraderSpawnListener;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.models.PlayerProfileModel;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class Main extends JavaPlugin
{
//    public static Main plugin;
    public static tradeListManager tradeList;
    public static FileConfiguration config;
    public static List<GameProfile> playerProfiles;
    public static List<ItemStack> whitelistedPlayerHeads;

    public static List<WanderingTrader> unprocessedWanderingTraders;

    // Plugin startup logic
    @Override
    public void onEnable()
    {
        playerProfiles = new ArrayList<>();
        this.saveDefaultConfig();
        config = getConfig();
        tradeList = new tradeListManager(this);

        getServer().getPluginManager().registerEvents(new WanderingTraderSpawnListener(), this);

        getWhitelistedPlayerSkinsFromAPI();
    }

    // Plugin shutdown logic
    @Override
    public void onDisable()
    {
    }

    private void getWhitelistedPlayerSkinsFromAPI()
    {

//        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
        whitelistedPlayerHeads = new ArrayList<>();

        Runnable getPlayerProfilesFromMojang = new Runnable()
        {
            @Override
            public void run()
            {
                Bukkit.getConsoleSender().sendMessage( "[CraftEra Suite - Wandering Trades] " +
                        " Loading whitelisted player's heads");

                List<OfflinePlayer> playersList = new ArrayList<>( Bukkit.getWhitelistedPlayers() );

                for( OfflinePlayer player : playersList )
                {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                                                     .uri(URI.create(
                                                             "https://sessionserver.mojang.com/session/minecraft/profile/" +
                                                                     player.getUniqueId()
                                                                    ))
                                                     .build();

                        ItemStack playerHead = new ItemStack(
                                Material.PLAYER_HEAD,
                                Main.config.getInt("whitelist.heads_rewarded_per_trade"));

                        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                        meta.setOwningPlayer( player );
                        playerHead.setItemMeta( meta );

                        whitelistedPlayerHeads.add( playerHead );

//                    try
//                    {
//                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//                        PlayerProfileModel playerProfile = new Gson().fromJson(response.body(), PlayerProfileModel.class);
//
////                        System.out.print("==================================");
////                        System.out.print(playerProfile.getId());
////                        System.out.print(playerProfile.getName());
////                        System.out.print(playerProfile.getProperties().get(0).getValue());
////                        System.out.print("==================================");
//
//
//                        ItemStack playerHead = new ItemStack(
//                                Material.PLAYER_HEAD,
//                                Main.config.getInt("whitelist.heads_rewarded_per_trade"));
//
//                        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
//                        meta.setOwningPlayer( player );
//
////                        GameProfile profile = new GameProfile(
////                                player.getUniqueId(),
////                                player.getName());
//
////                        profile.getProperties().put(
////                                "textures",
////                                new Property("textures", playerProfile.getProperties().get(0).getValue()));
//
////                        try {
////                            Field profileField = meta.getClass().getDeclaredField("profile");
////                            profileField.setAccessible(true);
////                            profileField.set(meta, playerProfile);
////                        } catch (NoSuchFieldException | IllegalAccessException e) {
////                            e.printStackTrace();
////                        }
//
//
//                        playerHead.setItemMeta( meta );
//
////                        Bukkit.getPlayer("ScorchedPsyche").
////                                getInventory().addItem
////                                (   playerHead
////                                )  ;
////                        Bukkit.getPlayer("ScorchedPsyche").updateInventory();
//                    } catch ( IOException ex ) {
//                        ex.printStackTrace();
//                    } catch ( InterruptedException ex ) {
//                        ex.printStackTrace();
//                    }
                }
                Bukkit.getConsoleSender().sendMessage( "[CraftEra Suite - Wandering Trades] " +
                                                               " Finished loading whitelisted players' heads");
            }
        };

        Bukkit.getScheduler().runTaskAsynchronously( Main.getPlugin(Main.class), getPlayerProfilesFromMojang );
    }


//    public void LoadAsJavaObjectList()
//    {
//        Yaml yaml = new Yaml();
//        try (InputStream in = LoadAsJavaObject.class.getResourceAsStream(
//                "craftera_suite-wandering_trades/trade_list.yml"))
//        {
//            TradeModel trades = yaml.loadAs(in, TradeModel.class);
//        }
//    }

//    public void LoadAsJavaObject() {
//        Yaml yaml = new Yaml();
//        try (InputStream in = LoadAsJavaObject.class
//                .getResourceAsStream("/person.yml")) {
//            Person person = yaml.loadAs(in, Person.class);
//            System.out.println(person);
//        }
//    }
//}

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

package com.github.scorchedpsyche.craftera_suite.wandering_trades;

import com.github.scorchedpsyche.craftera_suite.wandering_trades.core.TradeListManager;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.listeners.WanderingTraderSpawnListener;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin
{
    public static TradeListManager tradeList;
    public static FileConfiguration config;
    public static List<GameProfile> playerProfiles;
    public static List<ItemStack> whitelistedPlayerHeads;

    // Plugin startup logic
    @Override
    public void onEnable()
    {
        playerProfiles = new ArrayList<>();
        this.saveDefaultConfig();
        config = getConfig();
        tradeList = new TradeListManager(this);

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
        whitelistedPlayerHeads = new ArrayList<>();

        Runnable getPlayerProfilesFromMojang = () -> {
            Bukkit.getConsoleSender().sendMessage( "[CraftEra Suite - Wandering Trades] " +
                    " Loading whitelisted player's heads");

            List<OfflinePlayer> playersList = new ArrayList<>( Bukkit.getWhitelistedPlayers() );

            for( OfflinePlayer player : playersList )
            {
                    ItemStack playerHead = new ItemStack(
                            Material.PLAYER_HEAD,
                            Main.config.getInt("whitelist.heads_rewarded_per_trade"));

                    SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                    assert meta != null;
                    meta.setOwningPlayer(player);
                    playerHead.setItemMeta( meta );

                    whitelistedPlayerHeads.add( playerHead );
            }
            Bukkit.getConsoleSender().sendMessage( "[CraftEra Suite - Wandering Trades] " +
                                                           " Finished loading whitelisted players' heads");
        };

        Bukkit.getScheduler().runTaskAsynchronously( Main.getPlugin(Main.class), getPlayerProfilesFromMojang );
    }
}

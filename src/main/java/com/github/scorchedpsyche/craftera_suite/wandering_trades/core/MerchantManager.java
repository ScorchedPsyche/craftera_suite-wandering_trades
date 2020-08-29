package com.github.scorchedpsyche.craftera_suite.wandering_trades.core;

import com.github.scorchedpsyche.craftera_suite.wandering_trades.Main;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.models.TradeEntryModel;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftMerchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class MerchantManager
{
    public static void removeDefaultTrades(  WanderingTrader merchant )
    {
        merchant.setRecipes( new ArrayList<>() );
    }

    public static void addTrades( WanderingTrader merchant )
    {
        List<MerchantRecipe> trades = new ArrayList<>();

        System.out.print("====================================================");
        for( TradeEntryModel trade : Main.tradeList.Trades.offers )
        {
            System.out.print(trade.getMinecraftId());
            if( trade.getMinecraftId().equalsIgnoreCase("player_head") )
            {
                if( trade.getTexture() == null )
                {
                    // Player Head
                    System.out.print( "OWNER: " + trade.getOwnerId());

                    ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                    meta.setOwningPlayer( Bukkit.getPlayerExact( trade.getOwnerId() ) );
                    playerHead.setItemMeta( meta );

                    MerchantRecipe recipe = new MerchantRecipe(
                            playerHead,
                            1
                    );

                    recipe.addIngredient(new ItemStack(
                            Material.DIAMOND,
                            1));

                    trades.add( recipe );
                } else {
                    // Decoration Head
                    System.out.print( "3");
                }
            } else {
                // Other items
                MerchantRecipe recipe = new MerchantRecipe(
                        new ItemStack(
                                Material.matchMaterial(trade.getMinecraftId()),
                                trade.getAmount()),
                        trade.getUsesMax()
                );

                recipe.addIngredient(new ItemStack(
                        Material.matchMaterial(trade.getPriceItem1()),
                        trade.getPrice1()));

                if ( trade.getPriceItem2() != null && trade.getPrice2() != null )
                {
                    recipe.addIngredient(new ItemStack(
                            Material.matchMaterial(trade.getPriceItem2()),
                            trade.getPrice2()));
                }
                trades.add( recipe );
            }
        }



        // WHITELIST Player Head Synchronization
        if( Main.config.getBoolean("whitelist.enable_synchronization") ) // TO DO
        {
//            Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
//                Collections.shuffle( Main.whitelistedPlayerHeads );

                for( int i = 0; i < Main.config.getInt("whitelist.number_of_player_head_offers"); i++ )
                {
                    // Item Reward
                    MerchantRecipe recipe = new MerchantRecipe(
                            Main.whitelistedPlayerHeads.get(i),
                            0,
                            Main.config.getInt("whitelist.maximum_number_of_trades"),
                            Main.config.getBoolean("whitelist.experience_rewarded_for_each_trade")
                    );

                    // Item 1
                    if( Main.config.contains("whitelist.price.item1") )
                    {
                        recipe.addIngredient(new ItemStack(
                                Material.matchMaterial( Main.config.getString("whitelist.price.item1.minecraft_id") ),
                                Main.config.getInt("whitelist.price.item1.quantity")));
                    }

                    // Item 2
                    if( Main.config.contains("whitelist.price.item2") )
                    {
                        recipe.addIngredient(new ItemStack(
                                Material.matchMaterial( Main.config.getString("whitelist.price.item2.minecraft_id") ),
                                Main.config.getInt("whitelist.price.item2.quantity")));
                    }

                    trades.add( recipe );
                }

//                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                    merchant.setRecipes( trades );
//                });
//            });





















//            List<OfflinePlayer> playersList = new ArrayList<>( Bukkit.getWhitelistedPlayers() );
////            Collections.shuffle(playersList);
//            Collections.shuffle( Main.whitelistedPlayerHeads );
//
//            for( int i = 0; i < Main.config.getInt("whitelist.number_of_player_head_offers"); i++ )
//            {
////                OfflinePlayer randomPlayer = playersList.get( i );
////
////                ItemStack playerHead = new ItemStack(
////                        Material.PLAYER_HEAD,
////                        Main.config.getInt("whitelist.heads_rewarded_per_trade"));
////
////                SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
////                meta.setOwningPlayer( randomPlayer );
////                playerHead.setItemMeta( meta );
//
//                // Item Reward
//                MerchantRecipe recipe = new MerchantRecipe(
//                        Main.whitelistedPlayerHeads.get(i),
//                        0,
//                        Main.config.getInt("whitelist.maximum_number_of_trades"),
//                        Main.config.getBoolean("whitelist.experience_rewarded_for_each_trade")
//                );
//
//                // Item 1
//                if( Main.config.contains("whitelist.price.item1") )
//                {
//                    recipe.addIngredient(new ItemStack(
//                            Material.matchMaterial( Main.config.getString("whitelist.price.item1.minecraft_id") ),
//                            Main.config.getInt("whitelist.price.item1.quantity")));
//                }
//
//                // Item 2
//                if( Main.config.contains("whitelist.price.item2") )
//                {
//                    recipe.addIngredient(new ItemStack(
//                            Material.matchMaterial( Main.config.getString("whitelist.price.item2.minecraft_id") ),
//                            Main.config.getInt("whitelist.price.item2.quantity")));
//                }
//
//                trades.add( recipe );
//            }
//
//
//
////            for ( OfflinePlayer player : Bukkit.getWhitelistedPlayers() )
////            {
////                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
////                SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
////                meta.setOwningPlayer( player );
////                playerHead.setItemMeta( meta );
////
////                MerchantRecipe recipe = new MerchantRecipe(
////                        playerHead,
////                        64
////                );
////
////                recipe.addIngredient(new ItemStack(
////                        Material.DIAMOND,
////                        1));
////
////                trades.add( recipe );
////            }
        }
//
//        Runnable updateRecipesTask = new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                merchant.setRecipes( trades );
//            }
//        };
////
////        Thread thread = new Thread() {
////            public void run() {
////                merchant.setRecipes( trades );
////            }
////        };
////        thread.start();
//
//        Bukkit.getScheduler().runTaskAsynchronously( Main.getPlugin(Main.class), updateRecipesTask );
////        Bukkit.getScheduler().scheduleSyncDelayedTask( Main.getPlugin(Main.class), updateRecipesTask );
    }

    private static void addItemOffers(List<MerchantRecipe> trades)
    {
        for( TradeEntryModel trade : Main.tradeList.Trades.offers )
        {
            MerchantRecipe recipe = new MerchantRecipe(
                    new ItemStack(
                            Material.matchMaterial(trade.getMinecraftId()),
                            trade.getAmount()),
                    trade.getUsesMax()
            );

            recipe.addIngredient(new ItemStack(
                    Material.matchMaterial(trade.getPriceItem1()),
                    trade.getPrice1()));

            if ( trade.getPriceItem2() != null && trade.getPrice2() != null )
            {
                recipe.addIngredient(new ItemStack(
                        Material.matchMaterial(trade.getPriceItem2()),
                        trade.getPrice2()));
            }
            trades.add( recipe );
        }
    }

    private static void addPlayerHeadOffers(List<MerchantRecipe> trades)
    {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        meta.setOwningPlayer(Bukkit.getPlayerExact("ScorchedPsyche"));
        playerHead.setItemMeta( meta );

        MerchantRecipe recipe = new MerchantRecipe(
                playerHead,
                1
        );

        recipe.addIngredient(new ItemStack(
                Material.DIAMOND,
                1));

        trades.add( recipe );
    }
}

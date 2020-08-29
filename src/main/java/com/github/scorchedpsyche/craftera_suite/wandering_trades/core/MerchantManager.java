package com.github.scorchedpsyche.craftera_suite.wandering_trades.core;

import com.github.scorchedpsyche.craftera_suite.wandering_trades.Main;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.models.TradeEntryModel;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MerchantManager
{
    public static void removeDefaultTrades(  WanderingTrader merchant )
    {
        merchant.setRecipes( new ArrayList<>() );
    }

    public static void addTrades( WanderingTrader merchant )
    {
        List<MerchantRecipe> trades = new ArrayList<>();

        // Loops through trade files
        for( TradeEntryModel trade : Main.tradeList.Trades.offers )
        {
            if( !trade.getMinecraftId().equalsIgnoreCase("player_head") )
            {
                Material material = Material.matchMaterial( trade.getMinecraftId() );

                // Check if material exists
                if( material != null )
                {
                    // OTHER ITEMS
                    MerchantRecipe recipe = new MerchantRecipe(
                            new ItemStack(
                                    material,
                                    trade.getAmount()),
                            trade.getUsesMax()
                    );

                    material = Material.matchMaterial(trade.getPriceItem1());
                    if( material != null )
                    {
                        recipe.addIngredient(new ItemStack(
                                material,
                                trade.getPrice1()));

                        if ( trade.getPriceItem2() != null && trade.getPrice2() != null )
                        {
                            material = Material.matchMaterial(trade.getPriceItem2());
                            if( material != null )
                            {
                                recipe.addIngredient(new ItemStack(
                                        material,
                                        trade.getPrice2()));
                            } else {
                                Bukkit.getConsoleSender().sendMessage(
                                        "[CraftEra Suite - Wandering Trades] " +
                                        "ERROR on 'price_item2' of " + trade.getMinecraftId() +
                                        ". Item was added only with Item 1." );
                            }
                        }
                        trades.add( recipe );
                    } else {
                        Bukkit.getConsoleSender().sendMessage("[CraftEra Suite - Wandering Trades] " +
                                                                "ERROR on 'price_item1' of " + trade.getMinecraftId() +
                                                                ". Item was not added." );
                    }
                }
            } else {
                if( trade.getOwnerId() != null && trade.getTexture() != null )
                {
                    // PLAYER HEAD

                    ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                    assert meta != null;
                    meta.setOwningPlayer(Bukkit.getPlayerExact(trade.getOwnerId()));
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
                    // DECORATION HEAD

                    ItemStack decorationHead = new ItemStack(Material.PLAYER_HEAD, 1);

                    SkullMeta decorationHeadMeta = (SkullMeta) decorationHead.getItemMeta();
                    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                    profile.getProperties().put(
                            "textures",
                            new Property("textures", trade.getTexture()));
                    Field profileField;

                    try {
                        assert decorationHeadMeta != null;
                        profileField = decorationHeadMeta.getClass().getDeclaredField("profile");
                        profileField.setAccessible(true);
                        profileField.set(decorationHeadMeta, profile);
                    } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
                        e1.printStackTrace();
                    }
                    decorationHead.setItemMeta(decorationHeadMeta);

                    MerchantRecipe recipe = new MerchantRecipe(
                            decorationHead,
                            trade.getUsesMax()
                    );

                    Material material = Material.matchMaterial( trade.getPriceItem1() );
                    if( material != null )
                    {
                        recipe.addIngredient(new ItemStack(
                                material,
                                trade.getPrice1()));

                        if( trade.getPriceItem2() != null && trade.getPrice2() != null )
                        {
                            material = Material.matchMaterial( trade.getPriceItem2() );

                            if( material != null )
                            {
                                recipe.addIngredient(new ItemStack(
                                        material,
                                        trade.getPrice2()));
                            } else {
                                Bukkit.getConsoleSender().sendMessage(
                                        "[CraftEra Suite - Wandering Trades] " +
                                        "ERROR on 'price_item2' of " + trade.getMinecraftId() +
                                        ". Item was added only with Item 1." );
                            }
                        }

                        trades.add( recipe );
                    } else {
                        Bukkit.getConsoleSender().sendMessage(
                            "[CraftEra Suite - Wandering Trades] " +
                            "ERROR on 'price_item1' of " + trade.getMinecraftId() +
                            ". Item was not added." );
                    }
                }
            }
        }



        // WHITELIST Player Head Synchronization
        if( Main.config.getBoolean("whitelist.enable_synchronization") ) // TO DO
        {
//            Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
            Collections.shuffle(Main.whitelistedPlayerHeads);

            for (int i = 0; i < Main.config.getInt("whitelist.number_of_player_head_offers"); i++)
            {
                // Item Reward
                MerchantRecipe recipe = new MerchantRecipe(
                        Main.whitelistedPlayerHeads.get(i),
                        0,
                        Main.config.getInt("whitelist.maximum_number_of_trades"),
                        Main.config.getBoolean("whitelist.experience_rewarded_for_each_trade")
                );

                // Item 1
                if (    Main.config.contains("whitelist.price.item1") &&
                        Main.config.contains("whitelist.price.item1.minecraft_id") )
                {
                    Material material = Material.matchMaterial(
                            Main.config.getString("whitelist.price.item1.minecraft_id" ) );
                    if( material != null )
                    {
                        recipe.addIngredient(new ItemStack(
                                material,
                                Main.config.getInt("whitelist.price.item1.quantity")));

                        // Item 2
                        if (    Main.config.contains("whitelist.price.item2") &&
                                Main.config.contains("whitelist.price.item2.minecraft_id") )
                        {
                            material = Material.matchMaterial(Main.config.getString("whitelist.price.item2" +
                                                                                            ".minecraft_id"));
                            if( material != null  )
                            {
                                recipe.addIngredient(new ItemStack(
                                        material,
                                        Main.config.getInt("whitelist.price.item2.quantity")));
                            }
                        } else {
                            Bukkit.getConsoleSender().sendMessage(
                                    "[CraftEra Suite - Wandering Trades] " +
                                            "ERROR on config file: whitelist.price.item2.minecraft_id. Item ID is " +
                                            "wrong." );
                        }

                        trades.add(recipe);
                    } else {
                        Bukkit.getConsoleSender().sendMessage(
                                "[CraftEra Suite - Wandering Trades] " +
                                        "ERROR on config file: whitelist.price.item1.minecraft_id. Item ID is wrong." );
                    }
                }
            }

            merchant.setRecipes(trades);
        }
    }
}

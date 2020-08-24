package com.github.scorchedpsyche.craftera_suite.wandering_trades.listeners;

import com.github.scorchedpsyche.craftera_suite.wandering_trades.Main;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WanderingTraderSpawnListener implements Listener
{
    @EventHandler
    public void onWanderingTraderSpawned(EntitySpawnEvent event)
    {
        System.out.print("SPAWNER");
        if ( event.getEntity().getType() == EntityType.WANDERING_TRADER )
        {
            WanderingTrader trader = (WanderingTrader) event.getEntity();

            MerchantRecipe recipe = new MerchantRecipe(
                    new ItemStack(Material.ELYTRA, 1),
                    1
            );
            recipe.addIngredient(new ItemStack(Material.DIAMOND, 32));
            recipe.addIngredient(new ItemStack(Material.AIR, 1));

            List<MerchantRecipe> trades = new ArrayList<MerchantRecipe>();

            trades.add( recipe );

            for( MerchantRecipe originalRecipe : ((WanderingTrader) trader).getRecipes() )
            {
                trades.add(originalRecipe);
            }
            trader.setRecipes( trades );
        }
    }
}

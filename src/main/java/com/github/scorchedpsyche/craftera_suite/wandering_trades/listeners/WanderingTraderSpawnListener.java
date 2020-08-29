package com.github.scorchedpsyche.craftera_suite.wandering_trades.listeners;

import com.github.scorchedpsyche.craftera_suite.wandering_trades.Main;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.core.MerchantManager;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.models.TradeEntryModel;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.models.TradeModel;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WanderingTraderSpawnListener implements Listener
{
    @EventHandler
    public void onWanderingTraderSpawned(EntitySpawnEvent event)
    {

//        System.out.print(Main.tradeList.);

        if ( event.getEntity().getType() == EntityType.WANDERING_TRADER )
        {
            WanderingTrader trader = (WanderingTrader) event.getEntity();
MerchantManager.removeDefaultTrades( trader );

Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
    MerchantManager.addTrades( trader );
});








//            MerchantManager.addTrades( trader );














//            WanderingTrader trader = (WanderingTrader) event.getEntity();
//
//            List<MerchantRecipe> trades = new ArrayList<>();
//
//            for( TradeEntryModel trade : Main.tradeList.Trades.offers )
//            {
//                MerchantRecipe recipe = new MerchantRecipe(
//                        new ItemStack(
//                                Material.matchMaterial(trade.getMinecraftId()),
//                                trade.getAmount()),
//                        trade.getUsesMax()
//                );
//
//                recipe.addIngredient(new ItemStack(
//                        Material.matchMaterial(trade.getPriceItem1()),
//                        trade.getPrice1()));
//
//                if ( trade.getPriceItem2() != null && trade.getPrice2() != null )
//                {
//                    recipe.addIngredient(new ItemStack(
//                            Material.matchMaterial(trade.getPriceItem2()),
//                            trade.getPrice2()));
//                }
//                trades.add( recipe );
//            }






//            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
//            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
//            meta.setOwningPlayer( Bukkit.getPlayerExact("ScorchedPsyche") );
//            playerHead.setItemMeta( meta );




//            MerchantRecipe recipe = new MerchantRecipe(
//                    playerHead,
//                    1
//            );
//
//            recipe.addIngredient(new ItemStack(
//                    Material.DIAMOND,
//                    1));

//            trades.add( recipe );

//            if( playerHead instanceof SkullMeta)
//            {
//                Bukkit.getConsoleSender().sendMessage("SKULLMETA");
//            } else if (playerHead instanceof Skull){
//                Bukkit.getConsoleSender().sendMessage("SKULL");
//            } else {
//                Bukkit.getConsoleSender().sendMessage("FAIL");
//                Bukkit.getConsoleSender().sendMessage(String.valueOf(playerHead.getType()));
//                Bukkit.getConsoleSender().sendMessage(String.valueOf(((SkullMeta) playerHead.getItemMeta()).getOwningPlayer()));
//            }








//            trader.setRecipes( trades );

//
//            for( MerchantRecipe originalRecipe : ((WanderingTrader) trader).getRecipes() )
//            {
//                trades.add(originalRecipe);
//            }
        }
    }
}

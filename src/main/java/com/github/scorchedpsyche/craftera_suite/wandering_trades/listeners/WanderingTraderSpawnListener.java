package com.github.scorchedpsyche.craftera_suite.wandering_trades.listeners;

import com.github.scorchedpsyche.craftera_suite.wandering_trades.Main;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.core.MerchantManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class WanderingTraderSpawnListener implements Listener
{
    @EventHandler
    public void onWanderingTraderSpawned(EntitySpawnEvent event)
    {
        if ( event.getEntity().getType() == EntityType.WANDERING_TRADER )
        {
            WanderingTrader trader = (WanderingTrader) event.getEntity();

            // Should remove default trades?
            if( Main.config.contains( "remove_default_trades" ) && Main.config.getBoolean( "remove_default_trades" ) )
            {
                MerchantManager.removeDefaultTrades( trader );
            }

            Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
                MerchantManager.addTrades( trader );
            });
        }
    }
}

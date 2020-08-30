package com.github.scorchedpsyche.craftera_suite.wandering_trades.listeners;

import com.github.scorchedpsyche.craftera_suite.wandering_trades.CraftEraSuiteWanderingTrades;
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
            CraftEraSuiteWanderingTrades.merchantManager.setMerchantTrades((WanderingTrader) event.getEntity());
        }
    }
}

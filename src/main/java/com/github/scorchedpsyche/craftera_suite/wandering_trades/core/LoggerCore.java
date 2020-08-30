package com.github.scorchedpsyche.craftera_suite.wandering_trades.core;

import org.bukkit.Bukkit;

public class LoggerCore
{
    private static final String pluginPrefix = "[CraftEra Suite - Wandering Trades] ";

    public static void Log(String message)
    {
        Bukkit.getConsoleSender().sendMessage(pluginPrefix + message);
    }
}

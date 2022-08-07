package com.cptingle.boardgames;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class BoardGames extends JavaPlugin {
    private Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.info("BoardGames plugin enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("BoardGames plugin disabled!");
    }
}

package com.req.attemptsstatictic;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class AttemptsStatistic extends JavaPlugin implements Listener {

    private int attempts;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration cfg = getConfig();
        this.attempts = cfg.getInt("attempts", 0);

        Bukkit.getPluginManager().registerEvents(this, this);

        Objects.requireNonNull(getCommand("setattempts")).setExecutor(this);
        Objects.requireNonNull(getCommand("attempts")).setExecutor(this);
    }

    private void saveAttempts() {
        getConfig().set("attempts", attempts);
        saveConfig();
    }

    @Override
    public void onDisable() {
        saveAttempts();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        attempts++;
        saveAttempts();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player respawned = event.getPlayer();
        World world = respawned.getWorld();

        // читаем сложность из конфига
        String diffName = getConfig().getString("difficulty-on-respawn", "HARD");
        Difficulty difficulty;
        try {
            difficulty = Difficulty.valueOf(diffName.toUpperCase());
        } catch (IllegalArgumentException e) {
            // если в конфиге фигня — по умолчанию HARD
            difficulty = Difficulty.HARD;
        }

        world.setDifficulty(difficulty);
        String main = getConfig().getString("title-main", "Попытка #%attempts%");
        String sub = getConfig().getString("title-sub", "Удачи!");

        main = main.replace("%attempts%", String.valueOf(attempts));
        sub = sub.replace("%attempts%", String.valueOf(attempts));

        final String titleMain = main;
        final String titleSub = sub;

        // delay for loading new world
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(titleMain, titleSub, 10, 60, 10);
            }
        }, 40L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (!getConfig().getBoolean("show-title-on-join", true)) return;

        String main = getConfig().getString("join-title-main", "Сейчас попытка #%attempts%");
        String sub = getConfig().getString("join-title-sub", "");

        main = main.replace("%attempts%", String.valueOf(attempts));
        sub = sub.replace("%attempts%", String.valueOf(attempts));

        p.sendTitle(main, sub, 10, 60, 10);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();

        if (cmd.equals("setattempts")) {
            if (!sender.hasPermission("attemptsstatictic.admin")) {
                sender.sendMessage("§cOnly admins can use this command.");
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("reset")) {
                attempts = 0;
                saveAttempts();
                sender.sendMessage("§aГлобальный счётчик попыток сброшен до 0.");
                return true;
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                try {
                    int value = Integer.parseInt(args[1]);
                    attempts = value;
                    saveAttempts();
                    sender.sendMessage("§aГлобальный счётчик попыток установлен на " + value + ".");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cNeed a num: /setattempts set <number>");
                }
                return true;
            }

            sender.sendMessage("§eUsage: /setattempts <reset|set <number>>");
            return true;
        }

        if (cmd.equals("attempts")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can use this command.");
                return true;
            }

            sender.sendMessage("§6=== ПОПЫТКА ===");
            sender.sendMessage("§b" + attempts);

            return true;
        }

        return false;
    }

}


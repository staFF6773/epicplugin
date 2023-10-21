package staff.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import staff.main.Epicplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PvPCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private final Map<String, Long> cooldowns = new HashMap<>();
    private final Map<String, Boolean> pvpStates = new HashMap<>();
    private final Map<String, BossBar> bossBars = new HashMap<>();
    private int cooldownDuration;
    private int cooldownCheckerInterval;
    private boolean enableBossBar;

    private String noPermissionMessage;
    private String incorrectUsageMessage;
    private String activationMessage;
    private String deactivationMessage;
    private String cooldownErrorMessage;
    private String disabledMessage;
    private String cooldownExpiredMessage;
    private String bossBarMessage;

    public PvPCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin.getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(), plugin);

        this.plugin.saveDefaultConfig();
        loadConfig();
        loadMessages();

        startCooldownChecker();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        cooldownDuration = config.getInt("pvp.cooldown_duration", 600);
        cooldownCheckerInterval = config.getInt("pvp.cooldown_checker_interval", 600);
        enableBossBar = config.getBoolean("pvp.enable_bossbar", true);
    }

    private void loadMessages() {
        FileConfiguration config = plugin.getConfig();
        noPermissionMessage = ChatColor.translateAlternateColorCodes('&', config.getString("message.noPermissionPvP"));
        incorrectUsageMessage = ChatColor.translateAlternateColorCodes('&', config.getString("message.incorrectUsagePvP"));
        activationMessage = ChatColor.translateAlternateColorCodes('&', config.getString("message.activationPvP"));
        deactivationMessage = ChatColor.translateAlternateColorCodes('&', config.getString("message.deactivationPvP"));
        cooldownErrorMessage = ChatColor.translateAlternateColorCodes('&', config.getString("message.cooldownErrorPvP"));
        disabledMessage = ChatColor.translateAlternateColorCodes('&', config.getString("message.disabledMessagePvP"));
        cooldownExpiredMessage = ChatColor.translateAlternateColorCodes('&', config.getString("message.cooldownExpiredPvP"));
        bossBarMessage = ChatColor.translateAlternateColorCodes('&', config.getString("message.bossBarMessage", "&cPvP disabled: &e%remaining_time%s"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " &cUPS sorry but this command is only for players."));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("epicplugin.nopvp")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " " + noPermissionMessage));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " " + incorrectUsageMessage));
            return true;
        }

        String arg = args[0].toLowerCase();

        // Verifica si el argumento es "on" o "off"
        if (arg.equals("on") || arg.equals("off")) {
            // Verifica si hay un cooldown para el jugador solo cuando se desactiva el PvP
            if (arg.equals("off") && cooldowns.containsKey(player.getName())) {
                long cooldownEnd = cooldowns.get(player.getName());

                // Verifica si el cooldown ha terminado
                if (System.currentTimeMillis() < cooldownEnd) {
                    long remainingTime = (cooldownEnd - System.currentTimeMillis()) / 1000L;
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " " + cooldownErrorMessage.replace("%remaining_time%", String.valueOf(remainingTime))));
                    return true;
                }
            }

            if (arg.equals("on")) {
                pvpStates.put(player.getName(), true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " " + activationMessage));

                // Elimina la BossBar asociada al jugador si existe y la BossBar está habilitada
                if (enableBossBar) {
                    removeBossBar(player);
                }
            } else if (arg.equals("off")) {
                // Se aplica el cooldown solo cuando se desactiva el PvP
                setCooldown(player);
                pvpStates.put(player.getName(), false);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " " + deactivationMessage.replace("%cooldown%", String.valueOf(cooldownDuration))));

                // Muestra la BossBar con el tiempo restante del PvP desactivado
                if (enableBossBar) {
                    showCooldownBossBar(player);
                }
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " " + incorrectUsageMessage));
        }

        return true;
    }


    // Método para establecer el cooldown para un jugador
    private void setCooldown(Player player) {
        cooldowns.put(player.getName(), System.currentTimeMillis() + (cooldownDuration * 1000));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            Boolean damagedPvP = pvpStates.get(damaged.getName());
            if (damagedPvP == null) {
                damagedPvP = true;
            }

            Boolean damagerPvP = pvpStates.get(damager.getName());
            if (damagerPvP == null) {
                damagerPvP = true;
            }

            if (!damagedPvP || !damagerPvP) {
                event.setCancelled(true);
                damager.sendMessage(disabledMessage.replace("%player%", damaged.getName()));

                // Muestra la BossBar con el tiempo restante del PvP desactivado
                if (enableBossBar) {
                    showCooldownBossBar(damager);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Boolean pvpState = pvpStates.get(player.getName());

        // Cancela el evento solo si el jugador tiene el PvP desactivado
        if (pvpState != null && !pvpState) {
            // Muestra la BossBar con el tiempo restante del PvP desactivado
            if (enableBossBar) {
                showCooldownBossBar(player);
            }

            // Cancela el evento de recogida de items, pero permite que el jugador lo recoja si el PvP está activado
            event.setCancelled(true);
        }
    }

    // Método para mostrar la BossBar con el tiempo restante del PvP desactivado
    private void showCooldownBossBar(Player player) {
        if (!enableBossBar) {
            return;  // No mostrar BossBar si está deshabilitada
        }

        long cooldownEndTime = cooldowns.getOrDefault(player.getName(), 0L);
        long currentTime = System.currentTimeMillis();

        if (cooldownEndTime > currentTime) {
            // Obtiene la BossBar existente o crea una nueva
            BossBar bossBar = bossBars.computeIfAbsent(player.getName(),
                    key -> Bukkit.createBossBar(parseBossBarMessage(player, cooldownDuration), BarColor.RED, BarStyle.SOLID));

            // Añade la BossBar al jugador si no está presente
            if (!bossBar.getPlayers().contains(player)) {
                bossBar.addPlayer(player);
            }

            // Programa una tarea para actualizar la BossBar cada segundo
            new BukkitRunnable() {
                @Override
                public void run() {
                    long remainingTime = (cooldownEndTime - System.currentTimeMillis()) / 1000L;

                    // Actualiza la BossBar
                    bossBar.setTitle(parseBossBarMessage(player, remainingTime));
                    bossBar.setProgress((double) remainingTime / cooldownDuration);

                    // Cancela la tarea si el tiempo restante es menor o igual a cero
                    if (remainingTime <= 0) {
                        bossBar.removeAll();
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L); // 20 ticks = 1 segundo
        }
    }

    private void removeBossBar(Player player) {
        if (!enableBossBar) {
            return;  // No eliminar BossBar si está deshabilitada
        }

        BossBar bossBar = bossBars.remove(player.getName());
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    private class PlayerJoinQuitListener implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            bossBars.remove(player.getName());
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            Player player = event.getPlayer();
            bossBars.remove(player.getName());
        }
    }

    private String parseBossBarMessage(Player player, long remainingTime) {
        return ChatColor.translateAlternateColorCodes('&', bossBarMessage
                .replace("%player%", player.getName())
                .replace("%remaining_time%", String.valueOf(remainingTime)));
    }

    private void forcePvPOnIf60SecondsRemaining(Player player) {
        if (cooldowns.containsKey(player.getName())) {
            long cooldownEnd = cooldowns.get(player.getName());
            long currentTime = System.currentTimeMillis();

            if (cooldownEnd > currentTime && (cooldownEnd - currentTime) <= 60000) {
                // Menos de 60 segundos de cooldown restantes, forzar nopvp on
                pvpStates.put(player.getName(), true);
                cooldowns.remove(player.getName());
                removeBossBar(player);

                // Envía un mensaje al jugador si lo deseas
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " " + activationMessage));
            }
        }
    }


    private void checkCooldowns() {
        long currentTime = System.currentTimeMillis();

        for (String playerName : new ArrayList<>(cooldowns.keySet())) {
            long cooldownEndTime = cooldowns.get(playerName);

            if (currentTime > cooldownEndTime) {
                pvpStates.put(playerName, true);
                cooldowns.remove(playerName);
                removeBossBar(Bukkit.getPlayerExact(playerName));

                Player player = plugin.getServer().getPlayerExact(playerName);
                if (player != null) {
                    player.sendMessage(cooldownExpiredMessage.replace("%player%", player.getName()));
                }
            } else {
                Player player = plugin.getServer().getPlayerExact(playerName);
                if (player != null) {
                    forcePvPOnIf60SecondsRemaining(player);
                }
            }
        }
    }

    private void startCooldownChecker() {
        long ticksInterval = cooldownCheckerInterval * 20L;
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkCooldowns, 20L, ticksInterval);
    }
}

package staff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import staff.main.Epicplugin;

import java.util.HashMap;
import java.util.Map;

public class PvPCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private final Map<String, Long> cooldowns = new HashMap<>();
    private final Map<String, Boolean> pvpStates = new HashMap<>();
    private int cooldownDuration;
    private int cooldownCheckerInterval;

    private String noPermissionMessage;
    private String incorrectUsageMessage;
    private String activationMessage;
    private String deactivationMessage;
    private String cooldownErrorMessage;
    private String disabledMessage;
    private String cooldownExpiredMessage;

    public PvPCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin.saveDefaultConfig();
        loadConfig();
        loadMessages();

        startCooldownChecker();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        cooldownDuration = config.getInt("pvp.cooldown_duration", 600);
        cooldownCheckerInterval = config.getInt("pvp.cooldown_checker_interval", 600);
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
        if (arg.equals("off")) {
            pvpStates.put(player.getName(), true);
            cooldowns.put(player.getName(), System.currentTimeMillis());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " " + activationMessage));
        } else if (arg.equals("on")) {
            if (!cooldowns.containsKey(player.getName()) || System.currentTimeMillis() - cooldowns.get(player.getName()) > cooldownDuration * 1000) {
                pvpStates.put(player.getName(), false);
                cooldowns.put(player.getName(), System.currentTimeMillis() + (cooldownDuration * 1000));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " " + deactivationMessage.replace("%cooldown%", String.valueOf(cooldownDuration))));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " " + cooldownErrorMessage));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Epicplugin.prefix + " " + incorrectUsageMessage));
        }

        return true;
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
            }
        }
    }

    private void checkCooldowns() {
        long currentTime = System.currentTimeMillis();

        for (String playerName : cooldowns.keySet()) {
            long cooldownEndTime = cooldowns.get(playerName);

            if (currentTime > cooldownEndTime) {
                pvpStates.put(playerName, true);
                cooldowns.remove(playerName);
                Player player = plugin.getServer().getPlayerExact(playerName);
                if (player != null) {
                    player.sendMessage(cooldownExpiredMessage.replace("%player%", player.getName()));
                }
            }
        }
    }

    private void startCooldownChecker() {
        long ticksInterval = cooldownCheckerInterval * 20L;
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkCooldowns, 20L, ticksInterval);
    }
}

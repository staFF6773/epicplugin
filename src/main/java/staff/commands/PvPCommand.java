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
    private final Map<String, Long> cooldowns = new HashMap<>(); // Almacena el tiempo de cooldown por jugador en milisegundos
    private final Map<String, Boolean> pvpStates = new HashMap<>(); // Almacena el estado PvP por jugador
    private int cooldownDuration; // Duración del cooldown en segundos
    private int cooldownCheckerInterval; // Frecuencia de verificación del cooldown en segundos

    public PvPCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);

        // Cargar configuración desde el archivo config.yml
        this.plugin.saveDefaultConfig();
        loadConfig();

        // Iniciar el verificador de cooldowns
        startCooldownChecker();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        cooldownDuration = config.getInt("pvp.cooldown_duration", 600);
        cooldownCheckerInterval = config.getInt("pvp.cooldown_checker_interval", 600);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', Epicplugin.prefix+" &cUPS sorry but this command is only for players."));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("epicplugin.pvp")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&',Epicplugin.prefix+" &c&lUPS &cSorry but you do not have permissions to execute this command"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', Epicplugin.prefix+" &c&lUPS &cIncorrect use of the command /pvp <on | off>"));
            return true;
        }

        String arg = args[0].toLowerCase();
        if (arg.equals("on")) {
            pvpStates.put(player.getName(), true);
            cooldowns.put(player.getName(), System.currentTimeMillis());
            player.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', Epicplugin.prefix+" &aThe PvP has been activated for you!"));
        } else if (arg.equals("off")) {
            if (!cooldowns.containsKey(player.getName()) || System.currentTimeMillis() - cooldowns.get(player.getName()) > cooldownDuration * 1000) {
                pvpStates.put(player.getName(), false);
                cooldowns.put(player.getName(), System.currentTimeMillis() + (cooldownDuration * 1000));
                player.sendMessage(ChatColor.translateAlternateColorCodes(
                        '&', Epicplugin.prefix+" &aPvP will be automatically deactivated after &a&l" + cooldownDuration + "&a seconds!"));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes(
                        '&', Epicplugin.prefix+" &c&lUPS &cYou are still in cooldown. You must wait before disabling PvP again."));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', Epicplugin.prefix+" &c&lUPS &cIncorrect use of the command /pvp <on | off>"));
        }

        return true;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            // Verificar el estado PvP del jugador dañado
            Boolean damagedPvP = pvpStates.get(damaged.getName());
            if (damagedPvP == null) {
                damagedPvP = true; // Si no hay estado registrado, asumimos que está habilitado
            }

            // Verificar el estado PvP del jugador que inflige el daño
            Boolean damagerPvP = pvpStates.get(damager.getName());
            if (damagerPvP == null) {
                damagerPvP = true; // Si no hay estado registrado, asumimos que está habilitado
            }

            // Cancelar el evento de daño si el PvP está desactivado para el jugador dañado o el que daña
            if (!damagedPvP || !damagerPvP) {
                event.setCancelled(true);
                damager.sendMessage(ChatColor.translateAlternateColorCodes(
                        '&', Epicplugin.prefix+" &cPvP is disabled for &c&l" + damaged.getName() + "&c!"));
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
                    player.sendMessage(ChatColor.translateAlternateColorCodes(
                            '&', Epicplugin.prefix+" &f&l" + player.getName() + "&chas been automatically activated because your cooldown has already expired."));
                }
            }
        }
    }

    // Called every second to check for expired cooldowns
    private void startCooldownChecker() {
        long ticksInterval = cooldownCheckerInterval * 20L;
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkCooldowns, 20L, ticksInterval);
    }
}

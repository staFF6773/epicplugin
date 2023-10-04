package staff.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import staff.main.Epicplugin;

import java.util.HashMap;
import java.util.Map;

public class heal implements CommandExecutor {
    private final Map<Player, Long> cooldowns = new HashMap<>();
    private int cooldownTimeInSeconds; // Tiempo de reutilización en segundos

    public heal(Epicplugin plugin) {
        // Carga la configuración desde el archivo config.yml al inicializar el comando
        FileConfiguration config = plugin.getConfig();
        cooldownTimeInSeconds = config.getInt("heal-cooldown", 600); // Valor predeterminado: 10 minutos (600 segundos)
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', Epicplugin.prefix+" &cThis command is only for players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("epicplugin.heal")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', Epicplugin.prefix+" &c&lUPS &cSorry but you do not have permissions to execute this command"));
            return true;
        }

        if (hasCooldown(player)) {
            long remainingTime = getCooldownTime(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', Epicplugin.prefix+" &cYou must wait " + remainingTime + " seconds before using this command again."));
            return true;
        }

        // Elimina el cooldown anterior y establece uno nuevo
        setCooldown(player);

        player.setHealth(20);
        player.setFoodLevel(20);
        player.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', Epicplugin.prefix+" &aYou have been successfully cured"));

        return true;
    }

    private boolean hasCooldown(Player player) {
        if (cooldowns.containsKey(player)) {
            long currentTime = System.currentTimeMillis() / 1000; // Convertir a segundos
            long lastTimeUsed = cooldowns.get(player);

            return (currentTime - lastTimeUsed) < cooldownTimeInSeconds;
        }

        return false;
    }

    private long getCooldownTime(Player player) {
        if (cooldowns.containsKey(player)) {
            long currentTime = System.currentTimeMillis() / 1000; // Convertir a segundos
            long lastTimeUsed = cooldowns.get(player);
            long remainingTime = cooldownTimeInSeconds - (currentTime - lastTimeUsed);

            return Math.max(0, remainingTime); // Evita valores negativos
        }

        return 0;
    }

    private void setCooldown(Player player) {
        long currentTime = System.currentTimeMillis() / 1000; // Convertir a segundos
        cooldowns.put(player, currentTime);
    }
}

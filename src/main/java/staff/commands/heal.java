package staff.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class heal implements CommandExecutor {
    private final Map<Player, Long> cooldowns = new HashMap<>();
    private final int cooldownTimeInSeconds = 600; // 10 minutos (600 segundos)

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§f[§bEpicPlugin§f] §cThis command is only for players");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("epicplugin.heal")) {
            player.sendMessage("§f[§bEpicPlugin§f] §c§lUPS §cIt seems that you do not have permissions to execute this command");
            return true;
        }

        if (hasCooldown(player)) {
            long remainingTime = getCooldownTime(player);
            player.sendMessage("§f[§bEpicPlugin§f] §cYou must wait " + remainingTime + " seconds before using this command again.");
            return true;
        }

        // Elimina el cooldown anterior y establece uno nuevo
        setCooldown(player);

        player.setHealth(20);
        player.setFoodLevel(20);
        player.sendMessage("§f[§bEpicPlugin§f] §aYou have been successfully cured");

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

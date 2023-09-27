package staff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import staff.main.Epicplugin;
import staff.utils.MessageUtils;

import java.util.HashMap;

public class repair implements CommandExecutor {
    private final Epicplugin plugin;
    private final HashMap<String, Long> cooldowns = new HashMap<>(); // Almacena el tiempo del último uso por jugador
    private final long cooldownDuration = 60 * 1000; // Duración del cooldown en milisegundos (en este caso, 60 segundos)

    public repair(Epicplugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&',Epicplugin.prefix+" &c&lUPS &cI'm sorry but this command is only for players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("tuPlugin.repair")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&',Epicplugin.prefix+" &c&lUPS &cSorry but you do not have permissions to execute this command"));
            return true;
        }

        if (hasCooldown(player)) {
            long timeRemaining = (cooldowns.get(player.getName()) + cooldownDuration - System.currentTimeMillis()) / 1000;
            player.sendMessage("§f[§bEpicPlugin§f] §cYou must wait " + timeRemaining + " seconds before using this command again.");
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType().isAir()) {
            player.sendMessage("§f[§bEpicPlugin§f] §c§lUPS §cYou are not holding any object in your hand.");
            return true;
        }

        // Verificar si el objeto en la mano está dañado y puede ser reparado
        if (itemInHand.getDurability() > 0) {
            // Reparar el objeto estableciendo su durabilidad a cero
            itemInHand.setDurability((short) 0);
            player.sendMessage("§f[§bEpicPlugin§f] §aThe item you were holding has been successfully repaired..");

            // Establecer el tiempo del último uso para el jugador
            setCooldown(player);
        } else {
            player.sendMessage("§f[§bEpicPlugin§f] §c§lUPS §cI'm sorry but the item you have in your hand does not need repair.");
        }

        return true;
    }

    // Verifica si un jugador está en cooldown
    private boolean hasCooldown(Player player) {
        if (cooldowns.containsKey(player.getName())) {
            long currentTime = System.currentTimeMillis();
            long lastUseTime = cooldowns.get(player.getName());
            return currentTime - lastUseTime < cooldownDuration;
        }
        return false;
    }

    // Establece el tiempo del último uso para un jugador
    private void setCooldown(Player player) {
        cooldowns.put(player.getName(), System.currentTimeMillis());
    }
}
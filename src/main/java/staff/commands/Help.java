package staff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Help implements CommandExecutor {

    private final Map<String, String> commandDescriptions;
    private final JavaPlugin plugin;

    public Help(JavaPlugin plugin) {
        this.plugin = plugin;
        // Inicializa las descripciones de los comandos. Puedes personalizar esta lista.
        commandDescriptions = new HashMap<>();
        commandDescriptions.put("§bnopvp", "§7Manage player pvp.");
        commandDescriptions.put("§bepicplugin:heal", "§7Regenerated your hearts and food");
        commandDescriptions.put("§bepicplugin:repair", "§7Repair the item in your hand.");
        commandDescriptions.put("§bepicplugin:reload", "§7Reload the plugin configuration");
        // Agrega más comandos y descripciones según sea necesario.j
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            // Mensaje personalizado para jugadores con permisos de operador (OP).
        } else if (!sender.hasPermission("epicplugin.help")) {
            // Mensaje personalizado para jugadores sin permiso.
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', "&f[&bEpicPlugin&f] &cSorry but you do not have permissions to execute this command"));
            return true;
        }

        sender.sendMessage("§7Commands:");

        // Itera a través de la lista de comandos y muestra sus descripciones.
        for (Map.Entry<String, String> entry : commandDescriptions.entrySet()) {
            sender.sendMessage("§b/" + entry.getKey() + " §f- " + entry.getValue());
        }

        return true;
    }
}

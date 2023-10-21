package staff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import staff.main.Epicplugin;
import staff.utils.ChatUtils;

public class reload implements CommandExecutor {
    private final Epicplugin plugin;

    public reload(Epicplugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the player has the required permission to execute the reload command.
        if (!sender.hasPermission("epicplugin.reload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', "&f[&bEpicPlugin&f] &cYou do not have permission to execute this command."));
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', "&f[&bEpicPlugin&f] &aThe configuration file has been reloaded."));
        sender.sendMessage(ChatUtils.getColoredMessage("&7(Some options only apply after the server has been restarted)."));
        return true;
    }
}

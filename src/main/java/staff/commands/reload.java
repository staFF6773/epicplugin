package staff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import staff.main.Epicplugin;
import staff.utils.ChatUtils;


public class reload implements CommandExecutor {
    Epicplugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin = Epicplugin.getPlugin();
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', "&f[&bEpicPlugin&f] &aThe configuration file was reloaded."));
        sender.sendMessage(ChatUtils.getColoredMessage("&7(Some options only apply after the server has been restarted.)"));
        return true;
    }
}

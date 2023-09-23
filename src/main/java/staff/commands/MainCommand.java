package staff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import staff.main.Epicplugin;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)){
            //consola
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', Epicplugin.prefix+" &cUPS sorry but this command can only be used by one player."));
            return true;
        }

        //jugador
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', Epicplugin.prefix+"You have just used the command &7/epicplugin."));

        return true;
    }
}

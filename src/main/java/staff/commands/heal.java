package staff.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class heal implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if(sender instanceof Player && p.hasPermission("epicplugin.heal")){
            p.setHealth(20);
            p.setFoodLevel(20);
            p.sendMessage("§f[§bEpicPlugin§f] §aYou have been successfully cured");
        } else if (sender instanceof Player && !(p.hasPermission("epicplugin.heal"))){
            p.sendMessage("§f[§bEpicPlugin§f] §c§lUPS §cIt seems that you do not have permissions to execute this command");
            
        } else {
            Bukkit.getConsoleSender().sendMessage("§f[§bEpicPlugin§f] §cThis command is only for players");
        }
        return true;
    }
}

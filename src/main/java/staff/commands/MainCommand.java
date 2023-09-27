package staff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import staff.main.Epicplugin;
import staff.utils.MessageUtils;

public class MainCommand implements CommandExecutor {

    private Epicplugin plugin;

    public MainCommand(Epicplugin plugin){
        this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)){
            //consola
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', Epicplugin.prefix+" &cUPS sorry but this command can only be used by one player."));
            return true;
        }

        //jugador
        Player player = (Player) sender;


        //Argumentos
        if(args.length >= 1){
            if(args[0].equalsIgnoreCase("get")){
                //epicplugin get
                subCommandGet(sender, args);
            }else{
                help(sender);
            }
        }else{
            //epicplugin
            help(sender);
        }


        return true;
    }

    //help command
    public void help(CommandSender sender){
        sender.sendMessage(MessageUtils.getColoredMessage("&f&l-----------COMMANDS &b&lEPICPLUGIN&f&l-----------"));
        sender.sendMessage(MessageUtils.getColoredMessage("&7- /epicplugin:heal"));
        sender.sendMessage(MessageUtils.getColoredMessage("&7- /epicplugin:repair"));
        sender.sendMessage(MessageUtils.getColoredMessage("&7- /epicplugin get <author/version>"));
        sender.sendMessage(MessageUtils.getColoredMessage("&f&l-----------------------------------------"));
    }

    public void subCommandGet(CommandSender sender,String[] args){
        if(!sender.hasPermission("epicplugin.commands.get")){
            sender.sendMessage(MessageUtils.getColoredMessage(
                    Epicplugin.prefix+" &c&lUPS &cSorry but you do not have permissions to execute this command."));
            return;

        }

        if(args.length == 1){
            sender.sendMessage(MessageUtils.getColoredMessage(Epicplugin.prefix+" &c&lUPS &cSorry but you have to use a valid value."));
            sender.sendMessage(MessageUtils.getColoredMessage("&7/epicplugin get <author/version>."));
            return;

        }

        if(args[1].equalsIgnoreCase("author")){
            //epicplugin get author
            sender.sendMessage(MessageUtils.getColoredMessage(
                    Epicplugin.prefix+" &7The authors of the plugin are: &e"+plugin.getDescription().getAuthors()));

        } else if (args[1].equalsIgnoreCase("version")) {
            //epicplugin get version
            sender.sendMessage(MessageUtils.getColoredMessage(
                    Epicplugin.prefix+" &7The plugin version is: &e"+plugin.getDescription().getVersion()));
        }else{
            sender.sendMessage(MessageUtils.getColoredMessage(Epicplugin.prefix+" &c&lUPS &cSorry but you have to use a valid value."));
            sender.sendMessage(MessageUtils.getColoredMessage("&7/epicplugin get <author/version>."));
        }

    }
}

package staff.main;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import staff.commands.MainCommand;
import staff.commands.heal;
import staff.evets.playerJoinQuit;
import staff.utils.MessageUtils;

public final class Epicplugin extends JavaPlugin {

    public static String prefix = "&f[&bEpicPlugin&f]";
    private String version = getDescription().getVersion();
    ConsoleCommandSender mycmd = Bukkit.getConsoleSender();
    @Override //INICIO DE PLUGIN
    public void onEnable() {
        registerCommands();

        getServer().getPluginManager().registerEvents(new playerJoinQuit(), this);
        getCommand("heal").setExecutor(new heal());
        mycmd.sendMessage(MessageUtils.getColoredMessage(prefix+" &aThe plugin has started successfully! Version: "+version));
    }

    @Override//FINALIZAR PLUGIN
    public void onDisable(){
        mycmd.sendMessage(MessageUtils.getColoredMessage(prefix+" &aThe plugin has been successfully deactivated! Version: "+version));
    }

    public void registerCommands(){
        this.getCommand("epicplugin").setExecutor(new MainCommand());
    }
}

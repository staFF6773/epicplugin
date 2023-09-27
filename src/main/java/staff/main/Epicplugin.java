package staff.main;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import staff.commands.MainCommand;
import staff.commands.heal;
import staff.commands.repair;
import staff.manager.ConfigManager;
import staff.utils.MessageUtils;

import java.io.File;

public final class Epicplugin extends JavaPlugin {

    public static String prefix = "&f[&bEpicPlugin&f]";
    private String version = getDescription().getVersion();
    ConsoleCommandSender mycmd = Bukkit.getConsoleSender();
    @Override //INICIO DE PLUGIN
    public void onEnable() {

        ConfigManager.setupConfig(this);

        registerCommands();

        mycmd.sendMessage(MessageUtils.getColoredMessage(prefix+" &aThe plugin has started successfully! Version: "+version));
        mycmd.sendMessage(MessageUtils.getColoredMessage(prefix+" &aThank you very much for using me :D &7ATT: not_staff"));
    }

    @Override//FINALIZAR PLUGIN
    public void onDisable(){
        mycmd.sendMessage(MessageUtils.getColoredMessage(prefix+" &aThe plugin has been successfully deactivated! Version: "+version));
        mycmd.sendMessage(MessageUtils.getColoredMessage(prefix+" &aThank you very much for using me :D &7ATT: not_staff"));
    }

    public void registerCommands(){
        this.getCommand("repair").setExecutor(new repair(this));
        this.getCommand("epicplugin").setExecutor(new MainCommand(this));
        this.getCommand("heal").setExecutor(new heal());
    }
}

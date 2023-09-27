package staff.manager;

import org.bukkit.configuration.file.FileConfiguration;
import staff.main.Epicplugin;

public class ConfigManager {

    private static FileConfiguration config;

    public static void setupConfig(Epicplugin epicplugin){
        ConfigManager.config = epicplugin.getConfig();
        epicplugin.saveDefaultConfig();
    }
}

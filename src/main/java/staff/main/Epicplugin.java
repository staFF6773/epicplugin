package staff.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import staff.commands.*;
import staff.manager.ConfigManager;
import staff.manager.checkforupdates;
import staff.utils.ChatUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public final class Epicplugin extends JavaPlugin implements Listener {

    private checkforupdates checkforupdates;
    public static String prefix;
    private String version = getDescription().getVersion();
    ConsoleCommandSender mycmd = Bukkit.getConsoleSender();

    private static Epicplugin plugin;
    private final File playerUUIDsFile = new File(getDataFolder(), "playerUUIDs.txt");
    private final Set<String> uniqueUUIDs = new HashSet<>();

    @Override
    public void onEnable() {
        checkforupdates = new checkforupdates(this, "https://api.spigotmc.org/legacy/update.php?resource=112887");
        checkforupdates.iniciarVerificacionDeActualizaciones();

        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "&f[&bEpicPlugin&f]"));

        plugin = this;

        ConfigManager.setupConfig(this);

        registerCommands();

        mycmd.sendMessage(ChatUtils.getColoredMessage("&f[&bEpicPlugin&f] &aThe plugin has started successfully! Version: " + version));
        mycmd.sendMessage(ChatUtils.getColoredMessage("&f[&bEpicPlugin&f] &aThank you very much for using me :D &7ATT: not_staff"));

        if (getConfig().getBoolean("Player-join.enabled")) {
            if (getConfig().getBoolean("Player-quit.enabled"))
                Bukkit.getPluginManager().registerEvents(this, this);
        }

        loadUniqueUUIDs();
    }

    @Override
    public void onDisable() {
        mycmd.sendMessage(ChatUtils.getColoredMessage("&f[&bEpicPlugin&f] &aThe plugin has been successfully deactivated! Version: " + version));
        mycmd.sendMessage(ChatUtils.getColoredMessage("&f[&bEpicPlugin&f] &aThank you very much for using me :D &7ATT: not_staff"));

        saveUniqueUUIDs();
    }

    public void registerCommands() {
        this.getCommand("nopvp").setExecutor(new PvPCommand(this));
        this.getCommand("reload").setExecutor(new reload());
        this.getCommand("repair").setExecutor(new repair(this));
        this.getCommand("epicplugin").setExecutor(new MainCommand(this));
        this.getCommand("heal").setExecutor(new heal(this));
    }

    @EventHandler
    public void joinserver(PlayerJoinEvent e) {
        if (this.getConfig().getBoolean("Player-join.enabled")) {
            Player player = e.getPlayer();
            String playerJoin = "PlayerJoin";
            e.setJoinMessage(ChatColor.translateAlternateColorCodes(
                    '&', this.getConfig().getString(playerJoin)
                            .replace("{player_Name}", player.getName())));

            // Almacenar el UUID del jugador en un archivo
            saveUUIDToFile(player.getUniqueId().toString());
        }
    }

    @EventHandler
    public void leaveServer(PlayerQuitEvent e) {
        if (this.getConfig().getBoolean("Player-quit.enabled")) {
            Player player = e.getPlayer();
            String playerLeave = "PlayerLeave";
            e.setQuitMessage(ChatColor.translateAlternateColorCodes(
                    '&', this.getConfig().getString(playerLeave)
                            .replace("{player_Name}", player.getName())));
        }
    }

    private void saveUUIDToFile(String uuid) {
        if (uniqueUUIDs.add(uuid)) {
            try (FileWriter writer = new FileWriter(playerUUIDsFile, true)) {
                writer.write(uuid + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadUniqueUUIDs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(playerUUIDsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                uniqueUUIDs.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUniqueUUIDs() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerUUIDsFile))) {
            for (String uuid : uniqueUUIDs) {
                writer.write(uuid + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Epicplugin getPlugin() {
        return plugin;
    }
}

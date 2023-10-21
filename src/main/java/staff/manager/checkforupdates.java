package staff.manager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class checkforupdates {

    private final JavaPlugin plugin;
    private final String pluginVersion;
    private final String updateCheckURL;

    public checkforupdates(JavaPlugin plugin, String updateCheckURL) {
        this.plugin = plugin;
        this.pluginVersion = plugin.getDescription().getVersion();
        this.updateCheckURL = updateCheckURL;
    }

    public void iniciarVerificacionDeActualizaciones() {
        // Verificar actualizaciones cada hora (72000 ticks)
        new BukkitRunnable() {
            @Override
            public void run() {
                verificarActualizaciones();
            }
        }.runTaskTimer(plugin, 0, 72000);
    }

    private void verificarActualizaciones() {
        String currentVersion = plugin.getConfig().getString("version");

        // Consultar la versión más reciente y la URL desde la URL de actualización
        UpdateInfo updateInfo = getLatestVersionInfo();

        if (updateInfo != null) {
            if (!updateInfo.getVersion().equals(currentVersion)) {
                String message = "A new version of the plugin is available! Current version: " + currentVersion +
                        ", Latest version: " + updateInfo.getVersion() + " - " + updateInfo.getDownloadUrl();
                // Mostrar el mensaje en el chat del juego
                Bukkit.getServer().broadcastMessage(message);
            }
        }
    }

    private UpdateInfo getLatestVersionInfo() {
        try {
            URL url = new URL(updateCheckURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String version = reader.readLine();
            String downloadUrl = reader.readLine(); // Se asume que la segunda línea contiene el enlace de descarga
            return new UpdateInfo(version, downloadUrl);
        } catch (IOException e) {
            plugin.getLogger().warning("§f[§bEpicplugin§f] The most recent version info could not be obtained: " + e.getMessage());
            return null;
        }
    }

    // Clase para almacenar información sobre la actualización
    private static class UpdateInfo {
        private final String version;
        private final String downloadUrl;

        public UpdateInfo(String version, String downloadUrl) {
            this.version = version;
            this.downloadUrl = downloadUrl;
        }

        public String getVersion() {
            return version;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }
    }
}

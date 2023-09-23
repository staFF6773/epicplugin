package staff.evets;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class playerJoinQuit implements Listener {

    @EventHandler
    public void Entrada(PlayerJoinEvent e){
        e.setJoinMessage("§aEl Jugador §l"+e.getPlayer().getName()+"§a ha entrado");
    }

    @EventHandler
    public void Salida(PlayerQuitEvent e){
        e.setQuitMessage("§cEl jugador §l"+e.getPlayer().getName()+"§c ha salido");
    }
}

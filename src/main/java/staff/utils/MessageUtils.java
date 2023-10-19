//No tocar esta parte

package staff.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    public static String getColoredMessage(String message) {

        if(Bukkit.getVersion().contains("1.18")) {

            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while(matcher.find()) {
                String color = message.substring(matcher.start(),matcher.end());
                message = message.replace(color, ChatColor.of(color)+"");

                matcher = pattern.matcher(message);
            }

        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}

package idk.plugin.nearestplayer;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.utils.Config;

public class Main extends PluginBase implements Listener {

    private Config c;

    public void onEnable() {
        saveDefaultConfig();
        c = getConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("nearestplayer") || command.getName().equalsIgnoreCase("nplayer")) {
            if (sender instanceof Player) {
                nearestPlayer((Player) sender);
            } else {
                sender.sendMessage(c.getString("messageOnlyInGame"));
            }
        }

        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != PlayerInteractEvent.Action.PHYSICAL && c.getBoolean("enableItem")) {
            if (e.getItem().getId() == c.getInt("itemID") && e.getItem().getDamage() == c.getInt("itemDamage")) {
                if (e.getPlayer().hasPermission("nearestplayer.item")) {
                    nearestPlayer(e.getPlayer());
                }
            }
        }
    }

    private void nearestPlayer(Player p) {
        double distance = Double.MAX_VALUE;
        Player closest = null;

        for (Player pl : p.getLevel().getPlayers().values()) {
            if (pl.equals(p)) {
                continue;
            }

            double dis = p.distance(pl);

            if (dis < distance) {
                closest = pl;
                distance = dis;
            }
        }

        if (closest == null) {
            p.sendMessage(c.getString("messageNoPlayers"));
        } else {
            p.sendMessage(c.getString("messageSuccess").replace("%player", closest.getName()).replace("%distance", String.valueOf(Math.round(distance))));
        }
    }
}

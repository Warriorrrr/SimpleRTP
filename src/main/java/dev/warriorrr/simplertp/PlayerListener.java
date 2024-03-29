package dev.warriorrr.simplertp;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {

    private final SimpleRTP plugin;

    public PlayerListener(SimpleRTP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawnLocationEvent(PlayerSpawnLocationEvent event) {
        if (event.getPlayer().hasPlayedBefore() || !plugin.config().rtpFirstJoin())
            return;

        final Location location = plugin.generator().getAndRemove();
        event.setSpawnLocation(location);

        plugin.getServer().getAsyncScheduler().runDelayed(plugin, task -> {
            event.getPlayer().sendRichMessage("<gradient:blue:aqua>You have been randomly teleported to: " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ".");
        }, 1L, TimeUnit.SECONDS);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!plugin.config().respawnNearbyOnDeath())
            return;

        // Return if the player is already being teleported to a respawn anchor or bed, or the player has a town
        if (event.isAnchorSpawn() || event.isBedSpawn() || (plugin.townyCompat() != null && plugin.townyCompat().hasTown(event.getPlayer())))
            return;

        event.setRespawnLocation(plugin.generator().generateRespawnLocation(event.getPlayer().getLocation()));
    }
}

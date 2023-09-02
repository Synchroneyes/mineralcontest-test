package fr.synchroneyes.custom_events;

import fr.synchroneyes.mineral.Core.Game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MCEvent extends Event {

    private static final HandlerList handlers = new HandlerList();


    public MCEvent() {
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void callEvent() {
        Bukkit.getServer().getPluginManager().callEvent(this);
    }
}

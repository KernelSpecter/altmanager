package com.kernelspecter.altmanager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Swaps the account the game plays as by replacing {@code Minecraft.session}. Remembers the session the launcher
 * started with so it can switch back to it.
 */
public final class SessionSwitcher {
    private static final Logger LOG = LogManager.getLogger("AltManager");

    private final Minecraft mc;
    private final Session original;

    public SessionSwitcher(Minecraft mc) {
        this.mc = mc;
        this.original = mc.getSession();
    }

    public String originalName() {
        return original.getUsername();
    }

    public boolean onOriginal() {
        return mc.getSession() == original;
    }

    public boolean switchTo(String name) {
        return set(new Session(name, OfflineIds.playerId(name), "0", "legacy"));
    }

    public boolean restore() {
        return set(original);
    }

    private boolean set(Session session) {
        try {
            sessionField().set(mc, session);
            return true;
        } catch (Exception e) {
            LOG.error("Couldn't switch account", e);
            return false;
        }
    }

    /** Found by type rather than by name, so it doesn't matter what mappings the client runs with (Forge, Lunar). */
    private static Field sessionField() throws NoSuchFieldException {
        for (Field f : Minecraft.class.getDeclaredFields()) {
            if (f.getType() == Session.class && !Modifier.isStatic(f.getModifiers())) {
                f.setAccessible(true);
                return f;
            }
        }
        throw new NoSuchFieldException("no Session field in Minecraft");
    }
}

package com.kernelspecter.altmanager;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** The UUID an offline-mode server gives a name, so client and server agree on who the player is. */
public final class OfflineIds {
    private OfflineIds() {}

    public static UUID uuid(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }

    /** The UUID without dashes, the form a {@code Session} takes. */
    public static String playerId(String name) {
        return uuid(name).toString().replace("-", "");
    }
}

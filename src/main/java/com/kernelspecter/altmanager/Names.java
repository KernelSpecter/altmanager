package com.kernelspecter.altmanager;

import java.util.Random;

/** Minecraft username rules, and random names for the Random button. */
public final class Names {
    private static final String[] ADJECTIVES = {
            "Swift", "Silent", "Lucky", "Frosty", "Crimson", "Shadow", "Brave", "Clever", "Wild", "Mellow",
            "Rapid", "Sneaky", "Golden", "Stormy", "Cosmic", "Fuzzy", "Quiet", "Bold", "Icy", "Rusty"};
    private static final String[] NOUNS = {
            "Fox", "Wolf", "Otter", "Falcon", "Tiger", "Raven", "Panda", "Cobra", "Moose", "Lynx",
            "Badger", "Hawk", "Shark", "Bison", "Gecko", "Koala", "Viper", "Crane", "Moth", "Yak"};

    private Names() {}

    /** 3-16 letters, digits or underscores: what Minecraft and servers accept. */
    public static boolean valid(String name) {
        return name != null && name.matches("[A-Za-z0-9_]{3,16}");
    }

    /** Like "SwiftFox482": an adjective, an animal and up to three digits, never over 16 characters. */
    public static String random(Random r) {
        String base = ADJECTIVES[r.nextInt(ADJECTIVES.length)] + NOUNS[r.nextInt(NOUNS.length)];
        String name = base + r.nextInt(1000);
        return name.length() <= 16 ? name : name.substring(0, 16);
    }
}

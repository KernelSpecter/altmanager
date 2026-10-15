package com.kernelspecter.altmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** The saved alts, in config/altmanager.json. Usernames only: offline accounts have no password or token. */
public final class AltStore {
    public enum Result { ADDED, DUPLICATE, INVALID }

    private static final Logger LOG = LogManager.getLogger("AltManager");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File file;
    private final List<String> alts = new ArrayList<String>();

    public AltStore(File file) {
        this.file = file;
    }

    public List<String> alts() {
        return Collections.unmodifiableList(alts);
    }

    public Result add(String name) {
        if (!Names.valid(name)) {
            return Result.INVALID;
        }
        if (contains(name)) {
            return Result.DUPLICATE;
        }
        alts.add(name);
        return Result.ADDED;
    }

    public void remove(String name) {
        alts.remove(name);
    }

    /** Names differing only in case count as the same alt. */
    public boolean contains(String name) {
        for (String a : alts) {
            if (a.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /** Missing file: empty list. Unreadable file: kept as altmanager.json.bad, empty list. */
    public void load() {
        alts.clear();
        if (!file.isFile()) {
            return;
        }
        Data data;
        try {
            Reader in = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            try {
                data = GSON.fromJson(in, Data.class);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            keepAside(e);
            return;
        } catch (JsonParseException e) {
            keepAside(e);
            return;
        }
        if (data != null && data.alts != null) {
            for (String a : data.alts) {
                add(a);
            }
        }
    }

    public void save() {
        Data data = new Data();
        data.alts = new ArrayList<String>(alts);
        try {
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            Writer out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            try {
                GSON.toJson(data, out);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            LOG.error("Couldn't save " + file, e);
        }
    }

    private void keepAside(Exception cause) {
        File bad = new File(file.getPath() + ".bad");
        bad.delete();
        boolean moved = file.renameTo(bad);
        LOG.warn("Couldn't read " + file + (moved ? "; kept it as " + bad.getName() : "") + " and started with no alts", cause);
    }

    private static final class Data {
        List<String> alts;
    }
}

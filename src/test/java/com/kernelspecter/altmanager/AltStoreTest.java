package com.kernelspecter.altmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AltStoreTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void addKeepsOrderAndRefusesBadOrDuplicateNames() {
        AltStore s = new AltStore(new File(tmp.getRoot(), "altmanager.json"));
        assertEquals(AltStore.Result.ADDED, s.add("First"));
        assertEquals(AltStore.Result.ADDED, s.add("Second_2"));
        assertEquals(AltStore.Result.DUPLICATE, s.add("first"));
        assertEquals(AltStore.Result.INVALID, s.add("no spaces"));
        assertEquals(Arrays.asList("First", "Second_2"), s.alts());
    }

    @Test
    public void savesAndLoadsBack() throws Exception {
        File f = new File(tmp.getRoot(), "altmanager.json");
        AltStore s = new AltStore(f);
        s.add("One");
        s.add("Two");
        s.remove("One");
        s.save();
        AltStore back = new AltStore(f);
        back.load();
        assertEquals(Collections.singletonList("Two"), back.alts());
    }

    @Test
    public void missingFileIsAnEmptyList() {
        AltStore s = new AltStore(new File(tmp.getRoot(), "none.json"));
        s.load();
        assertTrue(s.alts().isEmpty());
    }

    @Test
    public void corruptFileIsKeptAsideAndTheListStartsEmpty() throws Exception {
        File f = new File(tmp.getRoot(), "altmanager.json");
        FileOutputStream out = new FileOutputStream(f);
        out.write("{ not json".getBytes(StandardCharsets.UTF_8));
        out.close();
        AltStore s = new AltStore(f);
        s.load();
        assertTrue(s.alts().isEmpty());
        assertTrue(new File(tmp.getRoot(), "altmanager.json.bad").isFile());
        assertFalse(f.exists());
    }

    @Test
    public void badNamesInTheFileAreDropped() throws Exception {
        File f = new File(tmp.getRoot(), "altmanager.json");
        FileOutputStream out = new FileOutputStream(f);
        out.write("{\"alts\":[\"Good\",\"bad name\",null,\"good\"]}".getBytes(StandardCharsets.UTF_8));
        out.close();
        AltStore s = new AltStore(f);
        s.load();
        assertEquals(Collections.singletonList("Good"), s.alts());
    }
}

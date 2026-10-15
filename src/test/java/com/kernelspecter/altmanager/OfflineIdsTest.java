package com.kernelspecter.altmanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OfflineIdsTest {
    @Test
    public void matchesWhatOfflineServersCompute() {
        assertEquals("b50ad385-829d-3141-a216-7e7d7539ba7f", OfflineIds.uuid("Notch").toString());
        assertEquals("b50ad385829d3141a2167e7d7539ba7f", OfflineIds.playerId("Notch"));
    }

    @Test
    public void caseMatters() {
        // Offline servers hash the name as typed, so "notch" is a different player.
        assertEquals(false, OfflineIds.uuid("notch").equals(OfflineIds.uuid("Notch")));
    }
}

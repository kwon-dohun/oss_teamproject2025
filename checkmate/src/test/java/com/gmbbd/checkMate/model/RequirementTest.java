package com.gmbbd.checkMate.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RequirementTest {

    @Test
    void constructor_and_getters_shouldWork() {
        Requirement req = new Requirement(1L, "Sample");

        assertEquals(1L, req.getId());
        assertEquals("Sample", req.getRawText());
    }

    @Test
    void setters_shouldWork() {
        Requirement req = new Requirement();
        req.setId(10L);
        req.setRawText("ABC");

        assertEquals(10L, req.getId());
        assertEquals("ABC", req.getRawText());
    }
}

package com.example.a911simulator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class SimulatedDialpadActivityTest {

    @Test
    public void isValidAppend_Equals() {
        assertEquals(SimulatedDialpadActivity.appendDialed("", "1"), "1");
        assertEquals(SimulatedDialpadActivity.appendDialed("", "0"), "0");
        assertEquals(SimulatedDialpadActivity.appendDialed("1", "1"), "11");
        assertEquals(SimulatedDialpadActivity.appendDialed("", "9"), "9");
        assertEquals(SimulatedDialpadActivity.appendDialed("9", "1"), "91");
        assertEquals(SimulatedDialpadActivity.appendDialed("91", "1"), "911");
    }

    @Test
    public void isValidSubstring_Equals(){
        assertEquals(SimulatedDialpadActivity.removeLastChar(""), "");
        assertEquals(SimulatedDialpadActivity.removeLastChar("09"), "0");
        assertEquals(SimulatedDialpadActivity.removeLastChar("#"), "");
        assertEquals(SimulatedDialpadActivity.removeLastChar("911#5"), "911#");
    }

    @Test
    public void isValidBtnPress_ReturnsTrue(){
        assertFalse(SimulatedDialpadActivity.isWrongBtn("", "9"));
        assertFalse(SimulatedDialpadActivity.isWrongBtn("9", "1"));
        assertFalse(SimulatedDialpadActivity.isWrongBtn("91", "1"));
        assertFalse(SimulatedDialpadActivity.isWrongBtn("911", "call"));
    }

    @Test
    public void isValidBtnPress_ReturnsFalse(){
        assertTrue(SimulatedDialpadActivity.isWrongBtn("", "8"));
        assertTrue(SimulatedDialpadActivity.isWrongBtn("", "call"));
        assertTrue(SimulatedDialpadActivity.isWrongBtn("9", "2"));
        assertTrue(SimulatedDialpadActivity.isWrongBtn("9", "4"));
    }
}
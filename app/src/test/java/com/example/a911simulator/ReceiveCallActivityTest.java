package com.example.a911simulator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Null;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class ReceiveCallActivityTest {

    @Test
    public void isValidDate_Equals() {
        assertTrue(ReceiveCallActivity.getMMDD() != null);
        assertTrue(ReceiveCallActivity.getMMDD().length() > 3);
    }
}
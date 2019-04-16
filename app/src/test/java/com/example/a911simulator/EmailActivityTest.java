package com.example.a911simulator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class EmailActivityTest {

    @Test
    public void isValidEmail_CorrectEmailReturnsTrue() {
        assertTrue(EmailActivity.isValidEmail("name@gmail.com"));
        assertTrue(EmailActivity.isValidEmail("av6780@al-isd.org"));
        assertTrue(EmailActivity.isValidEmail("something@ymail.com"));
        assertTrue(EmailActivity.isValidEmail("something@my.unt.edu"));
        assertTrue(EmailActivity.isValidEmail("something@nctcog.gov"));
        assertTrue(EmailActivity.isValidEmail("a.v@rocketmail.com"));
    }

    @Test
    public void isValidEmail_IncorrectEmailReturnsFalse() {
        assertFalse(EmailActivity.isValidEmail("This is incorrect"));
        assertFalse(EmailActivity.isValidEmail("123456"));
    }
}
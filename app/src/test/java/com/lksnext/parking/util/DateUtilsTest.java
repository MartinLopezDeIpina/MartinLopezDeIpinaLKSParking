package com.lksnext.parking.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DateUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void compareStringDates() {
        int expected = -1;

        String date1 = "2021-06-01";
        String date2 = "2021-06-02";

        int result = DateUtils.compareStringDates(date1, date2);
        assertEquals(expected, result);


        int expected2 = 0;

        int result2 = DateUtils.compareStringDates(date1, date1);
        assertEquals(expected, result);
    }

    @Test
    public void compareStringHours() {
    }

    @Test
    public void getOrderedHours() {
    }
}
package com.example.creditcardvalidator;

import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class ValidationUtilsTest {

    @Test
    public void testConvertToIntWhenStringHas5Entries() {
        String items = "12345";
        int[] expectedItemsValues = {1, 2, 3, 4, 5};
        int[] itemsValues = ValidationUtils.convertToInt(items);

        assertArrayEquals(itemsValues, expectedItemsValues);
    }

    @Test
    public void testConvertToIntWhenStringHas0Entries() {
        CharSequence items = "";
        int[] itemsValues = ValidationUtils.convertToInt(items);

        assertEquals(0, itemsValues.length);
    }

    public void testConvertToIntWhenStringHas5EntriesAndText() {
        String items = "12345 stop charts.";
        int[] expectedItemsValues = {1, 2, 3, 4, 5};
        int[] itemsValues = ValidationUtils.convertToInt(items);

        assertArrayEquals(itemsValues, expectedItemsValues);

    }

    @Test
    public void checkExpirationDate() {
        assertEquals(true, ValidationUtils.checkExpirationDate("01/18"));
        assertEquals(false, ValidationUtils.checkExpirationDate("0118"));
        assertEquals(false, ValidationUtils.checkExpirationDate("18"));


    }

    @Test
    public void testConvertToIntShouldReturnValidArrayWhenStringIsValid() {
        int[] expectedArray = {1,2,3,4,5,6,7,8};
        int[] data = ValidationUtils.convertToInt("12345678");
        assertArrayEquals(expectedArray, data);

    }

    @Test
    public void testConvertToIntShouldReturnEmptyArrayWhenStringIsAlpha() {
        PowerMockito.mockStatic(Log.class);
        int[] expectedArray = {};
        int[] data = ValidationUtils.convertToInt("123ABCD45678");
        assertArrayEquals(expectedArray, data);

    }

}
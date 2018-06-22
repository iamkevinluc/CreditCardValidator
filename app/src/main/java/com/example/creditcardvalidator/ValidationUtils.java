package com.example.creditcardvalidator;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;

import io.reactivex.Observable;

public class ValidationUtils {
    private static String TAG = ValidationUtils.class.getCanonicalName();

    public static Boolean checkExpirationDate(CharSequence candidate) {
        return Boolean.valueOf(candidate.toString().matches("\\d\\d/\\d\\d"));
    }

    public static boolean checkCardChecksum(int[] digits) {
        int sum = 0;
        int length = digits.length;
        for (int i = 0; i < length; i++) {
            // Get digits in reverse order
            int digit = digits[length - i - 1];
            // Every 2nd number multiply with 2
            if (i % 2 == 1) {
                digit *= 2;
            }
            sum += digit > 9 ? digit - 9 : digit;
        }
        return sum % 10 == 0;
    }

    public static int[] convertToInt(@NonNull CharSequence items){
        int[] arr = {};

        if(items.length() == 0) {
            return arr;
        }

        try {
            arr = Arrays.stream(items.toString().split(""))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }catch (NumberFormatException nfe){
            //use default arr value
            Log.d(TAG, "Number Format Exception "+ nfe.getMessage());
        }
        return arr;
    }

    public static Observable<Boolean> and(Observable<Boolean> a, Observable<Boolean> b) {
        return Observable.combineLatest(a, b,
                (valueA, valueB) -> valueA && valueB);
    }


    public static Observable<Boolean> equals(Observable<Integer> a, Observable<Integer> b) {
        return Observable.combineLatest(a, b,
                (valueA, valueB) -> valueA == valueB);
    }

    public static Observable<Boolean> and(Observable<Boolean> a, Observable<Boolean> b, Observable<Boolean> c) {
        return Observable.combineLatest(a, b, c,
                (valueA, valueB, valueC) -> valueA && valueB && valueC);
    }
}

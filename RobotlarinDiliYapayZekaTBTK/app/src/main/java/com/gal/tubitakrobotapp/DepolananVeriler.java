package com.gal.tubitakrobotapp;
import android.content.Context;
import android.content.SharedPreferences;
public class DepolananVeriler {
    private static final String TercihAdi = "TbtkTercihler";

    // Method to save an integer value to SharedPreferences
    public static void kaydetInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(TercihAdi, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    // Method to retrieve an integer value from SharedPreferences
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(TercihAdi, Context.MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }

    // Method to save a string value to SharedPreferences
    public static void kaydetString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(TercihAdi, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Method to retrieve a string value from SharedPreferences
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(TercihAdi, Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }
    public static boolean contains(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(TercihAdi, Context.MODE_PRIVATE);
        return prefs.contains(key);
    }

}

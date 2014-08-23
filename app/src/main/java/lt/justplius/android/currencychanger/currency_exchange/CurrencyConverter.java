package lt.justplius.android.currencychanger.currency_exchange;

import android.util.Log;

/**
 * This class encapsulates currency converter by providing static methods. Usage:
 * using set...() methods set all the initial values. After that call countValues().
 * Receive currency exchange rates using getValueFrom() and getValueTo() methods.
 */
public class CurrencyConverter {
    private static final String ERROR_TAG = "CurrencyConverter.java: ";

    // Currency quantity
    private static double sQuantityFrom;
    private static double sQuantityTo;
    // Currency rate with Litas
    private static double sRateWithLitasFrom;
    private static double sRateWithLitasTo;
    // Currency quantity and rate with Litas, a "buying power"
    private static double sValueFrom;
    private static double sValueTo;
    // Used when user enters it's quantities in EditText
    private static double sMultiplier = 1;

    // Determine if provided string is parsable  to double type
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isDouble (String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            Log.d(ERROR_TAG, "NumberFormatException: " + e.toString());
            return false;
        }
    }

    // Count "buying powers" expressed as Litas of ValueFrom and ValueTo
    public static void countValues() {
        sValueFrom = sRateWithLitasFrom / sQuantityFrom;
        sValueTo = sRateWithLitasTo / sQuantityTo;
    }

    // Return exchange ratio for "from" EditText
    public static double getValueFrom() {
        return sMultiplier * sValueTo / sValueFrom;
    }

    // Return exchange ratio for "to" EditText
    public static double getValueTo() {
        return sMultiplier * sValueFrom / sValueTo;
    }

    // Various getters and setters

    public static double getDouble (String string) {
        return Double.parseDouble(string);
    }

    public static void setQuantityFrom(double quantityFrom) {
        sQuantityFrom = quantityFrom;
    }

    public static void setQuantityTo(double quantityTo) {
        sQuantityTo = quantityTo;
    }

    public static void setRateWithLitasFrom(double rateWithLitasFrom) {
        sRateWithLitasFrom = rateWithLitasFrom;
    }

    public static void setRateWithLitasTo(double rateWithLitasTo) {
        sRateWithLitasTo = rateWithLitasTo;
    }

    public static void setMultiplier(double multiplier) {
        sMultiplier = multiplier;
    }
}

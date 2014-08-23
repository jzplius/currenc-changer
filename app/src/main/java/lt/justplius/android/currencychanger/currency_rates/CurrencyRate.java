package lt.justplius.android.currencychanger.currency_rates;

import lt.justplius.android.currencychanger.R;

/**
 * This class encapsulates CurrencyRate object
 */
public class CurrencyRate {

    private String mCurrencyCode;
    private String mCurrencyName;
    private float mExchangeRate;
    private int mQuantity;
    // Resource ID of icon drawable, indicating country flag
    private int mCountryFlagResourceId;

    // Instantiate all member variables with single constructor
    public CurrencyRate(String currencyName, String currencyCode, int quantity, float exchangeRate) {
        mCurrencyCode = currencyCode;
        mCurrencyName = currencyName;
        mExchangeRate = exchangeRate;
        mQuantity = quantity;
        // Retrieves appropriate icon resource ID, based on currency code
        mCountryFlagResourceId = matchResourceByCurrencyCode(currencyCode);
    }

    // Retrieves appropriate icon resource ID, based on currency code
    private int matchResourceByCurrencyCode(String currencyCode) {
        int resourceId = R.drawable.ic_launcher;
        switch (currencyCode){
            case "AUD":
                resourceId = R.drawable.australia;
                break;
            case "BGN":
                resourceId = R.drawable.bulgaria;
                break;
            case "BYR":
                resourceId = R.drawable.belarus;
                break;
            case "CAD":
                resourceId = R.drawable.canada;
                break;
            case "CHF":
                resourceId = R.drawable.switzerland;
                break;
            case "CNY":
                resourceId = R.drawable.china;
                break;
            case "CZK":
                resourceId = R.drawable.czech_republic;
                break;
            case "DKK":
                resourceId = R.drawable.denmark;
                break;
            case "EUR":
                resourceId = R.drawable.european_union;
                break;
            case "GBP":
                resourceId = R.drawable.united_kingdom;
                break;
            case "HRK":
                resourceId = R.drawable.croatia;
                break;
            case "HUF":
                resourceId = R.drawable.hungary;
                break;
            case "ISK":
                resourceId = R.drawable.iceland;
                break;
            case "JPY":
                resourceId = R.drawable.japan;
                break;
            case "KZT":
                resourceId = R.drawable.kazakhstan;
                break;
            case "MDL":
                resourceId = R.drawable.moldova;
                break;
            case "NOK":
                resourceId = R.drawable.norway;
                break;
            case "PLN":
                resourceId = R.drawable.poland;
                break;
            case "RON":
                resourceId = R.drawable.romania;
                break;
            case "RUB":
                resourceId = R.drawable.russian_federation;
                break;
            case "SEK":
                resourceId = R.drawable.sweden;
                break;
            case "TRY":
                resourceId = R.drawable.turkey;
                break;
            case "UAH":
                resourceId = R.drawable.ukraine;
                break;
            case "USD":
                resourceId = R.drawable.united_states_of_america;
                break;
            case "LTL":
                resourceId = R.drawable.lithuania;
                break;
            case "XDR":
                resourceId = R.drawable.ic_currency;
                break;
        }
        return resourceId;
    }

    // Getter and setter methods
    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        mCurrencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return mCurrencyName;
    }

    public void setCurrencyName(String currencyName) {
        mCurrencyName = currencyName;
    }

    public float getExchangeRate() {
        return mExchangeRate;
    }

    public void setExchangeRate(float exchangeRate) {
        mExchangeRate = exchangeRate;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public int getCountryFlagResourceId() {
        return mCountryFlagResourceId;
    }

    public void setCountryFlagResourceId(int countryFlagResourceId) {
        mCountryFlagResourceId = countryFlagResourceId;
    }
}

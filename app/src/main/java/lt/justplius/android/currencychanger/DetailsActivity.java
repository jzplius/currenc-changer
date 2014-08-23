package lt.justplius.android.currencychanger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import lt.justplius.android.currencychanger.currency_rates.CurrencyRate;
import lt.justplius.android.currencychanger.currency_rates.CurrencyRatesDownloader;
import lt.justplius.android.currencychanger.currency_exchange.CurrencyExchangeFragment;
import lt.justplius.android.currencychanger.currency_rates.CurrencyRatesFragment;
import lt.justplius.android.currencychanger.history.HistoryFragment;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a MenuItems
 * in a {@link MainActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a references to fragments: {@link lt.justplius.android.currencychanger.currency_rates.CurrencyRatesFragment},
 * {@link lt.justplius.android.currencychanger.currency_exchange.CurrencyExchangeFragment},{@link lt.justplius.android.currencychanger.history.HistoryFragment}.
 */
public class DetailsActivity extends BaseActivity {

    public static final String EXTRA_MENU_POSITION
            = "lt.justplius.android.currencychanger.menu_position";
    public static final String EXTRA_POSITION_FROM
            = "lt.justplius.android.currencychanger.position_from";
    public static final String EXTRA_POSITION_TO
            = "lt.justplius.android.currencychanger.position_to";
    public static final String EXTRA_CURRENCY_RATES
            = "lt.justplius.android.currencychanger.currency_rates";

    // ArrayList of CurrencyRates, retrieved from CurrencyRatesFragment
    private ArrayList<CurrencyRate> mCurrencyRates;
    private FragmentManager mFragmentManager;
    // Google Json's serializer for object's serialization to Json string
    // and vice-versa
    private Gson mGson;
    private int mMenuPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        mFragmentManager = getSupportFragmentManager();
        mGson = new Gson();

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.

        // Download the initial data
        if (getCurrencyRates() == null) {
            new CurrencyRatesDownloader(this);
        }

        // On screen rotation retrieve arguments
        if (savedInstanceState != null) {
            // Deserialize Json string to particular ArrayList<TrueFalse> object
            String jsonString = savedInstanceState.getString(EXTRA_CURRENCY_RATES);
            Type collectionType = new TypeToken<ArrayList<CurrencyRate>>() {
            }.getType();
            mCurrencyRates = mGson.fromJson(jsonString, collectionType);
            // Retrieve MenuItem selected position extra
            mMenuPosition = getIntent().getExtras().getInt(EXTRA_MENU_POSITION, 0);
        } else  {
            // Retrieve MenuItem selected position extra
            mMenuPosition = getIntent().getExtras().getInt(EXTRA_MENU_POSITION, 0);
            // Deserialize Json string to particular ArrayList<TrueFalse> object
            String jsonString = getIntent().getExtras().getString(EXTRA_CURRENCY_RATES);
            Type collectionType = new TypeToken<ArrayList<CurrencyRate>>(){}.getType();
            mCurrencyRates = mGson.fromJson(jsonString,  collectionType);

            switch (mMenuPosition) {
                case 0:
                    // Display CurrencyRatesFragment fragment
                    CurrencyRatesFragment currencyRatesFragment = new CurrencyRatesFragment();
                    mFragmentManager
                            .beginTransaction()
                            .replace(
                                    R.id.item_detail_container,
                                    currencyRatesFragment,
                                    "CurrencyRatesFragment"
                            )
                            .commit();
                    break;
                case 1:
                    // Retrieve CurrencyRate selected position extra and compare
                    // Litas with selected currency, or first and second
                    // currency if no extras provided
                    int positionFrom = getIntent().getExtras().getInt(
                            EXTRA_POSITION_FROM,
                            mCurrencyRates.size() - 1);
                    int positionTo = getIntent().getExtras().getInt(
                            EXTRA_POSITION_TO,
                            0);
                    CurrencyExchangeFragment currencyExchangeFragment
                            = CurrencyExchangeFragment.newInstance(
                                    positionFrom,
                                    positionTo,
                                    mCurrencyRates);
                    mFragmentManager.beginTransaction()
                            .replace(
                                    R.id.item_detail_container,
                                    currencyExchangeFragment,
                                    "CurrencyExchangeFragment")
                            .commit();
                    break;
                case 2:
                    HistoryFragment historyFragment
                            = new HistoryFragment();
                    mFragmentManager.beginTransaction()
                            .replace(
                                    R.id.item_detail_container,
                                    historyFragment,
                                    "HistoryFragment")
                            .commit();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Home or Up button, which navigates back to MainActivity
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCurrencyRateSelected(int position) {
        // Retrieve CurrencyRate selected position extra and compare
        // Litas with selected currency, or first and second
        // currency if no extras provided
        CurrencyExchangeFragment currencyExchangeFragment
                = CurrencyExchangeFragment.newInstance(
                        mCurrencyRates.size() - 1,
                        position,
                        mCurrencyRates);
        mFragmentManager.beginTransaction()
                .replace(
                        R.id.item_detail_container,
                        currencyExchangeFragment,
                        "CurrencyExchangeFragment")
                .addToBackStack(null)
                .commit();
    }

    // Save a reference of currencyRates in activity
    @Override
    public void setCurrencyRates (ArrayList<CurrencyRate> currencyRates){
        mCurrencyRates = currencyRates;
    }

    // Retrieve a reference of currencyRates
    @Override
    public ArrayList<CurrencyRate> getCurrencyRates (){
        return mCurrencyRates;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_CURRENCY_RATES, mGson.toJson(mCurrencyRates));
        outState.putInt(EXTRA_MENU_POSITION, mMenuPosition);
    }
}

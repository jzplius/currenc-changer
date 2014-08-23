package lt.justplius.android.currencychanger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import lt.justplius.android.currencychanger.common.BackStackDoubleTapExit;
import lt.justplius.android.currencychanger.common.MenuItemsFragment;
import lt.justplius.android.currencychanger.currency_rates.CurrencyRate;
import lt.justplius.android.currencychanger.currency_rates.CurrencyRatesDownloader;
import lt.justplius.android.currencychanger.currency_exchange.CurrencyExchangeFragment;
import lt.justplius.android.currencychanger.currency_rates.CurrencyRatesFragment;
import lt.justplius.android.currencychanger.history.HistoryFragment;

/**
 * An activity allowing to commit currency exchange. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of MenuItems, which when touched,
 * lead to a {@link DetailsActivity} representing
 * item details. On tablets, the activity presents the list of MenuItems and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of MenuItems is a
 * {@link lt.justplius.android.currencychanger.common.MenuItemsFragment} and the item details
 * (if present) is a {@link lt.justplius.android.currencychanger.currency_exchange.CurrencyExchangeFragment}, {@link lt.justplius.android.currencychanger.currency_rates.CurrencyRatesFragment}
 * or {@link lt.justplius.android.currencychanger.history.HistoryFragment}.
 * <p>
 * Activity implements the required
 * {@link lt.justplius.android.currencychanger.common.MenuItemsFragment.Callbacks} interface to listen for MenuItem selections,
 * {@link lt.justplius.android.currencychanger.currency_rates.CurrencyRatesFragment.Callbacks} interface to listen for CurrencyRates selections.
 */
public class MainActivity extends BaseActivity {

    private static final String ARG_CURRENCY_RATES
            = "currency_rates";
    private static final String ARG_MENU_POSITION
            = "menu_position";

    private FragmentManager mFragmentManager;

    // Whether or not the activity is in two-pane mode, i.e. running on a tablet
    private boolean mTwoPane = false;
    // MenuItem selected position
    private int mMenuPosition = 0;
    // ArrayList of CurrencyRates, retrieved from CurrencyRatesFragment
    private ArrayList<CurrencyRate> mCurrencyRates;
    // Google JSON's serializer for object's serialization to JSON string
    // and vice-versa
    private Gson mGson;
    private int  mItemPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mFragmentManager = getSupportFragmentManager();
        mGson = new Gson();

        // Download the initial data
        if (getCurrencyRates() == null) {
            new CurrencyRatesDownloader(this);
        }

        // On screen rotation retrieve arguments
        if (savedInstanceState != null) {
            // Deserialize Json string to particular ArrayList<TrueFalse> object
            String jsonString = savedInstanceState.getString(ARG_CURRENCY_RATES);
            Type collectionType = new TypeToken<ArrayList<CurrencyRate>>(){}.getType();
            mCurrencyRates = mGson.fromJson(jsonString, collectionType);
            mMenuPosition = savedInstanceState.getInt(ARG_MENU_POSITION);
        } else {

            // In two-pane mode first time initialize details fragment
            if (mTwoPane) {
                // In two-pane mode, list items should be given the
                // 'activated' state when touched.
                ((MenuItemsFragment) mFragmentManager
                        .findFragmentById(R.id.item_list))
                        .setActivateOnItemClick(true);

                // Display CurrencyRatesFragment fragment by default
                performMenuItemClick(mMenuPosition, -1);
            } else {
                // In single-pane mode, list items should not be 'activated' state
                ((MenuItemsFragment) mFragmentManager
                        .findFragmentById(R.id.item_list))
                        .setActivateOnItemClick(false);
            }
        }
    }

    /**
     * Callback method from {@link MenuItemsFragment.Callbacks}
     * indicating that the MenuItems with the given ID was selected.
     */
    @Override
    public void onMenuItemSelected(int position) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // replacing the detail fragment

            switch (position) {
                case 0:
                    CurrencyRatesFragment currencyRatesFragment
                            = new CurrencyRatesFragment();
                    mFragmentManager.beginTransaction()
                            .replace(
                                    R.id.item_detail_container,
                                    currencyRatesFragment,
                                    "CurrencyRatesFragment")
                            .commit();
                    break;
                case 1:
                    // Compare Litas with selected currency, or first
                    // and second currency if no extras provided
                    CurrencyExchangeFragment currencyExchangeFragment
                            = CurrencyExchangeFragment.newInstance(
                                mCurrencyRates.size() - 1,
                                mItemPosition,
                                mCurrencyRates);
                    mFragmentManager.beginTransaction()
                            .replace(
                                    R.id.item_detail_container,
                                    currencyExchangeFragment,
                                    "CurrencyExchangeFragment")
                            //.addToBackStack(null)
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
        } else {
            // In single-pane mode, simply start the DetailsActivity, which handles
            // the selected item ID.
            Intent detailIntent = new Intent(this, DetailsActivity.class);
            detailIntent.putExtra(DetailsActivity.EXTRA_MENU_POSITION, position);
            detailIntent.putExtra(DetailsActivity.EXTRA_CURRENCY_RATES, mGson.toJson(mCurrencyRates));
            startActivity(detailIntent);
        }
        // Reset item position
        mItemPosition = 0;
        mMenuPosition = position;
    }

    /**
     * Callback method from {@link CurrencyRatesFragment.Callbacks}
     * indicating that the currency with the given code was selected.
     */
    @Override
    public void onCurrencyRateSelected(int position) {
        // Save a reference of currencyRates in activity
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // replacing the detail fragment

            // Compare Litas with selected currency
            performMenuItemClick(1, position);

        } else {
            // In single-pane mode, simply start the DetailsActivity, which handles
            // CurrencyExchangeFragment lifecycle.
            Intent detailIntent = new Intent(this, DetailsActivity.class);
            detailIntent.putExtra(DetailsActivity.EXTRA_MENU_POSITION, 1);
            detailIntent.putExtra(DetailsActivity.EXTRA_POSITION_FROM, mCurrencyRates.size() - 1);
            detailIntent.putExtra(DetailsActivity.EXTRA_POSITION_TO, position);
            startActivity(detailIntent);
        }
    }

    private void performMenuItemClick(int menuPosition, int itemPosition) {
        // Carries additional information about which specific
        // which specific item was selected, -1 indicates, that
        // item is not specified
        if (itemPosition != -1) {
            mItemPosition = itemPosition;
        }

        // In two-pane layout we want MenuItem to be selected
        // when a fragment was inflated not via MenuItem press.
        // So we imitate MenuItem key-press after inflate
        ListView listView = ((MenuItemsFragment) mFragmentManager
                .findFragmentById(R.id.item_list)).getListView();
        listView.performItemClick(
                    listView.getAdapter().getView(menuPosition, null, null),
                    menuPosition,
                    listView.getAdapter().getItemId(menuPosition));
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

    // Disable on back button pressed event by default
    // and exit from app on back button pressed twice
    @Override
    public void onBackPressed() {
        // If the button has been pressed twice go to the main screen of phone
        new BackStackDoubleTapExit(getApplicationContext());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_CURRENCY_RATES, mGson.toJson(mCurrencyRates));
        outState.putInt(ARG_MENU_POSITION, mMenuPosition);
    }
}

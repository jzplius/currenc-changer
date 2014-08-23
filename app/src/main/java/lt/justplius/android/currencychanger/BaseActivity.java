package lt.justplius.android.currencychanger;

import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

import lt.justplius.android.currencychanger.common.MenuItemsFragment;
import lt.justplius.android.currencychanger.common.ConnectionChangeReceiver;
import lt.justplius.android.currencychanger.currency_rates.CurrencyRate;
import lt.justplius.android.currencychanger.currency_rates.CurrencyRatesFragment;

/**
 * Base activity used to be extended by subclasses. It contains callbacks to
 * MenuItemsFragment.Callbacks and CurrencyRatesFragment.Callbacks
 */


public class BaseActivity extends FragmentActivity
        implements MenuItemsFragment.Callbacks, CurrencyRatesFragment.Callbacks {

    // Receiver handling internet connection change events
    private ConnectionChangeReceiver ccr;

    // To be Overriden by subclass
    @Override
    public void onMenuItemSelected(int position) {
    }

    // To be Overriden by subclass
    @Override
    public void onCurrencyRateSelected(int position) {

    }

    // Save a reference of currencyRates in activity
    // To be Overriden by subclass
    public void setCurrencyRates (ArrayList<CurrencyRate> currencyRates){
    }

    // Retrieve a reference of currencyRates
    // To be Overriden by subclass
    public ArrayList<CurrencyRate> getCurrencyRates (){
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register receiver in onResume() activity callback
        registerReceiver(ccr, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister receiver in onPause() activity callback
        //unregisterReceiver(ccr);
    }
}

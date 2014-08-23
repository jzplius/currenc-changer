package lt.justplius.android.currencychanger.currency_rates;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import au.com.bytecode.opencsv.CSVReader;
import lt.justplius.android.currencychanger.BaseActivity;
import lt.justplius.android.currencychanger.R;
import lt.justplius.android.currencychanger.common.NetworkState;

/**
 * A list fragment representing a list of CurrencyRates.
 * Activities containing this fragment must implement the {@link Callbacks}
 * interface, indicating particular currency selected onCurrencyRateSelected().
 */
public class CurrencyRatesFragment extends ListFragment {

    private static final String ERROR_TAG = "CurrencyRatesFragment.java: ";

    // ArrayList of CurrencyRates
    ArrayList<CurrencyRate> mCurrencyRates;

    // A callback interface that all activities containing this fragment must
    // implement. This mechanism allows activities to be notified of item
    // selections.
    public interface Callbacks {
        // Callback for CurrencyRate item selected from ListVIew.
        public void onCurrencyRateSelected(int position);
    }
    // The fragment's current callback object, which is notified of list item clicks.
    private Callbacks mCallbacks = null;

    // Mandatory empty constructor for the fragment manager to instantiate the
    // fragment (e.g. upon screen orientation changes).
    public CurrencyRatesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrencyRates = ((BaseActivity) getActivity()).getCurrencyRates();
        //Log.d("currency fragment create", String.valueOf(mCurrencyRates));
        if (mCurrencyRates == null) {
            Calendar calendar = Calendar.getInstance();
            // Format a String to URL with current date
            String url = getString(
                    R.string.url_currency_rates,
                    String.valueOf(calendar.get(Calendar.YEAR)),
                    String.valueOf(calendar.get(Calendar.MONTH)),
                    String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            // Commit asynchronous download of Currency Rates
            new DownloadCurrencyRatesTask().execute(url);
        } else {
            // Set a adapter to ListView, to pair view items with their corresponding data
            CurrencyRatesListViewAdapter adapter =
                    new CurrencyRatesListViewAdapter(getActivity(), mCurrencyRates, false);
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        // Save fragment instance on screen rotations
        setRetainInstance(true);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onCurrencyRateSelected(position);
    }

    // Implementation of AsyncTask used to download CSV feed from lb.lt and return
    // ArrayList<CurrencyRate>. With given ArrayList<CurrencyRate> prepare ListView
    // adapter and set it to ListView.
    private class DownloadCurrencyRatesTask
            extends AsyncTask<String, Void, ArrayList<CurrencyRate>> {

        // Actions to be performed in background thread
        @Override
        protected ArrayList<CurrencyRate> doInBackground(String... urls) {
            // From given URL response's CSV parse and return ArrayList<CurrencyRate>
            return parseCsvResponseAsCurrencyRates(urls[0]);
        }

        // Actions to be performed in main thread, after actions
        // are completed in background thread
        @Override
        protected void onPostExecute(ArrayList<CurrencyRate> items) {
            // Use list of retrieved CurrencyRates
            if (items != null) {
                // Set a adapter to ListView, to pair view items with their corresponding data
                CurrencyRatesListViewAdapter adapter =
                        new CurrencyRatesListViewAdapter(getActivity(), items, false);
                setListAdapter(adapter);
                adapter.notifyDataSetChanged();
                mCurrencyRates = items;
                // Add Lithuanian litas as a currency, so that we could use it later
                mCurrencyRates.add(new CurrencyRate("Lietuvos litas", "LTL", 1, 1));
                ((BaseActivity) getActivity()).setCurrencyRates(mCurrencyRates);
            } else {
                // In case when no results are returned inform user
                Toast.makeText(getActivity(), getActivity().getResources().
                        getString(R.string.no_results), Toast.LENGTH_LONG).show();
            }
        }

        // From given URL response's CSV parse and return ArrayList<CurrencyRate>
        private ArrayList<CurrencyRate> parseCsvResponseAsCurrencyRates(String url){
            ArrayList<CurrencyRate> mCurrencies = new ArrayList<>();
            InputStream is = null;

            // Make a HTTP request on a given URL and retrieve it's response as a input stream
            try {
                // HTTP POST request
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                // HTTP POST response
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (Exception e) {
                Log.e(ERROR_TAG, "error in http connection: " + e.toString());
            }

            // Convert CSV response to ArrayList<CurrencyRatio>
            if (is != null) {
                try {
                    // Convert response to string
                    CSVReader reader = new CSVReader(
                            new BufferedReader(
                                    new InputStreamReader(is, "Windows-1257")));
                    String line[];
                    // Parse every line of response
                    while ((line = reader.readNext()) != null) {
                        // Use string items as CurrencyRate parameters
                        mCurrencies.add(new CurrencyRate(
                                line[0],
                                line[1],
                                Integer.valueOf(line[2]),
                                Float.valueOf(line[3])));
                    }
                } catch (Exception e) {
                    Log.e(ERROR_TAG, "error converting result: " + e.toString());
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return mCurrencies;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //handle internet connection, if no network available
        NetworkState.handleIfNoNetworkAvailable(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        // Retrieve callbacks of parent activity
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Reset the active callbacks to default
        mCallbacks = null;
    }

}

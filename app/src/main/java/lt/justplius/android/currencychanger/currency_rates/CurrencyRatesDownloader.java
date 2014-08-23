package lt.justplius.android.currencychanger.currency_rates;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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

// Implementation of AsyncTask used to download CSV feed from lb.lt and return
// ArrayList<CurrencyRate>. With given ArrayList<CurrencyRate> prepare ListView
// adapter and set it to ListView.
public class CurrencyRatesDownloader {

    private static final String ERROR_TAG = "DownloadCurrencyRatesTask.java: ";

    private Context mContext;

    public CurrencyRatesDownloader(Context context) {
        mContext = context;
        Calendar calendar = Calendar.getInstance();
        // Format a String to URL with current date
        String url = mContext.getString(
                R.string.url_currency_rates,
                String.valueOf(calendar.get(Calendar.YEAR)),
                String.valueOf(calendar.get(Calendar.MONTH)),
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        // Commit asynchronous download of Currency Rates
        new DownloadCurrencyRatesTask().execute(url);
    }

    private class DownloadCurrencyRatesTask extends AsyncTask<String, Void, ArrayList<CurrencyRate>> {

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
                // Add Lithuanian litas as a currency, so that we could use it later
                items.add(new CurrencyRate("Lietuvos litas", "LTL", 1, 1));
                ((BaseActivity) mContext).setCurrencyRates(items);
            } else {
                // In case when no results are returned inform user
                Toast.makeText(mContext, mContext.getResources().
                        getString(R.string.no_results), Toast.LENGTH_LONG).show();
            }
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
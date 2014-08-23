package lt.justplius.android.currencychanger.history;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import lt.justplius.android.currencychanger.R;
import lt.justplius.android.currencychanger.common.FileReaderWriter;
import lt.justplius.android.currencychanger.common.NetworkState;

/**
 * This class displays a list of HistoryItems, retrieved from saved file
 */
public class HistoryFragment extends ListFragment {
    private static final String ERROR_TAG = "HistoryFragment.java: ";

    // Used to access file name
    public static final String HISTORY_ITEMS_FILE = "HistoryItemsList.txt";

    // ArrayList of HistoryItems
    ArrayList<HistoryItem> mHistoryItems;

    // Mandatory empty constructor for the fragment manager to instantiate the
    // fragment (e.g. upon screen orientation changes).
    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get read history items, if they are not present;

        // Google JSON's serializer for object's serialization to JSON string
        // and vice-versa
        Gson gson = new Gson();

        // Retrieve ArrayList<HistoryItems> form file
        FileReaderWriter fileReaderWriter
                = new FileReaderWriter(getActivity(), HISTORY_ITEMS_FILE);
        String jsonString = fileReaderWriter.readFromFile();
        // Deserialize Json string to particular ArrayList<HistoryItem> object
        Type collectionType = new TypeToken<ArrayList<HistoryItem>>() {
        }.getType();
        mHistoryItems = gson.fromJson(jsonString, collectionType);

        if (mHistoryItems != null) {
            // Reverse ArrayList so that newest would be displayed at top
            Collections.reverse(mHistoryItems);

            // Set a adapter to ListView, to pair view items with their corresponding data
            HistoryItemsListViewAdapter adapter =
                    new HistoryItemsListViewAdapter(getActivity(), mHistoryItems, false);
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(), R.string.nothing_to_display, Toast.LENGTH_LONG).show();
        }

        // Save fragment instance on screen rotations
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        //handle internet connection, if no network available
        NetworkState.handleIfNoNetworkAvailable(getActivity());
    }
}

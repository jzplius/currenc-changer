package lt.justplius.android.currencychanger.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import lt.justplius.android.currencychanger.R;

/**
 * A list fragment representing a list of MenuItems.
 * This fragment supports tablet devices by allowing list items
 * to be given an 'activated' state upon selection.
 * Activities containing this fragment must implement the {@link Callbacks}
 * interface.
 */
public class MenuItemsFragment extends ListFragment {

    // Bundle key representing the activated item position.
    // The current activated item position. Only used on tablets.
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private int mActivatedPosition = 0;

    //The fragment's current callback object, which is notified of list item clicks
    private Callbacks mCallbacks = null;

    // A callback interface that all activities containing this fragment must
    // implement. This mechanism allows activities to be notified of MenuItem selections.
    public interface Callbacks {
        /// Callback for when an item has been selected.
        public void onMenuItemSelected(int position);
    }

    //Mandatory empty constructor
    public MenuItemsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Populate ListView with MenuItems
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(1, getString(R.string.menu_currencies_rates)));
        menuItems.add(new MenuItem(2, getString(R.string.menu_currency_exchange)));
        menuItems.add(new MenuItem(3, getString(R.string.menu_currency_history)));
        setListAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                menuItems));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setActivateOnItemClick(true);
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onMenuItemSelected(position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    // Turns on activate-on-click mode. When this mode is on, list items will be
    // given the 'activated' state when touched.
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    // Set either selected menu position or deselect it at all
    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
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

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface.
        mCallbacks = null;
    }

    // Simple MenuItem class, that ecapsulates menu
    class MenuItem {
        public int mId;
        public String mName;

        public MenuItem(int id, String name) {
            mId = id;
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }
}


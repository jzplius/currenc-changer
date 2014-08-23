package lt.justplius.android.currencychanger.currency_exchange;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import lt.justplius.android.currencychanger.BaseActivity;
import lt.justplius.android.currencychanger.currency_rates.CurrencyRate;
import lt.justplius.android.currencychanger.currency_rates.CurrencyRatesListViewAdapter;
import lt.justplius.android.currencychanger.R;


/**
 * This class provides popup list of CurrencyRates
 * Use the {@link CurrencyPickerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 * It implements {@link AdapterView.OnItemClickListener}, used to provide functionality
 * needed for onItemClick() event (picking currency from list).
 * Fragments containing this fragment must implement the {@link Callbacks}
 * interface.
 */

public class CurrencyPickerDialogFragment extends DialogFragment
        implements AdapterView.OnItemClickListener {

    // References to view objects
    private ListView mListView;

    // Mandatory constructor without parameters
    public CurrencyPickerDialogFragment() {

    }

    // A callback interface that all fragment containing this fragment must
    // implement. This mechanism allows parent fragments to be notified of item
    // selections.
    public interface Callbacks {
        // Callback for CurrencyRate item selected from ListView.
        public void onCurrencySelected(int position);
    }
    // The fragment's current callback object, which is notified of list item clicks
    private Callbacks mCallbacks = null;

    // Get a this fragment instance with targetFragment() set to parent fragment
    public static CurrencyPickerDialogFragment newInstance(Callbacks listener){
        CurrencyPickerDialogFragment f = new CurrencyPickerDialogFragment();
        f.setTargetFragment((Fragment) listener, /*requestCode*/ 1234);
        return f;
    }
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Save fragment instance on screen rotations
        setRetainInstance(true);

        // Get the LayoutInflater
        LayoutInflater li = (LayoutInflater)
                getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        // Inflate our custom layout for the dialog to a View
        View view = li.inflate(R.layout.list_view, null);
        // Retrieve reference to ListView
        mListView = (ListView) view.findViewById(R.id.listView);
        mListView.setOnItemClickListener(this);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inform the dialog it has a custom View
        builder.setView(view);
        builder.setMessage(R.string.select_currency)
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dismiss();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get ArrayList of CurrencyRate from parent Activity
        ArrayList<CurrencyRate> currencyRates = ((BaseActivity) getActivity()).getCurrencyRates();
        // Set create and set adapter for ListView
        CurrencyRatesListViewAdapter adapter =
                new CurrencyRatesListViewAdapter(getActivity(), currencyRates, true);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Pass a selected item position to callback method
        mCallbacks.onCurrencySelected(position);
        getDialog().dismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        CurrencyExchangeFragment parentFragment = (CurrencyExchangeFragment) getTargetFragment();

        // Fragments containing this fragment must implement its callbacks.
        if (!(parentFragment instanceof Callbacks)) {
            throw new IllegalStateException("Parent fragment must implement fragment's callbacks.");
        }
        // Retrieve callbacks of parent Fragment
        mCallbacks = ((Callbacks) parentFragment);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Reset the active callbacks to default
        mCallbacks = null;
    }

    @Override
    public void onDestroyView()
    {
        /*// Work around bug: http://code.google.com/p/android/issues/detail?id=17423
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);*/
        super.onDestroyView();
    }

}

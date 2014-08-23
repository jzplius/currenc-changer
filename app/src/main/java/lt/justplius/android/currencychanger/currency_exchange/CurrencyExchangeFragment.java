package lt.justplius.android.currencychanger.currency_exchange;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import lt.justplius.android.currencychanger.R;
import lt.justplius.android.currencychanger.common.FileReaderWriter;
import lt.justplius.android.currencychanger.common.NetworkState;
import lt.justplius.android.currencychanger.currency_rates.CurrencyRate;
import lt.justplius.android.currencychanger.history.HistoryFragment;
import lt.justplius.android.currencychanger.history.HistoryItem;

/**
 * This class handles fragment currency exchange.
 * Use the {@link CurrencyExchangeFragment#newInstance} method to
 * create an instance of this fragment.
 * It implements {@link CurrencyPickerDialogFragment.Callbacks}, used to provide functionality
 * needed for onCurrencySelected event (picking currency from list).
 * For handling currency rates values between two currencies it heavily uses
 * a static {@link CurrencyConverter} class,
 * which provides all needed functionality.
 */
public class CurrencyExchangeFragment extends Fragment
        implements CurrencyPickerDialogFragment.Callbacks{
    // The fragment initialization parameters, indicating currencies
    // positions from ArrayList<CurrencyRate>
    private static final String ARG_POSITION_FROM = "position_from";
    private static final String ARG_POSITION_TO = "position_to";
    private static final String ARG_CURRENCY_RATES = "currency_rates";
    private int mPositionFrom;
    private int mPositionTo;

    // ArrayList of CurrencyRates, retrieved via Callback of
    // of parent Activity
    private ArrayList<CurrencyRate> mCurrencyRates;
    // ArrayList of HistoryItems, which is appended with new item
    // on fragment save handler
    ArrayList<HistoryItem> mHistoryItems;

    // Google JSON's serializer for object's serialization
    // to JSON string and vice-versa
    private Gson mGson;

    // Indicator whether 'from' or 'to' item was changed
    private boolean mIsFromChanged;

    // Indicates whether EditText is currently being handled
    // by TextWatcher. Used to prevent recurring handling
    // when it is already being handled.
    private boolean mIsEditTextBeingHandled = false;

    // View object's references
    private ImageView mImageViewFlagFrom;
    private ImageView mImageViewFlagTo;
    private EditText mEditTextQuantityFrom;
    private EditText mEditTextQuantityTo;
    private TextView mTextViewCodeFrom;
    private TextView mTextViewCodeTo;
    private TextView mTextViewNameFrom;
    private TextView mTextViewNameTo;
    private RelativeLayout mRelativeLayoutFrom;
    private RelativeLayout mRelativeLayoutTo;
    private ImageView mImageViewSwapCurrencies;
    private InputMethodManager mInputMethodManager;
    private TextView mTextViewLastCheckDate;

    /**
     * Factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param positionFrom indicates "from" currency
     * position at ArrayList<CurrencyRate>.
     * @param positionTo indicates "to" currency
     * position at ArrayList<CurrencyRate>.
     * @param currencyRates JSON string representation of ArrayList<CurrencyRate>.
     * @return A new instance of Fragment CurrencyExchangeFragment.
     */
    public static CurrencyExchangeFragment newInstance(
            int positionFrom,
            int positionTo,
            ArrayList<CurrencyRate> currencyRates) {
        CurrencyExchangeFragment fragment = new CurrencyExchangeFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION_FROM, positionFrom);
        args.putInt(ARG_POSITION_TO, positionTo);
        args.putString(ARG_CURRENCY_RATES, new Gson().toJson(currencyRates));
        fragment.setArguments(args);
        return fragment;
    }

    // Required empty public constructor
    public CurrencyExchangeFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate initial parameters for this class and CurrencyConverter
        mGson = new Gson();

        // Called on screen rotations
        if (savedInstanceState != null) {
            // The fragment saved state parameters, indicating currencies
            // positions from ArrayList<CurrencyRate>
            mPositionFrom = savedInstanceState.getInt(ARG_POSITION_FROM);
            mPositionTo = savedInstanceState.getInt(ARG_POSITION_TO);
            // Deserialize Json string to particular ArrayList<TrueFalse> object
            String jsonString = savedInstanceState.getString(ARG_CURRENCY_RATES);
            Type collectionType = new TypeToken<ArrayList<CurrencyRate>>() {
            }.getType();
            mCurrencyRates = mGson.fromJson(jsonString, collectionType);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_currency_exchange, container, false);

        mImageViewFlagFrom = (ImageView) v.findViewById(R.id.image_view_flag_from);
        mImageViewFlagTo = (ImageView) v.findViewById(R.id.image_view_flag_to);

        mTextViewCodeFrom = (TextView) v.findViewById(R.id.text_view_currency_code_from);
        mTextViewCodeTo = (TextView) v.findViewById(R.id.text_view_currency_code_to);
        mTextViewNameFrom = (TextView) v.findViewById(R.id.text_view_currency_name_from);
        mTextViewNameTo = (TextView) v.findViewById(R.id.text_view_currency_name_to);
        mTextViewLastCheckDate = (TextView) v.findViewById(R.id.text_view_last_check_date);

        mRelativeLayoutFrom
                = (RelativeLayout) v.findViewById(R.id.relative_layout_from_currency);
        mRelativeLayoutTo
                = (RelativeLayout) v.findViewById(R.id.relative_layout_to_currency);

        mEditTextQuantityFrom = (EditText) v.findViewById(R.id.edit_text_quantity_from);
        mEditTextQuantityTo = (EditText) v.findViewById(R.id.edit_text_quantity_to);

        mImageViewSwapCurrencies
                = (ImageView) v.findViewById(R.id.image_view_swap_currencies);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Called first time when fragment is created
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            // The fragment initialization parameters, indicating currencies
            // positions from ArrayList<CurrencyRate>
            mPositionFrom = args.getInt(ARG_POSITION_FROM);
            mPositionTo = args.getInt(ARG_POSITION_TO);
            // Deserialize Json string to particular ArrayList<TrueFalse> object
            String jsonString = args.getString(ARG_CURRENCY_RATES);
            Type collectionType = new TypeToken<ArrayList<CurrencyRate>>() {
            }.getType();
            mCurrencyRates = mGson.fromJson(jsonString, collectionType);
        }

        // Instantiate CurrencyConverter with initial values
        // Currency rate with Litas
        CurrencyConverter.setRateWithLitasFrom(mCurrencyRates.get(mPositionFrom).getExchangeRate());
        CurrencyConverter.setRateWithLitasTo(mCurrencyRates.get(mPositionTo).getExchangeRate());
        // Currency quantity
        CurrencyConverter.setQuantityFrom(mCurrencyRates.get(mPositionFrom).getQuantity());
        CurrencyConverter.setQuantityTo(mCurrencyRates.get(mPositionTo).getQuantity());
        // Count "buying powers" expressed as Litas of ValueFrom and ValueTo
        CurrencyConverter.setMultiplier(1);
        CurrencyConverter.countValues();

        // Set values and handlers to View objects

        mImageViewFlagFrom.setImageDrawable(getActivity().getResources().getDrawable(
                mCurrencyRates.get(mPositionFrom).getCountryFlagResourceId()
        ));
        mImageViewFlagTo.setImageDrawable(getActivity().getResources().getDrawable(
                mCurrencyRates.get(mPositionTo).getCountryFlagResourceId()
        ));

        mTextViewCodeFrom.setText(mCurrencyRates.get(mPositionFrom).getCurrencyCode());
        mTextViewCodeTo.setText(mCurrencyRates.get(mPositionTo).getCurrencyCode());
        mTextViewNameFrom.setText(mCurrencyRates.get(mPositionFrom).getCurrencyName());
        mTextViewNameTo.setText(mCurrencyRates.get(mPositionTo).getCurrencyName());
        Calendar calendar = Calendar.getInstance();
        mTextViewLastCheckDate.setText(getString(
                R.string.text_view_exhange_date,
                calendar.get(Calendar.YEAR),
                getMonthName(calendar.get(Calendar.MONTH)),
                calendar.get(Calendar.DAY_OF_MONTH)));

        // Set a on click event, which provides a list of currencies
        mRelativeLayoutFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates a new instance of CurrencyPickerFragment, pairs it with parent fragment
                // via targetFragment() and shows a DialogFragment with currencies list
                showCurrencyPickerDialogFragment(true);
            }
        });
        // Set a on click event, which provides a list of currencies
        mRelativeLayoutTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates a new instance of CurrencyPickerFragment, pairs it with parent fragment
                // via targetFragment() and shows a DialogFragment with currencies list
                showCurrencyPickerDialogFragment(false);
            }
        });

        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        mEditTextQuantityFrom.setText(String.valueOf("1.0"));
        // Add TextChanged Listener
        mEditTextQuantityFrom.addTextChangedListener(new TextWatcherClass(true));
        // Force keyboard to be hidden
        mEditTextQuantityFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        mEditTextQuantityTo.setText(String.valueOf(CurrencyConverter.getValueTo()));
        // Add TextChanged Listener
        mEditTextQuantityTo.addTextChangedListener(new TextWatcherClass(false));
        // Force keyboard to be hidden
        mEditTextQuantityTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        mImageViewSwapCurrencies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsFromChanged) {
                    mEditTextQuantityFrom.setText(mEditTextQuantityTo.getText());
                } else {
                    mEditTextQuantityTo.setText(mEditTextQuantityFrom.getText());
                }
                mIsFromChanged = !mIsFromChanged;
            }
        });
    }

    // Creates a new instance of CurrencyPickerDialogFragment, pairs it with parent fragment
    // via targetFragment() and shows a DialogFragment with currencies list
    private void showCurrencyPickerDialogFragment(boolean isFromChanged) {
        // Indicator whether 'from' or 'to' was changed
        mIsFromChanged = isFromChanged;

        // Get a CurrencyPickerFragment instance with targetFragment() set to this fragment
        CurrencyPickerDialogFragment dialog
                = CurrencyPickerDialogFragment.newInstance(this);
        dialog.show(
                getActivity().getSupportFragmentManager(), "CurrencyPickerDialogFragment");
    }

    // afterTextChanged() event handler
    private class TextWatcherClass implements TextWatcher {
        // Indicator whether 'from' or 'to' was changed
        private boolean mIsFrom;

        public TextWatcherClass (boolean isFromChanged) {
            mIsFrom = isFromChanged;
        }

        // Simply overriding
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        // Simply overriding
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        // React to text changed event by counting currency ratio
        @Override
        public void afterTextChanged(Editable s) {
            // Indicates whether EditText is currently being handled
            // by TextWatcher. Used to prevent recurring handling
            // when it is already being handled.
            if (!mIsEditTextBeingHandled) {
                mIsEditTextBeingHandled = !mIsEditTextBeingHandled;

                String string = s.toString();

                // If it is double value exchange currencies quantities
                if (CurrencyConverter.isDouble(string)) {

                    double doubleValue = CurrencyConverter.getDouble(string);
                    if (doubleValue > 0) {
                        CurrencyConverter.setMultiplier(doubleValue);
                        // Indicator whether 'from' or 'to' was changed
                        if (mIsFrom) {
                            mEditTextQuantityTo.setText(
                                    String.valueOf(CurrencyConverter.getValueTo()));
                        } else {
                            mEditTextQuantityFrom.setText(
                                    String.valueOf(CurrencyConverter.getValueFrom()));
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                R.string.negative_number,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(
                            getActivity(),
                            R.string.wrong_number,
                            Toast.LENGTH_LONG).show();
                }
                mIsEditTextBeingHandled = !mIsEditTextBeingHandled;
            }
        }
    }

    /**
     * Callback method from {@link CurrencyPickerDialogFragment.Callbacks}
     * indicating position of the currency, that was selected.
     */
    @Override
    public void onCurrencySelected(int position) {

        CurrencyConverter.setMultiplier(1);
        // Update CurrencyConverter values and view values
        // Indicator whether 'from' or 'to' was changed
        if (mIsFromChanged) {
            mPositionFrom = position;

            CurrencyConverter.setRateWithLitasFrom(mCurrencyRates.get(mPositionFrom).getExchangeRate());
            CurrencyConverter.setQuantityFrom(mCurrencyRates.get(mPositionFrom).getQuantity());

            mImageViewFlagFrom.setImageDrawable(getActivity().getResources().getDrawable(
                    mCurrencyRates.get(mPositionFrom).getCountryFlagResourceId()));
            mTextViewCodeFrom.setText(mCurrencyRates.get(mPositionFrom).getCurrencyCode());
            mTextViewNameFrom.setText(mCurrencyRates.get(mPositionFrom).getCurrencyName());
            CurrencyConverter.countValues();
            mEditTextQuantityFrom.setText("1.0");
            // Force keyboard to be hidden
            mInputMethodManager.hideSoftInputFromWindow(mEditTextQuantityFrom.getWindowToken(), 0);

        } else {
            mPositionTo = position;

            CurrencyConverter.setRateWithLitasTo(mCurrencyRates.get(mPositionTo).getExchangeRate());
            CurrencyConverter.setQuantityTo(mCurrencyRates.get(mPositionTo).getQuantity());

            mImageViewFlagTo.setImageDrawable(getActivity().getResources().getDrawable(
                    mCurrencyRates.get(mPositionTo).getCountryFlagResourceId()));
            mTextViewCodeTo.setText(mCurrencyRates.get(mPositionTo).getCurrencyCode());
            mTextViewNameTo.setText(mCurrencyRates.get(mPositionTo).getCurrencyName());
            CurrencyConverter.countValues();
            mEditTextQuantityTo.setText("1.0");
            // Force keyboard to be hidden
            mInputMethodManager.hideSoftInputFromWindow(mEditTextQuantityTo.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Save a unique HistoryItem to file, so that we could see it in history
        HistoryItem historyItem = formatHistoryItem();

        // Add a HistoryItem to file. Before adding it we check if last HistoryItem
        // of ArrayList<HistoryItem> is not equal to being added one. If they are equal
        // then we skip this item (it is already inserted).
        updateListOfHistoryItems(historyItem);
    }

    @Override
    public void onResume() {
        super.onResume();

        //handle internet connection, if no network available
        NetworkState.handleIfNoNetworkAvailable(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_POSITION_FROM, mPositionFrom);
        outState.putInt(ARG_POSITION_TO, mPositionTo);
        outState.putString(ARG_CURRENCY_RATES, mGson.toJson(mCurrencyRates));
    }

    // Add a HistoryItem to file. Before adding it we check if last HistoryItem
    // of ArrayList<HistoryItem> is not equal to being added one. If they are equal
    // then we skip this item (it is already inserted).
    private void updateListOfHistoryItems(HistoryItem item) {
        // If HistoryItem is found in ArrayList<HistoryItem>
        boolean isFound = false;
        ArrayList<HistoryItem> historyItems = null;

        // File to JSON string
        FileReaderWriter fileReaderWriter
                = new FileReaderWriter(getActivity(), HistoryFragment.HISTORY_ITEMS_FILE);
        String jsonString = fileReaderWriter.readFromFile();

        // JSON string to ArrayList<HistoryItem>
        if (jsonString != "") {
            // Deserialize Json string to particular ArrayList<HistoryItem> object
            Type collectionType = new TypeToken<ArrayList<HistoryItem>>() {
            }.getType();
            historyItems = mGson.fromJson(jsonString, collectionType);

            // Does ArrayList contain HistoryItem
            if (historyItems.get(historyItems.size()-1).equals(item)) {
                isFound = true;
            }
        }

        // If it is new HistoryItem add it to ArrayList and save to file
        if (!isFound) {
            if (historyItems == null) {
                historyItems = new ArrayList<>();
            }
            historyItems.add(item);

            // ArrayList<HistoryItem> to JSON string
            jsonString = mGson.toJson(historyItems);

            // JSON string to File
            fileReaderWriter.writeToFile(jsonString);
        }
    }

    private HistoryItem formatHistoryItem() {
        // Construct HistoryItem
        Calendar calendar = Calendar.getInstance();
        HistoryItem historyItem = new HistoryItem();

        // Pass image drawables references
        historyItem.setImageResourceIdFrom(mCurrencyRates
                .get(mPositionFrom).getCountryFlagResourceId());
        historyItem.setImageResourceIdTo(mCurrencyRates
                .get(mPositionTo).getCountryFlagResourceId());

        // Format exchange rate string
        String exchangeRate = getString(
                R.string.text_view_currency_exchange_rate,
                mEditTextQuantityFrom.getText(),
                mCurrencyRates.get(mPositionFrom).getCurrencyCode(),
                mEditTextQuantityTo.getText(),
                mCurrencyRates.get(mPositionTo).getCurrencyCode()
        );
        historyItem.setExchangeRate(exchangeRate);

        // Format exchange date string
        String exchangeDate = getString(
                R.string.text_view_exhange_date,
                calendar.get(Calendar.YEAR),
                getMonthName(calendar.get(Calendar.MONTH)),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        historyItem.setExchangeDate(exchangeDate);

        return historyItem;
    }

    // Return month name representation in text,
    // i.e. return "March" instead of "2"
    public String getMonthName(int monthInt){
        String month = "";
        switch (monthInt){
            case 0:
                month = getString(R.string.month_1_ltu);
                break;
            case 1:
                month = getString(R.string.month_2_ltu);
                break;
            case 2:
                month = getString(R.string.month_3_ltu);
                break;
            case 3:
                month = getString(R.string.month_4_ltu);
                break;
            case 4:
                month = getString(R.string.month_5_ltu);
                break;
            case 5:
                month = getString(R.string.month_6_ltu);
                break;
            case 6:
                month = getString(R.string.month_7_ltu);
                break;
            case 7:
                month = getString(R.string.month_8_ltu);
                break;
            case 8:
                month = getString(R.string.month_9_ltu);
                break;
            case 9:
                month = getString(R.string.month_10_ltu);
                break;
            case 10:
                month = getString(R.string.month_11_ltu);
                break;
            case 11:
                month = getString(R.string.month_12_ltu);
                break;
        }
        return month;
    }
}

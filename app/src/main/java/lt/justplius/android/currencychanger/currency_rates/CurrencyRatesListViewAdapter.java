package lt.justplius.android.currencychanger.currency_rates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lt.justplius.android.currencychanger.R;

/**
 * This adapter links single ListView item with CurrencyRate.
 * ListView item is returned on getView(int, View, ViewGroup) method.
 */
public class CurrencyRatesListViewAdapter extends ArrayAdapter<CurrencyRate> {

    // Contains all CurrencyRates
    private ArrayList<CurrencyRate> mAllItems;
    private Context mContext;
    private boolean mIsSimplified = false;

    public CurrencyRatesListViewAdapter(Context context, ArrayList<CurrencyRate> allItems, boolean isSimplified) {
        super(context, R.layout.activity_item_detail);
        // Get object's references
        this.mAllItems = allItems;
        mContext = context;
        // If it is simplified (used for currency picker), pass
        // currency code instead of currency exchange rate
        mIsSimplified = isSimplified;
    }

    // Returns single ListView item (list_view_item_currency.xml) populated
    // with CurrencyRate information.
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // View holder for single ListView item
        final ViewHolder holder;

        // First time initialize ViewHolder, other times retrieve saved one
        if (convertView == null) {
            holder = new ViewHolder();
            // Inflate a "list_view_item_currency.xml" layout into ListView's item
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_view_item_currency, parent, false);
            // Retrieve references to views
             holder.imageViewFlag = (ImageView)
                     convertView.findViewById(R.id.image_view_flag_to);
             holder.textViewCurrencyExchangeRate = (TextView)
                    convertView.findViewById(R.id.text_view_currency_code_to);
             holder.textViewCurrencyName = (TextView)
                    convertView.findViewById(R.id.text_view_currency_name_to);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set flag on ImageView based on drawable resource ID
        holder.imageViewFlag.setImageDrawable(
                mContext.getResources().getDrawable(
                        mAllItems.get(position).getCountryFlagResourceId()));
        holder.textViewCurrencyName.setText(mAllItems.get(position).getCurrencyName());
        // If it is simplified (used for currency picker), pass
        // currency code instead of currency exchange rate
        if (mIsSimplified) {
            holder.textViewCurrencyExchangeRate.setText(mAllItems.get(position).getCurrencyCode());
        } else {
            // Format a string, indicating currency exchange rate
            holder.textViewCurrencyExchangeRate.setText(mContext.getString(
                    R.string.text_view_currency_exchange_rate,
                    mAllItems.get(position).getQuantity(),
                    mAllItems.get(position).getCurrencyCode(),
                    mAllItems.get(position).getExchangeRate(),
                    "LTL"));
        }

        return convertView;
    }

    // Static ViewHolder for each row
    static class ViewHolder {
        ImageView imageViewFlag;
        TextView textViewCurrencyExchangeRate;
        TextView textViewCurrencyName;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mAllItems.size();
    }

    @Override
    public CurrencyRate getItem(int position) {
        return mAllItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(CurrencyRate item) {
        return mAllItems.indexOf(item);
    }
}

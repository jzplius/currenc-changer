package lt.justplius.android.currencychanger.history;

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
 * This adapter links single ListView item with HistoryItem.
 * ListView item is returned on getView(int, View, ViewGroup) method.
 */
public class HistoryItemsListViewAdapter extends ArrayAdapter<HistoryItem> {

    // Contains all HistoryItems
    private ArrayList<HistoryItem> mAllItems;
    private Context mContext;

    public HistoryItemsListViewAdapter(Context context, ArrayList<HistoryItem> allItems, boolean isSimplified) {
        super(context, R.layout.activity_item_detail);
        // Get object's references
        this.mAllItems = allItems;
        mContext = context;
    }

    // Returns single ListView item (list_view_item_currency.xml) populated
    // with HistoryItem information.
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // View holder for single ListView item
        final ViewHolder holder;

        // First time initialize ViewHolder, other times retrieve saved one
        if (convertView == null) {
            holder = new ViewHolder();
            // Inflate a "list_view_item_history.xml" layout into ListView's item
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_view_item_history, parent, false);
            // Retrieve references to views
             holder.textViewExchangeRate = (TextView)
                     convertView.findViewById(R.id.text_view_exchange);
             holder.imageViewFlagFrom = (ImageView)
                    convertView.findViewById(R.id.image_view_flag_from);
            holder.imageViewFlagTo = (ImageView)
                    convertView.findViewById(R.id.image_view_flag_to);
            holder.textViewExchangeDate = (TextView)
                    convertView.findViewById(R.id.text_view_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.textViewExchangeRate.setText(mAllItems.get(position).getExchangeRate());
        // Set flags on ImageView based on drawable resources ID
        holder.imageViewFlagFrom.setImageDrawable(
                mContext.getResources().getDrawable(
                        mAllItems.get(position).getImageResourceIdFrom()));
        // Set flags on ImageView based on drawable resources ID
        holder.imageViewFlagTo.setImageDrawable(
                mContext.getResources().getDrawable(
                        mAllItems.get(position).getImageResourceIdTo()));
        holder.textViewExchangeDate.setText(mAllItems.get(position).getExchangeDate());

        return convertView;
    }

    // Static ViewHolder for each row
    static class ViewHolder {
        TextView textViewExchangeRate;
        ImageView imageViewFlagFrom;
        ImageView imageViewFlagTo;
        TextView textViewExchangeDate;
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
    public HistoryItem getItem(int position) {
        return mAllItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(HistoryItem item) {
        return mAllItems.indexOf(item);
    }
}

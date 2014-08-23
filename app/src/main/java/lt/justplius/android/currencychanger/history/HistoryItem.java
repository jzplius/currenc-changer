package lt.justplius.android.currencychanger.history;

/**
 * This class encapsulates HistoryItem
 */
public class HistoryItem {

    private String mExchangeRate;
    private int mImageResourceIdFrom;
    private int mImageResourceIdTo;
    private String mExchangeDate;

    public HistoryItem() {}

    // Various getters and setters

    public String getExchangeRate() {
        return mExchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        mExchangeRate = exchangeRate;
    }

    public int getImageResourceIdFrom() {
        return mImageResourceIdFrom;
    }

    public void setImageResourceIdFrom(int imageResourceFrom) {
        mImageResourceIdFrom = imageResourceFrom;
    }

    public int getImageResourceIdTo() {
        return mImageResourceIdTo;
    }

    public void setImageResourceIdTo(int imageResourceTo) {
        mImageResourceIdTo = imageResourceTo;
    }

    public String getExchangeDate() {
        return mExchangeDate;
    }

    public void setExchangeDate(String exchangeDate) {
        mExchangeDate = exchangeDate;
    }

    // Customized objects equality check
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof HistoryItem)) {
            return false;
        }

        HistoryItem that = (HistoryItem) other;

        // Custom equality check for each field
        return  this.mExchangeRate.equals(that.mExchangeRate)
                && this.mExchangeDate.equals(that.mExchangeDate)
                && (this.mImageResourceIdFrom == that.mImageResourceIdFrom)
                && (this.mImageResourceIdTo == that.mImageResourceIdTo);
    }

}

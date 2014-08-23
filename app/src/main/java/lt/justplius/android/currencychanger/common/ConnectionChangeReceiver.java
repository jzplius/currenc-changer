package lt.justplius.android.currencychanger.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class responds to internet state changes. When informed about state change it
 * asynchronously executes checkIfDeviceIsConnected(), to check device's new state.
 * While attached to activity checkIfDeviceIsConnected determines network state
 * changes, such as "Network available", "Network is active", "Network is inactive",
 * "No network available".
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    // Handle the network state change event by asynchronously executing checkIfDeviceIsConnected()
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkState.checkIfDeviceIsConnected(context);
    }
}

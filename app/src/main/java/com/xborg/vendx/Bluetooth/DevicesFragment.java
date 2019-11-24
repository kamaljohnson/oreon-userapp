package com.xborg.vendx.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.xborg.vendx.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DevicesFragment extends ListFragment {

    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> listItems = new ArrayList<>();
    private ArrayAdapter<BluetoothDevice> listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        connectDevice();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(bluetoothAdapter == null)
            setEmptyText("<bluetooth not supported>");
        else if(!bluetoothAdapter.isEnabled())
            setEmptyText("<bluetooth is disabled>");
        else
            setEmptyText("<no bluetooth devices found>");
    }

    public void connectDevice() {
        Bundle args = new Bundle();
        args.putString("device", "48:4B:AA:0F:E6:8C");
        Log.e(TAG, "address: " + "48:4B:AA:0F:E6:8C");
        Fragment fragment = new BluetoothFragment();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.bluetooth_fragment, fragment, "terminal").addToBackStack(null).commit();
    }
}

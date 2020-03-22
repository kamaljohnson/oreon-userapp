package com.xborg.vendx.activities.vendingActivity.fragments.deviceScanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xborg.vendx.R;
import com.xborg.vendx.activities.vendingActivity.SharedViewModel;
import com.xborg.vendx.activities.vendingActivity.VendingState;
import com.xborg.vendx.activities.vendingActivity.fragments.deviceCommunicator.DeviceCommunicator;
import com.xborg.vendx.database.Machine;
import com.xborg.vendx.preferences.SharedPreference;


/**
 * show list of BLE devices
 */
public class DeviceScanner extends Fragment {

    private String TAG = "DeviceScanner";

    private enum ScanState {NONE, LESCAN, DISCOVERY, DISCOVERY_FINISHED}

    private ScanState scanState = ScanState.NONE;
    private static final long LESCAN_PERIOD = 10000; // similar to bluetoothAdapter.startDiscovery
    private Handler leScanStopHandler = new Handler();
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BroadcastReceiver discoveryBroadcastReceiver;
    private IntentFilter discoveryIntentFilter;
    private BluetoothAdapter bluetoothAdapter;

    private DeviceScannerViewModel viewModel;
    private SharedViewModel sharedViewModel;

    public DeviceScanner() {
        leScanCallback = (device, rssi, scanRecord) -> {
            if (device != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    updateScan(device);
                });
            }
        };
        discoveryBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getType() != BluetoothDevice.DEVICE_TYPE_CLASSIC && getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateScan(device));
                    }
                }
                if (intent.getAction().equals((BluetoothAdapter.ACTION_DISCOVERY_FINISHED))) {
                    scanState = ScanState.DISCOVERY_FINISHED; // don't cancel again
                    stopScan();
                }
            }
        };
        discoveryIntentFilter = new IntentFilter();
        discoveryIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_scanner, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(DeviceScannerViewModel.class);

        sharedViewModel.getSelectedMachine().observe(getViewLifecycleOwner(), new Observer<Machine>() {
            @Override
            public void onChanged(Machine machine) {
                sharedViewModel.getDeviceConnectionState().setValue(DeviceScannerState.DeviceInfo);
                Log.i(TAG, "selected machine changed");
            }
        });

        sharedViewModel.getDeviceConnectionState().observe(getViewLifecycleOwner(), new Observer<DeviceScannerState>() {

            @Override
            public void onChanged(DeviceScannerState deviceScannerState) {
                switch (deviceScannerState) {
                    case DeviceInfo:
                        Machine machine = sharedViewModel.getSelectedMachine().getValue();
                        if (machine != null) {
                            Log.i(TAG, "device info loaded");
                            sharedViewModel.getDeviceConnectionState().setValue(DeviceScannerState.ScanMode);
                        }
                        break;
                    case ScanMode:
                        Log.i(TAG, "device scanning mode");
                        break;
                    case DeviceNearby:
                        Log.i(TAG, "selected device is nearby");
                        startScan();
                        break;
                    case DeviceNotNearby:
                        Log.i(TAG, "selected device is not nearby");
                        break;
                    case DeviceIdle:
                        Log.i(TAG, "selected device is idle");
                        loadDeviceCommunicator();
                        break;
                    case DeviceBusy:
                        Log.i(TAG, "selected device is busy");
                        break;
                }
            }
        });
        getSelectedMachineMac();

        getView().findViewById(R.id.retry_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                sharedViewModel.getDeviceConnectionState().setValue(DeviceScannerState.ScanMode);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(discoveryBroadcastReceiver, discoveryIntentFilter);
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth BLE not supported");
        } else if (!bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth BLE is not enabled");
        } else {
            Log.e(TAG, "Bluetooth BLE all clear to go");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScan();
        getActivity().unregisterReceiver(discoveryBroadcastReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void getSelectedMachineMac() {
        Log.i(TAG, "machine selection");
        Machine machine = new Machine();
        SharedPreference preference = new SharedPreference(getContext());
        machine.setMac(preference.getSelectedMachineMac());
        sharedViewModel.getSelectedMachine().setValue(machine);
    }


    @SuppressLint("StaticFieldLeak") // AsyncTask needs reference to this fragment
    private void startScan() {
        Log.i(TAG, "startScan");
        if (scanState != ScanState.NONE)
            return;
        scanState = ScanState.LESCAN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                scanState = ScanState.NONE;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Location permission required");
                builder.setMessage("This app does not use location information, but scanning for Bluetooth LE devices requires this permission");
                builder.setPositiveButton(android.R.string.ok,
                        (dialog, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0));
                builder.show();
                return;
            }
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean locationEnabled = false;
            try {
                locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ignored) {
            }
            try {
                locationEnabled |= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ignored) {
            }
            if (!locationEnabled)
                scanState = ScanState.DISCOVERY;
        }
        if (scanState == ScanState.LESCAN) {
            leScanStopHandler.postDelayed(this::stopScan, LESCAN_PERIOD);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void[] params) {
                    bluetoothAdapter.startLeScan(null, leScanCallback);
                    return null;
                }
            }.execute(); // start async to prevent blocking UI, because startLeScan sometimes take some seconds
        } else {
            bluetoothAdapter.startDiscovery();
        }
    }

    private void updateScan(BluetoothDevice device) {
        Log.i("TEST", "LeScan On Track scanState: " + scanState + " discoveredDevice: " + device.getAddress());

        if (scanState == ScanState.NONE)
            return;
        if (device.getAddress().toUpperCase().equals(sharedViewModel.getSelectedMachine().getValue().getMac().toUpperCase())) {
            Log.i(TAG, "device discovered using ble: " + device.getAddress());
            sharedViewModel.getDeviceConnectionState().setValue(DeviceScannerState.DeviceIdle);
            stopScan();
        }
    }

    private void stopScan() {
        if (scanState == ScanState.NONE)
            return;
        if (scanState == ScanState.LESCAN) {
            leScanStopHandler.removeCallbacks(this::stopScan);
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
        scanState = ScanState.DISCOVERY_FINISHED;
        Log.i(TAG, "before comparision: " + sharedViewModel.getDeviceConnectionState().getValue());
        if (sharedViewModel.getDeviceConnectionState().getValue().compareTo(DeviceScannerState.DeviceIdle) < 0) {
            sharedViewModel.getDeviceConnectionState().setValue(DeviceScannerState.DeviceBusy);
        }
    }

    private void loadDeviceCommunicator() {
        sharedViewModel.getVendingState().setValue(VendingState.DeviceDiscovered);
        Log.i(TAG, "loading device communicator");
        Bundle args = new Bundle();
        args.putString("device", sharedViewModel.getSelectedMachine().getValue().getMac());
        Fragment fragment = new DeviceCommunicator();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.device_connector_fragment_container, fragment, "DeviceCommunicator")
                .addToBackStack(null).commit();
    }
}

package com.xborg.vendx.activities.vendingActivity.fragments.deviceCommunicator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.xborg.vendx.R;
import com.xborg.vendx.activities.vendingActivity.SharedViewModel;
import com.xborg.vendx.activities.vendingActivity.VendingState;
import com.xborg.vendx.activities.vendingActivity.helper.SerialListener;
import com.xborg.vendx.activities.vendingActivity.helper.SerialService;
import com.xborg.vendx.activities.vendingActivity.helper.SerialSocket;

import java.util.Arrays;
import java.util.Objects;


public class DeviceCommunicator extends Fragment implements ServiceConnection, SerialListener {

    private String TAG = "DeviceCommunicator";

    private enum Connected {False, Pending, True}

    private String deviceAddress;

    private SerialSocket socket;
    private SerialService service;
    private static boolean initialStart = true;
    private Connected connected = Connected.False;

    private SharedViewModel sharedViewModel;

    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        if (service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation")
    // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try {
            getActivity().unbindService(this);
        } catch (Exception ignored) {
        }
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(initialStart && service !=null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "onActivityCreated");

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getVendingState().observe(getViewLifecycleOwner(), new Observer<VendingState>() {
            @Override
            public void onChanged(VendingState vendingState) {

                switch (vendingState) {

                    case ConnectionRequest:
                        //ConnectionRequest
                        break;

                    case Connecting:
                        //Connecting...
                        break;

                    case Connected:
                        //Connected, check for OTP from device
                        break;

                    case ReceivedOtp:
                        break;

                    case SendOtpWithBag:
                        break;

                    case Vending:
                        break;

                    case VendingDone:
                        break;

                    case VendingComplete:
                        break;

                    case ReceivedLog:
                        break;

                    case SendLogAck:
                        break;

                    case Error:
                        break;
                }
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        if(initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    /*
     * UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_communicator, container, false);
    }

    /*
     * Serial + UI
     */
    private void connect() {
        try {
            sharedViewModel.getVendingState().setValue(VendingState.Connecting);
            deviceAddress = sharedViewModel.getSelectedMachine().getValue().getMac();
            Log.i(TAG, "deviceAddress : " + deviceAddress);
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            String deviceName = device.getName() != null ? device.getName() : device.getAddress();
            status("connecting...");
            connected = Connected.Pending;
            socket = new SerialSocket();
            service.connect(this, "Connected to " + deviceName);
            socket.connect(getContext(), service, device);
        } catch (Exception e) {
            Log.i(TAG, "Connection Error : " + e);
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
        socket.disconnect();
        socket = null;
    }

    private void send(String str) {
        if (connected != Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            byte[] data = (str).getBytes();
            socket.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(byte[] data) {
        Log.i(TAG, (new String(data)));

        String dataString = data.toString();
        switch (Objects.requireNonNull(sharedViewModel.getVendingState().getValue())) {

            case Connecting:
                //Connecting...
                break;

            case Connected:
                switch (dataString) {
                    //TODO: check other possibilities
                    default:
                        Log.i(TAG, "OTP: " + dataString);
                        sharedViewModel.getVendingState().setValue(VendingState.ReceivedOtp);
                        break;
                }
                break;

            case ReceivedOtp:
                break;

            case SendOtpWithBag:
                break;

            case Vending:
                break;

            case VendingDone:
                break;

            case VendingComplete:
                break;

            case ReceivedLog:
                break;

            case SendLogAck:
                break;

            case Error:
                break;
        }
    }

    private void status(String str) {
        Log.i(TAG, str);
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        status("connected");
        connected = Connected.True;
        sharedViewModel.getVendingState().setValue(VendingState.Connected);
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        Log.i("Debug", "read test : " + new String(data));
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }
}

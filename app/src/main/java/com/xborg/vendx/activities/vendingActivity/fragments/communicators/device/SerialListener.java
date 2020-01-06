package com.xborg.vendx.activities.vendingActivity.fragments.communicators.device;

interface SerialListener {
    void onSerialConnect();
    void onSerialConnectError (Exception e);
    void onSerialRead(byte[] data);
    void onSerialIoError      (Exception e);
}

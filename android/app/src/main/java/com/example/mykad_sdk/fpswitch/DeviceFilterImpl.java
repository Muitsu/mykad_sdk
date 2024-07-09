package com.example.mykad_sdk.fpswitch;

import android.hardware.usb.UsbDevice;
import android.util.Log;

import com.securemetric.reader.myid.MyIDDeviceFilter;

import java.util.Locale;
import java.util.Objects;

public class DeviceFilterImpl extends MyIDDeviceFilter {
    private static final String TAG = "MyIDApp_DeviceFilter";

    @Override
    public void onDeviceFound(Object device, DeviceType type) {
        if (Objects.requireNonNull(type) == DeviceType.USB) {
            UsbDevice usbDevice = (UsbDevice) device;
            Log.i(TAG, String.format(Locale.getDefault(), "USB device found (%s).", usbDevice.getDeviceName()));
            addDevice(device);
        }
    }
}

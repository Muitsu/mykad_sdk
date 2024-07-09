package com.example.mykad_sdk;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.mykad_sdk.fpswitch.DeviceInfo;
import com.example.mykad_sdk.fpswitch.DeviceType;
import com.example.mykad_sdk.fpswitch.V11FPUsbSwitch;
import com.securemetric.reader.myid.readers.FPScanner;
import com.securemetric.reader.myid.readers.FPScannerFactory;
import com.securemetric.reader.myid.readers.MorphoFP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FingerPrintManager {
    private static final String TAG = "MyKAD_FP_SDK";
    private static final String ACTION_USB_PERMISSION = "com.securemetric.myidreader.action.USB_PERMISSION";
    boolean isTurnedOn = false;
    private final AtomicReference<FPScanner> mSelectedFpScanner = new AtomicReference<>(null);
    V11FPUsbSwitch v11FPUsbSwitch = V11FPUsbSwitch.getInstance();
    public UsbManager mUsbManager;
    public PendingIntent mPermissionIntent;
    private final List<FPScanner> listOfFpScanner = new ArrayList<>();

    // Private constructor to prevent instantiation
    private FingerPrintManager() {
    }

    // Static inner class - inner classes are not loaded until they are referenced.
    private static class SingletonHelper {
        private static final FingerPrintManager INSTANCE = new FingerPrintManager();
    }

    // Global point of access
    public static FingerPrintManager getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private final BroadcastReceiver mUsbReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            } else if (action.equals(ACTION_USB_PERMISSION)) {
                UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (usbDevice == null)
                    return;
                setUSBScanner(usbDevice, false, context);
            } else if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (usbDevice == null)
                    return;
//                addDeviceToList(usbDevice, context);
                Log.d("YEAAAY", usbDevice.toString());
            }
        }
    };
    private BroadcastReceiver mUsbRemove = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null || !action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED))
                return;

            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (usbDevice == null)
                return;

//            removeDeviceFromList(usbDevice);
        }
    };


    private void setUSBScanner(UsbDevice device, boolean requestPermission, Context context) {
        if (mUsbManager.hasPermission(device))
            setSelectedScanner(FPScannerFactory.get(context, device));

        if (requestPermission)
            mUsbManager.requestPermission(device, mPermissionIntent);
        else
            Log.w(TAG, "No permission given ,device: " + device.getDeviceName());
    }

    private void setSelectedScanner(FPScanner scanner) {
        mSelectedFpScanner.getAndSet(scanner);
    }

    public void initFPConnector(Context context) {
        int pendingIntentFlag = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            pendingIntentFlag = PendingIntent.FLAG_MUTABLE;
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), pendingIntentFlag);
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (mUsbManager == null)
            Log.w("YEAAAY", "Invalid Usb handler");
        turnOnSwitch(context);
    }

    public List<DeviceInfo> getFPDeviceList(Context context) {
        listOfFpScanner.clear();
        List<Object> deviceList;
        List<DeviceInfo> deviceInfos = new ArrayList<>(); // Initialize with a mutable list
        if (mUsbManager == null)
            return Collections.emptyList();
        Log.d(TAG, "GET LIST OF FP");
        deviceList = new ArrayList<>(mUsbManager.getDeviceList().values());
        for (Object device : deviceList) {
            DeviceInfo devInfo = convertToDeviceInfo(device, context);
            if (devInfo == null)
                continue;
            deviceInfos.add(devInfo);
        }
        return deviceInfos;
    }

    public FPScanner getFPScanner() {
        return mSelectedFpScanner.get();
    }

    private DeviceInfo convertToDeviceInfo(Object device, Context context) {
        FPScanner fpScanner;
        if (device instanceof UsbDevice) {
            fpScanner = FPScannerFactory.get(context, (UsbDevice) device);
            listOfFpScanner.add(fpScanner);
        } else {
            return null;
        }
        if (fpScanner == null) {
            return null;
        }
        DeviceInfo devInfo = new DeviceInfo();
        Object dev = fpScanner.getDeviceObject();
        if (dev instanceof MorphoFP) {
            devInfo.setTitle("Morpho");
        } else if (dev instanceof UsbDevice) {
            devInfo.setDescription(((UsbDevice) device).getDeviceName());
            devInfo.setDeviceType(DeviceType.USB_DEVICE);
        } else {
            return null;
        }
        return devInfo;
    }

    public boolean connectFPDevice(Context context) {
        Log.i(TAG, "connect");
        if (listOfFpScanner.isEmpty()) {
            return false;
        }
        Toast.makeText(context, "Fingerprint scanner connected", Toast.LENGTH_SHORT).show();
        FPScanner scanner = listOfFpScanner.get(0);
        mSelectedFpScanner.getAndSet(scanner);
        return true;
    }

    public boolean disconnectFPDevice() {
        try {
            mSelectedFpScanner.getAndSet(null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void scanFP() {

    }


    private DeviceInfo getDeviceInfo(FPScanner scanner) {
        DeviceInfo deviceInfo = new DeviceInfo();
        Object device = scanner.getDeviceObject();

        if (scanner instanceof MorphoFP)
            deviceInfo.setTitle("Morpho");
        else
            return null;
        if (device instanceof UsbDevice) {
            deviceInfo.setDescription(((UsbDevice) device).getDeviceName());
            deviceInfo.setDeviceType(DeviceType.USB_DEVICE);
        }
        return deviceInfo;
    }

    public void turnOnSwitch(Context context) {
        Log.i(TAG, "turnOnSwitch");
        new Handler(Looper.getMainLooper()).post(() -> {
            v11FPUsbSwitch.setUSBSwitch(true);
        });
        if (mUsbManager != null) {
//            addAllDevices(DeviceRecyclerAdapter.DeviceType.USB_DEVICE);
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            context.registerReceiver(mUsbReceive, filter);
            context.registerReceiver(mUsbRemove, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));
        }
        if (V11FPUsbSwitch.getInstance().isCorrespondingDevice())
            V11FPUsbSwitch.getInstance().start();
    }

    public void turnOffSwitch(Context context) {
        Log.i(TAG, "turnOffSwitch");
        new Handler(Looper.getMainLooper()).post(() -> {
            v11FPUsbSwitch.setUSBSwitch(false);
        });
        try {
            context.unregisterReceiver(mUsbReceive);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "unregister receiver error", e);
        }
        if (V11FPUsbSwitch.getInstance().isPollingStarted())
            V11FPUsbSwitch.getInstance().stop();
    }

    private void addAllDevices(DeviceType deviceType) {
        List<Object> deviceList = Collections.emptyList();
        if (deviceType == DeviceType.BT_DEVICE) {
            //TODO: ADD BT IF NEEDED
        } else {
            if (mUsbManager == null)
                return;

            deviceList = new ArrayList<>(mUsbManager.getDeviceList().values());
        }
        Log.d(TAG, deviceList.size() + "");
    }
}

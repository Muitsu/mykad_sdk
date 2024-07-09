package com.example.mykad_sdk.fpswitch;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class V11FPUsbSwitch {
    public interface OnUsbSwitchExternalListener {
        void onSwitch(boolean isUsbSwitchActivate);
    }

    private static class OnUsbSwitchExternalListenerProxy implements OnUsbSwitchExternalListener {
        private OnUsbSwitchExternalListener listener = null;

        @Override
        public void onSwitch(boolean isUsbSwitchActivate) {
            if (listener == null)
                return;

            new Handler(Looper.getMainLooper()).post(() -> listener.onSwitch(isUsbSwitchActivate));

        }

        public void setListener(OnUsbSwitchExternalListener listener) {
            this.listener = listener;
        }
    }

    private static final String TAG = "MyIDApp_V11FPUsbSwitch";

    private static final V11FPUsbSwitch insstance = new V11FPUsbSwitch();

    private static String usbSwitchFile;

    static {
        if ("V10P".equals(Build.MODEL) || "V11".equals(Build.MODEL)) {
            usbSwitchFile = "/sys/class/usb_switch/phy/usb_host_mode_switch";
        } else if ("V20".equals(Build.MODEL)) {
            usbSwitchFile = "/sys/devices/platform/soc/soc:hid_gpio/hid_usb_hub_power_enable";
        }
    }

    private final AtomicBoolean prevIsActive = new AtomicBoolean(false);
    private boolean isPollingStarted = false;
    private final OnUsbSwitchExternalListenerProxy switchExternalListener = new OnUsbSwitchExternalListenerProxy();

    private final PollingThread usbSwitchCheck = new PollingThread(() -> {
        boolean active = isSwitchActive();

        Log.i(TAG, String.format("USB Switch: %s", active ? "active" : "inactive"));
        // if value is not the same
        if (active ^ prevIsActive.get()) {
            switchExternalListener.onSwitch(active);

            prevIsActive.set(active);
        }
    });

    public void start() {
        if (isPollingStarted)
            stop();

        usbSwitchCheck.startPolling();
        isPollingStarted = true;
    }

    public void stop() {
        isPollingStarted = false;
        usbSwitchCheck.quitPolling();
        prevIsActive.set(false);
    }

    public boolean isPollingStarted() {
        return isPollingStarted;
    }

    public boolean isSwitchActive() {
        usbSwitchCheck.lock();
        try {
            FileInputStream inputStream = new FileInputStream(usbSwitchFile);
            byte[] fileInput = new byte[inputStream.available()];
            inputStream.read(fileInput);

            inputStream.close();
            return new String(fileInput).contains("1");
        } catch (Exception e) {
            Log.e(TAG, "Read File", e);
            return false;
        } finally {
            usbSwitchCheck.unlock();
        }
    }

    public synchronized boolean setUSBSwitch(boolean activate) {
        usbSwitchCheck.lock();
        try {
            Charset charset;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                charset = StandardCharsets.UTF_8;
            else
                charset = Charset.forName("UTF-8");

            FileOutputStream outputStream = new FileOutputStream(usbSwitchFile);
            outputStream.write((activate ? "1" : "0").getBytes(charset));
            outputStream.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "File write IO error", e);
            return false;
        } finally {
            usbSwitchCheck.unlock();
        }
    }

    public boolean isCorrespondingDevice() {
        // If the device use is the correct device and contain the usbSwitchFile then can USB switch
        if ("V10P".equals(Build.MODEL) || "V11".equals(Build.MODEL) || "V20".equals(Build.MODEL)) {
            File file = new File(usbSwitchFile);
            return file.exists();
        }
        return false;
    }

    public void setSwitchExternalListener(OnUsbSwitchExternalListener switchExternalListener) {
        this.switchExternalListener.setListener(switchExternalListener);
    }

    public static V11FPUsbSwitch getInstance() {
        return insstance;
    }

}

package com.example.mykad_sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.securemetric.reader.myid.MYKAD;
import com.securemetric.reader.myid.MYKID;
import com.securemetric.reader.myid.MyID;
import com.securemetric.reader.myid.MyIDFP;
import com.securemetric.reader.myid.MyIDFPHandcode;
import com.securemetric.reader.myid.readers.FPScanner;

import io.flutter.plugin.common.MethodChannel;

enum CardType {
    MYKAD, MYKID, ERROR
}

public class ReadCard extends AsyncTaskExecutorService<String, String, String> {
    private String readerName;
    private final MyID mReaderManager;
    private static final String TAG = "MyKAD_SDK";
    private final MethodChannel methodChannel;
    private CardType cardType = CardType.ERROR;
    private boolean usingFP;
    private final Context context;

    public ReadCard(MyID mReaderManager, MethodChannel methodChannel, String readerName, boolean usingFP, Context context) {
        this.methodChannel = methodChannel;
        this.mReaderManager = mReaderManager;
        this.readerName = readerName;
        this.usingFP = usingFP;
        this.context = context;
    }

    String err = "";

    @Override
    protected String doInBackground(String param) {
        String photo = null;

        readerName = param;
        try {
            FPScanner fpScanner = FingerPrintManager.getInstance().getFPScanner();
            mReaderManager.connectReader(readerName, fpScanner);
            Object mRead;
            try {
                mRead = mReaderManager.readMyIDCard();
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return "Invalid card";
            }
            if (mRead instanceof MYKAD) {
                MYKAD value = (MYKAD) mRead;
                MyIDFP fpResult = null;
                cardType = CardType.MYKAD;
                byte[] photoArray = (byte[]) mReaderManager.readMyKADPhoto(MyID.PLAIN_FORMAT);
                photo = Base64.encodeToString(photoArray, Base64.NO_WRAP);

                if (isCancelled()) {
                    return "";
                }
                return MyKadModel.mykadToJson(value, photo);
            } else if (mRead instanceof MYKID) {
                MYKID value = (MYKID) mRead;
                if (isCancelled()) {
                    return "";
                }
                cardType = CardType.MYKID;
                return MyKidModel.mykidToJson(value);
            } else {
                Log.e(TAG, "Error Read Smart Card");
                return "Error Read Smart Card";
            }

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            err = Log.getStackTraceString(e);
            return "Error Read Smart Card";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        String methodErr = ReaderStatus.CARD_INSERTED_ERROR.name();
        try {
            if (result.equalsIgnoreCase("Error Read Smart Card") || result.isEmpty()) {
                boolean isCancel = result.isEmpty();
                String msg = isCancel ? " cancel" : " others";
                SDKResponse sdkResponse = new SDKResponse(methodErr, "Error card" + msg, err);
                methodChannel.invokeMethod(methodErr, sdkResponse.toJson().toString());
            } else if (result.equalsIgnoreCase("Invalid card")) {
                SDKResponse sdkResponse = new SDKResponse("INVALID CARD", "Invalid card ", result);
                methodChannel.invokeMethod(methodErr, sdkResponse.toJson().toString());
            } else {
                String method = ReaderStatus.CARD_SUCCESS.name();
                SDKResponse sdkResponse = new SDKResponse(method, "Success data " + cardType, result);
                methodChannel.invokeMethod(method, sdkResponse.toJson().toString());
            }

        } catch (Exception e) {
            SDKResponse sdkResponse = new SDKResponse(methodErr, "Error Data Conversion", "");
            methodChannel.invokeMethod(methodErr, sdkResponse);
        }
    }

    @Override
    protected void onCancelled(String s) {
        mReaderManager.disconnectReader();
    }


}

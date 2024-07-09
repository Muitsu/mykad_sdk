package com.example.mykad_sdk.fpswitch;

import android.content.Context;
import android.widget.Toast;

import com.example.mykad_sdk.AsyncTaskExecutorService;
import com.example.mykad_sdk.FingerPrintManager;
import com.securemetric.reader.myid.MyID;
import com.securemetric.reader.myid.MyIDFP;
import com.securemetric.reader.myid.MyIDFPHandcode;
import com.securemetric.reader.myid.readers.FPScanner;

public class ReadFingerprint extends AsyncTaskExecutorService<String, String, String> {
    private final MyID mReaderManager;
    private final Context context;

    public ReadFingerprint(MyID mReaderManager, Context context) {
        this.mReaderManager = mReaderManager;
        this.context = context;
    }

    @Override
    protected String doInBackground(String param) {
        FingerPrintManager fpManger = FingerPrintManager.getInstance();
        try {
            //Connect to FP Scanner
            FPScanner fpScanner = fpManger.getFPScanner();
            if (fpScanner == null) {
                return "Scanner not found";
            }
            mReaderManager.connectReader(param, fpScanner);
            int handcode = 0;
            String err_msg = "Failed to verify fingerprint!";
            int fingerprintTimeout = MyID.DEFAULT_FINGER_VERIFY_TIMEOUT;
            MyIDFP fpResult = null;
            if (!mReaderManager.isFingerprintAvailable()) {
                return "Scanner not found";
            }
            try {
                MyIDFPHandcode myIDFPHandcode = MyIDFPHandcode.AUTO;
                fpResult = mReaderManager.verifyFingerprint(myIDFPHandcode, fingerprintTimeout);
                if (isCancelled())
                    return "";
                if (fpResult == null)
                    return err_msg;
                return "Success Fingerprint";
            } catch (Exception e) {
                return err_msg;
            }
        } catch (Exception e) {
            return "Scanner not found";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (result.equalsIgnoreCase("Failed to verify fingerprint!")) {
                Toast.makeText(context, "Failed to verify fingerprint!", Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("Scanner not found")) {
                Toast.makeText(context, "Scanner not found", Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("Success Fingerprint")) {
                Toast.makeText(context, "Success Fingerprint", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onCancelled(String s) {

    }
}

package com.example.mykad_sdk;

import android.util.Log;

import com.securemetric.reader.myid.MYKAD;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyKadModel {
    private String name;
    private String gmpcName;
    private String kptName;
    private String icNo;
    private String oldIcNo;
    private Date dob;
    private String pob;
    private String gender;
    private String citizenship;
    private Date issueDate;
    private String race;
    private String religion;
    private String address1;
    private String address2;
    private String address3;
    private String postCode;
    private String city;
    private String state;
    private String photo;

    public static String mykadToJson(MYKAD myKAD, String photo) {
        JSONObject jMyKad = new JSONObject();
        JSONObject jMyKadDetail = new JSONObject();
        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd z", Locale.getDefault());

        try {
            jMyKadDetail.put("name", myKAD.getName());
            jMyKadDetail.put("gmpc_name", myKAD.getGmpcName());
            jMyKadDetail.put("kpt_name", myKAD.getKptName());
            jMyKadDetail.put("icno", myKAD.getIcNo());
            jMyKadDetail.put("old_icno", myKAD.getOldIcNo());

            Date dob = myKAD.getDob();
            if (dob != null) {
                try {
                    sdt.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    jMyKadDetail.put("date_of_birth", sdt.format(dob));
                } catch (Exception e) {
                    Log.w("MyKAD_SDK", Log.getStackTraceString(e));

                }
            }

            jMyKadDetail.put("place_of_birth", myKAD.getPob());
            jMyKadDetail.put("gender", myKAD.getGender());
            jMyKadDetail.put("citizenship", myKAD.getCitizenship());

            Date issueDate = myKAD.getIssueDate();
            if (issueDate != null) {
                try {
                    sdt.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    jMyKadDetail.put("issue_date", sdt.format(issueDate));
                } catch (Exception e) {
                    Log.w("MyKAD_SDK", Log.getStackTraceString(e));

                }
            }

            jMyKadDetail.put("race", myKAD.getRace());
            jMyKadDetail.put("religion", myKAD.getReligion());
            jMyKadDetail.put("address1", myKAD.getAddress1());
            jMyKadDetail.put("address2", myKAD.getAddress2());
            jMyKadDetail.put("address3", myKAD.getAddress3());
            jMyKadDetail.put("poscode", myKAD.getPostCode());
            jMyKadDetail.put("city", myKAD.getCity());
            jMyKadDetail.put("state", myKAD.getState());

            if (photo != null) {
                photo = photo.replace("\n", "");
                jMyKadDetail.put("photo", photo);
            }

            jMyKad.put("mykad", jMyKadDetail);

            return jMyKad.toString();
        } catch (Exception e) {
            Log.e("MyKAD_SDK", Log.getStackTraceString(e));
            return "";
        }
    }
}

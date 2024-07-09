package com.example.mykad_sdk;

import android.util.Log;

import com.securemetric.reader.myid.MYKID;

import org.json.JSONObject;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyKidModel {

    private String name;
    private String icNo;
    private String birthCertNo;
    private String gender;
    private String citizenship;
    private Date registrationDate;
    private Date dob;
    private String pob;
    private String address1;
    private String address2;
    private String address3;
    private String postcode;
    private String city;
    private String state;
    private Parent father;
    private Parent mother;

    // Nested Parent class
    public static class Parent {
        private String name;
        private String icNo;
        private String race;
        private String religion;
        private String residentType;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcNo() {
            return icNo;
        }

        public void setIcNo(String icNo) {
            this.icNo = icNo;
        }

        public String getRace() {
            return race;
        }

        public void setRace(String race) {
            this.race = race;
        }

        public String getReligion() {
            return religion;
        }

        public void setReligion(String religion) {
            this.religion = religion;
        }

        public String getResidentType() {
            return residentType;
        }

        public void setResidentType(String residentType) {
            this.residentType = residentType;
        }

        public JSONObject toJson() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("icno", icNo);
            jsonObject.put("race", race);
            jsonObject.put("religion", religion);
            jsonObject.put("resident_type", residentType);
            return jsonObject;
        }

        public static Parent fromJson(JSONObject jsonObject) throws JSONException {
            Parent parent = new Parent();
            parent.setName(jsonObject.getString("name"));
            parent.setIcNo(jsonObject.getString("icno"));
            parent.setRace(jsonObject.getString("race"));
            parent.setReligion(jsonObject.getString("religion"));
            parent.setResidentType(jsonObject.getString("resident_type"));
            return parent;
        }
    }

    public static String mykidToJson(MYKID myKid) {
        JSONObject jMyKid = new JSONObject();
        JSONObject jMyKidDetail = new JSONObject();
        JSONObject jMyKidFather = new JSONObject();
        JSONObject jMyKidMother = new JSONObject();

        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd z", Locale.getDefault());

        try {

            jMyKidFather.put("name", myKid.getFatherName());
            jMyKidFather.put("icno", myKid.getFatherIcNo());
            jMyKidFather.put("race", myKid.getFatherRace());
            jMyKidFather.put("religion", myKid.getFatherReligion());
            jMyKidFather.put("resident_type", myKid.getFatherResidentType());

            jMyKidMother.put("name", myKid.getMotherName());
            jMyKidMother.put("icno", myKid.getMotherIcNo());
            jMyKidMother.put("race", myKid.getMotherRace());
            jMyKidMother.put("religion", myKid.getMotherReligion());
            jMyKidMother.put("resident_type", myKid.getMotherResidentType());


            jMyKidDetail.put("name", myKid.getName());
            jMyKidDetail.put("icno", myKid.getIcNo());
            jMyKidDetail.put("birth_cert_no", myKid.getBirthCertNo());
            jMyKidDetail.put("gender", myKid.getGender());
            jMyKidDetail.put("citizenship", myKid.getCitizenship());

            Date registrationDate = myKid.getRegistrationDate();
            if (registrationDate != null) {
                try {
                    sdt.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    jMyKidDetail.put("registration_date", sdt.format(registrationDate));
                } catch (Exception e) {
                    Log.w("MyKAD_SDK", Log.getStackTraceString(e));

                }
            }

            Date dob = myKid.getDob();
            if (dob != null) {
                try {
                    sdt.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    jMyKidDetail.put("date_of_birth", sdt.format(dob));
                } catch (Exception e) {
                    Log.w("MyKAD_SDK", Log.getStackTraceString(e));

                }
            }

            jMyKidDetail.put("place_of_birth", myKid.getPob());
            jMyKidDetail.put("address1", myKid.getAddress1());
            jMyKidDetail.put("address2", myKid.getAddress2());
            jMyKidDetail.put("address3", myKid.getAddress3());
            jMyKidDetail.put("poscode", myKid.getPostcode());
            jMyKidDetail.put("city", myKid.getCity());
            jMyKidDetail.put("state", myKid.getState());

            jMyKidDetail.put("father", jMyKidFather);
            jMyKidDetail.put("mother", jMyKidMother);

            jMyKid.put("mykid", jMyKidDetail);

            return jMyKid.toString();
        } catch (Exception e) {
            Log.e("MyKAD_SDK", Log.getStackTraceString(e));
            return "";
        }
    }
}

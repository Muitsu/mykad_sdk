package com.example.mykad_sdk.fpswitch;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceInfo {
    @SerializedName("title")
    private String mTitle;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("deviceType")
    private DeviceType mDeviceType;

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.mDeviceType = deviceType;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public DeviceType getDeviceType() {
        return mDeviceType;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getTitle() {
        return mTitle;
    }

    // Convert a JSON string to a DeviceInfo object
    public static DeviceInfo fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, DeviceInfo.class);
    }

    // Convert a DeviceInfo object to a JSON string
    public String toJson() {
        return new Gson().toJson(this);
    }

    // Convert a list of DeviceInfo objects to a JSON string
    public static String listToJson(List<DeviceInfo> deviceInfoList) {
        return new Gson().toJson(deviceInfoList);
    }
}

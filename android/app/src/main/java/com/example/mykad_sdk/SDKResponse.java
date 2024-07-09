package com.example.mykad_sdk;

import com.google.gson.Gson;

public class SDKResponse {
    private String status;
    private String message;
    private String data;

    // Constructor
    public SDKResponse(String status, String message, String data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getter and Setter methods
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    // Method to convert object to JSON string
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Static method to create object from JSON string
    public static SDKResponse fromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, SDKResponse.class);
    }
}

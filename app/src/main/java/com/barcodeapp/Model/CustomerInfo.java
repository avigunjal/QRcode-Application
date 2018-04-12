package com.barcodeapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by AVI on 11-04-2018.
 */

public class CustomerInfo {
    @SerializedName("result")
    @Expose
    public Integer result;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public List<CustomerData> data = null;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CustomerData> getData() {
        return data;
    }

    public void setData(List<CustomerData> data) {
        this.data = data;
    }
}

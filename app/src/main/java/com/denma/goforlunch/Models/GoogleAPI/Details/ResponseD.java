package com.denma.goforlunch.Models.GoogleAPI.Details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseD {

    @SerializedName("result")
    @Expose
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}

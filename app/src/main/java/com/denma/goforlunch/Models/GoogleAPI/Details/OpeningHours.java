package com.denma.goforlunch.Models.GoogleAPI.Details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class OpeningHours {

    @SerializedName("periods")
    @Expose
    private List<Periods> periods = new ArrayList<Periods>();

    public List<Periods> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Periods> periods) {
        this.periods = periods;
    }
}

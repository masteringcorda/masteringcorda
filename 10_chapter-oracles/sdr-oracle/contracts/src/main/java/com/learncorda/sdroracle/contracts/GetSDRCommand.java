package com.learncorda.sdroracle.contracts;

import net.corda.core.contracts.CommandData;

public class GetSDRCommand implements CommandData {
    private String date;
    private Float rate;

    public GetSDRCommand(String date, Float rate) {
        this.date = date;
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public Float getRate() {
        return rate;
    }
}

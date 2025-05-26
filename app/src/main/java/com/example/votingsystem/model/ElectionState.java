package com.example.votingsystem.model;
public class ElectionState {
    private boolean isStarted;
    private String officialEndTime; // ISO 8601 or UNIX timestamp string

    public ElectionState(boolean isStarted, String officialEndTime) {
        this.isStarted = isStarted;
        this.officialEndTime = officialEndTime;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public String getOfficialEndTime() {
        return officialEndTime;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public void setOfficialEndTime(String officialEndTime) {
        this.officialEndTime = officialEndTime;
    }
}

package com.tuzhao.info;

/**
 * Created by juncoder on 2018/3/28.
 */

public class EverydayShareTimeInfo {

    private String startDate;

    private String endDate;

    private String statTime;

    private String endTime;

    public EverydayShareTimeInfo() {

    }

    public EverydayShareTimeInfo(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatTime() {
        if (statTime == null) {
            statTime = startDate.substring(startDate.indexOf(" ") + 1, startDate.length());
        }
        return statTime;
    }

    public String getEndTime() {
        if (endTime == null) {
            endTime = endDate.substring(endDate.indexOf(" ") + 1, endDate.length());
        }
        return endTime;
    }

    @Override
    public String toString() {
        return "EverydayShareTimeInfo{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", statTime='" + statTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }

}

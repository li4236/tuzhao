package com.tuzhao.info;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EverydayShareTimeInfo that = (EverydayShareTimeInfo) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(startDate, endDate);
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

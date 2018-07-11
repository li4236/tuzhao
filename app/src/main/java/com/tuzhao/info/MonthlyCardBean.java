package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

/**
 * Created by juncoder on 2018/7/9.
 */
public class MonthlyCardBean {


    /**
     * expiredCardSize : 2
     * areaCards : [{"id":3,"area":"中山市","status":"","expiredDate":"2018-08-01 00:00:00"}]
     * nationalCards : [{"id":2,"area":"全国","status":"1","expiredDate":"2018-08-02 00:00:00"}]
     */

    private int expiredCardSize;
    private List<CardBean> areaCards;
    private List<CardBean> nationalCards;

    public int getExpiredCardSize() {
        return expiredCardSize;
    }

    public void setExpiredCardSize(int expiredCardSize) {
        this.expiredCardSize = expiredCardSize;
    }

    public List<CardBean> getAreaCards() {
        return areaCards;
    }

    public void setAreaCards(List<CardBean> areaCards) {
        this.areaCards = areaCards;
    }

    public List<CardBean> getNationalCards() {
        return nationalCards;
    }

    public void setNationalCards(List<CardBean> nationalCards) {
        this.nationalCards = nationalCards;
    }

    public static class CardBean implements Parcelable {
        /**
         * id : 3
         * area : 中山市
         * status :
         * expiredDate : 2018-08-01 00:00:00
         */

        private int id;
        private String area;
        private String status;
        private String expiredDate;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getExpiredDate() {
            return expiredDate;
        }

        public void setExpiredDate(String expiredDate) {
            this.expiredDate = expiredDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CardBean cardBean = (CardBean) o;
            return id == cardBean.id;
        }

        @Override
        public int hashCode() {

            return Objects.hash(id);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.area);
            dest.writeString(this.status);
            dest.writeString(this.expiredDate);
        }

        public CardBean() {
        }

        protected CardBean(Parcel in) {
            this.id = in.readInt();
            this.area = in.readString();
            this.status = in.readString();
            this.expiredDate = in.readString();
        }

        public static final Parcelable.Creator<CardBean> CREATOR = new Parcelable.Creator<CardBean>() {
            @Override
            public CardBean createFromParcel(Parcel source) {
                return new CardBean(source);
            }

            @Override
            public CardBean[] newArray(int size) {
                return new CardBean[size];
            }
        };
    }

}

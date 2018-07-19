package com.tuzhao.info;

import com.tuzhao.utils.DateUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by juncoder on 2018/7/11.
 */
public class MonthlyCard {

    /**
     * 省
     */
    private String province;

    /**
     * 城市的月卡
     */
    private List<City> citys;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public List<City> getCitys() {
        return citys;
    }

    public void setCitys(List<City> citys) {
        this.citys = citys;
    }

    public static class City {

        /**
         * 城市
         */
        private String city;

        /**
         * 城市码
         */
        private String cityCode;

        private List<MonthlyCardPrice> cityMonthlyCards;

        private boolean sort;

        public static class MonthlyCardPrice {
            /**
             * 月卡的有效期
             */
            private String allotedPeriod;

            /**
             * 价格
             */
            private String price;

            public String getAllotedPeriod() {
                return allotedPeriod;
            }

            public void setAllotedPeriod(String allotedPeriod) {
                this.allotedPeriod = allotedPeriod;
            }

            public String getPrice() {
                return DateUtil.decreseOneZero(price);
            }

            public void setPrice(String price) {
                this.price = price;
            }

            @Override
            public String toString() {
                return "MonthlyCardPrice{" +
                        "allotedPeriod='" + allotedPeriod + '\'' +
                        ", price='" + price + '\'' +
                        '}';
            }

        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public List<MonthlyCardPrice> getCityMonthlyCards() {
            if (!sort) {
                Collections.sort(cityMonthlyCards, new Comparator<MonthlyCardPrice>() {
                    @Override
                    public int compare(MonthlyCardPrice o1, MonthlyCardPrice o2) {
                        return Integer.valueOf(o1.getAllotedPeriod())-Integer.valueOf(o2.getAllotedPeriod());
                    }
                });
                sort = true;
            }
            return cityMonthlyCards;
        }

        public void setCityMonthlyCards(List<MonthlyCardPrice> cityMonthlyCards) {
            this.cityMonthlyCards = cityMonthlyCards;
        }

        @Override
        public String toString() {
            return "City{" +
                    "city='" + city + '\'' +
                    ", cityCode='" + cityCode + '\'' +
                    ", cityMonthlyCards=" + cityMonthlyCards +
                    '}';
        }

    }

    @Override
    public String toString() {
        return "MonthlyCard{" +
                "province='" + province + '\'' +
                ", citys=" + citys +
                '}';
    }

}

package com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean;

import java.util.List;

/**
* 介绍：城市 HeaderView Bean
* 作者：TZL13
* 时间： 2017/7/5.
*/

public class HeaderBean extends BaseIndexPinyinBean {
   private List<CityBean> cityList;
   //悬停ItemDecoration显示的Tag
   private String suspensionTag;

   public HeaderBean() {
   }

   public HeaderBean(List<CityBean> cityList, String suspensionTag, String indexBarTag) {
       this.cityList = cityList;
       this.suspensionTag = suspensionTag;
       this.setBaseIndexTag(indexBarTag);
   }

   public List<CityBean> getCityList() {
       return cityList;
   }

   public HeaderBean setCityList(List<CityBean> cityList) {
       this.cityList = cityList;
       return this;
   }

   public HeaderBean setSuspensionTag(String suspensionTag) {
       this.suspensionTag = suspensionTag;
       return this;
   }

   @Override
   public String getTarget() {
       return null;
   }

   @Override
   public boolean isNeedToPinyin() {
       return false;
   }

   @Override
   public String getSuspensionTag() {
       return suspensionTag;
   }


}

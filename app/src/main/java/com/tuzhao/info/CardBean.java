package com.tuzhao.info;

import java.util.ArrayList;

/**
 * Created by juncoder on 2018/7/9.
 */
public class CardBean {

    private String expiredCardSize;

    private ArrayList<CardInfo> areaCards;

    private ArrayList<CardInfo> nationalCard;

    public String getExpiredCardSize() {
        return expiredCardSize;
    }

    public void setExpiredCardSize(String expiredCardSize) {
        this.expiredCardSize = expiredCardSize;
    }

    public ArrayList<CardInfo> getAreaCards() {
        return areaCards;
    }

    public void setAreaCards(ArrayList<CardInfo> areaCards) {
        this.areaCards = areaCards;
    }

    public ArrayList<CardInfo> getNationalCard() {
        return nationalCard;
    }

    public void setNationalCard(ArrayList<CardInfo> nationalCard) {
        this.nationalCard = nationalCard;
    }
}

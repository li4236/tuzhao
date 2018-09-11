package com.tuzhao.info;

import com.google.gson.annotations.SerializedName;

/**
 * Created by juncoder on 2018/4/8.
 */

public class FriendInfo {

    @SerializedName(value = "friendId", alternate = "userId")
    private String friendId;

    private String imgUrl = "";

    /**
     * 备注名
     */
    private String noteName;

    /**
     * 真实姓名
     */
    private String realName;

    private String telephone;

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Override
    public String toString() {
        return "FriendInfo{" +
                "friendId='" + friendId + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", noteName='" + noteName + '\'' +
                ", realName='" + realName + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }

}

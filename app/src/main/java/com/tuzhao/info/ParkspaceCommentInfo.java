package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by TZL12 on 2017/5/16.
 */

public class ParkspaceCommentInfo extends BaseInfo {

    private String id;
    private String user_id;//用户id
    private String nickname;//用户昵称
    private String username;//用户名
    private String user_img_url;//用户头像
    private String parkspace_id;//停车场id
    private String grade;//评论等级
    private String content;//评论内容
    private String park_time;//停车时间
    private String img_url;//评论图片

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getParkspace_id() {
        return parkspace_id;
    }

    public void setParkspace_id(String parkspace_id) {
        this.parkspace_id = parkspace_id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPark_time() {
        return park_time;
    }

    public void setPark_time(String park_time) {
        this.park_time = park_time;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUser_img_url() {
        return user_img_url;
    }

    public void setUser_img_url(String user_img_url) {
        this.user_img_url = user_img_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "ParkspaceCommentInfo{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", user_img_url='" + user_img_url + '\'' +
                ", parkspace_id='" + parkspace_id + '\'' +
                ", grade='" + grade + '\'' +
                ", content='" + content + '\'' +
                ", park_time='" + park_time + '\'' +
                ", img_url='" + img_url + '\'' +
                '}';
    }
}

package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.tuzhao.utils.ConstansUtil;

/**
 * Created by TZL12 on 2017/5/16.
 */

public class User_Info implements Parcelable {

    private String token;//用户登录标识
    private String id;//用户的唯一标识id
    private String username;//用户名：手机号码
    private String password;//用户密码
    private String balance;//账户余额
    private String nickname = "-1";//用户昵称
    private String realName;//真实姓名

    @SerializedName(value = "gender", alternate = {"sex"})
    private String gender;//性别
    private String birthday;//生日
    private String numberOfPark;//停车次数
    private String img_url;//用户头像
    private String create_time;//用户注册时间
    private String last_time;//用户最后一次登陆时的时间
    private String credit;//信用分
    private int leave_time = -1;//离开的弹性时间
    private String serect_code;//用户通行码
    private String alinumber = "-1";//支付宝账号
    private String aliNickname = "";//支付宝用户昵称
    private String openId;//微信的用户的openId
    private String unionId;//微信的unionId
    private String wechatNickname;//微信昵称
    private String paymentPassword;//支付密码（设置了就返回-1，没设置就返回""）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String autologin;//是否自动登录，用于本地数据库操作，默认值为1,1为可以自动登录，0为不

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getAutologin() {
        return autologin;
    }

    public void setAutologin(String autologin) {
        this.autologin = autologin;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getSerect_code() {
        return serect_code;
    }

    public void setSerect_code(String serect_code) {
        this.serect_code = serect_code;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getLeave_time() {
        if (leave_time == -1) {
            float creditScore = Float.valueOf(credit);
            if (creditScore >= ConstansUtil.VERY_GOOD_CREDIT_SCORE) {
                leave_time = 60;
            } else if (creditScore >= ConstansUtil.GOOD_CREDIT_SCORE) {
                leave_time = 45;
            } else if (creditScore >= ConstansUtil.FINE_CREDIT_SCORE) {
                leave_time = 30;
            } else if (creditScore >= ConstansUtil.POOR_CREDIT_SCORE) {
                leave_time = 15;
            } else if (creditScore >= ConstansUtil.VERY_POOR_CREDIT_SCORE) {
                leave_time = 10;
            } else {
                leave_time = 0;
            }
        }
        return leave_time;
    }

    public void setLeave_time(int leave_time) {
        this.leave_time = leave_time;
    }

    public String getAlinumber() {
        return alinumber;
    }

    public void setAlinumber(String alinumber) {
        this.alinumber = alinumber;
    }

    public String getAliNickname() {
        return aliNickname;
    }

    public void setAliNickname(String aliNickname) {
        this.aliNickname = aliNickname;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNumberOfPark() {
        return numberOfPark;
    }

    public void setNumberOfPark(String numberOfPark) {
        this.numberOfPark = numberOfPark;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getWechatNickname() {
        return wechatNickname;
    }

    public void setWechatNickname(String wechatNickname) {
        this.wechatNickname = wechatNickname;
    }

    public String getPaymentPassword() {
        return paymentPassword;
    }

    public void setPaymentPassword(String paymentPassword) {
        this.paymentPassword = paymentPassword;
    }

    /**
     * @return 是否已实名认证
     */
    public boolean isCertification() {
        return !realName.equals("-1") && !birthday.equals("0000-00-00");
    }

    @Override
    public String toString() {
        return "User_Info{" +
                "token='" + token + '\'' +
                ", id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", balance='" + balance + '\'' +
                ", nickname='" + nickname + '\'' +
                ", realName='" + realName + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", numberOfPark='" + numberOfPark + '\'' +
                ", img_url='" + img_url + '\'' +
                ", create_time='" + create_time + '\'' +
                ", last_time='" + last_time + '\'' +
                ", credit='" + credit + '\'' +
                ", leave_time=" + leave_time +
                ", serect_code='" + serect_code + '\'' +
                ", alinumber='" + alinumber + '\'' +
                ", aliNickname='" + aliNickname + '\'' +
                ", openId='" + openId + '\'' +
                ", unionId='" + unionId + '\'' +
                ", wechatNickname='" + wechatNickname + '\'' +
                ", autologin='" + autologin + '\'' +
                ", paymentPassword='" + paymentPassword + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.id);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.balance);
        dest.writeString(this.nickname);
        dest.writeString(this.realName);
        dest.writeString(this.gender);
        dest.writeString(this.birthday);
        dest.writeString(this.numberOfPark);
        dest.writeString(this.img_url);
        dest.writeString(this.create_time);
        dest.writeString(this.last_time);
        dest.writeString(this.credit);
        dest.writeInt(this.leave_time);
        dest.writeString(this.serect_code);
        dest.writeString(this.alinumber);
        dest.writeString(this.aliNickname);
        dest.writeString(this.openId);
        dest.writeString(this.unionId);
        dest.writeString(this.wechatNickname);
        dest.writeString(this.paymentPassword);
        dest.writeString(this.autologin);
    }

    public User_Info() {
    }

    protected User_Info(Parcel in) {
        this.token = in.readString();
        this.id = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.balance = in.readString();
        this.nickname = in.readString();
        this.realName = in.readString();
        this.gender = in.readString();
        this.birthday = in.readString();
        this.numberOfPark = in.readString();
        this.img_url = in.readString();
        this.create_time = in.readString();
        this.last_time = in.readString();
        this.credit = in.readString();
        this.leave_time = in.readInt();
        this.serect_code = in.readString();
        this.alinumber = in.readString();
        this.aliNickname = in.readString();
        this.openId = in.readString();
        this.unionId = in.readString();
        this.wechatNickname = in.readString();
        this.paymentPassword = in.readString();
        this.autologin = in.readString();
    }

    public static final Creator<User_Info> CREATOR = new Creator<User_Info>() {
        @Override
        public User_Info createFromParcel(Parcel source) {
            return new User_Info(source);
        }

        @Override
        public User_Info[] newArray(int size) {
            return new User_Info[size];
        }
    };
}

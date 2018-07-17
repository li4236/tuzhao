package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;
import com.tuzhao.utils.ConstansUtil;

/**
 * Created by TZL12 on 2017/5/16.
 */

public class User_Info extends BaseInfo {

    private String token;//用户登录标识
    private String id;//用户的唯一标识id
    private String username;//用户名：手机号码
    private String password;//用户密码
    private String balance;//账户余额
    private String nickname;//用户昵称
    private String realName;//真实姓名
    private String gender;//性别
    private String birthday;//生日
    private String numberOfPark;//停车次数
    private String img_url;//用户头像
    private String car_number;//车牌号
    private String create_time;//用户注册时间
    private String last_time;//用户最后一次登陆时的时间
    private String credit;//信用分
    private int ride_time;//到达的弹性时间
    private int leave_time = -1;//离开的弹性时间
    private String serect_code;//用户通行码
    private String alinumber = "-1";//支付宝账号
    private String aliNickName = "";//支付宝用户昵称
    private String openId;//微信的用户的openId
    private String sesameFraction;

    private String stage;//阶段分
    private String default_late_time;//默认晚退延时时间
    private String add_late_time;//完成订单加分
    private String stage_add_late_time;//阶段增长时间（逗号隔开）
    private String min_late_time;//最低离开延时时间
    private String max_late_time;//最高离开延时时间
    private String default_early_time;//默认迟到延时时间
    private String add_early_time;//完成订单加分
    private String stage_add_early_time;//阶段增长时间（逗号隔开）
    private String min_early_time;//最低迟到延时时间
    private String max_early_time;//最高迟到延时时间
    private String add_credit;//信用分增长幅度
    private String reduce_credit;//信用分降低幅度

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String autologin;//是否自动登录，用于本地数据库操作，默认值为1,1为可以自动登录，0为不

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getDefault_late_time() {
        return default_late_time;
    }

    public void setDefault_late_time(String default_late_time) {
        this.default_late_time = default_late_time;
    }

    public String getAdd_late_time() {
        return add_late_time;
    }

    public void setAdd_late_time(String add_late_time) {
        this.add_late_time = add_late_time;
    }

    public String getStage_add_late_time() {
        return stage_add_late_time;
    }

    public void setStage_add_late_time(String stage_add_late_time) {
        this.stage_add_late_time = stage_add_late_time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMin_late_time() {
        return min_late_time;
    }

    public void setMin_late_time(String min_late_time) {
        this.min_late_time = min_late_time;
    }

    public String getMax_late_time() {
        return max_late_time;
    }

    public void setMax_late_time(String max_late_time) {
        this.max_late_time = max_late_time;
    }

    public String getDefault_early_time() {
        return default_early_time;
    }

    public void setDefault_early_time(String default_early_time) {
        this.default_early_time = default_early_time;
    }

    public String getAdd_early_time() {
        return add_early_time;
    }

    public void setAdd_early_time(String add_early_time) {
        this.add_early_time = add_early_time;
    }

    public String getStage_add_earlt_time() {
        return stage_add_early_time;
    }

    public void setStage_add_earlt_time(String stage_add_earlt_time) {
        this.stage_add_early_time = stage_add_earlt_time;
    }

    public String getMin_early_time() {
        return min_early_time;
    }

    public void setMin_early_time(String min_early_time) {
        this.min_early_time = min_early_time;
    }

    public String getMax_early_time() {
        return max_early_time;
    }

    public void setMax_early_time(String max_early_time) {
        this.max_early_time = max_early_time;
    }

    public String getAdd_credit() {
        return add_credit;
    }

    public void setAdd_credit(String add_credit) {
        this.add_credit = add_credit;
    }

    public String getReduce_credit() {
        return reduce_credit;
    }

    public void setReduce_credit(String reduce_credit) {
        this.reduce_credit = reduce_credit;
    }

    public int getRide_time() {
        return ride_time;
    }

    public void setRide_time(int ride_time) {
        this.ride_time = ride_time;
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

    public String getAliNickName() {
        return aliNickName;
    }

    public void setAliNickName(String aliNickName) {
        this.aliNickName = aliNickName;
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

    public String getSesameFraction() {
        return sesameFraction;
    }

    public void setSesameFraction(String sesameFraction) {
        this.sesameFraction = sesameFraction;
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
                ", car_number='" + car_number + '\'' +
                ", create_time='" + create_time + '\'' +
                ", last_time='" + last_time + '\'' +
                ", credit='" + credit + '\'' +
                ", ride_time=" + ride_time +
                ", leave_time=" + leave_time +
                ", serect_code='" + serect_code + '\'' +
                ", alinumber='" + alinumber + '\'' +
                ", aliNickName='" + aliNickName + '\'' +
                ", openId='" + openId + '\'' +
                ", sesameFraction='" + sesameFraction + '\'' +
                ", stage='" + stage + '\'' +
                ", default_late_time='" + default_late_time + '\'' +
                ", add_late_time='" + add_late_time + '\'' +
                ", stage_add_late_time='" + stage_add_late_time + '\'' +
                ", min_late_time='" + min_late_time + '\'' +
                ", max_late_time='" + max_late_time + '\'' +
                ", default_early_time='" + default_early_time + '\'' +
                ", add_early_time='" + add_early_time + '\'' +
                ", stage_add_early_time='" + stage_add_early_time + '\'' +
                ", min_early_time='" + min_early_time + '\'' +
                ", max_early_time='" + max_early_time + '\'' +
                ", add_credit='" + add_credit + '\'' +
                ", reduce_credit='" + reduce_credit + '\'' +
                ", autologin='" + autologin + '\'' +
                '}';
    }
}

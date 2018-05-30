package com.tuzhao.http;

/**
 * Created by TZL11 on 2017/4/20.
 *
 * @function 定义所有网络请求的服务器地址
 */

public class HttpConstants {

    //    private static String ROOT_URL = "http://120.78.54.188/Home/charge/";//真正服务器
//    public static final String ROOT_IMG_URL = "http://120.78.54.188/Public/Uploads/";//真正服务器
    public static final String ROOT_URL = "https://api.toozhao.cn/public/index.php/tianzhili/";//测试服务器
    private static final String ROOT_BASE_IMG_URL = "https://api.toozhao.cn/public/uploads/";//测试服务器图片基本Url
    public static final String ROOT_MAPSTYLE_URL = ROOT_BASE_IMG_URL + "map_style/mystyle_sdk.data";//地图样式文件
    public static final String ROOT_IMG_URL_USER = ROOT_BASE_IMG_URL + "headimgs/";//用户头像
    public static final String ROOT_IMG_URL_PS = ROOT_BASE_IMG_URL + "parkspaces/";//车场图片
    public static final String ROOT_IMG_URL_PSCOM = ROOT_BASE_IMG_URL + "comments/";//车场评论
    public static final String ROOT_IMG_URL_CS = ROOT_BASE_IMG_URL + "chargestations/";//电场

    /**
     * 请求主页地图页面停车位和充电桩的具体位置的经纬度
     */
    public static String getNearPointLocData = ROOT_URL + "getNearPointLocData";
    /**
     * 获取一个电站信息
     */
    public static String getOneChargeStationData = ROOT_URL + "getOneChargeStationData";

    /**
     * 获取一个车场信息
     */
    public static String getOneParkSpaceData = ROOT_URL + "getOneParkSpaceData";

    /**
     * 获取一个车场的所有评论
     */
    public static String getParkspceComment = ROOT_URL + "getParkspceComment";

    /**
     * 登录的请求
     */
    public static String requestLogin = ROOT_URL + "requestLogin";

    /**
     * 添加用户的车牌号
     */
    public static String addUserCarNumber = ROOT_URL + "addUserCarNumber";

    /**
     * 删除用户的车牌号
     */
    public static String deleteUserCarNumber = ROOT_URL + "deleteUserCarNumber";

    /**
     * 查询用户的优惠券
     */
    public static String getUserDiscount = ROOT_URL + "getUserDiscount";

    /**
     * 新增停车预约订单接口
     */
    public static String addNewParkOrder = ROOT_URL + "addNewParkOrder";

    /**
     * 预约被锁定车位
     */
    public static String appointOrderLockPark = ROOT_URL + "appointOrderLockPark";

    /**
     * 获得用户收藏记录
     */
    public static String getCollectionDatas = ROOT_URL + "getCollectionDatas";

    /**
     * 添加一条收藏记录
     */
    public static String addCollection = ROOT_URL + "addCollection";

    /**
     * 删除收藏记录
     */
    public static String deleteCollection = ROOT_URL + "deleteCollection";

    /**
     * 获取用户停车位列表
     */
    public static String getParkFromUser = ROOT_URL + "getParkFromUser";

    /**
     * 用户删除停车位
     */
    public static String deleteParkForUser = ROOT_URL + "deleteParkForUser";

    /**
     * 修改停车位出租状态
     */
    public static String changeParkSpaceStatus = ROOT_URL + "changeParkSpaceStatus";

    /**
     * 删除停车位
     */
    public static String deleteParkSpace = ROOT_URL + "deleteParkSpace";

    /**
     * 获取一个停车场所有停车位的列表
     */
    public static String getParkList = ROOT_URL + "getParkList";

    /**
     * 预定停车位时获取用户停车订单列表
     */
    public static String getUserParkOrderForAppoint = ROOT_URL + "getUserParkOrderForAppoint";

    /**
     * 获取不同状态的订单
     */
    public static String getKindParkOrder = ROOT_URL + "getKindParkOrder";

    /**
     * 更换用户头像
     */
    public static String changeUserImage = ROOT_URL + "changeUserImage";

    /**
     * 更改用户手机号
     */
    public static String changeUserPhoneNumble = ROOT_URL + "changeUserPhoneNumble";

    /**
     * 绑定用户支付宝账号
     */
    public static String uploadUserAliNumber = ROOT_URL + "uploadUserAliNumber";

    /**
     * 删除停车订单
     */
    public static String deletelParkOrder = ROOT_URL + "deletelParkOrder";

    /**
     * 用户取消预约订单
     */
    public static String cancleAppointOrder = ROOT_URL + "cancleAppointOrder";

    /**
     * 结束停车
     */
    public static String endParking = ROOT_URL + "endParking";

    /**
     * 生成支付宝订单
     */
    public static String alipayApplyOrder = ROOT_URL + "alipayApplyOrder";

    /**
     * 完成停车订单支付
     */
    public static String finishParkOrder = ROOT_URL + "finishParkOrder";

    /**
     * 用户评论停车订单
     */
    public static String addPsComment = ROOT_URL + "addPsComment";

    /**
     * 更改用户账号密码
     */
    public static String changePassword = ROOT_URL + "changePassword";

    /**
     * 反馈意见
     */
    public static String uploadSuggest = ROOT_URL + "uploadSuggest";
    ;

    /**
     * 获取用户账单列表
     */
    public static String getUserBill = ROOT_URL + "getUserBill";

    /**
     * 获取所有已开放城市列表
     */
    public static String getAllOpenCity = ROOT_URL + "getAllOpenCity";

    /**
     * 编辑停车位状态
     */
    public static String editPark = ROOT_URL + "editPark";

    /**
     * 上传停车位的图片
     */
    public static String uploadParkPicture = ROOT_URL + "uploadParkPicture";

    /**
     * 删除用户停车位图片
     */
    public static String deleteParkPicture = ROOT_URL + "deleteParkPicture";

    /**
     * 用户请求添加新的停车位
     */
    public static String addUserPark = ROOT_URL + "addUserPark";

    /**
     * 获取指定城市所有停车场数据
     */
    public static String getParkSpaceDatasForCity = ROOT_URL + "getParkSpaceDatasForCity";

    /**
     * 更改用户昵称
     */
    public static String changeUserNickname = ROOT_URL + "changeUserNickname";

    /**
     * 用户请求控制车位锁
     */
    public static String controlParkLock = ROOT_URL + "controlParkLock";

    /**
     * 获取一个订单的具体详情
     */
    public static String getDetailOfParkOrder = ROOT_URL + "getDetailOfParkOrder";

    /**
     * 获取用户正在审核的停车位列表
     */
    public static String getPassingParkFromUser = ROOT_URL + "getPassingParkFromUser";

    /**
     * 请求发送验证码
     */
    public static String sendSms = ROOT_URL + "sendSms";

    /**
     * 核对验证码登录
     */
    public static String checkCodeLogin = ROOT_URL + "checkCodeLogin";

    /**
     * 设置新密码
     */
    public static String setPasswordLogin = ROOT_URL + "setPasswordLogin";

    /**
     * 校验验证码
     */
    public static String checkCode = ROOT_URL + "checkCode";

    public static String getAliAuthMessage = ROOT_URL + "getAliAuthMessage";

    /**
     * 上传支付宝用户授权参数
     */
    public static String uploadAliAuthMessage = ROOT_URL + "uploadAliAuthMessage";

    /**
     * 安装师傅未来7天的空闲情况
     */
    public static String getInstallWorkerTime = ROOT_URL + "getInstallWorkerTime";

    /**
     * 请求首页地图页面所有充电站的数据
     */
    public static String getAllChargeStationData = ROOT_URL + "getChargeStationData";

    /**
     * 请求第二页停车场页面所有停车场的数据
     */
    public static String getParkSpaceData = ROOT_URL + "getParkSpaceData";

    /**
     * 注册账号的请求
     */
    public static String requestRegister = ROOT_URL + "requestRegister";

    /**
     * 请求充电桩评论数据
     */
    public static String getChargeCommentData = ROOT_URL + "getChargeCommentData";

    public static String setChargeCommentData = ROOT_URL + "setChargeCommentData";

    /**
     * 请求用户车牌信息
     */
    public static String setUserCarNumberData = ROOT_URL + "setUserCarNumberData";

    /**
     * 获取个人信息页面的综合信息
     */
    public static String getMineMessage = ROOT_URL + "getMineMessage";

    /**
     * 订单相关
     */
    public static String setParkOrder = ROOT_URL + "setParkOrder";
    public static String getParkOrder = ROOT_URL + "getParkOrder";

    public static String startPark = ROOT_URL + "startPark";


    /**
     * 获得用户不同种类的订单
     */
    public static String getAllOrdersDatas = ROOT_URL + "getAllOrdersDatas";


    /**
     * 获得一个停车位的信息
     */
    public static String getOneParkSpace = ROOT_URL + "getOneParkSpace";

    /**
     * 获得一个充电桩的信息
     */
    public static String getOneChargeStation = ROOT_URL + "getOneChargeStation";

    /**
     * 获取身份证信息
     */
    public static String getIdMessage = "http://apicloud.mob.com/idcard/query";

    /**
     * 获取我的钱包页面的信息
     */
    public static String getMyWalletData = ROOT_URL + "getMyWalletData";

    /**
     * 请求用户充电桩位数据
     */
    public static String getChargeFromUser = ROOT_URL + "getChargeFromUser";

    public static String setChargeForUser = ROOT_URL + "setChargeForUser";

    /**
     * 获取正在充电的电桩信息
     */
    public static String getCharggingOrder = ROOT_URL + "getCharggingOrder";

    /**
     * 申请开放一个停车场（小区）
     */
    public static String applyParkSpace = ROOT_URL + "applyParkSpace";


    /**
     * 获取正在停车中的订单信息
     */
    public static String getRentingParkOrder = ROOT_URL + "getRentingParkOrder";

    /**
     * 开始停车
     */
    public static String beginPark = ROOT_URL + "beginPark";

    /**
     * 删除用户优惠券
     */
    public static String deleteUserDiscount = ROOT_URL + "deleteUserDiscount";

    /**
     * 获取未报销的发票
     */
    public static String getInvoice = ROOT_URL + "getInvoice";

    /**
     * 获取报销的发票进度
     */
    public static String getInvoiceSituation = ROOT_URL + "getInvoiceSituation";

    /**
     * 获取发票的到达时间
     */
    public static String getInvoiceArriveTime = ROOT_URL + "getInvoiceArriveTime";

    /**
     * 申请发票报销
     */
    public static String applyInvoiceReimbursement = ROOT_URL + "applyInvoiceReimbursement";

    /**
     * 添加收票地址
     */
    public static String addAcceptTicketAddress = ROOT_URL + "addAcceptTicketAddress";

    /**
     * 修改收票地址
     */
    public static String changeAcceptTicketAddress = ROOT_URL + "changeAcceptTicketAddress";

    /**
     * 获取收票地址
     */
    public static String getAcceptTicketAddress = ROOT_URL + "getAcceptTicketAddress";

    /**
     * 删除收票地址
     */
    public static String deleteAcceptTicketAddress = ROOT_URL + "deleteAcceptTicketAddress";

    /**
     * 设置默认收票地址
     */
    public static String setDefaultAcceptTicketAddress = ROOT_URL + "setDefaultAcceptTicketAddress";

    /**
     * 获取默认的收票地址
     */
    public static String getDefalutAcceptTicketAddress = ROOT_URL + "getDefalutAcceptTicketAddress";

    /**
     * 获取出租记录
     */
    public static String getRentalRecord = ROOT_URL + "getRentalRecord";

    /**
     * 获取共享时间
     */
    public static String getShareTime = ROOT_URL + "getShareTime";

    /**
     * 修改共享时间
     */
    public static String editShareTime = ROOT_URL + "editShareTime";

    /**
     * 添加亲友
     */
    public static String addFriend = ROOT_URL + "addFriend";

    /**
     * 获取亲友信息
     */
    public static String getUserInfo = ROOT_URL + "getUserInfo";

    /**
     * 删除亲友
     */
    public static String deleteFriend = ROOT_URL + "deleteFriend";

    /**
     * 修改亲友备注
     */
    public static String modifyFriendNickname = ROOT_URL + "modifyFriendNickname";

    /**
     * 获取亲友
     */
    public static String getBindingFriends = ROOT_URL + "getBindingFriends";

    /**
     * 申请添加车辆
     */
    public static String applyAddNewCar = ROOT_URL + "applyAddNewCar";

    /**
     * 获取好友分享给我的车位
     */
    public static String getFriendShareParkspace = ROOT_URL + "getFriendShareParkspace";

    /**
     * 车主请求控制车位锁
     */
    public static String userControlParkLock = ROOT_URL + "userControlParkLock";

    /**
     * 获取车位状态
     */
    public static String getParkLockStatus = ROOT_URL + "getParkLockStatus";

    public static String zTest = ROOT_URL + "ztest";

}

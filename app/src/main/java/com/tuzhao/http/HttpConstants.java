package com.tuzhao.http;

/**
 * Created by TZL11 on 2017/4/20.
 *
 * @function 定义所有网络请求的服务器地址
 */

public class HttpConstants {

    //    private static String ROOT_URL = "http://120.78.54.188/Home/charge/";//真正服务器
//    public static final String ROOT_IMG_URL = "http://120.78.54.188/Public/Uploads/";//真正服务器
    private static final String ROOT_URL = "https://api.toozhao.cn/public/index.php/tianzhili/";//测试服务器
    private static final String ROOT_BASE_IMG_URL = "https://api.toozhao.cn/public/uploads/";//测试服务器图片基本Url
    public static final String ROOT_MAPSTYLE_URL = ROOT_BASE_IMG_URL + "map_style/mystyle_sdk.data";//地图样式文件
    public static final String ROOT_IMG_URL_USER = ROOT_BASE_IMG_URL + "headimgs/";//用户头像
    public static final String ROOT_IMG_URL_PS = ROOT_BASE_IMG_URL + "parkspaces/";//车场图片
    public static final String ROOT_IMG_URL_PSCOM = ROOT_BASE_IMG_URL + "comments/";//车场评论
    public static final String ROOT_IMG_URL_CS = ROOT_BASE_IMG_URL + "chargestations/";//电场
    public static final String ROOT_IMG_URL_ID_CARD = ROOT_BASE_IMG_URL + "idcard/";//身份证照
    public static final String ROOT_IMG_URL_PROPERTY = ROOT_BASE_IMG_URL + "property/";//产权照
    public static final String ROOT_IMG_URL_DRIVER_LICENSE = ROOT_BASE_IMG_URL + "driver/";//驾驶证照
    public static final String ROOT_IMG_URL_COMPLAINT = ROOT_BASE_IMG_URL + "complaint/";//投诉照
    public static final String ROOT_IMG_URL_VEHICLE = ROOT_BASE_IMG_URL + "vehicle/";//行驶证
    public static final String ROOT_IMG_URL_GROUP_PHOTO = ROOT_BASE_IMG_URL + "groupPhoto/";//合影
    public static final String ROOT_IMG_URL_LOCK_FAULT = ROOT_BASE_IMG_URL + "lockFault";//车锁故障图

    /**
     * 检查更新
     */
    public static final String checkVersion = ROOT_URL + "checkVersion";

    /**
     * 请求主页地图页面停车位和充电桩的具体位置的经纬度
     */
    public static final String getNearPointLocData = ROOT_URL + "getNearPointLocData";
    /**
     * 获取一个电站信息
     */
    public static final String getOneChargeStationData = ROOT_URL + "getOneChargeStationData";

    /**
     * 获取一个车场信息
     */
    public static final String getOneParkSpaceData = ROOT_URL + "getOneParkSpaceData";

    /**
     * 获取一个车场的所有评论
     */
    public static final String getParkspceComment = ROOT_URL + "getParkspceComment";

    /**
     * 登录的请求
     */
    public static final String requestLogin = ROOT_URL + "requestLogin";

    /**
     * 添加用户的车牌号
     */
    public static final String addUserCarNumber = ROOT_URL + "addUserCarNumber";

    /**
     * 删除用户的车牌号
     */
    public static final String deleteUserCarNumber = ROOT_URL + "deleteUserCarNumber";

    /**
     * 查询用户的优惠券
     */
    public static final String getUserDiscount = ROOT_URL + "getUserDiscount";

    /**
     * 新增停车预约订单接口
     */
    public static final String addNewParkOrder = ROOT_URL + "addNewParkOrder";

    /**
     * 预约被锁定车位
     */
    public static final String appointOrderLockPark = ROOT_URL + "appointOrderLockPark";

    /**
     * 获得用户收藏记录
     */
    public static final String getCollectionDatas = ROOT_URL + "getCollectionDatas";

    /**
     * 添加一条收藏记录
     */
    public static final String addCollection = ROOT_URL + "addCollection";

    /**
     * 删除收藏记录
     */
    public static final String deleteCollection = ROOT_URL + "deleteCollection";

    /**
     * 获取用户停车位列表
     */
    public static final String getParkFromUser = ROOT_URL + "getParkFromUser";

    /**
     * 删除停车位
     */
    public static final String deleteParkSpace = ROOT_URL + "deleteParkSpace";

    /**
     * 获取一个停车场所有停车位的列表
     */
    public static final String getParkList = ROOT_URL + "getParkList";

    /**
     * 预定停车位时获取用户停车订单列表
     */
    public static final String getUserParkOrderForAppoint = ROOT_URL + "getUserParkOrderForAppoint";

    /**
     * 获取不同状态的订单
     */
    public static final String getKindParkOrder = ROOT_URL + "getKindParkOrder";

    /**
     * 更换用户头像
     */
    public static final String changeUserImage = ROOT_URL + "changeUserImage";

    /**
     * 更改用户手机号
     */
    public static final String changeUserPhoneNumble = ROOT_URL + "changeUserPhoneNumble";

    /**
     * 绑定用户支付宝账号
     */
    public static final String uploadUserAliNumber = ROOT_URL + "uploadUserAliNumber";

    /**
     * 删除停车订单
     */
    public static final String deletelParkOrder = ROOT_URL + "deletelParkOrder";

    /**
     * 用户取消预约订单
     */
    public static final String cancleAppointOrder = ROOT_URL + "cancleAppointOrder";

    /**
     * 结束停车
     */
    public static final String endParking = ROOT_URL + "endParking";

    /**
     * 生成支付宝订单
     */
    public static final String alipayApplyOrder = ROOT_URL + "alipayApplyOrder";

    /**
     * 获取支付宝缴纳押金订单信息
     */
    public static final String getAlipayLockDepositInfo = ROOT_URL + "getAlipayLockDepositInfo";

    /**
     * 获取微信支付订单参数
     */
    public static final String getWechatPayOrder = ROOT_URL + "getWechatPayOrder";

    /**
     * 获取微信缴纳车锁押金订单参数
     */
    public static final String getWechatLockDepositInfo = ROOT_URL + "getWechatLockDepositInfo";

    /**
     * 完成停车订单支付
     */
    public static final String finishParkOrder = ROOT_URL + "finishParkOrder";

    /**
     * 用户评论停车订单
     */
    public static final String addPsComment = ROOT_URL + "addPsComment";

    /**
     * 更改用户账号密码
     */
    public static final String changePassword = ROOT_URL + "changePassword";

    /**
     * 反馈意见
     */
    public static final String uploadSuggest = ROOT_URL + "uploadSuggest";

    /**
     * 获取用户账单列表
     */
    public static final String getUserBill = ROOT_URL + "getUserBill";

    /**
     * 获取所有已开放城市列表
     */
    public static final String getAllOpenCity = ROOT_URL + "getAllOpenCity";

    /**
     * 编辑停车位状态
     */
    public static final String editPark = ROOT_URL + "editPark";

    /**
     * 上传停车位的图片
     */
    public static final String uploadParkPicture = ROOT_URL + "uploadParkPicture";

    /**
     * 删除用户停车位图片
     */
    public static final String deleteParkPicture = ROOT_URL + "deleteParkPicture";

    /**
     * 用户请求添加新的停车位
     */
    public static final String addUserPark = ROOT_URL + "addUserPark";

    /**
     * 获取指定城市所有停车场数据
     */
    public static final String getParkSpaceDatasForCity = ROOT_URL + "getParkSpaceDatasForCity";

    /**
     * 更改用户昵称
     */
    public static final String changeUserNickname = ROOT_URL + "changeUserNickname";

    /**
     * 用户请求控制车位锁
     */
    public static final String controlParkLock = ROOT_URL + "controlParkLock";

    /**
     * 获取一个订单的具体详情
     */
    public static final String getDetailOfParkOrder = ROOT_URL + "getDetailOfParkOrder";

    /**
     * 获取用户正在审核的停车位列表
     */
    public static final String getPassingParkFromUser = ROOT_URL + "getPassingParkFromUser";

    /**
     * 请求发送验证码
     */
    public static final String sendSms = ROOT_URL + "sendSms";

    /**
     * 核对验证码登录
     */
    public static final String checkCodeLogin = ROOT_URL + "checkCodeLogin";

    /**
     * 设置新密码
     */
    public static final String setPasswordLogin = ROOT_URL + "setPasswordLogin";

    /**
     * 校验验证码
     */
    public static final String checkCode = ROOT_URL + "checkCode";

    public static final String getAliAuthMessage = ROOT_URL + "getAliAuthMessage";

    /**
     * 上传支付宝用户授权参数
     */
    public static final String uploadAliAuthMessage = ROOT_URL + "uploadAliAuthMessage";

    /**
     * 安装师傅未来7天的空闲情况
     */
    public static final String getInstallWorkerTime = ROOT_URL + "getInstallWorkerTime";

    /**
     * 请求首页地图页面所有充电站的数据
     */
    public static final String getAllChargeStationData = ROOT_URL + "getChargeStationData";

    /**
     * 请求第二页停车场页面所有停车场的数据
     */
    public static final String getParkSpaceData = ROOT_URL + "getParkSpaceData";

    /**
     * 注册账号的请求
     */
    public static final String requestRegister = ROOT_URL + "requestRegister";

    /**
     * 请求充电桩评论数据
     */
    public static final String getChargeCommentData = ROOT_URL + "getChargeCommentData";

    public static final String setChargeCommentData = ROOT_URL + "setChargeCommentData";

    /**
     * 请求用户车牌信息
     */
    public static final String setUserCarNumberData = ROOT_URL + "setUserCarNumberData";

    /**
     * 获取个人信息页面的综合信息
     */
    public static final String getMineMessage = ROOT_URL + "getMineMessage";

    /**
     * 订单相关
     */
    public static final String setParkOrder = ROOT_URL + "setParkOrder";
    public static final String getParkOrder = ROOT_URL + "getParkOrder";

    public static final String startPark = ROOT_URL + "startPark";


    /**
     * 获得用户不同种类的订单
     */
    public static final String getAllOrdersDatas = ROOT_URL + "getAllOrdersDatas";


    /**
     * 获得一个停车位的信息
     */
    public static final String getOneParkSpace = ROOT_URL + "getOneParkSpace";

    /**
     * 获得一个充电桩的信息
     */
    public static final String getOneChargeStation = ROOT_URL + "getOneChargeStation";

    /**
     * 获取身份证信息
     */
    public static final String getIdMessage = "http://apicloud.mob.com/idcard/query";

    /**
     * 获取我的钱包页面的信息
     */
    public static final String getMyWalletData = ROOT_URL + "getMyWalletData";

    /**
     * 请求用户充电桩位数据
     */
    public static final String getChargeFromUser = ROOT_URL + "getChargeFromUser";

    public static final String setChargeForUser = ROOT_URL + "setChargeForUser";

    /**
     * 获取正在充电的电桩信息
     */
    public static final String getCharggingOrder = ROOT_URL + "getCharggingOrder";

    /**
     * 申请开放一个停车场（小区）
     */
    public static final String applyParkSpace = ROOT_URL + "applyParkSpace";

    /**
     * 获取正在停车中的订单信息
     */
    public static final String getRentingParkOrder = ROOT_URL + "getRentingParkOrder";

    /**
     * 开始停车
     */
    public static final String beginPark = ROOT_URL + "beginPark";

    /**
     * 删除用户优惠券
     */
    public static final String deleteUserDiscount = ROOT_URL + "deleteUserDiscount";

    /**
     * 获取未报销的发票
     */
    public static final String getInvoice = ROOT_URL + "getInvoice";

    /**
     * 获取报销的发票进度
     */
    public static final String getInvoiceSituation = ROOT_URL + "getInvoiceSituation";

    /**
     * 获取发票的到达时间
     */
    public static final String getInvoiceArriveTime = ROOT_URL + "getInvoiceArriveTime";

    /**
     * 申请发票报销
     */
    public static final String applyInvoiceReimbursement = ROOT_URL + "applyInvoiceReimbursement";

    /**
     * 添加收票地址
     */
    public static final String addAcceptTicketAddress = ROOT_URL + "addAcceptTicketAddress";

    /**
     * 修改收票地址
     */
    public static final String changeAcceptTicketAddress = ROOT_URL + "changeAcceptTicketAddress";

    /**
     * 获取收票地址
     */
    public static final String getAcceptTicketAddress = ROOT_URL + "getAcceptTicketAddress";

    /**
     * 删除收票地址
     */
    public static final String deleteAcceptTicketAddress = ROOT_URL + "deleteAcceptTicketAddress";

    /**
     * 设置默认收票地址
     */
    public static final String setDefaultAcceptTicketAddress = ROOT_URL + "setDefaultAcceptTicketAddress";

    /**
     * 获取默认的收票地址
     */
    public static final String getDefalutAcceptTicketAddress = ROOT_URL + "getDefalutAcceptTicketAddress";

    /**
     * 获取出租记录
     */
    public static final String getRentalRecord = ROOT_URL + "getRentalRecord";

    /**
     * 获取共享时间
     */
    public static final String getShareTime = ROOT_URL + "getShareTime";

    /**
     * 修改共享时间
     */
    public static final String editShareTime = ROOT_URL + "editShareTime";

    /**
     * 添加亲友
     */
    public static final String addFriend = ROOT_URL + "addFriend";

    /**
     * 获取亲友信息
     */
    public static final String getUserInfo = ROOT_URL + "getUserInfo";

    /**
     * 删除亲友
     */
    public static final String deleteFriend = ROOT_URL + "deleteFriend";

    /**
     * 修改亲友备注
     */
    public static final String modifyFriendNickname = ROOT_URL + "modifyFriendNickname";

    /**
     * 获取亲友
     */
    public static final String getBindingFriends = ROOT_URL + "getBindingFriends";

    /**
     * 申请添加车辆
     */
    public static final String applyAddNewCar = ROOT_URL + "applyAddNewCar";

    /**
     * 获取好友分享给我的车位
     */
    public static final String getFriendShareParkspace = ROOT_URL + "getFriendShareParkspace";

    /**
     * 车主请求控制车位锁
     */
    public static final String userControlParkLock = ROOT_URL + "userControlParkLock";

    /**
     * 获取车位状态
     */
    public static final String getParkLockStatus = ROOT_URL + "getParkLockStatus";

    /**
     * 获取停车位的共享时间以及预约时间
     */
    public static final String getParkSpaceTime = ROOT_URL + "getParkSpaceTime";

    /**
     * 上传图片
     */
    public static final String uploadPicture = ROOT_URL + "uploadPicture";

    /**
     * 获取押金金额
     */
    public static final String getDepositSum = ROOT_URL + "getDepositSum";

    /**
     * 取消申请添加停车位
     */
    public static final String cancelApplyParkSpace = ROOT_URL + "cancelApplyParkSpace";

    /**
     * 修改审核前的停车位资料
     */
    public static final String modifyAuditParkSpaceInfo = ROOT_URL + "modifyAuditParkSpaceInfo";

    /**
     * 获取用户信用记录
     */
    public static final String getCreditRecord = ROOT_URL + "getCreditRecord";

    /**
     * 正在停车的用户请求延长停车时间
     */
    public static final String extendParkingTime = ROOT_URL + "extendParkingTime";

    /**
     * 重新给订单分配车位
     */
    public static final String redistributionOrderParkSpace = ROOT_URL + "redistributionOrderParkSpace";

    /**
     * 预约订单锁定的车位
     */
    public static final String reserveLockedParkSpaceForOrder = ROOT_URL + "reserveLockedParkSpaceForOrder";

    /**
     * 获取用户全部卡，地区卡，全国卡
     */
    public static final String getUserMonthlyCards = ROOT_URL + "getUserMonthlyCards";

    /**
     * 获取订单的评价
     */
    public static final String getOrderComment = ROOT_URL + "getOrderComment";

    /**
     * 获取开放的地区的月卡
     */
    public static final String getOpenAreaMonthlyCard = ROOT_URL + "getOpenAreaMonthlyCard";

    /**
     * 查询车位锁状态
     */
    public static final String selectParkState = ROOT_URL + "selectParkState";

    /**
     * 获取支付宝购买月卡订单信息
     */
    public static final String getAlipayBuyMonthlyCardInfo = ROOT_URL + "getAlipayBuyMonthlyCardInfo";

    /**
     * 获取微信购买月卡订单信息
     */
    public static final String getWechatBuyMonthlyCardInfo = ROOT_URL + "getWechatBuyMonthlyCardInfo";

    /**
     * 请求订单停车
     */
    public static final String requestOrderPark = ROOT_URL + "requestOrderPark";

    /**
     * 请求绑定微信
     */
    public static final String requestBindWechat = ROOT_URL + "requestBindWechat";

    /**
     * 请求解除绑定第三方账号
     */
    public static final String requestUnbindThirdPartyAccount = ROOT_URL + "requestUnbindThirdPartyAccount";

    /**
     * 请求发送验证码
     */
    public static final String sendVerificationCode = ROOT_URL + "sendVerificationCode";

    /**
     * 校验验证码
     */
    public static final String checkVerificationCode = ROOT_URL + "checkVerificationCode";

    /**
     * 通过原密码修改密码
     */
    public static final String requestChangePassword = ROOT_URL + "requestChangePassword";

    /**
     * 订单投诉
     */
    public static final String orderComplaint = ROOT_URL + "orderComplaint";

    /**
     * 获取用户的车牌号
     */
    public static final String getCarNumber = ROOT_URL + "getCarNumber";

    /**
     * 修改手机号码
     */
    public static final String changeTelephoneNumber = ROOT_URL + "changeTelephoneNumber";

    /**
     * 提现余额
     */
    public static final String withdrawlBalance = ROOT_URL + "withdrawlBalance";

    /**
     * 设置支付密码
     */
    public static final String setPaymentPassword = ROOT_URL + "setPaymentPassword";

    /**
     * 重置支付密码
     */
    public static final String resetPaymentPassword = ROOT_URL + "ResetPaymentPassword";

    /**
     * 获取调起支付宝进行芝麻认证的url
     */
    public static final String getCertifyZhimaUrl = ROOT_URL + "getCertifyZhimaUrl";

    /**
     * 获取芝麻认证结果
     */
    public static final String getCertifyZhimaResult = ROOT_URL + "getCertifyZhimaResult";

    /**
     * 预约亲友的车位
     */
    public static final String reserveFriendParkSpace = ROOT_URL + "reserveFriendParkSpace";

    /**
     * 获取在好友车位停车的记录
     */
    public static final String getFriendParkSpaceRecord = ROOT_URL + "getFriendParkSpaceRecord";

    /**
     * 登录判断手机号是否是新用户
     */
    public static final String phoneIsNewUser = ROOT_URL + "phoneIsNewUser";

    /**
     * 微信登录
     */
    public static final String wechatLogin = ROOT_URL + "wechatLogin";

    /**
     * 新用户微信登录
     */
    public static final String initialWechatLogin = ROOT_URL + "initialWechatLogin";

    /**
     * 老用户绑定微信并登录
     */
    public static final String bindingWechatLogin = ROOT_URL + "bindingWechatLogin";

    /**
     * 密码登录
     */
    public static final String requestPasswordLogin = ROOT_URL + "requestPasswordLogin";

    /**
     * 短信登录
     */
    public static final String requestSmsLogin = ROOT_URL + "requestSmsLogin";

    /**
     * 忘记密码登录
     */
    public static final String forgetPasswordLogin = ROOT_URL + "forgetPasswordLogin";

    /**
     * 新用户登录
     */
    public static final String newUserLogin = ROOT_URL + "newUserLogin";

    /**
     * 修改好友分享给我的车位的备注
     */
    public static final String changeParkSpaceNote = ROOT_URL + "changeParkSpaceNote";

    /**
     * 删除好友分享给我的车位
     */
    public static final String deleteFriendParkSpace = ROOT_URL + "deleteFriendParkSpace";

    /**
     * 获取亲友今后的预定记录
     */
    public static final String getFriendFutureReserveRecord = ROOT_URL + "getFriendFutureReserveRecord";

    /**
     * 获取在指定好友车位的预定记录
     */
    public static final String getOneFriendParkSpaceRecord = ROOT_URL + "getOneFriendParkSpaceRecord";

    /**
     * 取消在好友车位的预定记录
     */
    public static final String cancelRecordOnFriendParkSpace = ROOT_URL + "cancelRecordOnFriendParkSpace";

    /**
     * 好友车位预定记录开锁
     */
    public static final String openLockByRecord = ROOT_URL + "openLockByRecord";

    /**
     * 获取停车订单的详细信息
     */
    public static final String getParkOrderDetail = ROOT_URL + "getParkOrderDetail";

    /**
     * 绑定免费车场
     */
    public static final String bindFreeParkLot = ROOT_URL + "bindFreeParkLot";

    /**
     * 解绑免费车场
     */
    public static final String unbindFreeParkLot = ROOT_URL + "unbindFreeParkLot";

    /**
     * 新增长租订单
     */
    public static final String addLongRentOrder = ROOT_URL + "addLongRentOrder";

    /**
     * 获取支付宝支付长租订单信息
     */
    public static final String getAlipayLongRentOrderInfo = ROOT_URL + "getAlipayLongRentOrderInfo";

    /**
     * 获取微信支付长租订单信息
     */
    public static final String getWechatLongRentOrderInfo = ROOT_URL + "getWechatLongRentOrderInfo";

    /**
     * 修改车位的长租状态
     */
    public static final String editLongRentStatus = ROOT_URL + "editLongRentStatus";

    /**
     * 车锁保障
     */
    public static final String reportLockFailure = ROOT_URL + "reportLockFailure";

}

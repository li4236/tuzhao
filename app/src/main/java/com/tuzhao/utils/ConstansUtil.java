package com.tuzhao.utils;

import android.graphics.Color;

/**
 * Created by juncoder on 2018/4/11.
 */

public class ConstansUtil {

    public static final int B1_COLOR = Color.parseColor("#323232");

    public static final int Y2_COLOR = Color.parseColor("#ffcc30");

    public static final int Y3_COLOR = Color.parseColor("#ffa830");

    public static final int GREEN5_COLOR = Color.parseColor("#1fa72f");

    public static final int G6_COLOR = Color.parseColor("#808080");

    public static final int G10_COLOR = Color.parseColor("#cccccc");

    public static final String SERVER_ERROR = "服务器异常，请稍后重试";

    public static final String LOGIN_ACTION = "LOGIN_ACTION";//登录成功的动作
    public static final String LOGOUT_ACTION = "LOGIN_SUCCESS";//退出登录的动作

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "LoginSuccess";

    /**
     * 强制退出登录
     */
    public static final String FORCE_LOGOUT = "ForceLogout";

    public static final float MAX_CREDIT_SCORE = 950;

    public static final float VERY_GOOD_CREDIT_SCORE = 750;

    public static final float GOOD_CREDIT_SCORE = 650;

    public static final float FINE_CREDIT_SCORE = 550;

    public static final float POOR_CREDIT_SCORE = 350;

    public static final float VERY_POOR_CREDIT_SCORE = 200;

    /**
     * 最小开票金额
     */
    public static final int MINIMUN_INVOICE_AMOUNT = 100;

    /**
     * 长租订单最大保留的分钟数，过期自动取消
     */
    public static final int MAX_KEEP_LONG_RENT_ORDER_MINUTE = 15;

    /**
     * 一天的分钟数
     */
    public static final int ONE_DAY_MINUTES = 24 * 60;

    /**
     * 科大讯飞AppId
     */
    public static final String SPEECH_UTILITY_APP_ID = "592e14d9";

    /**
     * 微信的AppId
     */
    public static final String WECHAT_APP_ID = "wxb68fabefc83d5c48";

    /**
     * 微信小程序原始id
     */
    public static final String WECHAT_MINI_ID = "gh_93edeeccccd4";

    /**
     * 微信登录返回的code
     */
    public static final String WECHAT_CODE = "WechatCode";

    /**
     * 支付类型，0是停车订单支付，1是添加车位缴纳押金支付，2是购买月卡支付
     */
    public static final String PAY_TYPE = "PayType";

    /**
     * 支付的金额
     */
    public static final String PAY_MONEY = "PayMoney";

    public static final String PAY_SUCCESS = "PaySuccess";

    /**
     * 支付错误
     */
    public static final String PAY_ERROR = "PayError";

    /**
     * 用户取消支付
     */
    public static final String PAY_CANCEL = "PayCancel";

    /**
     * Intent里面的消息
     */
    public static final String INTENT_MESSAGE = "IntentMessage";

    public static final String PARK_SPACE_ID = "ParkSpaceId";

    public static final String PARK_SPACE_INFO = "ParkSpaceInfo";

    /**
     * 类型
     */
    public static final String TYPE = "Type";

    /**
     * 人民币符号
     */
    public static final String YUAN = "¥";

    /**
     * 城市码
     */
    public static final String CITY_CODE = "CityCode";

    /**
     * 传递选中的发票给确认订单
     */
    public static final String INVOICE_LIST = "InvoiceList";

    /**
     * 返回收票地址给确认订单
     */
    public static final String ACCEPT_ADDRESS_INFO = "AcceptAddressInfo";

    /**
     * 返回新建的收票地址给收票地址
     */
    public static final String ADD_ACCEPT_ADDRESS = "AddAcceptAddress";

    /**
     * 修改收票地址
     */
    public static final String CHAGNE_ACCEPT_ADDRESS = "ChangeAcceptAddress";

    public static final int REQUSET_CODE = 0x111;

    public static final int REQUEST_CODE_TWO = 0x123;

    public static final int RESULT_CODE = 0x222;

    /**
     * 调用相册获取图片的请求码
     */
    public static final int PICTURE_REQUEST_CODE = 0x666;

    /**
     * 跳转到另一个activity的key
     */
    public static final String REQUEST_FOR_RESULT = "RequestForResult";

    /**
     * key
     */
    public static final String FOR_REQEUST_RESULT = "ForRequestResult";

    /**
     * 报销的发票的详情
     */
    public static final String INVOICE_SITUATION = "InvoiceSituation";

    /**
     * 从添加车位跳转到修改共享时间
     */
    public static final String ADD_PARK_SPACE_TEME = "AddParkSpaceTime";

    /**
     * 查看大图的图片
     */
    public static final String PHOTO_IMAGE = "PhotoImage";

    /**
     * 订单状态
     */
    public static final String STATUS = "Status";

    /**
     * 订单列表
     */
    public static final String PARK_ORDER_LIST = "ParkOrderList";

    /**
     * 订单
     */
    public static final String PARK_ORDER_INFO = "ParkOrderInfo";

    /**
     * 订单id
     */
    public static final String PARK_ORDER_ID = "ParkOrderId";

    /**
     * 取消订单
     */
    public static final String CANCEL_ORDER = "CancelOrder";

    /**
     * 完成预约，即是已经开始变成停车中
     */
    public static final String FINISH_APPOINTMENT = "FinishAppointment";

    /**
     *
     */
    public static final String CHANGE_APPOINTMENT_INFO = "ChangeAppointmentInfo";

    /**
     * 修改停车信息，正在停车中的延长停车时长
     */
    public static final String CHANGE_PARK_ORDER_INRO = "ChangeParkOrderInfo";

    /**
     * 完成订单
     */
    public static final String FINISH_PARK = "FinishPark";

    /**
     * 订单支付完成
     */
    public static final String FINISH_PAY_ORDER = "FinishPayOrder";

    /**
     * 跳转页面时如果获取信息失败则显示"客户端异常，请稍后再试"
     */
    public static final String APP_ERROR_HINT = "客户端异常，请稍后再试";

    /**
     * 优惠券列表
     */
    public static final String DISCOUNT_LIST = "DiscountList";

    /**
     * 订单的金额
     */
    public static final String ORDER_FEE = "OrderFee";

    /**
     * 选择优惠券的请求码
     */
    public static final int DISOUNT_REQUEST_CODE = 0x347;

    /**
     * 是否选择优惠券/选择的优惠券
     */
    public static final String CHOOSE_DISCOUNT = "ChooseDisount";

    /**
     * 打开停车评价
     */
    public static final String OPEN_PARK_COMMENT = "OpenParkComment";

    /**
     * 关闭停车评价，显示停车详情
     */
    public static final String CLOSE_PARK_COMMENT = "CloseParkComment";

    /**
     * 评价成功
     */
    public static final String COMMENT_SUCCESS = "CommentSuccess";

    /**
     * 删除停车订单
     */
    public static final String DELETE_PARK_ORDER = "DeleteParkOrder";

    /**
     * 添加车位点击下一步
     */
    public static final String NEXT_STEP = "NextStep";

    /**
     * 添加车位时代表是第几个页面点击下一步的
     */
    public static final String POSITION = "Position";

    /**
     * 跳转到时间设置fragment
     */
    public static final String JUMP_TO_TIME_SETTING = "JumpToTimeSetting";

    /**
     * 跳转到押金支付fragment
     */
    public static final String JUMP_TO_DEPOSIT_PAYMENT = "JumpToDepositPayment";

    /**
     * 车场id
     */
    public static final String PARK_LOT_ID = "ParkLotId";


    /**
     * 车场名称
     */
    public static final String PARK_LOT_NAME = "ParkLotName";

    /**
     * 车场信息
     */
    public static final String PARK_LOT_INFO = "ParkLotInfo";

    /**
     * 电站id
     */
    public static final String CHARGE_ID = "ChargeId";

    /**
     * 对话框弹出,订单评论
     */
    public static final String DIALOG_SHOW = "DialogShow";

    /**
     * 对话框消失，订单评论
     */
    public static final String DIALOG_DISMISS = "DialogDismiss";

    /**
     * 对话框不可取消的，则把返回键的事件传递出去
     */
    public static final String DIALOG_ON_BACK_PRESS = "DialogOnBackPress";

    /**
     * 弹出对话框，我的车位fragment,检查更新
     */
    public static final String SHOW_DIALOG = "ShowDialog";

    public static final String SHARE_TIME_INFO = "ShareTimeInfo";

    /**
     * 押金金额
     */
    public static final String DEPOSIT_SUM = "DepositSum";

    /**
     * 支付押金成功
     */
    public static final String PAY_DEPOSIT_SUM_SUCCESS = "PayDepositSumSuccess";

    /**
     * 取消申请添加停车位
     */
    public static final String CANCEL_APPLY_PARK_SPACE = "CancelApplyParkSpace";

    /**
     * 修改审核前的停车位的资料
     */
    public static final String MODIFY_AUDIT_PARK_SPACE_INFO = "ModifyAuditParkSpaceInfo";

    /**
     * 卡列表
     */
    public static final String CARD_INFO_LIST = "CardInfoList";

    /**
     * 月卡的有效期
     */
    public static final String ALLOTED_PERIOD = "AllotedPeriod";

    /**
     * 显示空布局
     */
    public static final String SHOW_EMPTY_VIEW = "ShowEmptyView";

    /**
     * 通行码，短信修改密码时返回
     */
    public static final String PASS_CODE = "PassCode";

    /**
     * 修改密码
     */
    public static final String CHANGE_PASSWORD = "ChangePassword";

    /**
     * 手机号
     */
    public static final String TELEPHONE_NUMBER = "TelephoneNumber";

    /**
     * 预约时间，我的车位fragment传给activity
     */
    public static final String APPOINTMENT_TIME = "AppointmentTime";

    /**
     * 数据的大小
     */
    public static final String SIZE = "Size";

    /**
     * 我的车位切换到左边
     */
    public static final String LEFT = "Left";

    /**
     * 我的车位切换到右边
     */
    public static final String RIGHT = "Right";

    /**
     * 支付密码
     */
    public static final String PAYMENT_PASSWORD = "PaymentPassword";

    /**
     * 修改头像
     */
    public static final String CHANGE_PORTRAIT = "ChangePortrait";

    /**
     * 修改昵称
     */
    public static final String CHANGE_NICKNAME = "ChangeNickname";

    /**
     * 设置支付密码
     */
    public static final String SET_PAYMENT_PASSWORD = "SetPaymentPassword";

    /**
     * 重设支付密码
     */
    public static final String RESET_PAYMENT_PASSWORD = "ResetPaymentPassword";

    /**
     * 提现成功
     */
    public static final String WITHDRAWL_SUCCESS = "WithdrawlSuccess";

    /**
     * 经度
     */
    public static final String LATITUDE = "Latitude";

    /**
     * 纬度
     */
    public static final String LONGITUDE = "Longitude";

    /**
     * 经纬度
     */
    public static final String LATLNG = "LatLng";

    /**
     * 密码登录
     */
    public static final String PASSWORD_LOGIN = "PasswordLogin";

    /**
     * 短信登录
     */
    public static final String SMS_LOGIN = "SmsLogin";

    /**
     * 用户信息
     */
    public static final String USER_INFO = "UserInfo";

    /**
     * 弹出fragment
     */
    public static final String FINISH_FRAGMENT = "FinishFragment";

    /**
     * 微信登录绑定手机号
     */
    public static final String WECHAT_TELEPHONE_LOGIN = "WechatTelephoneLogin";

    /**
     * 微信初始输入密码登录
     */
    public static final String WECHAT_PASSWORD_LOGIN = "WechatPasswordLogin";

    /**
     * 忘记密码
     */
    public static final String FORGET_PASSWORD = "ForgetPassword";

    /**
     * 显示更新对话框
     */
    public static final String SHOW_UPDATE_DIALOG = "ShowUpdateDialog";

    /**
     * 开始下载apk
     */
    public static final String DOWNLOAD_APK = "DownloadApk";

    /**
     * 显示下载进度的对话框
     */
    public static final String SHOW_UPDATE_NOTIFICATION = "ShowUpdateNotification";

    /**
     * 用户手动检查更新
     */
    public static final String USER_UPDATE = "UserUpdate";

    /**
     * 更新信息
     */
    public static final String UPDATE_INFO = "UpdateInfo";

    /**
     * 更新进度
     */
    public static final String UPDATE_PROGRESS = "UpdateProgress";

    /**
     * 请求安装未知来源应用权限
     */
    public static final String REQUEST_INSTALL_PERMISSION = "RequestInstallPermission";

    /**
     * 安装apk
     */
    public static final String INSTALL_APK = "InstallApk";

    /**
     * 更新的对话框关闭后返回主activity时定位到当前位置
     */
    public static final String UPDATE_ACTIVITY_FINISH = "UpdateActivityFinish";

    /**
     * 车牌号
     */
    public static final String CAR_NUMBER = "CarNumber";

    /**
     * 预定车位成功
     */
    public static final String BOOK_PARK_SPACE = "BookParkSpace";

    /**
     * 新用户设置密码
     */
    public static final String SET_NEW_USER_PASSWORD = "SetNewUserPassword";

    /**
     * 修改好友车位备注
     */
    public static final String CHANGE_PARK_SPACE_NOTE = "ChangeParkSpaceNote";

    /**
     * 删除好友分享给我的车位
     */
    public static final String DELETE_FRIENT_PARK_SPACE = "DeleteFriendParkSpace";

    /**
     * 请求数据的开始项
     */
    public static final String START_ITME = "StartItem";

    /**
     * 取消记录
     */
    public static final String CANCEL_RECORD = "CancelRecord";

    /**
     * 编辑的状态
     */
    public static final String EDIT_STATUS = "EditStatus";

    /**
     * 删除收藏记录
     */
    public static final String DELETE_COLLECTION = "DeleteCollection";

    /**
     * 开发票成功
     */
    public static final String INVOICE_SUCCESS = "InvoiceSuccess";

    /**
     * 删除好友分享的车位
     */
    public static final String DELETE_SHARE_PARK_SPACE = "DeleteShareParkSpace";

    /**
     * 键盘高度改变
     */
    public static final String KEYBOARD_HEIGHT_CHANGE = "KeyboardHeightChange";

    public static final String HEIGHT = "Height";

    /**
     * 绑定/解绑免费车场
     */
    public static final String CHANGE_BIND_FREE_PARK = "ChangeBindFreePark";

    /**
     * 收到极光推送的订单结束停车消息
     */
    public static final String JI_GUANG_PARK_END = "JiGuangParkEnd";

    public static final String TIME = "Time";

    /**
     * 在已完成的订单里添加一条记录
     */
    public static final String ADD_FINISH_PARK_ORDER = "AddFinishParkOrder";

    /**
     * 订单编号
     */
    public static final String ORDER_NUMBER = "OrderNumber";

}

package com.tuzhao.utils;

import android.graphics.Color;

/**
 * Created by juncoder on 2018/4/11.
 */

public class ConstansUtil {

    public static final int B1_COLOR = Color.parseColor("#323232");

    public static final int Y2_COLOR = Color.parseColor("#ffcc30");

    public static final int Y3_COLOR = Color.parseColor("#ffa830");

    public static final int G6_COLOR = Color.parseColor("#808080");

    public static final int G10_COLOR = Color.parseColor("#cccccc");

    public static final String SERVER_ERROR = "服务器异常，请稍后重试";

    /**
     * 退出登录
     */
    public static final String LOGOUT_ACTION = "LOGOUT_ACTION";

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
     * 微信的AppId
     */
    public static final String WECHAT_APP_ID = "wxb68fabefc83d5c48";

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

    /**
     * 调用相册获取图片的请求码
     */
    public static final int PICTURE_REQUEST_CODE = 0x666;

    /**
     * 跳转到另一个activity的key
     */
    public static final String REQUEST_FOR_RESULT = "RequestForResult";

    /**
     * activity返回给上个activity的结果的key
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
    public static final String ORDER_STATUS = "OrderStatus";

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

    public static final String PARK_LOT_ID = "ParkLotId";

    /**
     * 车场名称
     */
    public static final String PARK_LOT_NAME = "ParkLotName";

    /**
     * 对话框弹出,订单评论
     */
    public static final String DIALOG_SHOW = "DialogShow";

    /**
     * 对话框消失，订单评论
     */
    public static final String DIALOG_DISMISS = "DialogDismiss";

    /**
     * 弹出对话框，我的车位fragment
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

}

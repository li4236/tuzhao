package com.tuzhao.utils;

import android.graphics.Color;

/**
 * Created by juncoder on 2018/4/11.
 */

public class ConstansUtil {

    public static final int B1_COLOR = Color.parseColor("#323232");

    public static final int Y2_COLOR = Color.parseColor("#ffcc30");


    /**
     * 微信的AppId
     */
    public static final String WECHAT_APP_ID = "wxb68fabefc83d5c48";

    /**
     * 支付类型，0是停车订单支付，1是添加车位缴纳押金支付
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
    public static final String FOR_REQUEST_RESULT = "ForRequestResult";


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
     * 评论的对话框弹出
     */
    public static final String DIALOG_SHOW = "DialogShow";

    /**
     * 评论的对话框消失
     */
    public static final String DIALOG_DISMISS = "DialogDismiss";

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

}

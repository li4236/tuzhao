package com.tuzhao.utils;

/**
 * Created by juncoder on 2018/4/11.
 */

public class ConstansUtil {

    public static final String PARK_SPACE_ID = "ParkSpaceId";

    public static final String PARK_SPACE_INFO = "ParkSpaceInfo";

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
     * 完成预约，既是已经开始变成停车中
     */
    public static final String FINISH_APPOINTMENT = "FinishAppointment";

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
     * 车场名称
     */
    public static final String PARK_LOT_NAME = "ParkLotName";

    /**
     * 收益比
     */
    public static final String REVENUE_RATIO = "RevenueRatio";

    /**
     * 车位号码
     */
    public static final String PARK_SPACE_NUMBER = "ParkSpaceNumber";

    /**
     * 车位描述
     */
    public static final String PARK_SPACE_DESCRIPTION = "ParkSpaceDescription";

    /**
     * 身份证正面照
     */
    public static final String ID_CARD_POSITIVE_PHOTO = "IdCardPositivePhoto";

    /**
     * 身份证反面照
     */
    public static final String ID_CARD_NEGATIVE_PHOTO = "IdCardNegativePhoto";

    /**
     * 车位产权正面照
     */
    public static final String PROPERTY_POSITIVE_PHOTO = "PropertyPositivePhoto";

    /**
     * 车位产权反面照
     */
    public static final String PROPERTY_NEGATIVE_PHOTO = "PropertyNegativePhoto";

}

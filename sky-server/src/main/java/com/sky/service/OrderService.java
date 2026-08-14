package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.*;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    OrderVO getOrderVO(Long id);

    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 用户取消订单
     * @param id 订单id
     */
    void cancel(Long id) throws Exception;

    /**
     * 再来一单
     * @param id 订单id
     */
    void repetition(Long id);

    /**
     * 催单
     * @param id 订单id
     */
    void reminder(Long id);
}

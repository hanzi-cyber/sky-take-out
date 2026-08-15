package com.sky.service;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;

public interface AdminOrderService {
    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO countOrderStatistics();

    void cancel(OrdersCancelDTO ordersCancelDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 接单
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 派送订单
     */
    void delivery(Long id);

    /**
     * 完成订单
     */
    void complete(Long id);
}

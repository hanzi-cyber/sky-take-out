package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;

public interface AdminOrderService {
    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO countOrderStatistics();
}

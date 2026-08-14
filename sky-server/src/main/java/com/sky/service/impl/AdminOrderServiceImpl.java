package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.mapper.AdminOrderMapper;
import com.sky.result.PageResult;
import com.sky.service.AdminOrderService;
import com.sky.vo.OrderStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Autowired
    private AdminOrderMapper adminOrderMapper;

    @Override
    public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = adminOrderMapper.page(ordersPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());

    }

    @Override
    public OrderStatisticsVO countOrderStatistics() {
        OrderStatisticsVO orderStatisticsVO = adminOrderMapper.countOrderStatistics();
        return orderStatisticsVO;
    }
}

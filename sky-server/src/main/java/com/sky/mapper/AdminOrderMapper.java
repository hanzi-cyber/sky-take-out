package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminOrderMapper {


    Page<Orders> page(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(case when status = 2 then 1 end) as toBeConfirmed, " +
            "count(case when status = 3 then 1 end) as confirmed, " +
            "count(case when status = 4 then 1 end) as deliveryInProgress " +
            "from orders")
    OrderStatisticsVO countOrderStatistics();
}

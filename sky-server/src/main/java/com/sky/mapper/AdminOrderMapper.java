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

    @Select("select count(*) from orders where status in (3,4,2) group by status;")
    OrderStatisticsVO countOrderStatistics();
}

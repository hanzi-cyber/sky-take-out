package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);


    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);



    Double getTurnover(@Param("beginTime") LocalDateTime beginTime,
                       @Param("endTime") LocalDateTime endTime,
                       @Param("status") Integer status);

    Integer getOrderCount(@Param("beginTime")LocalDateTime beginTime,@Param("endTime") LocalDateTime endTime);

    Integer getTotalOrderCount(@Param("startTime") LocalDateTime startTime,
                               @Param("breakTime") LocalDateTime breakTime);

    Integer getValidOrderCount(@Param("beginTime") LocalDateTime beginTime,
                               @Param("endTime") LocalDateTime endTime);

    Integer getTotalValidOrderCount(@Param("startTime") LocalDateTime startTime,
                                    @Param("breakTime") LocalDateTime breakTime);

    List<GoodsSalesDTO> getSalesTop10(@Param("beginTime") LocalDateTime beginTime,
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 订单管理数据总览（各状态订单数量）
     */
    @Select("select count(id) as allOrders, " +
            "count(case when status = 2 then 1 end) as waitingOrders, " +
            "count(case when status = 3 then 1 end) as deliveredOrders, " +
            "count(case when status = 5 then 1 end) as completedOrders, " +
            "count(case when status = 6 then 1 end) as cancelledOrders " +
            "from orders " +
            "where order_time >= #{begin} and order_time <= #{end}")
    OrderOverViewVO getOrderOverView(@Param("begin") LocalDateTime begin,
                                     @Param("end") LocalDateTime end);
}

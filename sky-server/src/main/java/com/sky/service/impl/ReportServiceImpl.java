package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        String date = StringUtils.join(dateList, ",");
        TurnoverReportVO turnoverReportVO=new TurnoverReportVO();
        turnoverReportVO.setDateList(date);

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate,LocalTime.MAX);
            Double turnover = orderMapper.getTurnover(beginTime, endTime, Orders.COMPLETED);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        String turnover = StringUtils.join(turnoverList, ",");
        turnoverReportVO.setTurnoverList(turnover);

        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserReport(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        String date = StringUtils.join(dateList, ",");
        UserReportVO userReportVO=new UserReportVO();
        userReportVO.setDateList(date);

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Integer newUser = userMapper.getNewUserCount(beginTime, endTime);
            newUserList.add(newUser);
            Integer totalUser = userMapper.getTotalUserCount(endTime);
            totalUserList.add(totalUser);
        }
        userReportVO.setNewUserList(StringUtils.join(newUserList, ","));
        userReportVO.setTotalUserList(StringUtils.join(totalUserList, ","));
        return userReportVO;
    }

    @Override
    public OrderReportVO getOrderReport(LocalDate begin, LocalDate end) {
        LocalDateTime startTime = convertToLocalDateTimeBegin(begin);
        LocalDateTime breakTime = convertToLocalDateTimeEnd(end);
        // 获取日期列表
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        String date = StringUtils.join(dateList, ",");
        OrderReportVO orderReportVO=new OrderReportVO();
        orderReportVO.setDateList(date);
        //订单数列表
        List<Integer> orderCountList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Integer orderCount = orderMapper.getOrderCount(beginTime, endTime);
            orderCountList.add(orderCount);
        }
        orderReportVO.setOrderCountList(StringUtils.join(orderCountList, ","));
        //订单总数
        Integer totalOrderCount = orderMapper.getTotalOrderCount(startTime, breakTime);
        orderReportVO.setTotalOrderCount(totalOrderCount);
        //有效订单数列表
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Integer validOrderCount = orderMapper.getValidOrderCount(beginTime, endTime);
            validOrderCountList.add(validOrderCount);
        }
        orderReportVO.setValidOrderCountList(StringUtils.join(validOrderCountList, ","));
        //有效订单总数
        Integer validOrderCount = orderMapper.getTotalValidOrderCount(startTime, breakTime);
        orderReportVO.setValidOrderCount(validOrderCount);
        //订单完成率，保留两位小数
        double orderCompletionRate = 0.0;
        if (totalOrderCount != null && totalOrderCount.doubleValue() != 0) {
            orderCompletionRate = (validOrderCount.doubleValue() / totalOrderCount.doubleValue());
            orderCompletionRate = Math.round(orderCompletionRate * 100) / 100.0;
        }
        orderReportVO.setOrderCompletionRate(orderCompletionRate);
        return orderReportVO;

    }

    @Override
    public SalesTop10ReportVO getSalesTop10Report(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = convertToLocalDateTimeBegin(begin);
        LocalDateTime endTime = convertToLocalDateTimeEnd(end);
        // 一条SQL同时查出top10商品名称和销量
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);

        List<String> names = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : salesTop10) {
            names.add(goodsSalesDTO.getName());
            numbers.add(goodsSalesDTO.getNumber());
        }

        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        salesTop10ReportVO.setNameList(StringUtils.join(names, ","));
        salesTop10ReportVO.setNumberList(StringUtils.join(numbers, ","));
        return salesTop10ReportVO;
    }


    private LocalDateTime convertToLocalDateTimeBegin(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }
    private LocalDateTime convertToLocalDateTimeEnd(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
    }



}

package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
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

    @Autowired
    private WorkSpaceService workSpaceService;

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


    /**
     * 导出最近30天的运营数据Excel报表
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // 1. 查询最近30天的运营数据（不含今天，今天是进行中的数据）
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        // 概览数据：整个区间的汇总
        BusinessDataVO businessData = workSpaceService.getBusinessData(
                LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        // 2. 基于模板文件创建Excel
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try (XSSFWorkbook excel = new XSSFWorkbook(in)) {
            XSSFSheet sheet = excel.getSheetAt(0);

            // 第2行：时间区间（B2:G2已合并）
            sheet.getRow(1).getCell(1).setCellValue("时间区间：" + dateBegin + "至" + dateEnd);

            // 第4行：营业额、订单完成率、新增用户数
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            // 第5行：有效订单、平均客单价
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            // 第8行起：填充每日明细数据，共30天
            int rowNum = 7;
            for (LocalDate date = dateBegin; !date.isAfter(dateEnd); date = date.plusDays(1)) {
                BusinessDataVO dailyData = workSpaceService.getBusinessData(
                        LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(rowNum++);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(dailyData.getTurnover());
                row.getCell(3).setCellValue(dailyData.getValidOrderCount());
                row.getCell(4).setCellValue(dailyData.getOrderCompletionRate());
                row.getCell(5).setCellValue(dailyData.getUnitPrice());
                row.getCell(6).setCellValue(dailyData.getNewUsers());
            }

            // 3. 通过输出流将Excel下载到客户端
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("content-disposition",
                    "attachment;filename=" + URLEncoder.encode("运营数据报表", "UTF-8") + ".xlsx");
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LocalDateTime convertToLocalDateTimeBegin(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }
    private LocalDateTime convertToLocalDateTimeEnd(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
    }



}

package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> getTurnoverReport(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        TurnoverReportVO turnoverReportVO = reportService.getTurnoverReport(begin,end);
        return Result.success(turnoverReportVO);
    }


    @GetMapping("/userStatistics")
    public Result<UserReportVO> getUserReport(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                              @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        UserReportVO userReportVO = reportService.getUserReport(begin,end);
        return Result.success(userReportVO);
    }

    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> getOrderReport(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        OrderReportVO orderReportVO = reportService.getOrderReport(begin,end);
        return Result.success(orderReportVO);
    }

    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> getSalesTop10Report(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        SalesTop10ReportVO salesTop10ReportVO = reportService.getSalesTop10Report(begin,end);
        return Result.success(salesTop10ReportVO);
    }


}






















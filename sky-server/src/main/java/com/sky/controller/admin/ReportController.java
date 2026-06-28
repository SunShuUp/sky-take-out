package com.sky.controller.admin;

import com.sky.entity.Orders;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;


@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "数据统计相关接口")
public class ReportController {
    @Autowired
    ReportService reportService;

    /**
     * 导出
     * @param response 用于将Excel文件写会给客户端
     */
    @GetMapping("/export")
    @ApiOperation("导出运营数据excel报表")
    public void  export(HttpServletResponse response){
        reportService.exportBusinessData(response);
    }
    @GetMapping("/top10")
    @ApiOperation("查询销量排名top10接口")
    public Result<SalesTop10ReportVO>  getTop10(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        Integer status = Orders.COMPLETED;
        SalesTop10ReportVO salesTop10ReportVO = reportService.getTop10(begin,end,status);
        return Result.success(salesTop10ReportVO);
    }
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计接口")
    public Result<UserReportVO>  userStatistics(
           @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
           @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        UserReportVO userReportVO=reportService.userStatistics(begin,end);
        return Result.success(userReportVO);
    }

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计接口")
    public Result<TurnoverReportVO>  turnoverStatistics(@RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginTime,
                                                        @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime){
        log.info("营业额统计接口：beginTime={}, endTime={}", beginTime, endTime);
        TurnoverReportVO turnoverReportVO= reportService.turnoverStatistics(beginTime,endTime);
        return Result.success(turnoverReportVO);
    }
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计接口")
    public  Result<OrderReportVO>  OrdersStatistics(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd")  LocalDate end){
        OrderReportVO orderReportVO= reportService.OrdersStatistics(begin,end);
        return Result.success(orderReportVO);
    }
}

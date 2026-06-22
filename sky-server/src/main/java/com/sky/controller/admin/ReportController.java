package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/report")
@Slf4j
@Api(tags = "数据统计相关接口")
public class ReportController {
    @GetMapping("/export")
    @ApiOperation("导出excel报表")
    public Result<String> export(){
        return Result.success();
    }
    @GetMapping("/top10")
    @ApiOperation("查询销量排名top10接口")
    public Result<Integer>  getTop10( String beginTime, String end){
        return Result.success();
    }
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计接口")
    public Result<Integer>  getUserStatistics(String beginTime, String endTime){
        return Result.success();
    }

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计接口")
    public Result<Integer>  getTurnoverStatistics(String beginTime, String endTime){
        return Result.success();
    }
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计接口")
    public  Result<String>  getOrdersStatistics(String beginTime, String endTime){
        return Result.success();
    }
}

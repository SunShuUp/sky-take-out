package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workspace")
@Api(tags = "工作台接口")
@Slf4j
public class WorkspaceCtroller {

    @GetMapping("/overviewSetmeals")
    @ApiOperation("套餐总览接口")
    public Result<Integer>  getOverviewSetmeals(){
        return Result.success();
    }
    @GetMapping("/businessData")
    @ApiOperation("查询今日运营数据")
    public Result<Integer>  getBusinessData(){
        return Result.success();
    }
    @GetMapping("/overviewDishes")
    @ApiOperation("查询菜单总览")
    public Result<Integer>  getOverviewDishes(){
        return Result.success();
    }
    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<Integer>  getOverviewOrders(){
        return Result.success();
    }
}

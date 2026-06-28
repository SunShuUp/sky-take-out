package com.sky.controller.admin;

import com.sky.mapper.SetmealMapper;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Api(tags = "工作台接口")
@Slf4j
public class WorkspaceCtroller {
    @Autowired
    WorkSpaceService workSpaceService;
    @GetMapping("/overviewSetmeals")
    @ApiOperation("套餐总览接口")
    public Result<SetmealOverViewVO>  OverviewSetmeals(){
        SetmealOverViewVO vo = workSpaceService.OverviewSetmeals();
        return Result.success(vo);
    }
    @GetMapping("/businessData")
    @ApiOperation("查询今日运营数据")
    public Result<BusinessDataVO>  businessData(){
        LocalDate today = LocalDate.now();
        LocalDateTime begin = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(today.plusDays(1), LocalTime.MIN);
        BusinessDataVO businessDataVO=workSpaceService.businessData(begin,end);
        return Result.success(businessDataVO);
    }
    @GetMapping("/overviewDishes")
    @ApiOperation("查询菜单总览")
    public Result<DishOverViewVO>  OverviewDishes(){
        DishOverViewVO dishOverViewVO=workSpaceService.OvervOverviewDishes();
        return Result.success(dishOverViewVO);
    }
    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<OrderOverViewVO>  OverviewOrders(){
        OrderOverViewVO orderOverViewVO=workSpaceService.OverviewOrders();
        return Result.success(orderOverViewVO);
    }
}

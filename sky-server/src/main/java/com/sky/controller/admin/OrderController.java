package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.Result;
import com.sky.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("/adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "订单管理")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;


    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result<Integer>  cancelOrder(){
        return Result.success();
    }
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<Integer>  statistics(){
        return Result.success();
    }
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<Integer>  completeOrder(@PathVariable Integer id){
        return Result.success();
    }
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<Integer>  rejectionOrder(){
        return Result.success();
    }
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<Integer>  confirmOrder(){
        return Result.success();
    }
    @GetMapping("/details/{id}")
    @ApiOperation("获取订单详情")
    public Result<OrderDetail>  getOrderDetail(@PathVariable Integer id){
        return Result.success();
    }
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<Integer>  deliveryOrder(@PathVariable Integer id){
        return Result.success();
    }
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<List<OrderDetail>>  getConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        return Result.success();
    }
}

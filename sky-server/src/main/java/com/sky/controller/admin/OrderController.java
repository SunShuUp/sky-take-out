package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
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
    public Result<String>  cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        orderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO>  statistics(){
        //根据状态 分别查询出 戴接单 待配送 配送中的 订单数量
        Integer toBeConfirmed=orderService.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed=orderService.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress=orderService.countStatus(Orders.DELIVERY_IN_PROGRESS);
        OrderStatisticsVO orderStatisticsVO=new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return Result.success(orderStatisticsVO);
    }
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<String>  completeOrder(@PathVariable long id){
        log.info("完成订单");
        orderService.completeOrder(id);
        return Result.success();
    }
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<String>  rejectionOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejectionOrder(ordersRejectionDTO);
        return Result.success();
    }
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<String>  confirmOrder(Long id){
        log.info("接单ID：：：：："+id);
        orderService.confirmOrder(id);
        return Result.success();
    }
    @GetMapping("/details/{id}")
    @ApiOperation("获取订单详1情")
    public Result<OrderVO>  getOrderDetail(@PathVariable Long  id){
        log.info("获取订单详情穿参："+id);
        OrderVO orderVO=orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<String>  deliveryOrder(@PathVariable Long id){
        log.info("派送订单...");
        orderService.deliveryOrder(id);
        return Result.success();
    }
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult>  getConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单搜素...");
        PageResult pageResult=orderService.getConditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
}

package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkSpaceServiceImpl implements WorkSpaceService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public SetmealOverViewVO OverviewSetmeals() {
         Integer sold=setmealMapper.getCountByStatus(StatusConstant.ENABLE);
         Integer discontinued=setmealMapper.getCountByStatus(StatusConstant.DISABLE);
        return SetmealOverViewVO.builder().sold(sold).discontinued(discontinued).build();
    }

    @Override
    public DishOverViewVO OvervOverviewDishes() {
        Integer sold=dishMapper.getCountByStatus(StatusConstant.ENABLE);
        Integer discontinued=dishMapper.getCountByStatus(StatusConstant.DISABLE);
        return DishOverViewVO.builder().sold(sold).discontinued(discontinued).build();
    }

    @Override
    public OrderOverViewVO OverviewOrders() {
        //待接单数量
         Integer waitingOrders =orderMapper.getCountByStatus(Orders.TO_BE_CONFIRMED);
        //待派送数量
         Integer deliveredOrders =orderMapper.getCountByStatus(Orders.DELIVERY_IN_PROGRESS);
        //已完成数量
         Integer completedOrders =orderMapper.getCountByStatus(Orders.COMPLETED);
        //已取消数量
         Integer cancelledOrders =orderMapper.getCountByStatus(Orders.CANCELLED);
        //全部订单
         Integer allOrders =orderMapper.getCountByStatus(null);
        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders).build();
    }

    @Override
    public BusinessDataVO businessData(LocalDateTime begin, LocalDateTime end) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        Integer totalOrderCount = orderMapper.countByMap(map);
        map.put("status", Orders.COMPLETED);
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null? 0.0 : turnover;
        Integer validOrderCount = orderMapper.countByMap(map);
        Double unitPrice = 0.0;
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0 && validOrderCount != 0){
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            //平均客单价
            unitPrice = turnover / validOrderCount;
        }

        Map userMap = new HashMap();
        userMap.put("begin", begin);
        userMap.put("end", end);
        Integer newUsers= userMapper.countByMap(userMap);
        return BusinessDataVO.builder()
                .newUsers(newUsers)
                .unitPrice(unitPrice)
                .turnover(turnover)
                .orderCompletionRate(orderCompletionRate)
                .validOrderCount(validOrderCount)
                .build();
    }
}

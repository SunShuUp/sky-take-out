package com.sky.task;

import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    OrderMapper orderMapper;
    /**
     * 定时处理超时订单  每分钟出发一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        log.info("定时处理超时订单："+ LocalDateTime.now().toString());
        LocalDateTime now = LocalDateTime.now().plusMinutes(-15);
        Integer status=Orders.PENDING_PAYMENT;
        List<Orders> orderList=orderMapper.getTimeoutOrder(status,now);
        if(orderList!=null&&orderList.size()>0){
            for (Orders order:orderList){
                order.setStatus(Orders.CANCELLED);
                order.setCancelTime(LocalDateTime.now());
                order.setCancelReason(MessageConstant.ORDER_TIME_OUT);
                orderMapper.update(order);
            }
        }

    }

    /**
     *处理一直在派送中的订单 。每天凌晨一点执行
     */
    @Scheduled(cron = "0 0 1  * * ?")
    public  void processDeliveryOrder(){
        log.info("处理一直处理派送中的订单："+ LocalDateTime.now().toString());
        LocalDateTime now = LocalDateTime.now().plusMinutes(-60);
        Integer status=Orders.DELIVERY_IN_PROGRESS;
        List<Orders> orderList=orderMapper.getTimeoutOrder(status,now);
        if(orderList!=null&&orderList.size()>0){
            for (Orders order:orderList){
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }

}

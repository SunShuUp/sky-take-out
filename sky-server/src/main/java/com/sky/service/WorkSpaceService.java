package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkSpaceService {
    /**
     * 套餐总览
     * @return
     */
    SetmealOverViewVO OverviewSetmeals();

    /**
     * 菜品总览
     * @return
     */
    DishOverViewVO OvervOverviewDishes();

    /**
     * 订单管理
     * @return
     */
    OrderOverViewVO OverviewOrders();

    /**
     *工作台今日数据查询
     * @param begin
     * @param end
     * @return
     */
    BusinessDataVO businessData(LocalDateTime begin, LocalDateTime end);
}

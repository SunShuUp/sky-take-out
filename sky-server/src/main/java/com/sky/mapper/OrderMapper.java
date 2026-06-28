package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 下单
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 修改订单状态
     * @param orderStatus
     * @param orderPaidStatus
     * @param checkoutTime
     * @param orderNumber
     */
    @Update("update orders set status = #{orderStatus}, pay_status = #{orderPaidStatus}, checkout_time = #{checkoutTime} where number = #{orderNumber}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime checkoutTime, String orderNumber);

    /**
     * 订单条件搜索
     * @param orders
     * @param beginTime
     * @param endTime
     * @return
     */
    Page<Orders> getConditionSearch(@Param("orders") Orders orders, LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 通过id获取订单信息
     * @param id
     * @return
     */
    @Select("select * from orders where id=#{id}")
    Orders getOrderById(Long id);

    /**
     * 查询各个状态的订单状态
     * @param status
     * @return
     */
    @Select("select count(*) from orders where status =#{status}")
    Integer countStatus(Integer status);

    void update(@Param("orders") Orders orders);

    /**
     * h获取所有历史订单
     * @param id
     * @return
     */
    Page<Orders>  historyOrders(@Param(("id")) long id,@Param("status")  Integer status);

    /**
     * 获取超时订单
     * @param status
     * @param now
     * @return
     */
    @Select("select * from orders where status =#{status} and order_time<(#{now}) ")
    List<Orders> getTimeoutOrder(Integer status, LocalDateTime now);

    /**
     * 通过订单号获取 订单信息
     * @param orderNumber
     * @return
     */
    @Select("select * from orders where number =#{orderNumber}")
    Orders getOrderByNumber(@Param("orderNumber") String orderNumber);

    /**
     * 计算一定时间区间的营业额
     * @param map
     * @return
     */
    @Select("select sum(amount) from orders where order_time >= #{map.begin} and order_time <= #{map.end} and status = #{map.status}")
    Double sumByMap(@Param("map") Map<String, Object> map);

    Integer getOrderCount(@Param("beginTime") LocalDateTime beginTime, @Param("endTime") LocalDateTime endTime,@Param("status") Integer status);

    /**
     * 统计指定区间内的销量排名
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(@Param("beginTime") LocalDateTime beginTime, @Param("endTime") LocalDateTime endTime,@Param("status") Integer status);

    Integer getCountByStatus( @Param("status") Integer status);
    Integer countByMap(@Param("map") Map map);
}

package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.OderWebSocketConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.until.SnowflakeIdGenerator;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    AddressBookMapper addressBookMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SnowflakeIdGenerator idGenerator;
    @Autowired
    WebSocketServer webSocketServer;

    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        if(ordersSubmitDTO.getAddressBookId() == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId=BaseContext.getCurrentId();

        //获取当前订单购物车里的东西
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList=shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //地址薄信息补充
        AddressBook addressBook=addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null || !userId.equals(addressBook.getUserId())) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Orders orders=new Orders();
        orders.setAddressBookId(ordersSubmitDTO.getAddressBookId());
        orders.setPayMethod(ordersSubmitDTO.getPayMethod());
        orders.setRemark(ordersSubmitDTO.getRemark());
        orders.setEstimatedDeliveryTime(ordersSubmitDTO.getEstimatedDeliveryTime());
        orders.setDeliveryStatus(ordersSubmitDTO.getDeliveryStatus());
        orders.setTablewareNumber(ordersSubmitDTO.getTablewareNumber() == null ? 0 : ordersSubmitDTO.getTablewareNumber());
        orders.setTablewareStatus(ordersSubmitDTO.getTablewareStatus());
        orders.setPackAmount(ordersSubmitDTO.getPackAmount() == null ? 0 : ordersSubmitDTO.getPackAmount());
        orders.setAmount(ordersSubmitDTO.getAmount());
        orders.setUserId(userId);
        orders.setStatus(Orders.PENDING_PAYMENT);//代付款
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);//未支付
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName()
                + addressBook.getDistrictName() + addressBook.getDetail());

        //用户信息补充
        User user=userMapper.getByUserId(userId);
        if (user != null) {
            orders.setUserName(user.getName());
        }
        String orderNumber = String.valueOf(idGenerator.generateId());

        orders.setNumber(orderNumber);
        //添加订单
        orderMapper.insert(orders);

        //购物车添加到订单详情表
        List<OrderDetail> orderDetailList=new ArrayList<OrderDetail>();
        for(ShoppingCart shoppingCart1:shoppingCartList){
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(shoppingCart1,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        //购物车清空
        shoppingCartMapper.clean(shoppingCart);

        //填充返回值
        OrderSubmitVO submitVO=new OrderSubmitVO();
        submitVO.setId(orders.getId());
        submitVO.setOrderTime(orders.getOrderTime());
        submitVO.setOrderAmount(orders.getAmount());
        submitVO.setOrderNumber(orders.getNumber());
        return submitVO;
    }

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 1. 注释掉调用微信支付接口的代码
    /*JSONObject jsonObject = weChatPayUtil.pay(
        ordersPaymentDTO.getOrderNumber(),
        new BigDecimal(0.01),
        "苍穹外卖订单",
        user.getOpenid()
    );*/
        // 2. 手动构造一个非空的返回值，模拟微信支付参数
        OrderPaymentVO vo = OrderPaymentVO.builder()
                .nonceStr(UUID.randomUUID().toString().replace("-", ""))
                .paySign("mock-pay-sign")
                .timeStamp(String.valueOf(System.currentTimeMillis() / 1000))
                .signType("RSA")
                .packageStr("prepay_id=mock")
                .build();
        String orderNumber = ordersPaymentDTO.getOrderNumber();
        Orders order =orderMapper.getOrderByNumber(orderNumber);
        orderMapper.updateStatus(Orders.TO_BE_CONFIRMED, Orders.PAID, LocalDateTime.now(), orderNumber);
        //通过websoket下个客户端浏览器推送消息
        Map map=new HashMap();
        map.put("type", OderWebSocketConstant.LAIDAN);
        map.put("orderId",order.getId());
        map.put("content","订单号："+orderNumber);

        String json=JSON.toJSONString(map).toString();
        webSocketServer.sendToAllClient(json);
        return  vo;

    }

    @Override
    public PageResult getConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Orders orders=new Orders();
        BeanUtils.copyProperties(ordersPageQueryDTO,orders);
        LocalDateTime beginTime=ordersPageQueryDTO.getBeginTime();
        LocalDateTime endTime=ordersPageQueryDTO.getEndTime();
        Page<Orders> ordersPage= orderMapper.getConditionSearch(orders,beginTime,endTime);
        //部分订单状态 需要额外返回订单 菜品信息 。 将order
        List<OrderVO> orderVOS=getOrderVoList(ordersPage);
        return new PageResult(ordersPage.getTotal(),orderVOS);
    }

    @Override
    public OrderVO getOrderDetail(Long  id) {
        OrderVO vo=new OrderVO();
        Orders order=orderMapper.getOrderById(id);
        BeanUtils.copyProperties(order,vo);

        List<OrderDetail> orderDetailList=orderDetailMapper.getByOrderId(id);
        vo.setOrderDetailList(orderDetailList);
        return vo;
    }

    @Override
    public void confirmOrder(Long  id) {
        Orders orders=Orders.builder().id(id).status(Orders.CONFIRMED).build();
        orderMapper.update(orders);
    }

    @Override
    public Integer countStatus(Integer status) {
        return orderMapper.countStatus(status);
    }

    @Override
    public void deliveryOrder(Long id) {
        Orders orders=Orders.builder().id(id).status(Orders.DELIVERY_IN_PROGRESS).build();
        orderMapper.update(orders);
    }

    @Override
    public void completeOrder(long id) {
        Orders orders=Orders.builder().id(id).status(Orders.COMPLETED).build();
        orderMapper.update(orders);
    }

    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders orders=orderMapper.getOrderById(ordersCancelDTO.getId());
        if(orders.getPayStatus().equals(Orders.PAID)){
            log.info("处理退款");
        }
        Orders temp=Orders.builder().id(ordersCancelDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(temp);
    }

    @Override
    public void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO) {

        Orders  orders=orderMapper.getOrderById(ordersRejectionDTO.getId());
        if(orders!=null || !orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if(orders.getPayStatus().equals(Orders.PAID)){
            log.info("申请退款");
        }
        //拒单
        Orders temp=Orders.builder().id(ordersRejectionDTO.getId())
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .status(Orders.CANCELLED)
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(temp);
    }

    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        long id = BaseContext.getCurrentId();
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        Page<Orders>  page=orderMapper.historyOrders(id,ordersPageQueryDTO.getStatus());
        List<Orders> ordersList=page.getResult();
        List<OrderVO> orderVOS=new ArrayList<>();
        for(Orders orders:ordersList){
            OrderVO vo=new OrderVO();
            BeanUtils.copyProperties(orders,vo);
            List<OrderDetail> orderDetailList=orderDetailMapper.getByOrderId(orders.getId());
            vo.setOrderDetailList(orderDetailList);
            orderVOS.add(vo);
        }
        return new PageResult(page.getTotal(),orderVOS);
    }

    @Override
    public void repetition(long id) {
        List<OrderDetail> orderDetailList=orderDetailMapper.getByOrderId(id);
        Long userId=BaseContext.getCurrentId();
        for(OrderDetail orderDetail:orderDetailList){
            ShoppingCart shoppingCart=new ShoppingCart();
            shoppingCart.setUserId(userId);
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public void reminder(long id) {
        log.info("用户催单");

        Orders orders=orderMapper.getOrderById(id);
        if(orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Map map=new HashMap();
        map.put("type", OderWebSocketConstant.CUIDAN);
        map.put("orderId",id);
        map.put("content","订单号："+orders.getNumber());
        String json=JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);

    }

    private List<OrderVO> getOrderVoList(Page<Orders> ordersPage) {
        List<OrderVO> orderVOS=new ArrayList<>();
        for (Orders orders: ordersPage.getResult()) {
            OrderVO orderVO=new OrderVO();
            BeanUtils.copyProperties(orders,orderVO);
            String  orderDetailList=getOrderDetail(orders);
            orderVO.setOrderDishes(orderDetailList);
            orderVOS.add(orderVO);
        }
        log.info(ordersPage.getOrderBy());
        return orderVOS;
    }

    private String getOrderDetail(Orders orders) {
        List<OrderDetail> orderDetailList=orderDetailMapper.getByOrderId(orders.getId());
        String  result=new String();
        for (OrderDetail orderDetail: orderDetailList) {
            result+=orderDetail.getName()+"*"+orderDetail.getNumber()+";";
        }
        return result;
    }


}

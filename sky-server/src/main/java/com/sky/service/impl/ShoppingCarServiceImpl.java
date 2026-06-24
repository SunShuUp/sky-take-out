package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
public class ShoppingCarServiceImpl implements ShoppingCarService {
    @Autowired
    ShoppingCartMapper shoppingCarMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;


    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        log.info("userId::::"+shoppingCart.getUserId());
        log.info("shoppingCartDTO::::"+shoppingCartDTO);
        log.info(shoppingCart.toString());
        List<ShoppingCart> shoppingCartList= shoppingCarMapper.list(shoppingCart);

        if(shoppingCartList.size()>0){
             shoppingCart=shoppingCartList.get(0);
             shoppingCart.setNumber(shoppingCart.getNumber()+1);
             shoppingCarMapper.update(shoppingCart);
        }else{
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(BaseContext.getCurrentId());

            if(shoppingCart.getDishId()!=null){
                Dish dish=dishMapper.getDishById(shoppingCartDTO.getDishId());

                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setName(dish.getName());

            }else{
                Setmeal  setmeal=setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            log.info(shoppingCart.toString());
            shoppingCarMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        return shoppingCarMapper.list(shoppingCart);
    }

    @Override
    public void clean() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCarMapper.clean(shoppingCart);
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list=shoppingCarMapper.list(shoppingCart);
        if(list==null||list.size()==0){
            return;
        }

            shoppingCart=list.get(0);
            if(shoppingCart.getNumber()>1){
                shoppingCart.setNumber(shoppingCart.getNumber()-1);
                log.info("update shoppingCart::::"+shoppingCart);
                shoppingCarMapper.update(shoppingCart);
                log.info("更新购物车记录 id={}, number={}", shoppingCart.getId(), shoppingCart.getNumber());
            }else{
                shoppingCarMapper.clean(shoppingCart);
            }


    }
}

package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealOverViewVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    DishMapper dishMapper;

    @Override
    public SetmealVO getSetmealWithDishById(Long id) {
        Setmeal setmeal=setmealMapper.getById(id);
        List<SetmealDish> setmealDishList=setmealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO=new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishList);
        return setmealVO;
    }

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper pageHelper = new PageHelper();
        pageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealPageQueryDTO,setmeal);
        Page<SetmealVO> page =setmealMapper.page(setmeal);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void insertWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        Long id=setmeal.getId();
        List<SetmealDish> setmealDishList=setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setSetmealId(id);
        }
        setmealDishMapper.insertBatch(setmealDishList);
    }

    @Override
    public void updateWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmeal.setStatus(StatusConstant.ENABLE);
        setmealMapper.update(setmeal);

        Long id=setmeal.getId();
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        List<SetmealDish> list=setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : list) {
            setmealDish.setSetmealId(id);
        }
        setmealDishMapper.insertBatch(list);
    }

    @Override
    public void stopOrUp(Integer status,Long id) {
        Setmeal setmeal=new Setmeal();
        setmeal.setStatus(status);
        if(status==StatusConstant.ENABLE){
            List<SetmealDish> setmealDishes=setmealDishMapper.getBySetmealId(id);
            for (SetmealDish setmealDish : setmealDishes) {
                Long dishId=setmealDish.getDishId();
                if(dishMapper.getDishById(dishId).getStatus()==StatusConstant.DISABLE){
                     new SetmealEnableFailedException("套餐中存在未起售的商品");;
                     return;
                }
            }
        }
        setmealMapper.update(setmeal);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
       for(Long id:ids){
           if(setmealMapper.getById(id).getStatus()==StatusConstant.ENABLE){
               throw  new DeletionNotAllowedException("起售中，不允许删除");
           }
       }
       for(Long id:ids){
           setmealDishMapper.deleteBySetmealId(id);
           setmealMapper.delete(id);
       }
    }

    @Override
    public List<SetmealVO> getSetmealWithDish(Setmeal setmeal) {

        return setmealMapper.getSetmealWithDish(setmeal);
    }

    @Override
    public List<DishVO> getDishbyId(Long id) {
        List<DishVO>  dishVOList= setmealMapper.getDishbyId(id);
        return dishVOList;
    }

    @Override
    public SetmealOverViewVO OverviewSetmeals() {

        return null;
    }
}

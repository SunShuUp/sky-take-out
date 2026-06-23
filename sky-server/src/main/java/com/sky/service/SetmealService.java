package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SetmealService {

    SetmealVO getSetmealWithDishById(Long id);

    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);
    @Transactional
    void insertWithDish(SetmealDTO setmealDTO);
    @Transactional
    void updateWithDish(SetmealDTO setmealDTO);

    void stopOrUp(Integer status,Long id);
    @Transactional
    void deleteBatch( List<Long> ids);

    List<SetmealVO> getSetmealWithDish(Setmeal Setmeal);

    List<DishVO> getDishbyId(Long id);
}

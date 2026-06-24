package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DishService {
    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO getDishById(long id);




    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 修改菜单
     * @param dishdto
     */
    @Transactional
    void updateWithFlavor(DishDTO dishdto);

    /**
     * 新增菜品
     * @param dishdto
     * @return
     */
    @Transactional
    void addDishWithFlavor(DishDTO dishdto);
    @Transactional
    void deleteWithFlavor(String Ids);

    /**
     * 根据分类查询菜品
     * @param dish
     * @return
     */
    List<DishVO> listwithFlavor(Dish dish);

}
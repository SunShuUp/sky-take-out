package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {
    public Dish getDishById(long id);
    List<DishVO> query(Long categoryId);

    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);
    @AutoFill(OperationType.INSERT)
    void add(Dish dish);
    @Delete("delete from dish where id in (#{Ids})")
    void deleteBatch(String Ids);
}

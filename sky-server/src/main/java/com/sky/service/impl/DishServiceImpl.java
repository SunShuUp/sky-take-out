package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.apache.poi.ss.formula.functions.BaseNumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;

    @Override
    public DishVO getDishById(long id) {
        Dish dish=dishMapper.getDishById(id);
        List<DishFlavor> flavors=dishFlavorMapper.getByDishId(id);
        DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }
    @Override
    public  List<DishVO>  query(Long categoryId) {
       List<DishVO> dishes= dishMapper.query(categoryId);
        return dishes;
    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        int pageNum = dishPageQueryDTO.getPage() > 0 ? dishPageQueryDTO.getPage() : 1;
        int pageSize = dishPageQueryDTO.getPageSize() > 0 ? dishPageQueryDTO.getPageSize() : 10;
        PageHelper.startPage(pageNum,pageSize);
        Page<DishVO> page=dishMapper.page(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void updateWithFlavor(DishDTO dishdto) {
        List<DishFlavor> flavors=dishdto.getFlavors();
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishdto,dish);
        dishMapper.update(dish);
        dishFlavorMapper.deleteByDishId(dishdto.getId());
       if(flavors!=null&&flavors.size()>0){
           flavors.forEach(flavor->{
               flavor.setDishId(dishdto.getId());
           });
           dishFlavorMapper.insertbatch(flavors);
       }
    }

    @Override
    public void addDishWithFlavor(DishDTO dishdto) {
        List<DishFlavor> flavors=dishdto.getFlavors();
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishdto,dish);
        dish.setUpdateUser(BaseContext.getCurrentId());
        dish.setUpdateTime(LocalDateTime.now());
        dish.setCreateUser(BaseContext.getCurrentId());
        dish.setCreateTime(LocalDateTime.now());
        dish.setStatus(StatusConstant.ENABLE);
        dishMapper.add(dish);
        if(flavors!=null&&flavors.size()>0){
            flavors.forEach(flavor->{
                flavor.setDishId(dish.getId());
            });
            dishFlavorMapper.insertbatch(flavors);
        }
    }

    @Override
    public void deleteWithFlavor(String Ids) {
        dishFlavorMapper.deleteBatch(Ids);
        dishMapper.deleteBatch(Ids);
    }

    @Override
    public List<DishVO> listwithFlavor(Dish dish) {
        List<DishVO> dishVOList=dishMapper.query(dish.getCategoryId());
        return dishVOList;
    }
}

package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    DishService dishService;
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getDish(@PathVariable Long id) {
        DishVO dishvo=dishService.getDishById(id);
        return Result.success(dishvo);
    }
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> getDishList(@RequestParam Long categoryId) {
        List<DishVO>  dishes=dishService.query(categoryId);
        return Result.success(dishes);
    }
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        PageResult page=dishService.page(dishPageQueryDTO);
        return Result.success(page);
    }
    @PostMapping
    @ApiOperation("新增菜品")
    public Result<String> add(@RequestBody DishDTO dishdto) {
        dishService.addDishWithFlavor(dishdto);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售、停售")
    public Result<Dish> status(@PathVariable Integer status,@RequestParam Long id) {
        DishDTO dishdto=new DishDTO();
        dishdto.setStatus(status);
        dishdto.setId(id);
        dishService.updateWithFlavor(dishdto);
        return Result.success();
    }
    @PutMapping
    @ApiOperation("修改商品")
    public Result<String> updateWithFlavor(@RequestBody DishDTO dishdto) {
        dishService.updateWithFlavor(dishdto);
        return Result.success();
    }
    @DeleteMapping
    @ApiOperation("批量删除商品")
    public Result<String> delete(@RequestParam("ids") String Ids) {
        dishService.deleteWithFlavor(Ids);
        return Result.success();
    }
}

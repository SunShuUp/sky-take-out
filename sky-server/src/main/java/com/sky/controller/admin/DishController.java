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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

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
        //清理缓存数据
       // redisTemplate.delete("dish_"+dishdto.getCategoryId());
        cleanCache("dish_"+dishdto.getCategoryId());
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售、停售")
    public Result<Dish> status(@PathVariable Integer status,@RequestParam Long id) {
        DishDTO dishdto=new DishDTO();
        dishdto.setStatus(status);
        dishdto.setId(id);

        dishService.updateWithFlavor(dishdto);
//        redisTemplate.delete("dish_"+dishdto.getCategoryId());
        cleanCache("dish_"+dishdto.getCategoryId());

        return Result.success();
    }
    @PutMapping
    @ApiOperation("修改商品")
    public Result<String> updateWithFlavor(@RequestBody DishDTO dishdto) {
        dishService.updateWithFlavor(dishdto);

        Set<String> keys=redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return Result.success();
    }
    @DeleteMapping
    @ApiOperation("批量删除商品")
    public Result<String> delete(@RequestParam("ids") String Ids) {
        dishService.deleteWithFlavor(Ids);
        cleanCache("dish_*");

//        Set keys=redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

        return Result.success();
    }

    private void cleanCache(String  pattern){
         Set keys=redisTemplate.keys(pattern);
         redisTemplate.delete(keys);
    }
}

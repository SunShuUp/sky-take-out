package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    SetmealService setmealSercice;

    @RequestMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getSetmealWithDishById(@PathVariable Long id) {
        SetmealVO setmealVO=setmealSercice.getSetmealWithDishById(id);
        return Result.success(setmealVO);
    }
    @RequestMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult=setmealSercice.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(allEntries = true)
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealSercice.insertWithDish(setmealDTO);
        return Result.success();
    }
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(value = "Setmel",key = "#setmealDTO.categoryId")
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealSercice.updateWithDish(setmealDTO);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售 停售")
    @CacheEvict(allEntries = true)
    public Result<String> stopOrUp(@PathVariable Integer status,Long id){
        setmealSercice.stopOrUp(status,id);
        return Result.success();

    }
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(allEntries = true)
    public Result<String> deleteBatch(@RequestParam  List<Long> ids){
        setmealSercice.deleteBatch(ids);
        return Result.success();
    }
}

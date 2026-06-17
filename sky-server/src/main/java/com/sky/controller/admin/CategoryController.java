package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api("分类管理")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult pageResult=categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
    @GetMapping("/list")
    @ApiOperation("分类列表查询")
    public Result<List<Category>> list(Long type) {
        List<Category> categories=categoryService.list(type);
        return Result.success(categories);
    }
    @PutMapping
    @ApiOperation("修改分类")
    public Result<String> update(@RequestBody CategoryDTO categorydto) {
        Category category=new Category();
        BeanUtils.copyProperties(categorydto,category);
        categoryService.update(category);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("启动禁止分类")
    public Result<String> updateStatus(@PathVariable("status") Integer status, @RequestParam Long id ) {
        Category category=new Category();
        category.setStatus(status);
        category.setId(id);
        categoryService.update(category);
        return Result.success();
    }
    @PostMapping
    public Result<String> add(@RequestBody CategoryDTO categorydto) {
        Category category=new Category();
        BeanUtils.copyProperties(categorydto,category);
        categoryService.save(category);
        return Result.success();
    }
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result<String> delete(@RequestParam Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}

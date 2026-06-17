package com.sky.service;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

public interface CategoryService {
    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 修改分类
     * @param category
     */
    void update(Category category);

    /**
     * 新增分类
     * @param category
     */
    void save(Category category);

    /**
     * 删除分类
     * @param id
     */
    void delete(Long id);
}

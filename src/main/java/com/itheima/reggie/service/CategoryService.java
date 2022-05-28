package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import org.springframework.stereotype.Service;

/**
 * @author jektong
 * @date 2022年05月06日 21:37
 */
public interface CategoryService extends IService<Category> {

    /**
     * 根据ID删除分类
     * @param id
     */
    public void remove(Long id);

}

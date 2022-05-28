package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jektong
 * @date 2022年05月06日 21:44
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;

    /**
     * 根据ID删除分类，分类之前需要判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =  new LambdaQueryWrapper<>();
        // 查询当前分类是否关联了菜品，若关联菜品，抛出异常
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count > 0){
            // 已经关联菜品，抛出异常
            throw new CustomException("当前分类已关联菜品，不可删除");
        }
        // 查询当前分类是否关联了套餐，若关联菜品，抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =  new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count>0){
            // 已经关联套餐，抛出异常
            throw new CustomException("当前分类已关联套餐，不可删除");
        }
        // 正常删除分类
        super.removeById(id);
    }


}

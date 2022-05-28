package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.CommonsConst;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jektong
 * @date 2022年05月16日 22:22
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishFlavorService flavorService;



    /**
     * 新增菜品，同时插入菜品对应的口味数据
     * @param dishDto
     * @return R<String>
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("DishDTO===>{}",dishDto);
        dishService.saveWithFlover(dishDto);
        return R.success("菜品添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        // 构造分页构造器
        Page<Dish> pageInfo = new Page(page, pageSize);
        // 因为前端需要展示分类的名称，所以封装成DishDto对象
        Page<DishDto> dishDtoPage = new Page(page, pageSize);
        // 构造条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        // 添加排序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行查询
        dishService.page(pageInfo, queryWrapper);
        // 进行对象拷贝,去除之前已经查出来的集合
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            // 获取categoryId
            Long categoryId = item.getCategoryId();
            // 给categoryName赋值
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                dishDto.setCategoryName(category.getName());
            }
            // 当前菜品ID
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper= new LambdaQueryWrapper<>();
            dishFlavorQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = flavorService.list(dishFlavorQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据ID查询菜品信息以及对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return R<String>
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("DishDTO===>{}",dishDto);
        dishService.updateWithFlavor(dishDto);
        return R.success("菜品修改成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        // 构造条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Dish::getStatus, CommonsConst.DISH_OPEN);
        queryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId, dish.getCategoryId());
        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = dishList.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            // 获取categoryId
            Long categoryId = item.getCategoryId();
            // 给categoryName赋值
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                dishDto.setCategoryName(category.getName());
            }
            // 当前菜品ID
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper= new LambdaQueryWrapper<>();
            dishFlavorQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = flavorService.list(dishFlavorQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }

    /**
     * 套餐的起售与停售
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> onOrClose(@PathVariable Integer status, Long[] ids){
        log.info("setmeal====>{},status==>{}" + ids +"====>" + status);
        for (int i = 0; i < ids.length; i++) {
            // 获取菜品
            Dish dish = dishService.getById(ids[i]);
            dish.setStatus(status);
            // 修改状态
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }

}

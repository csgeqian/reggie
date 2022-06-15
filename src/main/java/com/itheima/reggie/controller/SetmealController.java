package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jektong
 * @date 2022年05月21日 1:07
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    @Resource
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("SetmealDto===>{}" + setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("套餐信息插入成功");
    }

    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构建分页对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page(page, pageSize);
        // 构造条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        // 时间降序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);
        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto>list= records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            // 分类id
            Long categoryId = item.getCategoryId();
            // id查出分类名称
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }


    /**
     * 根据ID删除套餐信息，同时删除所关联的菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public  R<String> delete(@RequestParam List<Long> ids){
        log.info("ids==>{}" + ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
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
            Setmeal setmeal = setmealService.getById(ids[i]);
            setmeal.setStatus(status);
            // 修改状态
            setmealService.updateById(setmeal);
        }
        return R.success("修改成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        // 构造查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return R<String>
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("修改套餐信息{}", setmealDto);
        // 执行更新。
        setmealService.updateWithSetmeal(setmealDto);
        return R.success("套餐修改成功");
    }

    /**
     * 根据id查询套餐信息
     *(套餐信息的回显)
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        log.info("根据id查询套餐信息:{}", id);
        // 调用service执行查询
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }
}

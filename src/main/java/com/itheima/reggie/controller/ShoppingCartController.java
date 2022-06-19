package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jektong
 * @date 2022年05月28日 0:55
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        // 设置用户ID，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        // 构造查询条件
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        // 查询当前菜品或者套餐是否在购物车里
        Long dishId = shoppingCart.getDishId();
        // 根据菜品ID是否为NULL
        if(dishId!=null){
            // 添加菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            // 添加套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(queryWrapper);
        if (shoppingCartOne!=null){
            // 如果存在就在原来基础上加上1
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number+1);
            shoppingCartService.updateById(shoppingCartOne);
        }else {
            // 如果不存在则添加至购物车默认为1
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }
        return R.success(shoppingCartOne);
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        // 构建查询条件 根据用户ID查询
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");

    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrentId();
        //设置用户id，指定当前是哪个用户的购物车数据 ,否则会出现用户互相修改对方与自己购物车中相同套餐或者是菜品的数量
        shoppingCart.setUserId(userId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        if (dishId != null) {
            //说明此次添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            //添加的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        cartServiceOne.setNumber( cartServiceOne.getNumber() - 1);
        Integer number = cartServiceOne.getNumber();
        //查询出来的数量为1，点击减少菜品或套餐时，即number=0，直接执行else语句块的删除
        if (number  > 0) {
            shoppingCartService.updateById(cartServiceOne);
        } else {
            //直接在shopping_cart表中清除当前数据
            shoppingCartService.removeById(cartServiceOne);
        }
        return R.success(cartServiceOne);
    }

}

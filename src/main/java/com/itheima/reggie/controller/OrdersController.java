package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author jektong
 * @date 2022年05月28日 17:20
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Resource
    private OrdersService ordersService;


    /**
     * 下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 后端管理查询查询订单
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(int page, int pageSize, String number,
                        @DateTimeFormat(pattern = "yyyy-mm-dd HH:mm:ss") Date beginTime,
                        @DateTimeFormat(pattern = "yyyy-mm-dd HH:mm:ss") Date endTime) {
        log.info("beginTime={},endTime={}",beginTime,endTime);
        // 构建订单分页对象
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        // 构造条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        // 模糊查询根据订单号或者时间间隔查询
        // SELECT * FROM Order where number like ? or order_time between ? and ?
        queryWrapper.like(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        if (beginTime != null){
            queryWrapper.between(Orders::getOrderTime,beginTime,endTime);
        }
        // 时间降序
        queryWrapper.orderByDesc(Orders::getOrderTime);
        // 封装为分页对象
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 订单状态修改
     * @param orders
     * @return
     */
    @PutMapping()
    public R<String> orderStatus(@RequestBody Orders orders){
        // 修改状态
        ordersService.updateById(orders);
        return R.success("修改成功");
    }
}

package com.itheima.reggie.common;


import jdk.nashorn.internal.ir.debug.ClassHistogramElement;

/**
 * 常量定义
 * @author jektong
 * @date 2022/4/23
 */
public class CommonsConst {

    // 登录失败
    public static final String LOGIN_FAIL = "登录失败";

    // 账号禁用
    public static final String LOGIN_ACCOUNT_STOP = "账号禁止使用";

    // 员工账号禁用状态 0：禁用
    public static final Integer EMPLOYEE_STATUS_NO = 0;

    // 员工账号正常状态/user用户 1：正常使用
    public static final Integer EMPLOYEE_STATUS_YES = 1;
    // 添加时候初始化密码
    public static final String INIT_PASSWORD = "123456";

    // 套餐起售状态 起售：1
    public static final Integer DISH_OPEN = 1;
}

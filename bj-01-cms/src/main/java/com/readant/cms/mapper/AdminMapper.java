package com.readant.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.readant.cms.entity.Admin;
import org.apache.ibatis.annotations.Select;

/**
 * 管理员 Mapper —— 数据库"翻译官"
 *
 * BaseMapper<Admin> 是 MyBatis-Plus 提供的"万能翻译官"，
 * 它已经内置了 insert/deleteById/selectById/updateById 等常用方法。
 *
 * 自定义方法用 @Select 注解直接写 SQL，简单直观。
 */
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 根据用户名查询管理员
     */
    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin selectByUsername(String username);
}
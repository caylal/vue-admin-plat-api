package com.avenir.mapper;

import com.avenir.model.Admin;
import org.apache.ibatis.annotations.Param;

public interface AdminMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Admin record);

    int insertSelective(Admin record);

    Admin selectByPrimaryKey(Integer id);

    Admin selectByNameAndPwd(@Param("name") String name, @Param("pwd") String pwd);

    int updateByPrimaryKeySelective(Admin record);

    int updateByPrimaryKey(Admin record);
}
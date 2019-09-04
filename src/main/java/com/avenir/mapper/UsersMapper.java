package com.avenir.mapper;

import com.avenir.model.Users;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UsersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Users record);

    int insertSelective(Users record);

    Users selectByPrimaryKey(String vin);

    Users selectByUserId(Integer id);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);

    List<Users> findUser(Users record);

    Users getService(String vin);

    List<Users> getUserHasVinType(Users user);

    int insertBatch(List<Users> list);

    int updateBatch(List<Users> list);

    int updateByCarLicense(@Param("carLicense") String carl, @Param("id") Integer id);

}
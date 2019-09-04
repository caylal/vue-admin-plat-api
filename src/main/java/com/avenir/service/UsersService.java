package com.avenir.service;

import com.avenir.model.Admin;
import com.avenir.model.Users;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface UsersService {

    int deleteByPrimaryKey(Integer id);

    int insertOrUpdateSelective(Users record) throws SQLException;

    Users selectByPrimaryKey(String vin);

    Users selectByUserId(Integer id);

    Admin selectByNameAndPwd(String name, String pwd);

    List<Users> findUser(Users user);

    Users getService(String vin);

    List<Users> getUserHasVinType(Users user);

    int insertBatch(List<Users> list) throws SQLException;

    int updateBatch(List<Users> list) throws SQLException;

    int updateByCarLicense(String carl, Integer id) throws SQLException;

    void getCountByVinAndType(Users user, Integer typeId);

    void getCurrentYear(Users user, Date current, Integer typeId);
}

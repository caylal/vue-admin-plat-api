package com.avenir.service;

import com.avenir.model.OrderOV;
import com.avenir.model.Orders;
import com.avenir.model.Users;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrdersService {

    int deleteByPrimaryKey(Integer id);

    int insertOrUpdateSelective(Orders record) throws SQLException;

    List<Orders> findOrder(Orders record);

    Orders selectByOrderNo(String orderNo, Integer typeId);

    Orders selectRepeatOrder( Integer userId, Date beginTime, Date endTime);

    Orders selectRepeatOrder( Integer userId, Date useTime);

    int countByVin(String vin, Integer typeId, Date startTime, Date endTime);

    int countByVinAll(String vin, Integer typeId);

    Integer selectAuthority(Users user, Integer typeId, Date createTime) throws ParseException;

    List<Orders> selectUseDetail(String vin , Integer typeId);

    List<OrderOV> selectCountByVin(String vin , Integer typeId);

    List<OrderOV> selectOrderCountByType(Integer typeId);

    List<OrderOV> selectOrderCountByCarType(Integer typeId);

    List<OrderOV> selectOrderCountByExper(Integer typeId, String carType);

    int insertBatchAW (List<Orders> list) throws  SQLException;

    int insertBatchPickup(List<Orders> list) throws SQLException;

    int insertBatchRent(List<Orders> list) throws SQLException;
}

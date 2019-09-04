package com.avenir.mapper;

import com.avenir.model.OrderOV;
import com.avenir.model.Orders;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrdersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Orders record);

    int insertSelective(Orders record);

    Orders selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Orders record);

    int updateByPrimaryKey(Orders record);

    List<Orders> findOrder(Orders record);

    Orders selectByOrderNo(@Param("orderNo") String orderNo,@Param("typeId") Integer typeId);

    Orders selectRepeatOrder(@Param("userId") Integer userId, @Param("useTime") Date useTime, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    int countByVin(@Param("vin") String vin, @Param("typeId") Integer typeId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    int countByVinAll(@Param("vin") String vin, @Param("typeId") Integer typeId);

    List<Orders> selectUseDetail(@Param("vin") String vin , @Param("typeId") Integer typeId);

    List<OrderOV> selectCountByVin(@Param("vin") String vin , @Param("typeId") Integer typeId);

    List<OrderOV> selectOrderCountByType( @Param("typeId") Integer typeId);

    List<OrderOV> selectOrderCountByCarType(@Param("typeId") Integer typeId);

    List<OrderOV> selectOrderCountByExper(@Param("typeId") Integer typeId, @Param("carType") String carType);

    int insertBatchAW (List<Orders> list);

    int insertBatchPickup(List<Orders> list);

    int insertBatchRent(List<Orders> list);
}
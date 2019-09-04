package com.avenir.service.impl;

import com.avenir.constant.CommonConst;
import com.avenir.mapper.OrdersMapper;
import com.avenir.model.OrderOV;
import com.avenir.model.Orders;
import com.avenir.model.Users;
import com.avenir.service.OrdersService;
import com.avenir.service.UsersService;
import com.avenir.utils.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrdersServiceImpl  implements OrdersService{

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UsersService usersService;
    @Override
    public int deleteByPrimaryKey(Integer id){
        return ordersMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insertOrUpdateSelective(Orders record){
        Users user = record.getUser();
        try {
            if(record.getId() != null) {
                if(user != null) {
                    LogUtils.info("orderUser：" + user.toString());
                    Orders order = ordersMapper.selectByPrimaryKey(record.getId());
                    if(record.getCompleted() == 1 && order.getCompleted() != 1) { //
                        record.setState(2); // 设置订单状态已完成
                        if(record.getTypeId() == 1) {
                            user.setrTotalUsed(user.getrTotalUsed() + 1);
                        }
                        if(record.getTypeId() == 2) {
                            user.setaTotalUsed(user.getaTotalUsed() + 1);
                        }
                        if(record.getTypeId() == 4) {
                            user.setpTotalUsed(user.getpTotalUsed() + 1);
                        }
                    }
                    usersService.insertOrUpdateSelective(user);
                }
                return ordersMapper.updateByPrimaryKeySelective(record);
            } else {
                if(user != null) {
                    LogUtils.info("orderUser：" + user.toString());
                    if(record.getCompleted() == 1) { //
                        record.setState(2); // 设置订单状态已完成
                        if(record.getTypeId() == 1) {
                            user.setrTotalUsed(user.getrTotalUsed() + 1);
                        }
                        if(record.getTypeId() == 2) {
                            user.setaTotalUsed(user.getaTotalUsed() + 1);
                        }
                        if(record.getTypeId() == 4) {
                            user.setpTotalUsed(user.getpTotalUsed() + 1);
                        }
                    } else {
                        record.setState(1);
                    }
                    usersService.insertOrUpdateSelective(user);
                }
                record.setCreateTime(new Date());
                return ordersMapper.insertSelective(record);
            }
        }catch (SQLException e) {
            LogUtils.error("insetOrUpdateOrderImpl: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public List<Orders> findOrder(Orders record){
        return ordersMapper.findOrder(record);
    }

    @Override
    public Orders selectByOrderNo(String orderNo, Integer typeId) {
        return ordersMapper.selectByOrderNo(orderNo, typeId);
    }

    @Override
    public Orders selectRepeatOrder( Integer userId, Date beginTime, Date endTime) {
        return ordersMapper.selectRepeatOrder(userId, null, beginTime, endTime);
    }
    @Override
    public Orders selectRepeatOrder( Integer userId, Date useTime) {
        return ordersMapper.selectRepeatOrder(userId, useTime, null, null);
    }
    @Override
    public int countByVin(String vin, Integer typeId, Date startTime, Date endTime) {
        return ordersMapper.countByVin(vin, typeId, startTime, endTime);
    }

    @Override
    public int countByVinAll(String vin, Integer typeId){
        return ordersMapper.countByVinAll(vin, typeId);
    }
    @Override
    public Integer selectAuthority(Users user, Integer typeId, Date createTime) throws ParseException{
        String[] vinType = StringUtils.split(user.getVinType(), ",");
        if(createTime == null) {
            createTime = new Date();
        }
        if(vinType.length > 0) {
            LogUtils.info("vinType权益：" + vinType.toString());
            if(Arrays.asList(vinType).contains(typeId.toString())){ // 如果vin有接送机的权益
                if(user.getReportTime() == null) {
                    LogUtils.error("Vin没有上报时间：" + CommonConst.NO_REPORT);
                    return CommonConst.NO_REPORT;
                }
                else {
                    if(createTime.getTime() > user.getDueTime().getTime() || createTime.getTime() < user.getReportTime().getTime()) { // 是否在有效期内
                        LogUtils.error("Vin不在有效期内：" + CommonConst.OVER_DUE);
                        return CommonConst.OVER_DUE;
                    }
//                    Calendar date = Calendar.getInstance();
//                    Integer currentDate =date.get(Calendar.YEAR); // 当年
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Date current = sdf.parse(Integer.toString(currentDate) +"-01-01 00:00:00");

                    // 统计当年使用次数
//                    usersService.getCurrentYear(user, createTime, typeId);
//                    if(typeId == 1) { //租车 一年一次，有效期5年共5次
//                        if(user.getrTotalUsed() >= user.getrTotalCount()) { // 已使用次数大于等于总次数， 次数已用完
//                            LogUtils.error("Vin总次数已用完：" + CommonConst.OVER_DUE);
//                            return CommonConst.OVER_DUE;
//                        }
//                        if(user.getCountBy() >= user.getrCurrentCount()) { // 当年使用次数大于等于每年使用次数
//                            LogUtils.error("Vin当年次数已用完：" + CommonConst.NO_NUMBER);
//                            return CommonConst.NO_NUMBER;
//                        }
//                    }
//                    if(typeId == 2) { // 接送机 一年4次，有效期5年共20次
//                        if(user.getaTotalUsed() >= user.getaTotalCount()) {
//                            LogUtils.error("Vin总次数已用完：" + CommonConst.OVER_DUE);
//                            return CommonConst.OVER_DUE;
//                        }
//                        if(user.getCountBy() >= user.getaCurrentCount()) {
//                            LogUtils.error("Vin当年次数已用完：" + CommonConst.NO_NUMBER);
//                            return CommonConst.NO_NUMBER;
//                        }
//                    }
//                    if(typeId == 4) { //取送车 一年4次，有效期5年共20次
//                        if(user.getpTotalUsed() >= user.getpTotalCount()) {
//                            LogUtils.error("Vin总次数已用完：" + CommonConst.OVER_DUE);
//                            return CommonConst.OVER_DUE;
//                        }
//                        if(user.getCountBy() >= user.getpCurrentCount()) {
//                            LogUtils.error("Vin当年次数已用完：" + CommonConst.NO_NUMBER);
//                            return CommonConst.NO_NUMBER;
//                        }
//                    }
                }
                return CommonConst.HAS_NUMBER;
            } else {
                return CommonConst.NO_AUTH;
            }
        } else {
            return CommonConst.NO_AUTH;
        }
    }

    @Override
    public List<Orders> selectUseDetail(String vin , Integer typeId){
        return ordersMapper.selectUseDetail(vin, typeId);
    }
    @Override
    public  List<OrderOV> selectCountByVin(String vin , Integer typeId){
        return ordersMapper.selectCountByVin(vin, typeId);
    }

    @Override
    public List<OrderOV> selectOrderCountByType(Integer typeId) {
        return ordersMapper.selectOrderCountByType(typeId);
    }

    @Override
    public List<OrderOV> selectOrderCountByCarType(Integer typeId) {
        return  ordersMapper.selectOrderCountByCarType(typeId);
    }

    @Override
    public List<OrderOV> selectOrderCountByExper(Integer typeId, String carType) {
        return ordersMapper.selectOrderCountByExper(typeId, carType);
    }
    @Override
    public int insertBatchAW (List<Orders> list) {
        return ordersMapper.insertBatchAW(list);
    }

    @Override
    public  int insertBatchPickup(List<Orders> list) {
        return ordersMapper.insertBatchPickup(list);
    }

    @Override
    public int insertBatchRent(List<Orders> list) {
        return ordersMapper.insertBatchRent(list);
    }
}

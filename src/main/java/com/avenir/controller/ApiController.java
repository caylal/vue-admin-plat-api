package com.avenir.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.avenir.constant.CommonConst;
import com.avenir.constant.ResultCode;
import com.avenir.model.JsonResult;
import com.avenir.model.Orders;
import com.avenir.model.Types;
import com.avenir.model.Users;
import com.avenir.service.OrdersService;
import com.avenir.service.TypesService;
import com.avenir.service.UsersService;
import com.avenir.utils.LogUtils;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ApiController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private TypesService typesService;

    @Autowired
    private OrdersService ordersService;

    @RequestMapping(value="/getServiceList", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult getServiceList(@RequestBody String json) {
        JSONObject obj = JSON.parseObject(json);
        String vin = obj.getString("vin");
        LogUtils.info("getvin: " + vin);
        if(!StringUtils.isEmpty(vin)) {
            Users users = usersService.getService(vin);
            List<Map> list = new ArrayList<>();
            if(users != null) {
                LogUtils.info("getVinForUser: " + users.toString());
                String vinType = users.getVinType();
                if(!StringUtils.isEmpty(vinType)) {
                    String[] vinlist = StringUtils.split(vinType, ',');
                    for(String id : vinlist) {
                        Types type = typesService.selectByType(Integer.parseInt(id));
                        if(type != null) {
                            LogUtils.info("getType: " + type.toString());
                            Map<String, Object> map = new HashMap<>();
                            map.put("name", type.getName());
                            map.put("type", type.getType());
                            list.add(map);
                        }
                    }
                } else {
                    return JsonResult.error(ResultCode.NO_AUTHORITY, "该VIN码没有权益");
                }

            } else {
                return JsonResult.error(ResultCode.UNAVENIR,"该VIN码不是Avenir卡");
            }
            return JsonResult.success(list);
        } else {
            return JsonResult.error(ResultCode.LESS_PARAM, "缺少必要参数");
        }
    }

    @RequestMapping(value = "/syncAvenirCardInfo", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult syncAvenirCardInfo(@RequestBody String info) {
        JSONObject obj = JSON.parseObject(info);
        JSONArray array = obj.getJSONArray("data");
        if(array.size() > 0) {
            try {
                for (int i = 0; i < array.size(); i++) {
                    String vin = array.getJSONObject(i).get("vin").toString().trim();
                    String cardType = array.getJSONObject(i).get("card_type").toString();
                    Users isExist = usersService.selectByPrimaryKey(vin);
                    if(isExist != null) {
                        isExist.setPhone(array.getJSONObject(i).get("mobile").toString());
                        isExist.setServiceCard(array.getJSONObject(i).get("card_no").toString());
                        isExist.setServiceType(cardType);
                        usersService.insertOrUpdateSelective(isExist);
                    } else {
                        Users user = new Users();
                        user.setVin(vin);
                        user.setPhone(array.getJSONObject(i).get("mobile").toString());
                        user.setServiceCard(array.getJSONObject(i).get("card_no").toString());
                        user.setServiceType(cardType);
                        usersService.insertOrUpdateSelective(user);
                    }
                }
                return JsonResult.success("同步成功");
            }catch (SQLException e) {
                LogUtils.error(e.getMessage());
                return JsonResult.error(ResultCode.FAIL, "同步失败");
            }
        } else {
            return JsonResult.error(ResultCode.LESS_PARAM, "缺少必要参数");
        }
    }

    @RequestMapping(value = "/syncAvenirAirportTransferOrder", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult syncAvenirAirportTransferOrder(@RequestBody String json) throws ParseException{
        JSONObject obj = JSON.parseObject(json);
        LogUtils.info("同步接送机订单数据信息：" + obj);
        if(!obj.isEmpty() && !obj.getString("vin").isEmpty()) {
            Orders isExist = ordersService.selectByOrderNo(obj.getString("hibuick_order_id"), 2);
            if(isExist == null) {  // 如果没有该订单，则进行添加订单
                Users user = usersService.getService(obj.getString("vin"));
                if(user != null) { // 查找是否该Vin的信息，有则进行权益判断
                    // 计算是否有接送机权益，并且计算权益剩余次数
                    LogUtils.info("orderUser: " + user.toString());
                    Integer tag = ordersService.selectAuthority(user, 2, obj.getDate("use_car_time"));
                    if(tag == CommonConst.NO_AUTH) {
                        return  JsonResult.error(ResultCode.NO_AUTHORITY, "该VIN码没有权益");
                    } else if(tag == CommonConst.OVER_DUE){
                        return JsonResult.error(ResultCode.OVERDUE, "该VIN码已没有使用次数");
                    } else if(tag == CommonConst.NO_NUMBER) {
                        return JsonResult.error(ResultCode.OVER_NUMBER, "该VIN码超过今年使用次数");
                    } else if(tag == CommonConst.NO_REPORT) {
                        return JsonResult.error(ResultCode.NO_REPORT, "该VIN码没有上报时间");
                    } else {
                        try {
                            Orders order = new Orders();
                            order.setUser(user);
                            order.setUserTel(obj.getString("mobile").isEmpty() ? user.getPhone() : obj.getString("mobile"));
                            order.setOpenId(StringUtils.trimToNull(obj.getString("openid")));
                            order.setUserName(StringUtils.trimToNull(obj.getString("username")));
                            order.setTypeId(2); // 接送机
                            order.setOrderType(StringUtils.isEmpty(obj.getInteger("source").toString()) ? 2 : obj.getInteger("source")); // 微信
                            order.setUserId(user.getId());
                            order.setFlight(StringUtils.trimToNull(obj.getString("flight_info")));
                            order.setUseTime(obj.getDate("use_car_time") != null ? obj.getDate("use_car_time") : null);
                            order.setUseCarType(StringUtils.isEmpty(obj.getString("use_car_type")) ? null : (obj.getString("use_car_type").equals("接机") ? 1 : 2));
                            order.setAddress(StringUtils.trimToNull(obj.getString("start_place")));
                            order.setDestination(StringUtils.trimToNull(obj.getString("end_place")));
                            order.setAppointmentTime(obj.getDate("create_time") != null ? obj.getDate("create_time") : null);
                            order.setOrderNo(StringUtils.trimToNull(obj.getString("hibuick_order_id")));
                            order.setIsOrder(2);
                            order.setState(1);
                            order.setNeedOrder(1);
                            order.setIsVisit(2);
                            order.setCompleted(2);
                            order.setOrderTime(new Date());
                            LogUtils.info("insertOrderApi: " + order.toString());
                            ordersService.insertOrUpdateSelective(order);
                        }catch (SQLException e) {
                            LogUtils.error(e.getMessage());
                            return JsonResult.error(ResultCode.FAIL, "同步失败");
                        }
                        return JsonResult.success();
                    }

                } else {
                    return JsonResult.error(ResultCode.UNAVENIR,"该VIN码不是Avenir卡");
                }
            } else {
                return JsonResult.error(ResultCode.EXISTSED, "订单号已存在");
            }

        } else {
            return JsonResult.error(ResultCode.LESS_PARAM, "缺少必要参数");
        }
    }


    @RequestMapping(value = "/syncAvenirRentCarsOrder", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult syncAvenirRentCarsOrder(@RequestBody String json) throws ParseException{
        JSONObject obj = JSON.parseObject(json);
        LogUtils.info("同步租车订单数据信息：" + obj);
        if(!obj.isEmpty() && !obj.getString("vin").isEmpty()) {
            Orders isExist = ordersService.selectByOrderNo(obj.getString("ehi_order_id"), 1);
            if(isExist == null) {
                Users user = usersService.getService(obj.getString("vin"));
                if(user != null) {
                    LogUtils.info("orderUser: " + user.toString());
                    // 查找是否在有效期内
                    Integer tag = ordersService.selectAuthority(user, 1, obj.getDate("create_time"));
                    if(tag == CommonConst.NO_AUTH) {
                        return  JsonResult.error(ResultCode.NO_AUTHORITY, "该VIN码没有权益");
                    } else if(tag == CommonConst.OVER_DUE){
                        return JsonResult.error(ResultCode.OVERDUE, "该VIN码不在有效期内");
                    } else if(tag == CommonConst.NO_REPORT) {
                        return JsonResult.error(ResultCode.NO_REPORT, "该VIN码没有上报时间");
                    } else {
                        JSONObject priceData = obj.getJSONObject("price_data");
                        BigDecimal money = null;
                        String duration = null;
                        if(priceData != null && priceData.getBigDecimal("total_price") != null) {
                            money = priceData.getBigDecimal("total_price");
                        }
                        if(obj.getDate("pickup_date_time") != null && obj.getDate("return_date_time") != null) {
                            duration = formatTime(obj.getDate("pickup_date_time"),obj.getDate("return_date_time"));
                        }
                        try {
                            Orders order = new Orders();
                            order.setUser(user);
                            order.setUserTel(obj.getString("mobile"));
                            order.setUserName(obj.getString("username"));
                            order.setOpenId(obj.getString("openid"));
                            order.setTypeId(1); // 租车
                            order.setOrderType(obj.getInteger("source"));
                            order.setUserId(user.getId());
                            order.setRentCity(obj.getString("pickup_city"));
                            order.setOrderCarType(obj.getString("pickup_car_model"));
                            order.setBeginTime(obj.getDate("pickup_date_time"));
                            order.setStoreName(obj.getString("pickup_chain"));
                            order.setStoreAddr(obj.getString("pickup_chain_address"));
                            order.setContactTel(obj.getString("pickup_chain_phone"));
                            order.setReturnName(obj.getString("return_chain"));
                            order.setDealerAddr(obj.getString("return_chain_address"));
                            order.setReturnTel(obj.getString("return_chain_phone"));
                            order.setEndTime(obj.getDate("return_date_time"));
                            order.setIdNo(obj.getString("id_card_no"));
                            order.setExpectMoney(money);
                            order.setRemark(obj.getString("remark"));
                            order.setAppointmentTime(obj.getDate("create_time"));
                            order.setOrderNo(obj.getString("ehi_order_id"));
                            order.setDuration(duration);
                            order.setIsOrder(1);
                            order.setState(1);
                            order.setStatus("R");
                            order.setIsGl(2);
                            order.setIsReplace(2);
                            order.setCompleted(2);
                            order.setOrderTime(new Date());
                            ordersService.insertOrUpdateSelective(order);
                        }catch (SQLException e) {
                            LogUtils.error(e.getMessage());
                            return JsonResult.error(ResultCode.FAIL, "同步失败");
                        }
                        return JsonResult.success("同步成功");
                    }

                } else {
                    return JsonResult.error(ResultCode.UNAVENIR,"该VIN码不是Avenir卡");
                }
            } else {
                return JsonResult.error(ResultCode.EXISTSED, "订单号已存在");
            }

        } else {
            return JsonResult.error(ResultCode.LESS_PARAM, "缺少必要参数");
        }
    }

    @RequestMapping(value = "/syncAvenirRentCarsUpateOrder",method = RequestMethod.POST)
    @ResponseBody
    public JsonResult syncAvenirRentCarsUpateOrder(@RequestBody String json) throws ParseException{
        JSONObject obj = JSON.parseObject(json);
        LogUtils.info("更新租车订单数据信息：" + obj);
        if(!obj.isEmpty()) {
            String orderNo = obj.getString("ehi_order_id");
            if(!StringUtils.isEmpty(orderNo)){
                Orders order = ordersService.selectByOrderNo(orderNo, 1);
                if(order != null) {
                    Users user = usersService.selectByUserId(order.getUserId());
                    // 统计当年使用次数
                    usersService.getCurrentYear(user, new Date(), 1);
                    if(user.getrTotalUsed() >= user.getrTotalCount()) { // 已使用次数大于等于总次数， 次数已用完
                        return JsonResult.error(ResultCode.OVER_NUMBER, "该VIN码已没有使用次数，请取消该订单");
                    }
                    if(user.getCountBy() >= user.getrCurrentCount()) { // 当年使用次数大于等于每年使用次数
                        return JsonResult.error(ResultCode.OVER_NUMBER, "该VIN码今年已没有使用次数，请取消该订单");
                    }
                    String duration = null;
                    if(obj.getDate("pickup_date_time") != null && obj.getDate("return_date_time") != null) {
                        duration = formatTime(obj.getDate("pickup_date_time"),obj.getDate("return_date_time"));
                    } else if(obj.getDate("pickup_date_time") == null && order.getBeginTime() != null && obj.getDate("return_date_time") != null){
                        duration = formatTime(order.getBeginTime(), obj.getDate("return_date_time"));
                    }
                    order.setUser(user);
                    order.setRentCity(StringUtils.trimToNull(obj.getString("pickup_city")));
                    order.setRentCity(StringUtils.trimToNull(obj.getString("pickup_city")));
                    order.setOrderCarType(StringUtils.trimToNull(obj.getString("pickup_car_model")));
                    order.setBeginTime(obj.getDate("pickup_date_time"));
                    order.setStoreName(StringUtils.trimToNull(obj.getString("pickup_chain")));
                    order.setStoreAddr(StringUtils.trimToNull(obj.getString("pickup_chain_address")));
                    order.setContactTel(StringUtils.trimToNull(obj.getString("pickup_chain_phone")));
                    order.setReturnName(StringUtils.trimToNull(obj.getString("return_chain")));
                    order.setDealerAddr(StringUtils.trimToNull(obj.getString("return_chain_address")));
                    order.setReturnTel(StringUtils.trimToNull(obj.getString("return_chain_phone")));
                    order.setIdNo(obj.getString("id_card_no"));
                    order.setEndTime(obj.getDate("return_date_time"));
                    order.setRemark(obj.getString("remark"));
                    order.setDuration(duration);
                    JSONObject priceData = obj.getJSONObject("price_data");
                    if(priceData != null && priceData.getBigDecimal("total_price") != null) {
                       BigDecimal money = priceData.getBigDecimal("total_price");
                       order.setRealityMoney(money);
                    }
                    order.setStatus(obj.getString("status"));
                    if(!StringUtils.isEmpty(obj.getString("status")) && obj.getString("status").equals("K")) {
                        order.setState(2);
                        order.setCompleted(1);
                    } else if(!StringUtils.isEmpty(obj.getString("status")) && obj.getString("status").equals("F")) {
                        order.setState(3);
                    }
                    order.setUpdateTime(obj.getDate("update_time"));
                    try {
                       ordersService.insertOrUpdateSelective(order);
                       return JsonResult.success("更新成功");
                    }catch (SQLException e) {
                        LogUtils.error(e.getMessage());
                        return JsonResult.error(ResultCode.FAIL, "更新失败");
                    }
                } else {
                    return JsonResult.error(ResultCode.NO_DATA, "没找到相关订单");
                }
            } else {
                return JsonResult.error(ResultCode.LESS_PARAM, "缺少必要参数");
            }
        } else {
            return JsonResult.error(ResultCode.LESS_PARAM, "缺少必要参数");
        }
    }

    private String formatTime(Date start, Date end) {
        long diff = (end.getTime() - start.getTime());
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long day = diff / nd;
        long hour = diff % nd / nh;
        long min = diff % nd % nh / nm;
        return day + "天" + hour + "时" + min + "分";
    }
    @RequestMapping(value = "/getServiceUsedDetail", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult getServiceUsedDetail(@RequestBody String json) {
        JSONObject obj = JSON.parseObject(json);
        String vin = obj.getString("vin");
        Integer type = obj.getInteger("type");
        LogUtils.info("获取服务使用详情信息：" + obj);
        List<Map> r_list = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        if(!StringUtils.isEmpty(vin) && (type != null || type != 0)) {
            Users user = usersService.selectByPrimaryKey(vin);
            Types types = typesService.selectByType(type);
            if(types == null) {
                return JsonResult.error(ResultCode.NO_DATA,"没有该权益");
            }
            if(user != null) {
                String[] vinType = StringUtils.split(user.getVinType(), ",");
                if(vinType.length > 0) {
                    LogUtils.info("vinType权益：" + vinType);
                    if(Arrays.asList(vinType).contains(type.toString())){
                        List<Orders> list = ordersService.selectUseDetail(vin, type);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Integer used = 0; // 已使用次数
                        Integer total = 0; // 总次数
                        if(types.getType() == 1) {
                            used = user.getrTotalUsed();
                            total = user.getrTotalCount();
                        } else if(types.getType() == 2) {
                            used = user.getaTotalUsed();
                            total = user.getaTotalCount();
                        } else if(types.getType() == 4){
                            used = user.getpTotalUsed();
                            total = user.getaTotalCount();
                        }
                        if(list.size() > 0) {
                            for(Orders order: list) {
                                Map<String, Object> map = new HashMap<>();
                                if(order.getTypeId() == 1) {
                                    map.put("used_time", sdf.format(order.getBeginTime()));
                                    map.put("used_address", order.getRentCity());
                                } else if(order.getTypeId() == 2){
                                    map.put("used_time", sdf.format(order.getUseTime()));
                                    map.put("used_address",order.getUseCarType() == 1 ? order.getAddress() : order.getDestination());
                                } else if(order.getTypeId() == 4) {
                                    map.put("used_time", sdf.format(order.getUseTime()));
                                    map.put("used_address", order.getDealerAddr());
                                }
                                r_list.add(map);
                            }
                        }
                        resultMap.put("type", types.getType());
                        resultMap.put("name", types.getName());
                        resultMap.put("used", used);
                        resultMap.put("unused", total - used);
                        resultMap.put("used_detail", r_list);
                        return  JsonResult.success(resultMap);
                    } else {
                        return  JsonResult.error(ResultCode.NO_AUTHORITY, "该VIN码没有权益");
                    }
                } else {
                    return  JsonResult.error(ResultCode.NO_AUTHORITY, "该VIN码没有权益");
                }
            } else {
                return JsonResult.error(ResultCode.UNAVENIR,"该VIN码不是Avenir卡");
            }
        } else {
            return JsonResult.error(ResultCode.LESS_PARAM, "缺少必要参数");
        }
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult test(@RequestBody String json) {
        JSONObject jsons = JSON.parseObject(json);
        LogUtils.info("json:" + jsons);
        return JsonResult.success();
    }

}

package com.avenir.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.avenir.model.JsonResult;
import com.avenir.model.OrderOV;
import com.avenir.model.Orders;
import com.avenir.model.Users;
import com.avenir.service.OrdersService;
import com.avenir.service.UsersService;
import com.avenir.utils.DateUtils;
import com.avenir.utils.LogUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Controller
public class StatisticsController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UsersService usersService;

    @RequestMapping(value="/statistics/countByAirport")
    @ResponseBody
    public JsonResult countByAriport(@RequestBody String info) {
       Map<String, Object> obj = selectCount(info, 2);
        return JsonResult.success(obj);
    }

    @RequestMapping(value = "/statistics/countByPickup")
    @ResponseBody
    public JsonResult countByPickup(@RequestBody String info) {
        Map<String, Object> obj = selectCount(info, 4);
        return JsonResult.success(obj);
    }

    @RequestMapping(value = "/statistics/countByRent")
    @ResponseBody
    public JsonResult countByRent(@RequestBody String info) {
        Map<String, Object> obj = selectCount(info, 1);
        return JsonResult.success(obj);
    }

    private Map<String, Object> selectCount(String info, Integer typeId) {
        JSONObject json = JSONObject.parseObject(info);
        Integer pn = json.getInteger("pageNum");
        Integer ps = json.getInteger("pageSize");
        Users us = JSON.parseObject(json.getString("columnFilters"), Users.class);
        us.setVinType(typeId.toString());
        LogUtils.info("查询统计信息参数：" + us.toString());
        PageHelper.startPage(pn, ps);
        List<Users> users = usersService.getUserHasVinType(us);

        for (Users user: users) {
           usersService.getCountByVinAndType(user, typeId);
        }
        PageInfo<Users> page = new PageInfo<>(users);
        Map<String, Object> obj = new HashMap<>();
        obj.put("pageNum",page.getPageNum());
        obj.put("pageSize", page.getPageSize());
        obj.put("total", page.getTotal());
        obj.put("content", page.getList());
        return obj;
    }

    @RequestMapping(value = "count/countTotal")
    @ResponseBody
    public JsonResult byTotal(@RequestBody String json) {
        LogUtils.info("数据统计typeId: " + json);
        Integer typeId = Integer.parseInt(JSON.parseObject(json).getString("typeId"));
        List<OrderOV> list = ordersService.selectOrderCountByType(typeId);
        if(list.size() > 0) {
            for (OrderOV order : list) {
                if(typeId == 1) {
                    order.setCreateTime(order.getEndTime());
                } else {
                    order.setCreateTime(order.getUseTime());
                }
                BigDecimal completed = new BigDecimal(order.getCompleted());
                BigDecimal money = order.getMoney();
                BigDecimal average =  money.divide(completed, 2, BigDecimal.ROUND_HALF_UP);
                order.setAverage(average);
            }
        }
        return JsonResult.success(list);
    }
    @RequestMapping(value = "/count/countType")
    @ResponseBody
    public JsonResult byType(@RequestBody String json) {
        LogUtils.info("车型统计TypeId: " + json);
        Integer typeId = Integer.parseInt(JSON.parseObject(json).getString("typeId"));
        List<OrderOV> list = ordersService.selectOrderCountByCarType(typeId);
        return JsonResult.success(list);
    }
    @RequestMapping(value = "count/countExper")
    @ResponseBody
    public JsonResult byExper(@RequestBody String json) {
        LogUtils.info("体验统计TypeId: " + json);
        Integer typeId = Integer.parseInt(JSON.parseObject(json).getString("typeId"));
        List<OrderOV> sevenGl = ordersService.selectOrderCountByExper(typeId, "2017款GL8");
        List<OrderOV> eightGl = ordersService.selectOrderCountByExper(typeId, "2018款GL8");
        List<OrderOV> eightJy = ordersService.selectOrderCountByExper(typeId, "2018款君越");
        List<OrderOV> nineGl = ordersService.selectOrderCountByExper(typeId, "2019款GL8");
        List<OrderOV> nineJy = ordersService.selectOrderCountByExper(typeId, "2019款君越");
        JSONObject obj = new JSONObject();
        obj.put("sevenGl", sevenGl);
        obj.put("eightGl", eightGl);
        obj.put("eightJy", eightJy);
        obj.put("nineGl", nineGl);
        obj.put("nineJy", nineJy);
        return JsonResult.success(obj);
    }
}

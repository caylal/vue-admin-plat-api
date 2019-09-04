package com.avenir.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avenir.constant.ResultCode;
import com.avenir.model.JsonResult;
import com.avenir.service.TypesService;
import com.avenir.model.Types;
import com.avenir.utils.LogUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TypesController {
    @Autowired
    private TypesService typesService;

    @RequestMapping(value = "/type/findPage")
    @ResponseBody
    public JsonResult findType(@RequestParam(value = "pageNum") Integer pageNum, @RequestParam( value = "pageSize") Integer pageSize, @RequestParam( value = "columnFilters") String columnFilters){
        Types type = JSON.parseObject(columnFilters, Types.class);
        PageHelper.startPage(pageNum, pageSize);
        List<Types> list = typesService.findType(type);
        PageInfo<Types> page = new PageInfo<>(list);
        Map<String, Object> obj = new HashMap<>();
        obj.put("pageNum",page.getPageNum());
        obj.put("pageSize", page.getPageSize());
        obj.put("total", page.getTotal());
        obj.put("content", page.getList());
        return JsonResult.success(obj);
    }

    @RequestMapping(value = "/type/findAll")
    @ResponseBody
    public JsonResult findAll(){
        List<Types> list = typesService.findAll();
        return JsonResult.success(list);
    }

    @RequestMapping(value="/type/save", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult addType(@RequestBody String info, HttpSession session) {
        Types type = JSON.parseObject(info, Types.class);
        LogUtils.info("type信息：" + type.toString());
        Types isExistType = typesService.selectByType(type.getType());
        if(isExistType != null && type.getId() == null) {
            return JsonResult.error(ResultCode.EXISTSED, "该类型值已存在");
        }
        Types isExist = typesService.selectByName(type.getName());
        if(isExist != null && type.getId() == null) {
            return JsonResult.error(ResultCode.EXISTSED, "该类型名称已存在");
        }
        try {
            Integer i = typesService.insertOrUpdateSelective(type);
            if(i > 0) {
                return JsonResult.success();
            } else {
                return JsonResult.error();
            }
        }catch (SQLException e) {
            LogUtils.error(e.getMessage());
            return JsonResult.error();
        }

    }

    @RequestMapping(value="/type/delete", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult delete(@RequestBody String info) {
        Integer id = Integer.parseInt(JSON.parseObject(info).getString("id"));
        LogUtils.info("删除类型：" + id);
        Integer i = typesService.deleteByPrimaryKey(id);
        if(i > 0) {
            return JsonResult.success();
        } else {
            return JsonResult.error();
        }
    }

}

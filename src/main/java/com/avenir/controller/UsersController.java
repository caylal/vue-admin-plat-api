package com.avenir.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.avenir.constant.ResultCode;
import com.avenir.model.Admin;
import com.avenir.model.JsonResult;
import com.avenir.model.Users;
import com.avenir.service.UsersService;
import com.avenir.utils.DateUtils;
import com.avenir.utils.HttpUtils;
import com.avenir.utils.LogUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UsersController {
    @Autowired
    private UsersService usersService;

    @Value("${buick.avenir}")
    private String user_url;

    @RequestMapping(value = "/user/findPage")
    @ResponseBody
    public JsonResult findUser(@RequestParam(value = "pageNum") Integer pageNum, @RequestParam(value = "pageSize") Integer pageSize, @RequestParam( value = "columnFilters") String columnFilters) {
        Users user = JSON.parseObject(columnFilters, Users.class);
        LogUtils.info("查询车主信息：" + user.toString());
        PageHelper.startPage(pageNum, pageSize);
        List<Users> list = usersService.findUser(user);
        PageInfo<Users> page = new PageInfo<>(list);
        Map<String, Object> obj = new HashMap<>();
        obj.put("pageNum",page.getPageNum());
        obj.put("pageSize", page.getPageSize());
        obj.put("total", page.getTotal());
        obj.put("content", page.getList());
        return JsonResult.success(obj);
    }

    @RequestMapping(value = "/user/save", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult add(@RequestBody String info) {
        Users user = JSON.parseObject(info, Users.class);
        JSONArray blist = new JSONArray();
        LogUtils.info("添加user信息：" + user.toString());
        Users isExsit = usersService.selectByPrimaryKey(user.getVin());
        if(isExsit != null && user.getId() == null) { // 添加
            return JsonResult.error(ResultCode.EXISTSED, "该VIN码已存在");
        }
        if (user.getId() != null && isExsit != null && !user.getId().equals(isExsit.getId())) {
            return JsonResult.error(ResultCode.EXISTSED, "该VIN码已存在");
        }
        try {
            Integer i = usersService.insertOrUpdateSelective(user);
            if(i > 0) {
                JSONObject json = new JSONObject();
                json.put("vin", user.getVin());
                json.put("mobile", user.getPhone());
                json.put("card_no", user.getServiceCard());
                json.put("card_type", Integer.parseInt(user.getServiceType()));
                blist.add(json);
                // 推送车主信息
                if(blist.size() > 0) {
                    JSONObject res = HttpUtils.doPost(user_url, blist);
                    String rt = res != null ? res.toString():"推送失败";
                    LogUtils.info(">>>>>>>>>>>>推送车主信息结果： " + blist+",结果："+rt);
                }
                return JsonResult.success();
            } else {
                return JsonResult.error();
            }
        }catch (SQLException e) {
            LogUtils.error("insertUser: " + e.getMessage());
            return JsonResult.error();
        }
    }

    @RequestMapping(value = "/user/delete", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult delete(@RequestBody String info) {
        Integer id = Integer.parseInt(JSON.parseObject(info).getString("id"));
        LogUtils.info("删除用户：" + id);
        Integer i = usersService.deleteByPrimaryKey(id);
        if(i > 0) {
            return JsonResult.success();
        } else {
            return JsonResult.error();
        }
    }
    @RequestMapping(value = "/user/import")
    @ResponseBody
    public JsonResult importData(MultipartFile file, HttpServletRequest req) throws IOException {
        int total = 1;
        try {
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //日期格式化
           // SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");  //日期格式化
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<Users> list = new ArrayList<>();
            List<Users> update_list = new ArrayList<>();
            JSONArray blist = new JSONArray();
            //int rowSize = sheet.getPhysicalNumberOfRows();
            int rowLength =sheet.getPhysicalNumberOfRows(); //总行数
            Row row = sheet.getRow(0); //工作表的
            int colLength = row.getLastCellNum(); //总列数
            //得到指定的单元格
            Cell cell = row.getCell(0);

            //Date st = sdf.parse("2017-01-01 00:00:00");
            Date et = DateUtils.formatDate("2018-03-01 00:00:00", 3);
            Calendar date = Calendar.getInstance();
            for(int i = 1; i < rowLength; i++) {
                total ++;
                row = sheet.getRow(i);
                List<Object> li = new ArrayList<>();
                for(int j = 1; j < colLength; j++) {
                    cell = row.getCell(j);
                    li.add(getCellValue(cell));
                }
                String vin  = li.get(2).toString().trim();
                String phone = li.get(4).toString().trim();
                if(StringUtils.isEmpty(phone)) {
                    return JsonResult.error(501,"电话号码不能为空, 行 " + i);
                }
                Users isExist = null;
                if(!StringUtils.isEmpty(vin)) {
                    isExist = usersService.selectByPrimaryKey(vin); // 查找车主是否已存在
                    String report = li.get(6).toString().isEmpty() ? null : li.get(6).toString();
                    String invoice =li.get(5).toString().isEmpty() ? null : li.get(5).toString();
                    Date rptTime = report != null ? (report.indexOf("/") > 0 ? DateUtils.formatDate(report, 2): DateUtils.formatDate(report, 3)) : null;
                    Date ivTime =  invoice != null ? (invoice.indexOf("/") > 0 ? DateUtils.formatDate(invoice, 2) : DateUtils.formatDate(invoice, 3)) : null;
                    Integer dueCount = 0;
                    Date dueTime = null;
                    String cartype = "";
                    String serviceType = "0";
                    if(rptTime != null) {
                        date.setTime(rptTime);
                        if(rptTime.getTime() <= et.getTime()) {
                            dueCount = 6;
                            date.add(Calendar.YEAR, 6);
                        } else {
                            dueCount = 5;
                            date.add(Calendar.YEAR, 5);
                        }
                        dueTime = date.getTime();
                    }
                    if(vin != null && rptTime != null) {
                        date.setTime(rptTime);
                        int year = date.get(Calendar.YEAR);
                        if(vin.indexOf("GUL8") > 0) {
                            if (rptTime.getTime() < DateUtils.formatDate("2017-01-01 00:00:00", 3).getTime()) {
                                cartype = "2017款GL8";
                            } else {
                                cartype = year + "款GL8";
                            }
                        } else if(vin.indexOf("ZJ55") > 0) {
                            if(rptTime.getTime() < DateUtils.formatDate("2018-01-01 00:00:00", 3).getTime()) {
                                cartype = "2018款君越";
                            } else {
                                cartype = year + "款君越";
                            }
                        }
                        if(year <= 2017) {
                            serviceType = "0";
                        } else if(year == 2018){
                            serviceType = "1";
                        } else if(year == 2019){
                            serviceType = "2";
                        }
                    }
                    if(isExist == null) { // 新增
                        Users user = new Users();
                        user.setVin(vin);
                        user.setServiceCard(li.get(0).toString());
                        user.setServiceCardOld(li.get(1).toString());
                        user.setUserName(li.get(3).toString());
                        user.setPhone(phone);
                        user.setInvoiceTime(ivTime);
                        user.setReportTime(rptTime);
                        user.setDueCount(dueCount);
                        user.setDueTime(dueTime);
                        user.setServiceType(serviceType);
                        user.setCarType(cartype);
                        user.setaTotalCount(20);
                        user.setaCurrentCount(4);
                        user.setpTotalCount(20);
                        user.setpCurrentCount(4);
                        user.setrTotalCount(5);
                        user.setrCurrentCount(1);
                        user.setVinType("1,2,4");
                        user.setCreateBy("admin");
                        user.setCreateTime(new Date());
                        list.add(user);
                        JSONObject json = new JSONObject();
                        json.put("vin", user.getVin());
                        json.put("mobile", user.getPhone());
                        json.put("card_no", user.getServiceCard());
                        json.put("card_type", Integer.parseInt(user.getServiceType()));
                        blist.add(json);
                    } else {
                        isExist.setServiceCard(li.get(0).toString());
                        isExist.setServiceCardOld(li.get(1).toString());
                        isExist.setUserName(li.get(3).toString());
                        isExist.setPhone(phone);
                        isExist.setInvoiceTime(ivTime);
                        isExist.setReportTime(rptTime);
                        isExist.setDueCount(dueCount);
                        isExist.setDueTime(dueTime);
                        isExist.setServiceType(serviceType);
                        isExist.setCarType(cartype);
                        isExist.setaTotalCount(20);
                        isExist.setaCurrentCount(4);
                        isExist.setpTotalCount(20);
                        isExist.setpCurrentCount(4);
                        isExist.setrTotalCount(5);
                        isExist.setrCurrentCount(1);
                        isExist.setVinType("1,2,4");
                        isExist.setUpdateBy("admin");
                        isExist.setUpdateTime(new Date());
                        update_list.add(isExist);
                    }
                }
            }
            LogUtils.info("async_carInfo:" + list);
            LogUtils.info("async_carInfo-size:" + list.size());
            LogUtils.info("async_update_carInfo: " + update_list);
            LogUtils.info("async_update_carInfo-size:" + update_list.size());
            if(update_list.size() > 0) { // 更新
                LogUtils.info("update_carInfo>>>>>>" );
                int up_count = usersService.updateBatch(update_list);
                LogUtils.info("up_count:" + up_count);
            }
            if(list.size() > 0) { // 新增
                int count = usersService.insertBatch(list);
                LogUtils.info("count:" + count);
                if (count > 0) {
                    // 推送车主信息
                    if(blist.size() > 0) {
                        JSONObject res = HttpUtils.doPost(user_url, blist);
                        String rt = res != null ? res.toString():"推送失败";
                        LogUtils.info(">>>>>>>>>>>>推送车主信息结果： " + blist+",结果："+rt);
                    }
                    return JsonResult.success();
                }
            }
        }catch (Exception e) {
            LogUtils.error("import: 行：" + total + "，信息" + e.getMessage());
            return JsonResult.error(600,"行：" + total + "，信息" + e.getMessage());
        }

        return JsonResult.success();
    }

    private  Object getCellValue(Cell cell){
        Object value = "";
        DecimalFormat df = new DecimalFormat("0");  //格式化number String字符
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //日期格式化
        DecimalFormat df2 = new DecimalFormat("0");  //格式化数字
        if(cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    value = cell.getRichStringCellValue().getString();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if("General".equals(cell.getCellStyle().getDataFormatString())){
                        value = df.format(cell.getNumericCellValue());
                    }else if("m/d/yy".equals(cell.getCellStyle().getDataFormatString())){
                        value = sdf.format(cell.getDateCellValue());
                    }else{
                        value = df2.format(cell.getNumericCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    value = cell.getBooleanCellValue();
                    break;
                case Cell.CELL_TYPE_BLANK:
                    value = "";
                    break;
                default:
                    break;
            }
        }
        return value;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult login(@RequestBody String info){
        JSONObject obj = JSON.parseObject(info);
        String name = obj.getString("account");
        String pwd = obj.getString("password");
        Admin admin = usersService.selectByNameAndPwd(name, pwd);
        if(admin != null) {
            return JsonResult.success();
        } else {
            return JsonResult.error(ResultCode.FAIL, "用户名或密码错误");
        }
    }

    @RequestMapping(value = "user/test", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult test(@RequestBody String info){
        JSONObject obj = JSON.parseObject(info);
        LogUtils.info("return Obj:" + info);
        return JsonResult.success();
    }
}

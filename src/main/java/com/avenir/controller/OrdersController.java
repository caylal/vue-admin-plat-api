package com.avenir.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.avenir.constant.CommonConst;
import com.avenir.constant.ResultCode;
import com.avenir.model.JsonResult;
import com.avenir.model.Orders;
import com.avenir.model.Users;
import com.avenir.service.OrdersService;
import com.avenir.service.UsersService;
import com.avenir.utils.DateUtils;
import com.avenir.utils.HttpUtils;
import com.avenir.utils.LogUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.rmi.runtime.Log;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UsersService usersService;

    @Value("${buick.airport}")
    private String airport_url;

    @RequestMapping(value = "/order/findPage", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult findUser(@RequestBody String info) {
        JSONObject json = JSONObject.parseObject(info);
        Integer pn = json.getInteger("pageNum");
        Integer ps = json.getInteger("pageSize");
        Orders order = JSON.parseObject(json.getString("columnFilters"), Orders.class);
        LogUtils.info("查询订单信息：" + order.toString());
        PageHelper.startPage(pn, ps);
        List<Orders> list = ordersService.findOrder(order);
        PageInfo<Orders> page = new PageInfo<>(list);
        Map<String, Object> obj = new HashMap<>();
        obj.put("pageNum",page.getPageNum());
        obj.put("pageSize", page.getPageSize());
        obj.put("total", page.getTotal());
        obj.put("content", page.getList());
        return JsonResult.success(obj);
    }
    @RequestMapping(value = "/order/save", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult addOrEdit(@RequestBody String info) throws ParseException{
        Orders order = JSON.parseObject(info, Orders.class);
        Orders isExist = null;
        order.setUserId(order.getUser().getId());
        LogUtils.info("order信息：" + order.toString());
        if(order.getId() == null) { // 新增订单时 查找是否有重复订单
            Orders rep = new Orders();
            if(order.getTypeId() == 1) {
                rep = ordersService.selectRepeatOrder(order.getUser().getId(), order.getBeginTime(), order.getEndTime());
            } else {
                rep = ordersService.selectRepeatOrder(order.getUser().getId(), order.getUseTime());
            }
            if(rep != null) {
                return JsonResult.error("订单已存在是否要跳转到该订单", rep);
            }
        }

        if(order.getOrderNo() != null && !StringUtils.isEmpty(order.getOrderNo())) {
            isExist = ordersService.selectByOrderNo(order.getOrderNo(), order.getTypeId());
            if(isExist != null && order.getId() == null) { // 添加
                return JsonResult.error(ResultCode.EXISTSED, "订单号已存在");
            }
            if (order.getId() != null && isExist != null && !order.getId().equals(isExist.getId())) {
                return JsonResult.error(ResultCode.EXISTSED, "订单号已存在");
            }
        }
        try {
            Integer i = ordersService.insertOrUpdateSelective(order);
            if(i > 0) {
                if (order.getTypeId() == 2 && order.getOrderType() == 2 && isExist != null && order.getOrderNo() != null) { // 推送微信下的单
                    if(!order.getUserName().equals(isExist.getUserName()) || !order.getUserTel().equals(isExist.getUserTel()) ||
                            !order.getFlight().equals(isExist.getFlight()) || order.getUseTime().getTime() != isExist.getUseTime().getTime() ||
                            order.getUseCarType() != isExist.getUseCarType() || !order.getAddress().equals(isExist.getAddress()) ||
                            !order.getDestination().equals(isExist.getDestination()) || order.getAppointmentTime().getTime() != isExist.getAppointmentTime().getTime()) {
                        Map<String, String> map = new HashMap<>();
                        map.put("order_id", order.getOrderNo());
                        map.put("openid", order.getOpenId());
                        map.put("vin", order.getUser().getVin());
                        map.put("username", order.getUserName());
                        map.put("phonenumber", order.getUserTel());
                        map.put("flightinfo", order.getFlight());
                        map.put("usecartime", DateUtils.formatDate1(order.getUseTime(), 3));
                        map.put("usecartype", order.getUseCarType() == 1 ? "接机" : "送机");
                        map.put("startplace", order.getAddress());
                        map.put("endplace", order.getDestination());
                        map.put("addtime", DateUtils.formatDate1(order.getAppointmentTime(), 3));
                        try {
                            String res = HttpUtils.doPost(airport_url, map);
                            String rt = res != null ? res.toString():"推送失败";
                            LogUtils.info(">>>>>>>>>>>>推送车主信息结果： " + map+",结果："+rt);
                        }catch (Exception e) {
                            LogUtils.info(">>>>>>>>>>>>推送信息error： " + e.getMessage());
                        }

                    }

                }
                return JsonResult.success();
            } else {
                return JsonResult.error();
            }
        }catch (SQLException e) {
            LogUtils.error("insertOrUpdateOrder: " + e.getMessage());
            return JsonResult.error();
        }
    }

    @RequestMapping(value = "/order/cancel")
    @ResponseBody
    public JsonResult cancel(@RequestBody String json) {
        Orders order = JSON.parseObject(json, Orders.class);
        order.setState(3);
        order.setCompleted(2);
        try {
            Integer i = ordersService.insertOrUpdateSelective(order);
            if(i > 0) {
                return JsonResult.success();
            } else {
                return JsonResult.error();
            }
        }catch (SQLException e) {
            LogUtils.error("cancelOrder: " + e.getMessage());
            return JsonResult.error();
        }
    }

    @RequestMapping(value = "/order/countByVin", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult countByVin(@RequestBody String json) throws ParseException{
        JSONObject obj = JSON.parseObject(json);
        Integer typeId = obj.getInteger("typeId");
        Users user = JSON.parseObject(obj.getString("user"), Users.class);
        usersService.getCurrentYear(user,new Date(), typeId);
        return JsonResult.success(user);
    }
    @RequestMapping(value = "/order/delete", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult delete(@RequestBody String info) {
        Integer id = Integer.parseInt(JSON.parseObject(info).getString("id"));
        LogUtils.info("删除订单：" + id);
        Integer i = ordersService.deleteByPrimaryKey(id);
        if(i > 0) {
            return JsonResult.success();
        } else {
            return JsonResult.error();
        }
    }
    @RequestMapping(value = "/order/ExportDate", method = RequestMethod.POST)
    @ResponseBody
    public void export(@RequestBody String info, HttpServletResponse response) {
        JSONObject json = JSONObject.parseObject(info);
        Orders order = JSON.parseObject(json.getString("columnFilters"), Orders.class);
        LogUtils.info("导出查询订单信息：" + order.toString());
        List<Orders> list = ordersService.findOrder(order);
        OutputStream out = null;
        XSSFWorkbook workbook = new XSSFWorkbook();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fileName = null;
        if (order.getTypeId() == 1) {
            XSSFSheet sheet = workbook.createSheet("租车订单表");
            String[] title = {"下单渠道","来电时间","用户姓名","用户openid","电话","完整车架号","服务卡号","上报时间","车型","身份证号","驾驶证号码","是否有信用卡","租车城市","用户地址","预约时间","取车门店名称","取车门店地址","预约车型","租车开始时间", "租车结束时间","租车时长","服务商","记录客服","是否已下单","下单手机","下单时间","联系人姓名","联系电话","是否有GL8车型","回访时间","回访客服","如无车型是否同意更换","更换的车型","服务是否已完成","实际用车","预计金额（元）","实际金额（元）","订单号","备注","录音编号"};
            createTitle(workbook, sheet,title);
            int rowNum = 1;
            for(Orders o : list) {
                XSSFRow row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(o.getOrderType() != null ? (o.getOrderType().equals(1) ? "人工" : "微信") : "");
                row.createCell(1).setCellValue(o.getCallTime() != null ? df.format(o.getCallTime()) : "");
                row.createCell(2).setCellValue(StringUtils.trimToEmpty(o.getUserName()));
                row.createCell(3).setCellValue(StringUtils.trimToEmpty(o.getOpenId()));
                row.createCell(4).setCellValue(StringUtils.trimToEmpty(o.getUserTel()));
                row.createCell(5).setCellValue(o.getUser() != null ? StringUtils.trimToEmpty(o.getUser().getVin()) : "");
                row.createCell(6).setCellValue(o.getUser() != null ? StringUtils.trimToEmpty(o.getUser().getServiceCard()) : "");
                row.createCell(7).setCellValue(o.getUser() != null && o.getUser().getReportTime() != null ? df.format(o.getUser().getReportTime()) : "");
                row.createCell(8).setCellValue(o.getUser() != null ? StringUtils.trimToEmpty(o.getUser().getCarType()) : "");
                row.createCell(9).setCellValue(StringUtils.trimToEmpty(o.getIdNo()));
                row.createCell(10).setCellValue(StringUtils.trimToEmpty(o.getDriverNo()));
                row.createCell(11).setCellValue(o.getIsCredit() != null ? (o.getIsCredit().equals(1) ? "是" : "否") : "");
                row.createCell(12).setCellValue(StringUtils.trimToEmpty(o.getRentCity()));
                row.createCell(13).setCellValue(StringUtils.trimToEmpty(o.getUserAddr()));
                row.createCell(14).setCellValue(o.getAppointmentTime() != null ? df.format(o.getAppointmentTime()) : "");
                row.createCell(15).setCellValue(StringUtils.trimToEmpty(o.getStoreName()));
                row.createCell(16).setCellValue(StringUtils.trimToEmpty(o.getStoreAddr()));
                row.createCell(17).setCellValue(StringUtils.trimToEmpty(o.getOrderCarType()));
                row.createCell(18).setCellValue(o.getBeginTime() != null ? df.format(o.getBeginTime()) : "");
                row.createCell(19).setCellValue(o.getEndTime() != null ? df.format(o.getEndTime()) : "");
                row.createCell(20).setCellValue(StringUtils.trimToEmpty(o.getDuration()));
                row.createCell(21).setCellValue(StringUtils.trimToEmpty(o.getServiceProvider()));
                row.createCell(22).setCellValue(StringUtils.trimToEmpty(o.getOrderService()));
                row.createCell(23).setCellValue(o.getIsOrder() != null ? (o.getIsOrder().equals(1) ? "是" : "否") : "");
                row.createCell(24).setCellValue(StringUtils.trimToEmpty(o.getOrderTel()));
                row.createCell(25).setCellValue(o.getOrderTime() != null ? df.format(o.getOrderTime()) : "");
                row.createCell(26).setCellValue(StringUtils.trimToEmpty(o.getContactName()));
                row.createCell(27).setCellValue(StringUtils.trimToEmpty(o.getContactTel()));
                row.createCell(28).setCellValue(o.getIsGl() != null ? (o.getIsGl().equals(1) ? "是" : "否") : "");
                row.createCell(29).setCellValue(o.getVisitTime() != null ? df.format(o.getVisitTime()) : "");
                row.createCell(30).setCellValue(StringUtils.trimToEmpty(o.getVisitor()));
                row.createCell(31).setCellValue(o.getIsReplace() != null ? (o.getIsReplace().equals(1) ? "同意" : "不同意") : "");
                row.createCell(32).setCellValue(StringUtils.trimToEmpty(o.getReplaceCar()));
                row.createCell(33).setCellValue(o.getCompleted() != null ? (o.getCompleted().equals(1) ? "是" : "否") : "");
                row.createCell(34).setCellValue(StringUtils.trimToEmpty(o.getActualCar()));
                row.createCell(35).setCellValue(o.getExpectMoney() != null ? o.getExpectMoney().toString() : "");
                row.createCell(36).setCellValue(o.getRealityMoney() != null ? o.getRealityMoney().toString() : "");
                row.createCell(37).setCellValue(StringUtils.trimToEmpty(o.getOrderNo()));
                row.createCell(38).setCellValue(StringUtils.trimToEmpty(o.getRemark()));
                row.createCell(39).setCellValue(StringUtils.trimToEmpty(o.getRecordNo()));
                rowNum++;
            }
        } else if(order.getTypeId() == 2) {
            String[] title = {"下单渠道","来电时间","用户姓名","用户openid","电话","完整车架号","服务卡号","上报时间","车型","航班信息","机场/站","接车地址","目的地地址","用车时间","接机时间","预约时间","乘车人数","用车类型","是否需要下单","是否已下单","下单时间","下单客服","是否回访成功","回访人","回访日期","服务是否已完成","预计金额（元）","实际金额（元）","备注","订单号","录音编号"};
            XSSFSheet sheet = workbook.createSheet("接送机订单表");
            createTitle(workbook, sheet, title);
            int rowNum = 1;
            for(Orders o : list) {
                XSSFRow row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(o.getOrderType().equals(1) ? "人工" : "微信");
                row.createCell(1).setCellValue(o.getCallTime() != null ? df.format(o.getCallTime()) : "");
                row.createCell(2).setCellValue(StringUtils.trimToEmpty(o.getUserName()));
                row.createCell(3).setCellValue(StringUtils.trimToEmpty(o.getOpenId()));
                row.createCell(4).setCellValue(StringUtils.trimToEmpty(o.getUserTel()));
                row.createCell(5).setCellValue(o.getUser() != null ? StringUtils.trimToEmpty(o.getUser().getVin()) : "");
                row.createCell(6).setCellValue(o.getUser() != null ? StringUtils.trimToEmpty(o.getUser().getServiceCard()) : "");
                row.createCell(7).setCellValue(o.getUser() != null && o.getUser().getReportTime() != null ? df.format(o.getUser().getReportTime()) : "");
                row.createCell(8).setCellValue(o.getUser() != null ? StringUtils.trimToEmpty(o.getUser().getCarType()) : "");
                row.createCell(9).setCellValue(StringUtils.trimToEmpty(o.getFlight()));
                row.createCell(10).setCellValue(StringUtils.trimToEmpty(o.getAirport()));
                row.createCell(11).setCellValue(StringUtils.trimToEmpty(o.getAddress()));
                row.createCell(12).setCellValue(StringUtils.trimToEmpty(o.getDestination()));
                row.createCell(13).setCellValue(o.getUseTime() != null ? df.format(o.getUseTime()) : "");
                row.createCell(14).setCellValue(StringUtils.trimToEmpty(o.getAirportPickupTime()));
                row.createCell(15).setCellValue(o.getAppointmentTime() != null ? df.format(o.getAppointmentTime()) : "");
                row.createCell(16).setCellValue(o.getNumber() != null ? o.getNumber().toString() : "");
                row.createCell(17).setCellValue(o.getUseCarType() != null ? (o.getUseCarType().equals(1) ? "接机" : "送机") : "");
                row.createCell(18).setCellValue(o.getNeedOrder() != null ? (o.getNeedOrder().equals(1) ? "是" : "否") : "");
                row.createCell(19).setCellValue(o.getIsOrder() != null ? (o.getIsOrder().equals(1) ? "是" : "否") : "");
                row.createCell(20).setCellValue(o.getOrderTime() != null ? df.format(o.getOrderTime()) : "");
                row.createCell(21).setCellValue(StringUtils.trimToEmpty(o.getOrderService()));
                row.createCell(22).setCellValue(o.getIsVisit() != null ? (o.getIsVisit().equals(1) ? "是" : "否") : "");
                row.createCell(23).setCellValue(StringUtils.trimToEmpty(o.getVisitor()));
                row.createCell(24).setCellValue(o.getVisitTime() != null ? df.format(o.getVisitTime()) : "");
                row.createCell(25).setCellValue(o.getCompleted()!= null ? (o.getCompleted().equals(1) ? "是" : "否") : "");
                row.createCell(26).setCellValue(o.getExpectMoney() != null ? o.getExpectMoney().toString() : "");
                row.createCell(27).setCellValue(o.getRealityMoney() != null ? o.getRealityMoney().toString() : "");
                row.createCell(28).setCellValue(StringUtils.trimToEmpty(o.getRemark()));
                row.createCell(29).setCellValue(StringUtils.trimToEmpty(o.getOrderNo()));
                row.createCell(30).setCellValue(StringUtils.trimToEmpty(o.getRecordNo()));
                rowNum++;
            }
        } else if(order.getTypeId() == 4) {
            String[] title = {"来电时间","用户姓名","电话","完整车架号","服务卡号","上报时间","车型","车牌号","用户地址","出发时间","经销商地址","联系人姓名（4S店）","联系人手机（4S店）","上门取/送车","记录客服","是否已下单","下单时间","服务是否已完成","金额（元）","备注","订单号"};
            XSSFSheet sheet = workbook.createSheet("取送车订单表");
            createTitle(workbook, sheet, title);
            int rowNum = 1;
            for(Orders o : list) {
                XSSFRow row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(o.getCallTime() != null ? df.format(o.getCallTime()) : "");
                row.createCell(1).setCellValue(StringUtils.trimToEmpty(o.getUserName()));
                row.createCell(2).setCellValue(StringUtils.trimToEmpty(o.getUserTel()));
                row.createCell(3).setCellValue(o.getUser() != null ? StringUtils.trimToEmpty(o.getUser().getVin()) : "");
                row.createCell(4).setCellValue(o.getUser() != null ? StringUtils.trimToEmpty(o.getUser().getServiceCard()) : "");
                row.createCell(5).setCellValue(o.getUser() != null && o.getUser().getReportTime() != null ? df.format(o.getUser().getReportTime()) : "");
                row.createCell(6).setCellValue(o.getUser() != null ? StringUtils.trimToEmpty(o.getUser().getCarType()) : "");
                row.createCell(7).setCellValue(o.getUser() != null ? StringUtils.trimToEmpty(o.getUser().getCarLicense()) : "");
                row.createCell(8).setCellValue(StringUtils.trimToEmpty(o.getUserAddr()));
                row.createCell(9).setCellValue(o.getUseTime() != null ? df.format(o.getUseTime()) : "");
                row.createCell(10).setCellValue(StringUtils.trimToEmpty(o.getDealerAddr()));
                row.createCell(11).setCellValue(StringUtils.trimToEmpty(o.getContactName()));
                row.createCell(12).setCellValue(StringUtils.trimToEmpty(o.getContactTel()));
                row.createCell(13).setCellValue(o.getUseCarType() != null ? (o.getUseCarType().equals(1) ? "取车" : "送车") : "");
                row.createCell(14).setCellValue(StringUtils.trimToEmpty(o.getOrderService()));
                row.createCell(15).setCellValue(o.getIsOrder() != null ? (o.getIsOrder().equals(1) ? "是" : "否") : "");
                row.createCell(16).setCellValue(o.getOrderTime() != null ? df.format(o.getOrderTime()) : "");
                row.createCell(17).setCellValue(o.getCompleted() != null ? (o.getCompleted().equals(1) ? "是" : "否") : "");
                row.createCell(18).setCellValue(o.getRealityMoney() != null ? o.getRealityMoney().toString() : "");
                row.createCell(19).setCellValue(StringUtils.trimToEmpty(o.getRemark()));
                row.createCell(20).setCellValue(StringUtils.trimToEmpty(o.getOrderNo()));
                rowNum++;
            }
        }
        try {
            out = generateResponseExcel(fileName, response);
            workbook.write(out);

        } catch (IOException e) {
            LogUtils.error(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if(out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LogUtils.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private ServletOutputStream generateResponseExcel(String fileName, HttpServletResponse response) throws IOException {
        fileName = fileName == null || "".equals(fileName) ? "excel" : URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        return response.getOutputStream();
    }
    private void createTitle(XSSFWorkbook workbook, XSSFSheet sheet, String[] title) {
        XSSFRow row = sheet.createRow(0);
        // 设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
        sheet.setColumnWidth(2, 12 * 256);
        sheet.setColumnWidth(3, 17 * 256);
        // 设置为居中加粗
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setFont(font);
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm:ss"));
        XSSFCell cell;
        for(int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            XSSFRichTextString text = new XSSFRichTextString(title[i]);
            cell.setCellValue(text);
            cell.setCellStyle(style);
        }
    }

    /*
    * 导入订单数据，微信接送机
    * */
    @RequestMapping(value = "/import/orderAWechat", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult importData() throws ParseException {
        List<Orders> sqlData = new ArrayList<>();
        int userCount = 0;
        int count = 0;
        try {
            //File csv = new File("D:/Book/wechatAirport.csv");
            DataInputStream csv = new DataInputStream(new FileInputStream(new File("D:/Book/20190829/wechat_airport.csv")));
            BufferedReader br = new BufferedReader(new InputStreamReader(csv,"GBK"));
            String line="";
            int i = 0;
            while((line = br.readLine()) != null){
                Orders order = new Orders();
                String item[] = line.split(",");
                LogUtils.info("line: >>>>>>" + line);
                LogUtils.info("item: >>>>>>" + item.toString());
                String vin = item[10]; // 查找车主
                if(!vin.isEmpty()) {
                   Users user =  usersService.selectByPrimaryKey(vin.trim());
                    if(user != null) {
                       order.setUserId(user.getId());
                    } else {
                       Users u = new Users();
                       u.setVin(vin);
                       u.setPhone(item[3].trim());
                       u.setUserName(item[1].trim());
                       u.setOpenId(item[2].trim());
                       u.setCreateBy("admin");
                       u.setCreateTime(new Date());
                       try {
                           int ic =  usersService.insertOrUpdateSelective(u);
                           LogUtils.info("u.getId: " + u.getId());
                           if(ic > 0) {
                               userCount ++ ;
                               order.setUserId(u.getId());
                           }
                       }catch (SQLException e) {
                           LogUtils.error("insertError: " + e.getMessage());
                       }
                    }
                }
                order.setTypeId(2); //接送机
                order.setUserTel(item[3].trim());
                order.setUserName(StringUtils.trimToNull(item[1].trim()));
                order.setOpenId(StringUtils.trimToNull(item[2].trim()));
                order.setState(StringUtils.isEmpty(item[20]) ? 1 : (item[20].trim().equals("是") ? 2 : 3));
                order.setOrderType(2); // 微信
                order.setFlight(StringUtils.trimToNull(item[4].trim()));
                order.setAddress(StringUtils.trimToNull(item[7].trim()));
                order.setDestination(StringUtils.trimToNull(item[8].trim()));
                order.setUseTime(StringUtils.isEmpty(item[5].trim()) ? null : DateUtils.formatDate(item[5],1));
                order.setAirportPickupTime(StringUtils.trimToNull(item[6].trim()));
                order.setAppointmentTime(StringUtils.isEmpty(item[11].trim()) ? null : DateUtils.formatDate(item[11].trim(),1));
                order.setOrderTime(StringUtils.isEmpty(item[12].trim()) ? (StringUtils.isEmpty(item[19].trim()) ? null :  DateUtils.formatDate(item[19].trim(), 2)) : DateUtils.formatDate(item[12].trim(), 2));
                order.setUseCarType(StringUtils.isEmpty(item[9].trim().trim()) ? 2 : (item[9].trim().contains("接机") ? 1 : 2));
                order.setIsVisit(StringUtils.isEmpty(item[13].trim()) ? 2 : (item[13].trim().equals("回访成功") ? 1 : 2));
                order.setNeedOrder(StringUtils.isEmpty(item[14].trim()) ? 2 : (item[14].trim().equals("需要") ? 1 : 2));
                order.setVisitor(StringUtils.trimToNull(item[15].trim()));
                order.setVisitTime(StringUtils.isEmpty(item[16].trim()) ? null : DateUtils.formatDate(item[16].trim(), 1));
                order.setIsOrder(StringUtils.isEmpty(item[17].trim()) ? 2 : (item[17].trim().equals("是") ? 1 : 2));
                order.setOrderService(StringUtils.trimToNull(item[18].trim()));
                order.setCompleted(StringUtils.isEmpty(item[20].trim()) ? 2 : (item[20].trim().equals("是") ? 1 : 2));
                order.setRemark(item.length >21 ? (StringUtils.trimToNull(item[21].trim())) : null);
                if(item.length > 22 ) {
                    String m = item[22].trim();
                    if(!StringUtils.isEmpty(m)) {
                        BigDecimal b = new BigDecimal(m);
                        order.setExpectMoney(b);
                    }
                }
                if(item.length > 23) {
                    String m = item[23].trim();
                    if(!StringUtils.isEmpty(m)) {
                        BigDecimal b = new BigDecimal(m);
                        order.setRealityMoney(b);
                    }
                }
                order.setOrderNo(item.length >24 ? (StringUtils.trimToNull(item[24].trim())) : null);
                order.setRecordNo(item.length >25 ? (StringUtils.trimToNull(item[25].trim())) : null);
                order.setCreateBy("admin");
                order.setCreateTime(new Date());
                sqlData.add(order);
                i++;
            }
            System.out.println(i);
            try {
                count = ordersService.insertBatchAW(sqlData);

            } catch (SQLException e) {
                LogUtils.error(e.getMessage());
                return JsonResult.error(500, e.getMessage());
            }
            br.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return JsonResult.error(500, e.getMessage());
        }
        catch(IOException e){
            e.printStackTrace();
            return JsonResult.error(500, e.getMessage());
        }
        if(count > 0) {
            return JsonResult.success("插入车主：" + userCount + ", 订单：" + count);
        } else {
            return JsonResult.error();
        }
    }

    /*
   * 导入订单数据，人工接送机
   * */
    @RequestMapping(value = "/import/orderA", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult importDataa() throws ParseException {
        List<Orders> sqlData = new ArrayList<>();
        int userCount = 0;
        int count = 0;
        try {
            //File csv = new File("D:/Book/wechatAirport.csv");
            DataInputStream csv = new DataInputStream(new FileInputStream(new File("D:/Book/20190829/airport.csv")));
            BufferedReader br = new BufferedReader(new InputStreamReader(csv,"GBK"));
            String line="";
            int i = 0;
            while((line = br.readLine()) != null){
                Orders order = new Orders();
                String item[] = line.split(",");
                LogUtils.info("line: >>>>>>" + line);
                String vin = item[5]; // 查找车主
                if(!vin.isEmpty()) {
                    Users user =  usersService.selectByPrimaryKey(vin.trim());
                    if(user != null) {
                        order.setUserId(user.getId());
                    } else {
                        Users u = new Users();
                        u.setVin(vin);
                        u.setPhone(item[3].trim());
                        u.setUserName(item[2].trim());
                        u.setServiceCard(item[4].trim());
                        u.setCreateBy("admin");
                        u.setCreateTime(new Date());
                        try {
                            int ic =  usersService.insertOrUpdateSelective(u);
                            LogUtils.info("u.getId: " + u.getId());
                            if(ic > 0) {
                                userCount ++ ;
                                order.setUserId(u.getId());
                            }
                        }catch (SQLException e) {
                            LogUtils.error("insertError: " + e.getMessage());
                        }
                    }
                }
                order.setTypeId(2); //接送机
                order.setUserTel(item[3].trim());
                order.setUserName(StringUtils.trimToNull(item[2].trim()));
                order.setState(StringUtils.isEmpty(item[17]) ? 1 : (item[17].trim().equals("是") ? 2 : 3));
                order.setOrderType(1); // 人工
                order.setCallTime(StringUtils.isEmpty(item[1].trim()) ? null : DateUtils.formatDate(item[1],1));
                order.setFlight(StringUtils.trimToNull(item[12].trim()));
                order.setAirport(StringUtils.trimToNull(item[6].trim()));
                order.setAddress(StringUtils.trimToNull(item[7].trim()));
                order.setDestination(StringUtils.trimToNull(item[8].trim()));
                order.setUseTime(StringUtils.isEmpty(item[9].trim()) ? null : DateUtils.formatDate(item[9],1));
                order.setAirportPickupTime(StringUtils.trimToNull(item[11].trim()));
                order.setOrderTime(StringUtils.isEmpty(item[10].trim()) ?  null : DateUtils.formatDate(item[10].trim(), 1));
                order.setUseCarType(StringUtils.isEmpty(item[14].trim()) ? 2 : (item[14].trim().contains("接机") ? 1 : 2));
                order.setNumber(StringUtils.trimToNull(item[13].trim()));
                order.setIsOrder(StringUtils.isEmpty(item[16].trim()) ? 2 : (item[16].trim().equals("是") ? 1 : 2));
                order.setOrderService(StringUtils.trimToNull(item[15].trim()));
                order.setCompleted(StringUtils.isEmpty(item[17].trim()) ? 2 : (item[17].trim().equals("是") ? 1 : 2));
                order.setRemark(item.length >18 ? (StringUtils.trimToNull(item[18].trim())) : null);
                if(item.length > 19 ) {
                    String m = item[19].trim();
                    if(!StringUtils.isEmpty(m)) {
                        if(m.contains("预计")) {
                            String[] s = m.split("：");
                            LogUtils.info("Money str>>>:" + s[0] + " str1: " + s[1]);
                            if(m.contains("实际")) {
                                LogUtils.info("reMoney str>>>:" + s[0] + " str1: " + s[1] + "  str2:  " + s[2]);
                                BigDecimal c = new BigDecimal(s[2]);
                                order.setRealityMoney(c);
                            }
                            m = s[1].replaceAll("[^0-9]","").trim();

                        }
                        LogUtils.info("Money m>>>:" + m);
                        BigDecimal b = new BigDecimal(m);
                        order.setExpectMoney(b);
                    }
                }
                if(item.length > 20) {
                    String m = item[20].trim();
                    if(!StringUtils.isEmpty(m)) {
                        LogUtils.info("relMoney m>>>:" + m);
                        BigDecimal b = new BigDecimal(m);
                        order.setRealityMoney(b);
                    }
                }
                order.setOrderNo(item.length >21 ? (StringUtils.trimToNull(item[21].trim())) : null);
                order.setRecordNo(item.length >22 ? (StringUtils.trimToNull(item[22].trim())) : null);
                order.setCreateBy("admin");
                order.setCreateTime(new Date());
                sqlData.add(order);
                i++;
            }
            System.out.println(i);
            try {
                count = ordersService.insertBatchAW(sqlData);

            } catch (SQLException e) {
                LogUtils.error(e.getMessage());
                return JsonResult.error(500, e.getMessage());
            }
            br.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return JsonResult.error(500, e.getMessage());
        }
        catch(IOException e){
            e.printStackTrace();
            return JsonResult.error(500, e.getMessage());
        }
        if(count > 0) {
            return JsonResult.success("插入车主：" + userCount + ", 订单：" + count);
        } else {
            return JsonResult.error();
        }
    }

    /*
   * 导入订单数据，取送车
   * */
    @RequestMapping(value = "/import/orderP", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult importDatap() throws ParseException {
        List<Orders> sqlData = new ArrayList<>();
        int userCount = 0;
        int count = 0;
        int uc = 0;
        try {
            DataInputStream csv = new DataInputStream(new FileInputStream(new File("D:/Book/20190829/pickup.csv")));
            BufferedReader br = new BufferedReader(new InputStreamReader(csv,"GBK"));
            String line="";
            int i = 0;
            while((line = br.readLine()) != null){
                Orders order = new Orders();
                String item[] = line.split(",");
                LogUtils.info("line: >>>>>>" + line);
                String vin = item[5]; // 查找车主
                if(!vin.isEmpty()) {
                    Users user =  usersService.selectByPrimaryKey(vin.trim());
                    if(user != null) {
                        order.setUserId(user.getId());
                        if(user.getCarLicense() == null) {
                            try {
                                usersService.updateByCarLicense(StringUtils.trimToNull(item[6]), user.getId());
                                uc++;
                            }catch (SQLException e) {
                                LogUtils.error("pickupUpateUser:" + e.getMessage());
                                return JsonResult.error(501, e.getMessage());
                            }
                        }
                    } else {
                        Users u = new Users();
                        u.setVin(vin);
                        u.setPhone(item[3].trim());
                        u.setUserName(item[2].trim());
                        u.setServiceCard(item[4].trim());
                        u.setCreateBy("admin");
                        u.setCreateTime(new Date());
                        try {
                            int ic =  usersService.insertOrUpdateSelective(u);
                            LogUtils.info("u.getId: " + u.getId());
                            if(ic > 0) {
                                userCount ++ ;
                                order.setUserId(u.getId());
                            }
                        }catch (SQLException e) {
                            LogUtils.error("insertError: " + e.getMessage());
                        }
                    }
                }
                order.setTypeId(4); //取送车
                order.setUserTel(item[3].trim());
                order.setUserName(StringUtils.trimToNull(item[2].trim()));
                order.setState(StringUtils.isEmpty(item[16]) ? 1 : (item[16].trim().equals("是") ? 2 : 3));
                order.setOrderType(1); // 人工
                order.setCallTime(StringUtils.isEmpty(item[1].trim()) ? null : DateUtils.formatDate(item[1],1));
                order.setUseTime(StringUtils.isEmpty(item[8].trim()) ? null : DateUtils.formatDate(item[8],1));
                order.setUserAddr(StringUtils.trimToNull(item[7].trim()));
                order.setDealerAddr(StringUtils.trimToNull(item[10].trim()));
                order.setContactName(StringUtils.trimToNull(item[11].trim()));
                order.setContactTel(StringUtils.trimToNull(item[12].trim()));
                order.setOrderTime(StringUtils.isEmpty(item[9].trim()) ?  null : DateUtils.formatDate(item[9].trim(), 1));
                order.setUseCarType(StringUtils.isEmpty(item[13].trim()) ? 1 : (item[13].trim().contains("取车") ? 1 : 2));
                order.setIsOrder(StringUtils.isEmpty(item[15].trim()) ? 2 : (item[15].trim().equals("是") ? 1 : 2));
                order.setOrderService(StringUtils.trimToNull(item[14].trim()));
                order.setCompleted(StringUtils.isEmpty(item[16].trim()) ? 2 : (item[16].trim().equals("是") ? 1 : 2));
                order.setRemark(item.length >17 ? (StringUtils.trimToNull(item[17].trim())) : null);
                if(item.length > 18) {
                    String m = item[18].trim();
                    if(!StringUtils.isEmpty(m)) {
                        LogUtils.info("relMoney m>>>:" + m);
                        BigDecimal b = new BigDecimal(m);
                        order.setRealityMoney(b);
                    }
                }
                order.setOrderNo(item.length >19 ? (StringUtils.trimToNull(item[19].trim())) : null);
                order.setRecordNo(item.length >20 ? (StringUtils.trimToNull(item[20].trim())) : null);
                order.setCreateBy("admin");
                order.setCreateTime(new Date());
                sqlData.add(order);
                i++;
            }
            System.out.println(i);
            try {
                count = ordersService.insertBatchPickup(sqlData);

            } catch (SQLException e) {
                LogUtils.error(e.getMessage());
                return JsonResult.error(500, e.getMessage());
            }
            br.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return JsonResult.error(500, e.getMessage());
        }
        catch(IOException e){
            e.printStackTrace();
            return JsonResult.error(500, e.getMessage());
        }
        if(count > 0) {
            return JsonResult.success("插入车主：" + userCount + " 更新车主：" + uc + ", 订单：" + count);
        } else {
            return JsonResult.error();
        }
    }

    /*
   * 导入订单数据，人工租车
   * */
    @RequestMapping(value = "/import/orderR", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult importDatar() throws ParseException {
        List<Orders> sqlData = new ArrayList<>();
        int userCount = 0;
        int count = 0;
        int uc = 0;
        try {
            DataInputStream csv = new DataInputStream(new FileInputStream(new File("D:/Book/20190829/rent.csv")));
            BufferedReader br = new BufferedReader(new InputStreamReader(csv,"GBK"));
            String line="";
            int i = 0;
            while((line = br.readLine()) != null){
                Orders order = new Orders();
                String item[] = line.split(",");
                LogUtils.info("line: >>>>>>" + line);
                String vin = item[5]; // 查找车主
                if(!vin.isEmpty()) {
                    Users user =  usersService.selectByPrimaryKey(vin.trim());
                    if(user != null) {
                        order.setUserId(user.getId());
                    } else {
                        Users u = new Users();
                        u.setVin(vin);
                        u.setPhone(item[3].trim());
                        u.setUserName(item[2].trim());
                        u.setServiceCard(item[4].trim());
                        u.setCreateBy("admin");
                        u.setCreateTime(new Date());
                        try {
                            int ic =  usersService.insertOrUpdateSelective(u);
                            LogUtils.info("u.getId: " + u.getId());
                            if(ic > 0) {
                                userCount ++ ;
                                order.setUserId(u.getId());
                            }
                        }catch (SQLException e) {
                            LogUtils.error("insertError: " + e.getMessage());
                        }
                    }
                }
                order.setTypeId(1); //租车
                order.setUserTel(item[3].trim());
                order.setUserName(StringUtils.trimToNull(item[2].trim()));
                order.setState(StringUtils.isEmpty(item[29]) ? 1 : (item[29].trim().equals("是") ? 2 : 3));
                order.setOrderType(1); // 人工
                order.setCallTime(StringUtils.isEmpty(item[1].trim()) ? null : DateUtils.formatDate(item[1],1));
                order.setRentCity(StringUtils.trimToNull(item[6].trim()));
                order.setIsCredit(StringUtils.isEmpty(item[7].trim()) ? 2 : (item[7].trim().equals("是") ? 1 : 2));
                order.setIdNo(StringUtils.trimToNull(item[8].trim()));
                order.setDriverNo(StringUtils.trimToNull(item[9].trim()));
                order.setUserAddr(StringUtils.trimToNull(item[10].trim()));
                order.setBeginTime(StringUtils.isEmpty(item[11].trim()) ? null : DateUtils.formatDate(item[11],1));
                order.setEndTime(StringUtils.isEmpty(item[12].trim()) ? null : DateUtils.formatDate(item[12],1));
                order.setDuration(StringUtils.trimToNull(item[13].trim()));
                order.setOrderCarType(StringUtils.trimToNull(item[14].trim()));
                //order.setRecordAgent(StringUtils.trimToNull(item[15].trim()));
                order.setIsOrder(StringUtils.isEmpty(item[16].trim()) ? 2 : (item[16].trim().equals("是") ? 1 : 2));
                order.setOrderService(StringUtils.trimToNull(item[17].trim()));
                order.setOrderTel(StringUtils.trimToNull(item[18].trim()));
                order.setOrderTime(StringUtils.isEmpty(item[19].trim()) ?  null : DateUtils.formatDate(item[19].trim(), 1));
                order.setStoreName(StringUtils.trimToNull(item[20].trim()));
                order.setStoreAddr(StringUtils.trimToNull(item[21].trim()));
                order.setContactName(StringUtils.trimToNull(item[23].trim()));
                order.setContactTel(StringUtils.trimToNull(item[22].trim()));
                order.setIsGl(StringUtils.isEmpty(item[24].trim()) ? 2 : (item[13].trim().contains("是") ? 1 : 2));
                order.setVisitTime(StringUtils.isEmpty(item[25].trim()) ?  null : DateUtils.formatDate(item[25].trim(), 1));
                order.setVisitor(StringUtils.trimToNull(item[26].trim()));
                order.setIsReplace(StringUtils.isEmpty(item[27].trim()) ? 2 : (item[27].trim().contains("是") ? 1 : 2));
                order.setReplaceCar(StringUtils.trimToNull(item[28].trim()));
                order.setCompleted(StringUtils.isEmpty(item[29].trim()) ? 2 : (item[29].trim().equals("是") ? 1 : 2));
                order.setActualCar(StringUtils.trimToNull(item[30].trim()));
                if(item.length > 31) {
                    String m = item[31].trim();
                    if(!StringUtils.isEmpty(m)) {
                        LogUtils.info("ExpMoney m>>>:" + m);
                        BigDecimal b = new BigDecimal(m);
                        order.setExpectMoney(b);
                    }
                }
                if(item.length > 32) {
                    String m = item[32].trim();
                    if(!StringUtils.isEmpty(m)) {
                        LogUtils.info("relMoney m>>>:" + m);
                        BigDecimal b = new BigDecimal(m);
                        order.setRealityMoney(b);
                    }
                }

                order.setOrderNo(item.length >33 ? (StringUtils.trimToNull(item[33].trim())) : null);
                order.setRemark(item.length >34 ? (StringUtils.trimToNull(item[34].trim())) : null);
                order.setRecordNo(item.length >35 ? (StringUtils.trimToNull(item[35].trim())) : null);
                order.setCreateBy("admin");
                order.setCreateTime(new Date());
                sqlData.add(order);
                i++;
            }
            System.out.println(i);
            try {
                count = ordersService.insertBatchRent(sqlData);

            } catch (SQLException e) {
                LogUtils.error(e.getMessage());
                return JsonResult.error(500, e.getMessage());
            }
            br.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return JsonResult.error(500, e.getMessage());
        }
        catch(IOException e){
            e.printStackTrace();
            return JsonResult.error(500, e.getMessage());
        }
        if(count > 0) {
            return JsonResult.success("插入车主：" + userCount + " 更新车主：" + uc + ", 订单：" + count);
        } else {
            return JsonResult.error();
        }
    }

    /*
   * 导入订单数据，微信租车
   * */
    @RequestMapping(value = "/import/orderRw", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult importDataw() throws ParseException {
        List<Orders> sqlData = new ArrayList<>();
        int userCount = 0;
        int count = 0;
        int uc = 0;
        try {
            DataInputStream csv = new DataInputStream(new FileInputStream(new File("D:/Book/20190829/wechat_rent.csv")));
            BufferedReader br = new BufferedReader(new InputStreamReader(csv,"GBK"));
            String line="";
            int i = 0;
            while((line = br.readLine()) != null){
                Orders order = new Orders();
                String item[] = line.split(",");
                LogUtils.info("line: >>>>>>" + line);
                String vin = item[5]; // 查找车主
                if(!vin.isEmpty()) {
                    Users user =  usersService.selectByPrimaryKey(vin.trim());
                    if(user != null) {
                        order.setUserId(user.getId());
                    } else {
                        Users u = new Users();
                        u.setVin(vin);
                        u.setPhone(item[3].trim());
                        u.setUserName(item[1].trim());
                        u.setServiceCard(item[4].trim());
                        u.setOpenId(item[2].trim());
                        u.setCreateBy("admin");
                        u.setCreateTime(new Date());
                        try {
                            int ic =  usersService.insertOrUpdateSelective(u);
                            LogUtils.info("u.getId: " + u.getId());
                            if(ic > 0) {
                                userCount ++ ;
                                order.setUserId(u.getId());
                            }
                        }catch (SQLException e) {
                            LogUtils.error("insertError: " + e.getMessage());
                        }
                    }
                }
                order.setTypeId(1); //租车
                order.setUserTel(item[3].trim());
                order.setUserName(StringUtils.trimToNull(item[1].trim()));
                order.setOpenId(StringUtils.trimToNull(item[2].trim()));
                order.setState(StringUtils.isEmpty(item[30]) ? 1 : (item[30].trim().equals("是") ? 2 : 3));
                order.setOrderType(2); // 人工
                order.setIdNo(StringUtils.trimToNull(item[6].trim()));
                order.setRentCity(StringUtils.trimToNull(item[7].trim()));
                order.setBeginTime(StringUtils.isEmpty(item[9].trim()) ? null : DateUtils.formatDate(item[9],1));
                order.setEndTime(StringUtils.isEmpty(item[10].trim()) ? null : DateUtils.formatDate(item[10],1));
                order.setDuration(StringUtils.trimToNull(item[11].trim()));
                order.setOrderTime(StringUtils.isEmpty(item[13].trim()) ?  null : DateUtils.formatDate(item[13].trim(), 1));
                order.setDriverNo(StringUtils.trimToNull(item[14].trim()));
                order.setUserAddr(StringUtils.trimToNull(item[15].trim()));
                order.setOrderCarType(StringUtils.trimToNull(item[16].trim()));
                order.setIsOrder(StringUtils.isEmpty(item[17].trim()) ? 2 : (item[17].trim().equals("是") ? 1 : 2));
                order.setOrderService(StringUtils.trimToNull(item[18].trim()));
                order.setOrderTel(StringUtils.trimToNull(item[19].trim()));
                order.setStoreName(StringUtils.trimToNull(item[21].trim()));
                order.setStoreAddr(StringUtils.trimToNull(item[22].trim()));
                order.setContactName(StringUtils.trimToNull(item[24].trim()));
                order.setContactTel(StringUtils.trimToNull(item[23].trim()));
                order.setIsGl(StringUtils.isEmpty(item[25].trim()) ? 2 : (item[25].trim().contains("是") ? 1 : 2));
                order.setVisitTime(StringUtils.isEmpty(item[26].trim()) ?  null : DateUtils.formatDate(item[26].trim(), 1));
                order.setVisitor(StringUtils.trimToNull(item[27].trim()));
                order.setIsReplace(StringUtils.isEmpty(item[28].trim()) ? 2 : (item[28].trim().contains("是") ? 1 : 2));
                order.setReplaceCar(StringUtils.trimToNull(item[29].trim()));
                order.setCompleted(StringUtils.isEmpty(item[30].trim()) ? 2 : (item[30].trim().equals("是") ? 1 : 2));
                order.setActualCar(StringUtils.trimToNull(item[31].trim()));
                if(item.length > 32) {
                    String m = item[32].trim();
                    if(!StringUtils.isEmpty(m)) {
                        LogUtils.info("ExpMoney m>>>:" + m);
                        BigDecimal b = new BigDecimal(m);
                        order.setExpectMoney(b);
                    }
                }
                if(item.length > 33) {
                    String m = item[33].trim();
                    if(!StringUtils.isEmpty(m)) {
                        LogUtils.info("relMoney m>>>:" + m);
                        BigDecimal b = new BigDecimal(m);
                        order.setRealityMoney(b);
                    }
                }

                order.setOrderNo(item.length >34 ? (StringUtils.trimToNull(item[34].trim())) : null);
                order.setRemark(item.length >35 ? (StringUtils.trimToNull(item[35].trim())) : null);
                order.setRecordNo(item.length >36 ? (StringUtils.trimToNull(item[36].trim())) : null);
                order.setCreateBy("admin");
                order.setCreateTime(new Date());
                sqlData.add(order);
                i++;
            }
            System.out.println(i);
            try {
                count = ordersService.insertBatchRent(sqlData);

            } catch (SQLException e) {
                LogUtils.error(e.getMessage());
                return JsonResult.error(500, e.getMessage());
            }
            br.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return JsonResult.error(500, e.getMessage());
        }
        catch(IOException e){
            e.printStackTrace();
            return JsonResult.error(500, e.getMessage());
        }
        if(count > 0) {
            return JsonResult.success("插入车主：" + userCount + " 更新车主：" + uc + ", 订单：" + count);
        } else {
            return JsonResult.error();
        }
    }
}

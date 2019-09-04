package com.avenir.service.impl;

import com.avenir.mapper.AdminMapper;
import com.avenir.mapper.OrdersMapper;
import com.avenir.mapper.UsersMapper;
import com.avenir.model.Admin;
import com.avenir.model.OrderOV;
import com.avenir.model.Users;
import com.avenir.service.UsersService;
import com.avenir.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class UsersServiceImpl implements UsersService{
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return usersMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insertOrUpdateSelective(Users record){
        if(record.getId() != null) {
            record.setUpdateTime(new Date());
            return usersMapper.updateByPrimaryKeySelective(record);
        } else {
            record.setCreateTime(new Date());
            return usersMapper.insertSelective(record);
        }
    }

    @Override
    public Users selectByPrimaryKey(String vin) {
        return usersMapper.selectByPrimaryKey(vin);
    }

    @Override
    public Users selectByUserId(Integer id) {
        return usersMapper.selectByUserId(id);
    }

    @Override
    public Admin selectByNameAndPwd(String name, String pwd) { return adminMapper.selectByNameAndPwd(name, pwd); }

    @Override
    public List<Users> findUser(Users user) {
        return usersMapper.findUser(user);
    }

    @Override
    public Users getService(String vin) {
        return usersMapper.getService(vin);
    }

    @Override
    public  List<Users> getUserHasVinType(Users user) {
        return usersMapper.getUserHasVinType(user);
    }

    @Override
    public int insertBatch(List<Users> list){
        return usersMapper.insertBatch(list);
    }

    @Override
    public int updateBatch(List<Users> list) { return usersMapper.updateBatch(list); }
    @Override
    public int updateByCarLicense(String carl, Integer id) {
        return usersMapper.updateByCarLicense(carl, id);
    }
    // 获取当前所处年,并查询所处年份已使用次数
    @Override
    public void getCurrentYear(Users user, Date current, Integer typeId) {
        Calendar c = Calendar.getInstance();
        c.setTime(user.getReportTime());
        Date dd = null; // 上一年
        for(int j = 0; j < 6; j++) {
            c.add(Calendar.YEAR,  1); // 上报时间加一年，
            if (j == 0 && current.getTime() < c.getTime().getTime() && current.getTime() > user.getReportTime().getTime()) {
                Integer count = ordersMapper.countByVin(user.getVin(), typeId, user.getReportTime(), c.getTime());
                user.setYear(1);
                user.setCountBy(count);
                break;
            }else if(dd != null && current.getTime() < c.getTime().getTime() && current.getTime() > dd.getTime()){
                Integer count = ordersMapper.countByVin(user.getVin(), typeId, dd, c.getTime());
                if(j == 1) {
                    user.setYear(2);
                    user.setCountBy(count);
                    break;
                }
                if(j == 2) {
                    user.setYear(3);
                    user.setCountBy(count);
                    break;
                }
                if(j == 3) {
                    user.setYear(4);
                    user.setCountBy(count);
                    break;
                }
                if(j == 4) {
                    user.setYear(5);
                    user.setCountBy(count);
                    break;
                }
                if(j == 5) {
                    user.setYear(6);
                    user.setCountBy(count);
                    break;
                }
            }
            dd = c.getTime(); // 记录上次加一年的时间
        }
    }
    // 统计每年使用次数
    @Override
    public void getCountByVinAndType(Users user, Integer typeId){
        List<OrderOV> orders = ordersMapper.selectCountByVin(user.getVin(), typeId);
        if(orders.size() > 0) {
            Calendar ca = Calendar.getInstance();
            for (int i = 0; i < orders.size(); i++) {
                ca.setTime(user.getReportTime());
                Date dd = null;
                Date d = null; // 第一条时间
                if(typeId == 1) {
                    d = orders.get(i).getEndTime();
                } else {
                    d = orders.get(i).getUseTime();
                }
                for(int j = 0; j < 6; j++) {
                    ca.add(Calendar.YEAR,  1); // 上报时间加一年，
                    LogUtils.info("上一年时间：" + (dd != null ? dd : null));
                    LogUtils.info("加一年时间：" + ca.getTime());
                    if (j == 0 && d.getTime() < ca.getTime().getTime()) {
                        int count = orders.get(i).getCount();
                        if(user.getFirst() != null) {
                            int isc = user.getFirst();
                            user.setFirst(isc + count);
                        } else {
                            user.setFirst(count);
                        }
                        user.setYear(1);
                        break;
                    }else if(d.getTime() < ca.getTime().getTime() && d.getTime() > dd.getTime()){
                        int count = orders.get(i).getCount();
                        if(j == 1) {
                            if(user.getSecond() != null) {
                                int isc = user.getSecond();
                                user.setSecond(isc + count);
                            } else {
                                user.setSecond(count);
                            }
                            user.setYear(2);
                            break;
                        }
                        if(j == 2) {
                            if(user.getThird() != null) {
                                int isc = user.getThird();
                                user.setThird(isc + count);
                            } else {
                                user.setThird(count);
                            }
                            user.setYear(3);
                            break;
                        }
                        if(j == 3) {
                            if(user.getFour() != null) {
                                int isc = user.getFour();
                                user.setFour(isc + count);
                            } else {
                                user.setFour(count);
                            }
                            user.setYear(4);
                            break;
                        }
                        if(j == 4) {
                            if(user.getFive() != null) {
                                int isc = user.getFive();
                                user.setFive(isc + count);
                            } else {
                                user.setFive(count);
                            }
                            user.setYear(5);
                            break;
                        }
                        if(j == 5) {
                            if(user.getSix() != null) {
                                int isc = user.getSix();
                                user.setSix(isc + count);
                            } else {
                                user.setSix(count);
                            }
                            user.setYear(6);
                            break;
                        }
                    }
                    dd = ca.getTime(); // 记录上次加一年的时间
                }
            }
        }
    }
}

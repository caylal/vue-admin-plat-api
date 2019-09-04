package com.avenir.constant;

public class ResultCode {
    /**
     * 请求成功200
     */
    public final static Integer SUCCESS = 200;
    /**
     * 无对应API 404
     */
    public final static Integer NO_API = 404;
    /**
     * 请求方法不支持、不正确 405
     */
    public final static Integer MOTHOD_ERROR = 405;
    /**
     * 没有查找到相关数据
     */
    public final static Integer NO_DATA = 406;
    /**
     * 已经存在此数据
     */
    public final static Integer EXISTSED = 407;
    /**
     * 操作失败
     */
    public final static Integer FAIL = 600;
    /**
     * 发生异常
     */
    public final static Integer EXCEPTION = 601;
    /**
     * 参数不完整，缺少关键字段 602
     */
    public final static Integer LESS_PARAM = 602;
    /**
     * 参数为空
     * */
    public final static Integer PARAM_NULL = 605;
    /**
     * VIN码不是Avenir卡
     */
    public  final static  Integer UNAVENIR = 606;
    /**
     * VIN码没有权益
     */
    public  final static  Integer NO_AUTHORITY = 607;
    /**
     * VIN码已过期
     */
    public  final static  Integer OVERDUE = 608;
    /**
     * VIN码没有使用次数
     */
    public  final static  Integer OVER_NUMBER = 609;
    /**
     * VIN码没有使用次数
     */
    public  final static  Integer NO_REPORT = 408;
}

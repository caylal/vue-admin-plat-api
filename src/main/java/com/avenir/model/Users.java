package com.avenir.model;

import java.util.Date;
import java.util.List;

public class Users {
    private Integer id;

    private String vin;

    private String userName;

    private String userIdWx;

    private String openId;

    private String phone;

    private String serviceCard;

    private String serviceCardOld;

    private String serviceType;

    private String carType;

    private String carLicense;

    private String idNo;

    private String driverNo;

    private String userAddr;

    private Date invoiceTime;

    private Date reportTime;

    private Date dueTime;

    private Integer dueCount;

    private Integer aTotalCount;

    private Integer aTotalUsed;

    private Integer aCurrentCount;

    private Integer pTotalCount;

    private Integer pTotalUsed;

    private Integer pCurrentCount;

    private Integer rTotalCount;

    private Integer rTotalUsed;

    private Integer rCurrentCount;

    private String vinType;

    private String remark;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;

    private Integer countBy;

    private Integer first;

    private Integer second;

    private Integer third;

    private Integer four;

    private Integer five;

    private Integer six;

    private Integer year;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin == null ? null : vin.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserIdWx() {
        return userIdWx;
    }

    public void setUserIdWx(String userIdWx) {
        this.userIdWx = userIdWx == null ? null : userIdWx.trim();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getServiceCard() {
        return serviceCard;
    }

    public void setServiceCard(String serviceCard) {
        this.serviceCard = serviceCard == null ? null : serviceCard.trim();
    }

    public String getServiceType() { return serviceType; }

    public void setServiceType(String serviceType) { this.serviceType = serviceType == null ? null : serviceType.trim(); }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType == null ? null : carType.trim();
    }

    public String getCarLicense() {
        return carLicense;
    }

    public void setCarLicense(String carLicense) {
        this.carLicense = carLicense == null ? null : carLicense.trim();
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo == null ? null : idNo.trim();
    }

    public String getDriverNo() {
        return driverNo;
    }

    public void setDriverNo(String driverNo) {
        this.driverNo = driverNo == null ? null : driverNo.trim();
    }

    public String getUserAddr() {
        return userAddr;
    }

    public void setUserAddr(String userAddr) {
        this.userAddr = userAddr == null ? null : userAddr.trim();
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public Integer getDueCount() {
        return dueCount;
    }

    public void setDueCount(Integer dueCount) {
        this.dueCount = dueCount;
    }

    public Integer getaTotalCount() {
        return aTotalCount;
    }

    public void setaTotalCount(Integer aTotalCount) {
        this.aTotalCount = aTotalCount;
    }

    public Integer getaTotalUsed() {
        return aTotalUsed;
    }

    public void setaTotalUsed(Integer aTotalUsed) {
        this.aTotalUsed = aTotalUsed;
    }

    public Integer getaCurrentCount() {
        return aCurrentCount;
    }

    public void setaCurrentCount(Integer aCurrentCount) {
        this.aCurrentCount = aCurrentCount;
    }

    public Integer getpTotalCount() {
        return pTotalCount;
    }

    public void setpTotalCount(Integer pTotalCount) {
        this.pTotalCount = pTotalCount;
    }

    public Integer getpTotalUsed() {
        return pTotalUsed;
    }

    public void setpTotalUsed(Integer pTotalUsed) {
        this.pTotalUsed = pTotalUsed;
    }

    public Integer getpCurrentCount() {
        return pCurrentCount;
    }

    public void setpCurrentCount(Integer pCurrentCount) {
        this.pCurrentCount = pCurrentCount;
    }

    public Integer getrTotalCount() {
        return rTotalCount;
    }

    public void setrTotalCount(Integer rTotalCount) {
        this.rTotalCount = rTotalCount;
    }

    public Integer getrTotalUsed() {
        return rTotalUsed;
    }

    public void setrTotalUsed(Integer rTotalUsed) {
        this.rTotalUsed = rTotalUsed;
    }

    public Integer getrCurrentCount() {
        return rCurrentCount;
    }

    public void setrCurrentCount(Integer rCurrentCount) {
        this.rCurrentCount = rCurrentCount;
    }

    public String getVinType() {
        return vinType;
    }

    public void setVinType(String vinType) {
        this.vinType = vinType == null ? null : vinType.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateBy() { return createBy; }

    public void setCreateBy(String createBy) { this.createBy = createBy == null ? null : createBy.trim(); }

    public String getUpdateBy() { return updateBy; }

    public void setUpdateBy(String updateBy) { this.updateBy = updateBy == null ? null : updateBy.trim(); }

    public Integer getCountBy() {
        return countBy;
    }

    public void setCountBy(Integer countBy) {
        this.countBy = countBy;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    public Integer getThird() {
        return third;
    }

    public void setThird(Integer third) {
        this.third = third;
    }

    public Integer getFour() {
        return four;
    }

    public void setFour(Integer four) {
        this.four = four;
    }

    public Integer getFive() {
        return five;
    }

    public void setFive(Integer five) {
        this.five = five;
    }

    public Integer getSix() {
        return six;
    }

    public void setSix(Integer six) {
        this.six = six;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getServiceCardOld() {
        return serviceCardOld;
    }

    public void setServiceCardOld(String serviceCardOld) {
        this.serviceCardOld = serviceCardOld;
    }

    public Date getInvoiceTime() {
        return invoiceTime;
    }

    public void setInvoiceTime(Date invoiceTime) {
        this.invoiceTime = invoiceTime;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", vin='" + vin + '\'' +
                ", userName='" + userName + '\'' +
                ", userIdWx='" + userIdWx + '\'' +
                ", openId='" + openId + '\'' +
                ", phone='" + phone + '\'' +
                ", serviceCard='" + serviceCard + '\'' +
                ", serviceCardOld='" + serviceCardOld + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", carType='" + carType + '\'' +
                ", carLicense='" + carLicense + '\'' +
                ", idNo='" + idNo + '\'' +
                ", driverNo='" + driverNo + '\'' +
                ", userAddr='" + userAddr + '\'' +
                ", invoiceTime=" + invoiceTime +
                ", reportTime=" + reportTime +
                ", dueTime=" + dueTime +
                ", dueCount=" + dueCount +
                ", aTotalCount=" + aTotalCount +
                ", aTotalUsed=" + aTotalUsed +
                ", aCurrentCount=" + aCurrentCount +
                ", pTotalCount=" + pTotalCount +
                ", pTotalUsed=" + pTotalUsed +
                ", pCurrentCount=" + pCurrentCount +
                ", rTotalCount=" + rTotalCount +
                ", rTotalUsed=" + rTotalUsed +
                ", rCurrentCount=" + rCurrentCount +
                ", vinType='" + vinType + '\'' +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createBy='" + createBy + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", countBy=" + countBy +
                ", first=" + first +
                ", second=" + second +
                ", third=" + third +
                ", four=" + four +
                ", five=" + five +
                ", six=" + six +
                '}';
    }
}
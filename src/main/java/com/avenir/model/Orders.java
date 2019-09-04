package com.avenir.model;

import java.math.BigDecimal;
import java.util.Date;

public class Orders {
    private Integer id;

    private Integer typeId;

    private Integer userId;

    private String userName;

    private String openId;

    private String userTel;

    private Date callTime;

    private String status;

    private Integer state;

    private Integer orderType;

    private String flight;

    private String airport;

    private String address;

    private String destination;

    private Date useTime;

    private String airportPickupTime;

    private Date appointmentTime;

    private Date orderTime;

    private Integer useCarType;

    private String number;

    private Integer isVisit;

    private Integer needOrder;

    private String visitor;

    private Date visitTime;

    private String orderService;

    private Integer isOrder;

    private Integer completed;

    private BigDecimal expectMoney;

    private BigDecimal realityMoney;

    private String orderNo;

    private String recordNo;

    private String userAddr;

    private String dealerAddr;

    private String contactName;

    private String contactTel;

    private Integer isCredit;

    private String idNo;

    private String driverNo;

    private String rentCity;

    private String orderCarType;

    private String storeName;

    private String storeAddr;

    private String returnName;

    private String returnTel;

    private Date beginTime;

    private Date endTime;

    private String duration;

    private String serviceProvider;

    private String recordAgent;

    private String orderTel;

    private Integer isGl;

    private Integer isReplace;

    private String replaceCar;

    private String actualCar;

    private Date createTime;

    private Date updateTime;

    private String updateBy;

    private String createBy;

    private String remark;

    private Users user;

    private Types type;

    private Date useTimeStart;

    private Date useTimeEnd;

    private Date orderTimeStart;

    private Date orderTimeEnd;

    private Date reportTimeStart;

    private Date reportTimeEnd;

    private Date appointmentTimeStart;

    private Date appointmentTimeEnd;

    private Date beginTimeStart;

    private Date beginTimeEnd;

    private Date endTimeStart;

    private Date endTimeEnd;

    private Integer count;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public Date getCallTime() {
        return callTime;
    }

    public void setCallTime(Date callTime) {
        this.callTime = callTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight == null ? null : flight.trim();
    }

    public String getAirport() {
        return airport;
    }

    public void setAirport(String airport) {
        this.airport = airport == null ? null : airport.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination == null ? null : destination.trim();
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    public String getAirportPickupTime() {
        return airportPickupTime;
    }

    public void setAirportPickupTime(String airportPickupTime) {
        this.airportPickupTime = airportPickupTime == null ? null : airportPickupTime.trim();
    }

    public Date getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Integer getUseCarType() {
        return useCarType;
    }

    public void setUseCarType(Integer useCarType) {
        this.useCarType = useCarType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number == null ? null : number.trim();
    }

    public Integer getIsVisit() {
        return isVisit;
    }

    public void setIsVisit(Integer isVisit) {
        this.isVisit = isVisit;
    }

    public Integer getNeedOrder() {
        return needOrder;
    }

    public void setNeedOrder(Integer needOrder) {
        this.needOrder = needOrder;
    }

    public String getVisitor() {
        return visitor;
    }

    public void setVisitor(String visitor) {
        this.visitor = visitor == null ? null : visitor.trim();
    }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

    public String getOrderService() {
        return orderService;
    }

    public void setOrderService(String orderService) {
        this.orderService = orderService == null ? null : orderService.trim();
    }

    public Integer getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(Integer isOrder) {
        this.isOrder = isOrder;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public BigDecimal getExpectMoney() {
        return expectMoney;
    }

    public void setExpectMoney(BigDecimal expectMoney) {
        this.expectMoney = expectMoney;
    }

    public BigDecimal getRealityMoney() {
        return realityMoney;
    }

    public void setRealityMoney(BigDecimal realityMoney) {
        this.realityMoney = realityMoney;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(String recordNo) {
        this.recordNo = recordNo == null ? null : recordNo.trim();
    }

    public String getUserAddr() {
        return userAddr;
    }

    public void setUserAddr(String userAddr) {
        this.userAddr = userAddr == null ? null : userAddr.trim();
    }

    public String getDealerAddr() {
        return dealerAddr;
    }

    public void setDealerAddr(String dealerAddr) {
        this.dealerAddr = dealerAddr == null ? null : dealerAddr.trim();
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel == null ? null : contactTel.trim();
    }

    public Integer getIsCredit() {
        return isCredit;
    }

    public void setIsCredit(Integer isCredit) {
        this.isCredit = isCredit;
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

    public String getRentCity() {
        return rentCity;
    }

    public void setRentCity(String rentCity) {
        this.rentCity = rentCity == null ? null : rentCity.trim();
    }

    public String getOrderCarType() {
        return orderCarType;
    }

    public void setOrderCarType(String orderCarType) {
        this.orderCarType = orderCarType == null ? null : orderCarType.trim();
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName == null ? null : storeName.trim();
    }

    public String getStoreAddr() {
        return storeAddr;
    }

    public void setStoreAddr(String storeAddr) {
        this.storeAddr = storeAddr == null ? null : storeAddr.trim();
    }

    public String getReturnName() {
        return returnName;
    }

    public void setReturnName(String returnName) {
        this.returnName = returnName == null ? null : returnName.trim();
    }

    public String getReturnTel() {
        return returnTel;
    }

    public void setReturnTel(String returnTel) {
        this.returnTel = returnTel == null ? null : returnTel.trim();
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider == null ? null : serviceProvider.trim();
    }
    public String getRecordAgent() {
        return recordAgent;
    }

    public void setRecordAgent(String recordAgent) {
        this.recordAgent = recordAgent == null ? null : recordAgent.trim();
    }

    public String getOrderTel() {
        return orderTel;
    }

    public void setOrderTel(String orderTel) {
        this.orderTel = orderTel == null ? null : orderTel.trim();
    }

    public Integer getIsGl() {
        return isGl;
    }

    public void setIsGl(Integer isGl) {
        this.isGl = isGl;
    }

    public Integer getIsReplace() {
        return isReplace;
    }

    public void setIsReplace(Integer isReplace) {
        this.isReplace = isReplace;
    }

    public String getReplaceCar() {
        return replaceCar;
    }

    public void setReplaceCar(String replaceCar) {
        this.replaceCar = replaceCar == null ? null : replaceCar.trim();
    }

    public String getActualCar() {
        return actualCar;
    }

    public void setActualCar(String actualCar) {
        this.actualCar = actualCar == null ? null : actualCar.trim();
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

    public String getUpdateBy() { return updateBy; }

    public void setUpdateBy(String updateBy) { this.updateBy = updateBy == null ? null : updateBy.trim(); }

    public String getCreateBy() { return createBy; }

    public void setCreateBy(String createBy) { this.createBy = createBy == null ? null : createBy.trim(); }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Types getTypes() {
        return type;
    }

    public void setTypes(Types types) {
        this.type = types;
    }

    public Date getUseTimeStart() {
        return useTimeStart;
    }

    public void setUseTimeStart(Date useTimeStart) {
        this.useTimeStart = useTimeStart;
    }

    public Date getUseTimeEnd() {
        return useTimeEnd;
    }

    public void setUseTimeEnd(Date useTimeEnd) {
        this.useTimeEnd = useTimeEnd;
    }

    public Date getOrderTimeStart() {
        return orderTimeStart;
    }

    public void setOrderTimeStart(Date orderTimeStart) {
        this.orderTimeStart = orderTimeStart;
    }

    public Date getOrderTimeEnd() {
        return orderTimeEnd;
    }

    public void setOrderTimeEnd(Date orderTimeEnd) {
        this.orderTimeEnd = orderTimeEnd;
    }

    public Date getReportTimeStart() {
        return reportTimeStart;
    }

    public void setReportTimeStart(Date reportTimeStart) {
        this.reportTimeStart = reportTimeStart;
    }

    public Date getReportTimeEnd() {
        return reportTimeEnd;
    }

    public void setReportTimeEnd(Date reportTimeEnd) {
        this.reportTimeEnd = reportTimeEnd;
    }

    public Date getAppointmentTimeStart() {
        return appointmentTimeStart;
    }

    public void setAppointmentTimeStart(Date appointmentTimeStart) {
        this.appointmentTimeStart = appointmentTimeStart;
    }

    public Date getAppointmentTimeEnd() {
        return appointmentTimeEnd;
    }

    public void setAppointmentTimeEnd(Date appointmentTimeEnd) {
        this.appointmentTimeEnd = appointmentTimeEnd;
    }

    public Date getBeginTimeStart() {
        return beginTimeStart;
    }

    public void setBeginTimeStart(Date beginTimeStart) {
        this.beginTimeStart = beginTimeStart;
    }

    public Date getBeginTimeEnd() {
        return beginTimeEnd;
    }

    public void setBeginTimeEnd(Date beginTimeEnd) {
        this.beginTimeEnd = beginTimeEnd;
    }

    public Date getEndTimeStart() {
        return endTimeStart;
    }

    public void setEndTimeStart(Date endTimeStart) {
        this.endTimeStart = endTimeStart;
    }

    public Date getEndTimeEnd() {
        return endTimeEnd;
    }

    public void setEndTimeEnd(Date endTimeEnd) {
        this.endTimeEnd = endTimeEnd;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", openId='" + openId + '\'' +
                ", userTel='" + userTel + '\'' +
                ", callTime=" + callTime +
                ", status='" + status + '\'' +
                ", state=" + state +
                ", orderType=" + orderType +
                ", flight='" + flight + '\'' +
                ", airport='" + airport + '\'' +
                ", address='" + address + '\'' +
                ", destination='" + destination + '\'' +
                ", useTime=" + useTime +
                ", airportPickupTime='" + airportPickupTime + '\'' +
                ", appointmentTime=" + appointmentTime +
                ", orderTime=" + orderTime +
                ", useCarType=" + useCarType +
                ", number='" + number + '\'' +
                ", isVisit=" + isVisit +
                ", needOrder=" + needOrder +
                ", visitor='" + visitor + '\'' +
                ", visitTime=" + visitTime +
                ", orderService='" + orderService + '\'' +
                ", isOrder=" + isOrder +
                ", completed=" + completed +
                ", expectMoney=" + expectMoney +
                ", realityMoney=" + realityMoney +
                ", orderNo='" + orderNo + '\'' +
                ", recordNo='" + recordNo + '\'' +
                ", userAddr='" + userAddr + '\'' +
                ", dealerAddr='" + dealerAddr + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactTel='" + contactTel + '\'' +
                ", isCredit=" + isCredit +
                ", idNo='" + idNo + '\'' +
                ", driverNo='" + driverNo + '\'' +
                ", rentCity='" + rentCity + '\'' +
                ", orderCarType='" + orderCarType + '\'' +
                ", storeName='" + storeName + '\'' +
                ", storeAddr='" + storeAddr + '\'' +
                ", returnName='" + returnName + '\'' +
                ", returnTel='" + returnTel + '\'' +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                ", serviceProvider='" + serviceProvider + '\'' +
                ", orderTel='" + orderTel + '\'' +
                ", isGl=" + isGl +
                ", isReplace=" + isReplace +
                ", replaceCar='" + replaceCar + '\'' +
                ", actualCar='" + actualCar + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", updateBy='" + updateBy + '\'' +
                ", createBy='" + createBy + '\'' +
                ", remark='" + remark + '\'' +
                ", user=" + user +
                ", type=" + type +
                ", useTimeStart=" + useTimeStart +
                ", useTimeEnd=" + useTimeEnd +
                ", orderTimeStart=" + orderTimeStart +
                ", orderTimeEnd=" + orderTimeEnd +
                ", reportTimeStart=" + reportTimeStart +
                ", reportTimeEnd=" + reportTimeEnd +
                ", appointmentTimeStart=" + appointmentTimeStart +
                ", appointmentTimeEnd=" + appointmentTimeEnd +
                ", beginTimeStart=" + beginTimeStart +
                ", beginTimeEnd=" + beginTimeEnd +
                ", endTimeStart=" + endTimeStart +
                ", endTimeEnd=" + endTimeEnd +
                ", count=" + count +
                '}';
    }
}
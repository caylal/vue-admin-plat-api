package com.avenir.mapper;

import com.avenir.model.ServiceType;

public interface ServiceTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ServiceType record);

    int insertSelective(ServiceType record);

    ServiceType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ServiceType record);

    int updateByPrimaryKey(ServiceType record);
}
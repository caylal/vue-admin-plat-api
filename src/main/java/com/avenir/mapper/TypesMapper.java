package com.avenir.mapper;

import com.avenir.model.Types;

import java.util.List;

public interface TypesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Types record);

    int insertSelective(Types record);

    Types selectByName(String name);

    Types selectByType(Integer type);

    int updateByPrimaryKeySelective(Types record);

    int updateByPrimaryKey(Types record);

    List<Types> findType(Types record);

    List<Types> findAll();
}
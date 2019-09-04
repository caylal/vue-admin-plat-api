package com.avenir.service;

import com.avenir.model.Types;

import java.sql.SQLException;
import java.util.List;

public interface TypesService {

    int deleteByPrimaryKey(Integer id);

    int insertOrUpdateSelective(Types record) throws SQLException;

    Types selectByName(String name);

    Types selectByType(Integer type);

    List<Types> findType(Types record);

    List<Types> findAll();
}

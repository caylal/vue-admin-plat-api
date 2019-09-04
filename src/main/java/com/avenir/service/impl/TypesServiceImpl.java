package com.avenir.service.impl;

import com.avenir.mapper.TypesMapper;
import com.avenir.model.Types;
import com.avenir.service.TypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TypesServiceImpl implements TypesService{
    @Autowired
    private TypesMapper typesMapper;

    @Override
    public int deleteByPrimaryKey(Integer id){
        return typesMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insertOrUpdateSelective(Types record){
        if(record.getId() != null) {
           record.setUpdateTime(new Date());
           return typesMapper.updateByPrimaryKeySelective(record);
        } else {
            record.setCreateTime(new Date());
            return typesMapper.insertSelective(record);
        }

    }
    @Override
    public Types selectByName(String name) {
        return typesMapper.selectByName(name);
    }

    @Override
    public Types selectByType(Integer type) {
        return typesMapper.selectByType(type);
    }
    @Override
    public List<Types> findType(Types record){
        return typesMapper.findType(record);
    }

    @Override
    public List<Types> findAll(){ return typesMapper.findAll(); }
}

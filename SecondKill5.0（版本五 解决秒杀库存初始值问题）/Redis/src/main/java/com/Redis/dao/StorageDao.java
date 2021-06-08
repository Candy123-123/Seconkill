package com.Redis.dao;

import com.Redis.entity.Storage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StorageDao {
    void update(Storage storage);
    int readStorage(Storage storage);
}

package com.file.bigfile.mapper;

import com.file.bigfile.domain.FileTb;

public interface FileTbMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FileTb record);

    int insertSelective(FileTb record);

    FileTb selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FileTb record);

    int updateByPrimaryKey(FileTb record);

    void UpdateFile(FileTb fileTb);

    Integer isExist(String key);

    FileTb selectLatestIndex(String key);
}
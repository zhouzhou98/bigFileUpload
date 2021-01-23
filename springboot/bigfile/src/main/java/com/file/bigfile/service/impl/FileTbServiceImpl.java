package com.file.bigfile.service.impl;


import com.file.bigfile.constance.FileConstance;
import com.file.bigfile.domain.FileTb;
import com.file.bigfile.mapper.FileTbMapper;
import com.file.bigfile.service.IFileTbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class FileTbServiceImpl implements IFileTbService {
    @Autowired
    private FileTbMapper fileTbMapper;
    @Override
    public void saveFile(FileTb fileTb) {
        fileTbMapper.insert(fileTb);
    }
    @Override
    public void UpdateFile(FileTb fileTb) {
        fileTbMapper.UpdateFile(fileTb);
    }
    @Override
    public boolean isNotExist(String key){
        Integer id = fileTbMapper.isExist(key);
        if (ObjectUtils.isEmpty(id)) {
            return true;
        }
        return false;
    }
    @Override
    public FileTb selectLatestIndex(String key) {
        FileTb fileTb = fileTbMapper.selectLatestIndex(key);
        if (ObjectUtils.isEmpty(fileTb)) {
            fileTb = FileTb.builder().fKey(key).fIndex(-1L).fName("").build();
        }else {
            fileTb.setFName(FileConstance.ACCESS_PATH+fileTb.getFName());
        }
        return fileTb;
    }

}

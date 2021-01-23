package com.file.bigfile.service;


import com.file.bigfile.constance.FileConstance;
import com.file.bigfile.domain.FileTb;
import com.file.bigfile.mapper.FileTbMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


public interface IFileTbService {


    public void saveFile(FileTb fileTb);

    public void UpdateFile(FileTb fileTb);

    public boolean isNotExist(String key);
    public FileTb selectLatestIndex(String key) ;

}

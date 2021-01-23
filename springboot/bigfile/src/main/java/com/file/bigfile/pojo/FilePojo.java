package com.file.bigfile.pojo;

import lombok.Data;

@Data
public class FilePojo {
    private String key;
    private String fileName;
    private Long shardIndex;
    private Long shardSize;
    private Long shardTotal;
    private Long size;
    private String suffix;

}

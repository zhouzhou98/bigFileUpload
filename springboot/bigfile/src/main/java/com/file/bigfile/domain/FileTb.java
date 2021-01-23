package com.file.bigfile.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileTb implements Serializable {
    private Integer id;

    /**
    * 文件唯一标识
    */
    private String fKey;

    /**
    * 第几个分片
    */
    private Long fIndex;

    /**
    * 共有几个分片
    */
    private Integer fTotal;

    /**
    * 文件名称，后面可以返回出去
    */
    private String fName;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_F_KEY = "f_key";

    public static final String COL_F_INDEX = "f_index";

    public static final String COL_F_TOTAL = "f_total";

    public static final String COL_F_NAME = "f_name";

    public static FileTbBuilder builder() {
        return new FileTbBuilder();
    }
}
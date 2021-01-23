###　项目总体结构

*使用ｓｐｒｉｎｇｂｏｏｔ＋ｖｕｅ前后端分离的方式实现大文件切片快速上传*

**项目演示流程：**

![项目流程](C:\Users\14040\Desktop\大文件上传\img\4.gif)



　　上传的基本原理就是前端根据文件大小，按块大小分成很多块,目前是一个一个块的依次上传上去，暂时还没有开始研究多线程的方式，后期将会深化这一方面的应用。 创建数据库：

```ｓｑｌ
Date: 2021-01-23 22:45:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `file_tb`
-- ----------------------------
DROP TABLE IF EXISTS `file_tb`;
CREATE TABLE `file_tb` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_key` varchar(255) DEFAULT NULL COMMENT '文件唯一标识',
  `f_index` bigint(20) DEFAULT NULL COMMENT '第几个分片',
  `f_total` int(11) DEFAULT NULL COMMENT '共有几个分片',
  `f_name` varchar(255) DEFAULT NULL COMMENT '文件名称，后面可以返回出去',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
```

前端划分的方法是：使用ｅｌｅｍｅｎｔ－ｕｉ的ｅｌ－ｕｐｌｏａｄ

```ｖｕｅ
  <el-upload
                    class="upload-demo"
                    drag
                    ref="upload"
                    :limit=1
                    :action="actionUrl"
                    :on-exceed="handleExceed"
                    :http-request="handUpLoad"
                    :auto-upload="false"
            >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            </el-upload>
```

分片：` let shardTotal = Math.ceil(size / shardSize); *//总片数*`

使用ｍｄ５表示数据唯一标识符：

```ＨＴＭＬ
// 生成文件标识，标识多次上传的是不是同一个文件

 let key = this.$md5(file.name + file.size + file.type+new Date());
```

### 前端代码

```
<template>
    <div class="file-upload">
        <h1>大文件分片上传、极速秒传</h1>
        <div class="file-upload-el">

            <el-upload
                    class="upload-demo"
                    drag
                    ref="upload"
                    :limit=1
                    :action="actionUrl"
                    :on-exceed="handleExceed"
                    :http-request="handUpLoad"
                    :auto-upload="false"
            >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            </el-upload>
            <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">上传到服务器</el-button>
        </div>
    </div>
</template>

<script>
    export default {
        name: "FileUpload",
        data() {

            return {
                actionUrl: '/api/upload',//上传的后台地址
                shardSize: 10 * 1024 * 1024,
                videoUrl: 'D:/BaiduNetdiskDownload/精通mysql调优大师班六.mp4'

            };
        },
        methods: {

            handleExceed(files, fileList) {
                this.$message.warning(`当前限制选择 1个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
            },
            submitUpload() {
                this.$refs.upload.submit();
            },
            async check(key) {
                var res = await this.$http.get('/api/check', {
                    params: {'key': key}
                })
                let resData = res.data;
                return resData.data;
            },
            async recursionUpload(param, file) {
                //FormData私有类对象，访问不到，可以通过get判断值是否传进去
                let _this = this;
                let key = param.key;
                let shardIndex = param.shardIndex;
                let shardTotal = param.shardTotal;
                let shardSize = param.shardSize;
                let size = param.size;
                let fileName = param.fileName;
                let suffix = param.suffix;

                let fileShard = _this.getFileShard(shardIndex, shardSize, file);

                //param.append("file", fileShard);//文件切分后的分片
                //param.file = fileShard;
                let totalParam = new FormData();
                totalParam.append('file', fileShard);
                totalParam.append("key", key);
                totalParam.append("shardIndex", shardIndex);
                totalParam.append("shardSize", shardSize);
                totalParam.append("shardTotal", shardTotal);
                totalParam.append("size", size);
                totalParam.append("fileName", fileName);
                totalParam.append("suffix", suffix);
                let config = {
                    //添加请求头
                    headers: {"Content-Type": "multipart/form-data"}
                };
                console.log(param);
                var res = await this.$http.post('/api/upload', totalParam, config)

                var resData = res.data;
                if (resData.status) {
                    if (shardIndex < shardTotal) {
                        this.$notify({
                            title: '成功',
                            message: '分片' + shardIndex + '上传完成。。。。。。',
                            type: 'success'
                        });
                    } else {
                        this.videoUrl = resData.data;//把地址赋值给视频标签
                        this.$notify({
                            title: '全部成功',
                            message: '文件上传完成。。。。。。',
                            type: 'success'
                        });
                    }

                    if (shardIndex < shardTotal) {
                        console.log('下一份片开始。。。。。。');
                        // 上传下一个分片
                        param.shardIndex = param.shardIndex + 1;
                        _this.recursionUpload(param, file);
                    }
                }


            },

            async handUpLoad(req) {
                let _this = this;
                var file = req.file;
                /*  console.log('handUpLoad', req)
                  console.log(file);*/
                //let param = new FormData();
                //通过append向form对象添加数据

                //文件名称和格式，方便后台合并的时候知道要合成什么格式
                let fileName = file.name;
                let suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length).toLowerCase();
                //这里判断文件格式，有其他格式的自行判断
                if (suffix != 'mp4') {
                    this.$message.error('文件格式错了哦。。');
                    return;
                }

                // 文件分片
                // let shardSize = 10 * 1024 * 1024;    //以10MB为一个分片
                // let shardSize = 50 * 1024;    //以50KB为一个分片
                let shardSize = _this.shardSize;
                let shardIndex = 1;		//分片索引，1表示第1个分片
                let size = file.size;
                let shardTotal = Math.ceil(size / shardSize); //总片数
                // 生成文件标识，标识多次上传的是不是同一个文件
                let key = this.$md5(file.name + file.size + file.type+new Date());
                let param = {
                    key: key,
                    shardIndex: shardIndex,
                    shardSize: shardSize,
                    shardTotal: shardTotal,
                    size: size,
                    fileName: fileName,
                    suffix: suffix
                }
                /*param.append("uid", key);
                param.append("shardIndex", shardIndex);
                param.append("shardSize", shardSize);
                param.append("shardTotal", shardTotal);
                param.append("size", size);
                param.append("fileName", fileName);
                param.append("suffix", suffix);

*/

                let checkIndexData = await _this.check(key);//得到文件分片索引
                let checkIndex = checkIndexData.findex;

                //console.log(checkIndexData)
                if (checkIndex == -1) {
                    this.recursionUpload(param, file);
                } else if (checkIndex < shardTotal) {
                    param.shardIndex = param.shardIndex + 1;
                    this.recursionUpload(param, file);
                } else {
                    this.videoUrl = checkIndexData.fname;//把地址赋值给视频标签
                    this.$message({
                        message: '极速秒传成功。。。。。',
                        type: 'success'
                    });
                }


                //console.log('结果：', res)
            },

            getFileShard(shardIndex, shardSize, file) {
                let _this = this;
                let start = (shardIndex - 1) * shardSize;	//当前分片起始位置
                let end = Math.min(file.size, start + shardSize); //当前分片结束位置
                let fileShard = file.slice(start, end); //从文件中截取当前的分片数据
                return fileShard;
            },


        }
    }

</script>

<style scoped lang="less">
    .file-upload {
        .file-upload-el {

        }

    }
    .v-box-card{
        width: 50%;
    }
</style>
```

### 后端Controller

```
package com.file.bigfile.controller;


import com.file.bigfile.constance.FileConstance;
import com.file.bigfile.domain.FileTb;
import com.file.bigfile.pojo.FilePojo;
import com.file.bigfile.service.IFileTbService;
import com.file.bigfile.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@Slf4j
public class FileUploadController {
    @Autowired
    private IFileTbService fileTbService;
    @PostMapping(value = "/api/upload")
    public Result upload(@RequestParam(value = "file") MultipartFile file,
                         FilePojo filePojo) throws Exception {
        File fullDir = new File(FileConstance.FILE_PATH);
        if (!fullDir.exists()) {
            fullDir.mkdir();
        }

        //uid 防止文件名重复,又可以作为文件的唯一标识
        String fullPath = FileConstance.FILE_PATH + filePojo.getKey() + "." + filePojo.getShardIndex();
        File dest = new File(fullPath);
        file.transferTo(dest);
        log.info("文件分片 {} 保存完成",filePojo.getShardIndex());

        //开始保存索引分片信息 bu不存在就新加 存在就修改索引分片
        FileTb fileTb = FileTb.builder()
                .fKey(filePojo.getKey())
                .fIndex((long) Math.toIntExact(filePojo.getShardIndex()))
                .fTotal(Math.toIntExact(filePojo.getShardTotal()))
                .fName(filePojo.getFileName())
                .build();
        if (fileTbService.isNotExist(filePojo.getKey())) {
            fileTbService.saveFile(fileTb);
        }else {
            fileTbService.UpdateFile(fileTb);
        }


        if (filePojo.getShardIndex().equals(filePojo.getShardTotal())) {
            //开始合并
            merge(filePojo);

            return Result.success("上传成功");
        }
        return Result.success();
    }

    public void merge(FilePojo filePojo) throws Exception {
        Long shardTotal = filePojo.getShardTotal();
        File newFile = new File(FileConstance.FILE_PATH + filePojo.getFileName());
        if (newFile.exists()) {
            newFile.delete();
        }
        FileOutputStream outputStream = new FileOutputStream(newFile, true);//文件追加写入
        FileInputStream fileInputStream = null;//分片文件
        byte[] byt = new byte[10 * 1024 * 1024];
        int len;
        try {
            for (int i = 0; i < shardTotal; i++) {
                // 读取第i个分片
                fileInputStream = new FileInputStream(new File(FileConstance.FILE_PATH + filePojo.getKey() + "." + (i + 1))); //  course\6sfSqfOwzmik4A4icMYuUe.mp4.1
                while ((len = fileInputStream.read(byt)) != -1) {
                    outputStream.write(byt, 0, len);//一直追加到合并的新文件中
                }
            }
        } catch (IOException e) {
            log.error("分片合并异常", e);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                outputStream.close();
                log.info("IO流关闭");
                System.gc();
            } catch (Exception e) {
                log.error("IO流关闭", e);
            }
        }
        log.info("合并分片结束");
        System.gc();
        //等待100毫秒 等待垃圾回收去 回收完垃圾
        Thread.sleep(100);
        log.info("删除分片开始");
        for (int i = 0; i < shardTotal; i++) {
            String filePath = FileConstance.FILE_PATH + filePojo.getKey() + "." + (i + 1);
            File file1 = new File(filePath);
            boolean result = file1.delete();
            log.info("删除{}，{}", filePath, result ? "成功" : "失败");
        }
        log.info("删除分片结束");
    }

    //文件上传之前判断是否已经上传过 -1就是没有
    @GetMapping("/api/check")
    public Result check(@RequestParam String key){
        FileTb fileTb = fileTbService.selectLatestIndex(key);
        log.info("检查分片：{}");
        return Result.success(fileTb);

    }


    SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
    @PostMapping("/api/import")
    public Result importData(MultipartFile file, HttpServletRequest req) throws IOException {
        String format = sdf.format(new Date());
        String realPath ="D:/BaiduNetdiskDownload/" + format;
        File folder = new File(realPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String oldName = file.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."));
        file.transferTo(new File(folder,newName));
        String url = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/upload" + format + newName;
        System.out.println(url);
        return Result.success("上传成功!");
    }
}

```




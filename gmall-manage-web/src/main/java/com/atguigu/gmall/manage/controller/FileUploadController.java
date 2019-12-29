package com.atguigu.gmall.manage.controller;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class FileUploadController {
    @Value("${fileServer.url}")
    private String fileUrl;//对于服务器ip来讲都应该在程序中实现软编码

    //http://localhost:8082/fileUpload
    @RequestMapping("fileUpload")
    public String fileUpload(MultipartFile file) throws IOException, MyException {
        String imgUrl = fileUrl;
        if (file != null) {
            String configFile = this.getClass ().getResource ("/tracker.conf").getFile ();
            ClientGlobal.init (configFile);
            TrackerClient trackerClient = new TrackerClient ();
            TrackerServer trackerServer = trackerClient.getConnection ();
            StorageClient storageClient = new StorageClient (trackerServer, null);
            String orginalFilename = file.getOriginalFilename ();//获取文件名，例如a.jpg

            //设置文件后缀名
            String extName = StringUtils.substringAfterLast (orginalFilename, ".");

            String[] upload_file = storageClient.upload_file (file.getBytes (), extName, null);
            for (int i = 0; i < upload_file.length; i++) {
                String path = upload_file[i];
                imgUrl += "/" + path;
            }
            // System.out.println ("==================imgUrl============"+imgUrl);
            //imgUrl:http://192.168.1.66/group1/M00/00/00/wKgBQl4IP0-AOxdFAACbuutfIxI657.jpg
        }
        return imgUrl;
    }

}

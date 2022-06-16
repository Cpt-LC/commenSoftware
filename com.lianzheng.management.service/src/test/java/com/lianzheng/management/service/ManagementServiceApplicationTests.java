package com.lianzheng.management.service;



import com.lianzheng.core.auth.mgmt.utils.FileTokenUtils;
import com.lianzheng.core.auth.mgmt.utils.RedisUtils;
import com.lianzheng.notarization.master.configParameter.utils.GetYmlParamUtil;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.core.sign.Utils.SignUtils;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.service.MasterService;
import com.lianzheng.notarization.master.service.TruthService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(value = SpringRunner.class)
@SpringBootTest(classes = ManagementServiceApplication.class)
public class ManagementServiceApplicationTests {
    Path root = Paths.get("uploads");
    Path templatesRoot = Paths.get("templates");
    Path noticesRoot = Paths.get("notices");
    @Autowired
    private GeneratePdf generatePdf;
    @Autowired
    private UserNotarzationMasterService userNotarzationMasterService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private FileTokenUtils fileTokenUtils;
    @Autowired
    private GetYmlParamUtil getYmlParamUtil;
    @Autowired
    private SignUtils signUtils;
    @Autowired
    private TruthService truthService;


    @Test
    public void contextLoads() throws Exception {
        String source=this.templatesRoot + "/测试.docx";
        String targetDoc = this.noticesRoot + "/123_测试.docx";
        String targetPdf = this.noticesRoot + "/123_测试.pdf";
        ConcurrentHashMap<String, Map<String,Object>> hashMap = new ConcurrentHashMap<>();
        List<String> url = null;
        List<String> fileNameList = null;
        PdfVarEntity pdfVarEntity =null;

        pdfVarEntity=new PdfVarEntity("text","真不错",null);
        hashMap.put("text",pdfVarEntity.getMap());

        url=new ArrayList<String>();
        url.add(this.root+"/20211210160412_孙慧珍.png");
        url.add(this.root+"/20211210160412_孙慧珍.png");
        pdfVarEntity=new PdfVarEntity("img",url,null);
        hashMap.put("img",pdfVarEntity.getMap());


        fileNameList = new ArrayList<>();
        fileNameList.add("a");
        fileNameList.add("b");
        fileNameList.add("c");
        List<Map<String,Object>> param = new ArrayList<>();
        Map<String,Object> tableMap = new HashMap<>();
        tableMap.put("a",2222);
        tableMap.put("b",2223);
        tableMap.put("c",2224);
        param.add(tableMap);
        Map<String,Object> tableMap2 = new HashMap<>();
        tableMap2.put("a",2222);
        tableMap2.put("b",2223);
        tableMap2.put("c",2226);
        param.add(tableMap2);
        pdfVarEntity=new PdfVarEntity("table",param,fileNameList);
        hashMap.put("tab2",pdfVarEntity.getMap());

        String fileHash= generatePdf.generatePdf(hashMap,source,targetDoc,targetPdf,true);
        System.out.println(fileHash);
    }
    @Test
    public void test() throws Exception{
//        String source=this.noticesRoot + "/公证书.pdf";
//        String target = this.noticesRoot + "/公证书21.pdf";
//        Map<String,Object> map = new HashMap<>();
//        map.put("imgSign","uploads/冯艳.png");
//        map.put("imgStamp","uploads/冯艳.png");
//        map.put("certificateId","qweqweqw");
//
//
//        String fileHash= generatePdf.generateCertificate(target,source,map);
//        System.out.println(fileHash);


        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterService.getById("f739d886-56a3-11ec-9e34-b8cb299dab4c");
        MasterService masterService = (MasterService) SpringContextUtil.getBean("GR");
        masterService.generatePdf(notarzationMasterEntity.getId());
        masterService.generatePreparePaper(notarzationMasterEntity);
        userNotarzationMasterService.generateNotarization("f739d886-56a3-11ec-9e34-b8cb299dab4c");
    }


    @Test
    public void test2() throws Exception{
        signUtils.checkSign(new HashMap<>());
//        System.out.println(getYmlParamUtil.getYmlParam());
//        redisTemplate.opsForList().leftPush("list3", "hellow");
//        redisTemplate.opsForList().leftPush("list3", "hellow2");
//        redisTemplate.opsForList().leftPush("list3", "hellow3");
//        redisTemplate.opsForList().leftPush("PUSH_NOTARIZATIONID_AFTERPAY",null);
//        redisTemplate.opsForList().leftPush("PUSH_NOTARIZATIONID_AFTERPAY", "601fa556-2a56-4507-bdbf-58f702c2c645");

//          System.out.println(redisUtils.blocking("list3","list4"));
//        System.out.println(fileTokenUtils.fileToken("sdasdd"));
//        String[] s = new String[]{"1","2"};
//        List<String> list = new ArrayList<>();
//        list.add("1");
//        list.add("2");
//        System.out.println(s);
//        System.out.println(list);

    }

    @Test
    public void test3(){
//        System.out.println(translateLanguage);
//        System.out.println(DigestUtils.md5DigestAsHex("7da0da22-7e28-4d37-8622-7d4ca7371740.png".getBytes()));
//        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterService.getById("f739d886-56a3-11ec-9e34-b8cb299dab4c");
//        truthService.pushBaseData(notarzationMasterEntity);

    }
}

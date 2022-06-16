package com.lianzheng.h5.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.lianzheng.h5.entity.Document;
import com.lianzheng.h5.entity.NotarzationMaster;
import com.lianzheng.h5.mapper.NotarzationMasterMapper;
import com.lianzheng.h5.service.INotarzationMasterService;
import com.lianzheng.h5.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

/**
 * <p>
 * 公证申请主体表 服务实现类
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-15
 */
@Service
public class NotarzationMasterServiceImpl extends ServiceImpl<NotarzationMasterMapper, NotarzationMaster> implements INotarzationMasterService {

    @Value("${file.path}")
    private String dirPath;

    @Autowired
    DocumentServiceImpl documentService;

    public void saveDocument(String url, String categoryCode, String masterTableId) {
        if(!hasText(url)){
            return;
        }
        try {
            //图片信息保存
            Document document = new Document();
            document.setId(UUID.randomUUID().toString());
            extraFileInformation(url, categoryCode, masterTableId, document);
            documentService.save(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量保存文件信息
     *
     * @param urlList
     * @param categoryCode
     * @param masterTableId
     */
    public void batchSaveDocument(List<String> urlList, String categoryCode, String masterTableId) {
        if(CollectionUtil.isEmpty(urlList)){
            return;
        }
        try {
            //图片信息保存
            List<Document> documentList = Lists.newArrayList();
            urlList.forEach(model -> {
                Document document = new Document();
                document.setId(UUID.randomUUID().toString());
                extraFileInformation(model, categoryCode, masterTableId, document);
                documentList.add(document);
            });
            documentService.saveBatch(documentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 针对国徽面,录入头像和身份证人像面
     */
    public void fixedCategoryCodeCheck(String masterTableId, String url, String categoryCode) {
        Document document = documentService.getOne(Wrappers.<Document>lambdaQuery()
                .eq(Document::getRefererId, masterTableId)
                .eq(Document::getCategoryCode, categoryCode), false);

        if (document != null && !url.equals(document.getUploadedAbsolutePath())) {
            if(url==null||url.equals("")){
                documentService.removeById(document.getId());
                return;
            }
            //说明有替换更新URL,那么需要先将之前的上传信息给删除后,再保存
            extraFileInformation(url, categoryCode, masterTableId, document);
            documentService.updateById(document); //更新一下文档里面的图片信息
        }
    }

    private void extraFileInformation(String url, String categoryCode, String masterTableId, Document document) {
        File localFile = new File(url); //todo 做成内网访问
        //传来的url可能包含token信息, 先去掉
        url = StringUtil.removeToken(url);

        String fileNameAndSuffix = url.substring(url.lastIndexOf("/") + 1);


        document.setFileName(fileNameAndSuffix.split("\\.")[0]);
        document.setFileSize(localFile.length());
        document.setFileExt(fileNameAndSuffix.split("\\.")[1]);

        BigDecimal bigDecimal = BigDecimal.valueOf(localFile.length() / 1024.0 / 1024.0);
        bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
        document.setFileSizeM(bigDecimal);
        document.setRefererTableName("notarzation_master");
        document.setRefererId(masterTableId);
        document.setCategoryCode(categoryCode);
        document.setUploadedAbsolutePath(url);
        document.setUploadedRelativePath(dirPath);
    }
}

package com.lianzheng.core.wechat.sessionstore.message.worker;

import com.lianzheng.core.wechat.externalcontact.ExternalContractConfig;
import org.springframework.stereotype.Component;

/**
 * @Description: 处理在线文档的微信会话内容
 * "msgtype":"docmsg", *
 * "doc":{"title":"测试&演示客户","doc_creator":"test","link_url":"https://doc.weixin.qq.com/txdoc/excel?docid=xxx"}
 * @author: 何江雁
 * @date: 2021年10月19日 17:22
 */
@Component
public class DocMessageWorker extends MessageWorkerBase {
    @Override
    protected String overwriteContentType(String msgType){
        return "doc";
    }

    public DocMessageWorker(ExternalContractConfig externalContractConfig){
        super(externalContractConfig);
    }
}

package com.lianzheng.core.wechat.sessionstore.message.worker;

import com.lianzheng.core.wechat.externalcontact.ExternalContractConfig;
import org.springframework.stereotype.Component;

/**
 * @Description: 处理文本消息为image的微信会话内容。 image里的json字段为：
 * 参数	说明
 * msgtype	图片消息为：image。String类型
 * sdkfileid	媒体资源的id信息。String类型
 * md5sum	图片资源的md5值，供进行校验。String类型
 * filesize	图片资源的文件大小。Uint32类型
 *
 * @author: 何江雁
 * @date: 2021年10月19日 17:22
 */
@Component
public class ImageMessageWorker extends MessageWorkerBase {
    public ImageMessageWorker(ExternalContractConfig externalContractConfig){
        super(externalContractConfig);
    }
}

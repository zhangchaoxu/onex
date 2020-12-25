package com.nb6868.onexboot.api.modules.msg.service;

import com.nb6868.onexboot.api.modules.msg.dto.MailTplDTO;
import com.nb6868.onexboot.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onexboot.common.service.CrudService;

/**
 * 邮件模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface MailTplService extends CrudService<MailTplEntity, MailTplDTO> {

    /**
     * 通过类型和编码获取模板
     * @param code 模板编码
     * @param type 消息类型
     * @return 模板
     */
    MailTplEntity getByTypeAndCode(String type, String code);

}
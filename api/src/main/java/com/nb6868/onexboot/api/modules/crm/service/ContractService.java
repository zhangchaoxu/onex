package com.nb6868.onexboot.api.modules.crm.service;

import com.nb6868.onexboot.api.modules.crm.dto.ContractDTO;
import com.nb6868.onexboot.common.service.CrudService;
import com.nb6868.onexboot.api.modules.crm.entity.ContractEntity;

import java.util.List;
import java.util.Map;

/**
 * CRM合同
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ContractService extends CrudService<ContractEntity, ContractDTO> {

    List<Map<String, Object>> listContractMonthCount(Map<String, Object> params);

}
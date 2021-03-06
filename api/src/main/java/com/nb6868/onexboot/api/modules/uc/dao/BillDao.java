package com.nb6868.onexboot.api.modules.uc.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.uc.entity.BillEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单流水
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface BillDao extends BaseDao<BillEntity> {

}

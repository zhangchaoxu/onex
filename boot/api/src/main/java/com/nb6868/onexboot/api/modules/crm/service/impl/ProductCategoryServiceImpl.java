package com.nb6868.onexboot.api.modules.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.nb6868.onexboot.api.modules.crm.dao.ProductCategoryDao;
import com.nb6868.onexboot.api.modules.crm.dto.ProductCategoryDTO;
import com.nb6868.onexboot.api.modules.crm.entity.ProductEntity;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.api.modules.crm.entity.ProductCategoryEntity;
import com.nb6868.onexboot.api.modules.crm.service.ProductCategoryService;
import com.nb6868.onexboot.api.modules.crm.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

/**
 * CRM 产品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ProductCategoryServiceImpl extends CrudServiceImpl<ProductCategoryDao, ProductCategoryEntity, ProductCategoryDTO> implements ProductCategoryService {

    @Autowired
    ProductService productService;

    @Override
    public QueryWrapper<ProductCategoryEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<ProductCategoryEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper().orderByAsc("sort");
    }

    @Override
    public boolean logicDeleteById(Serializable id) {
        AssertUtils.isTrue(hasSub("pid", id), "存在子类别,不允许删除");
        AssertUtils.isTrue(SqlHelper.retBool(productService.query().eq("category_id", id).count()), "类别下存在商品,不允许删除");
        return super.logicDeleteById(id);
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, ProductCategoryDTO dto, ProductCategoryEntity existedEntity, int type) {
        if (1 == type && ret && !StringUtils.equals(existedEntity.getName(), dto.getName())) {
            // 更新成功, name发生变化,更新相关业务表中的code
            productService.update().eq("category_id", existedEntity.getId()).set("category_name", dto.getName()).update(new ProductEntity());
        }
    }

    @Override
    public ProductCategoryEntity getByName(String name, Long tenantId) {
        return query().eq("name", name).eq("tenant_id", tenantId).last(Const.LIMIT_ONE).one();
    }
}
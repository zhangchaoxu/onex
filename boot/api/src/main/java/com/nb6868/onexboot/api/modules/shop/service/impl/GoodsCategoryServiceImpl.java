package com.nb6868.onexboot.api.modules.shop.service.impl;

import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.TreeUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.api.modules.shop.dao.CategoryDao;
import com.nb6868.onexboot.api.modules.shop.dto.GoodsCategoryDTO;
import com.nb6868.onexboot.api.modules.shop.dto.GoodsCategoryTreeDTO;
import com.nb6868.onexboot.api.modules.shop.entity.GoodsCategoryEntity;
import com.nb6868.onexboot.api.modules.shop.service.GoodsCategoryService;
import com.nb6868.onexboot.api.modules.shop.service.GoodsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 商品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class GoodsCategoryServiceImpl extends CrudServiceImpl<CategoryDao, GoodsCategoryEntity, GoodsCategoryDTO> implements GoodsCategoryService {

    @Autowired
    private GoodsService goodsService;

    @Override
    public QueryWrapper<GoodsCategoryEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<GoodsCategoryEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .eq("pid", "pid")
                .eq("tenantId", "tenant_id")
                .eq("pid", "pid")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper()
                .orderByAsc("sort");
    }

    @Override
    public List<GoodsCategoryTreeDTO> tree(Map<String, Object> params) {
        List<GoodsCategoryEntity> entityList = getBaseMapper().selectList(getWrapper("tree", params));

        List<GoodsCategoryTreeDTO> dtoList = ConvertUtils.sourceToTarget(entityList, GoodsCategoryTreeDTO.class);

        return TreeUtils.build(dtoList);
    }

    @Override
    protected void beforeSaveOrUpdateDto(GoodsCategoryDTO dto, int type) {
        // 检查一下上级是否存在
        if (0L != dto.getPid()) {
            GoodsCategoryEntity pEntity = getById(dto.getPid());
            AssertUtils.isEmpty(pEntity, ErrorCode.PARENT_NOT_EXISTED);
            dto.setParentName(pEntity.getName());
        }

        if (1 == type) {
            // 上级不能为自己
            AssertUtils.isTrue(dto.getId().equals(dto.getPid()), ErrorCode.PARENT_EQ_SELF);
        }
    }

    @Override
    protected void inSaveOrUpdateDto(GoodsCategoryDTO dto, GoodsCategoryEntity existedEntity, int type) {
        // 一级变x级,已存在下级,不允许修改级别
        if (1 == type && !dto.getPid().equals(existedEntity.getPid())) {
            AssertUtils.isTrue(hasSub("pid", dto.getId()),"存在小类,不允许变更级别");
            AssertUtils.isTrue(SqlHelper.retBool(goodsService.query().eq("category_id",  dto.getId()).count()),"类别下存在商品,不允许变更级别");
        }
    }

    @Override
    public boolean logicDeleteById(Serializable id) {
        AssertUtils.isTrue(hasSub("pid", id), "存在子类别,不允许删除");
        AssertUtils.isTrue(SqlHelper.retBool(goodsService.query().eq("category_id", id).count()), "类别下存在商品,不允许删除");
        return super.logicDeleteById(id);
    }

    @Override
    public List<GoodsCategoryDTO> getParentMenuList(Long id) {
        List<GoodsCategoryDTO> menus = new ArrayList<>();
        while (id != 0) {
            GoodsCategoryDTO dto = getDtoById(id);
            if (dto != null) {
                menus.add(dto);
                id = dto.getPid();
            } else {
                id = 0L;
            }
        }
        Collections.reverse(menus);
        return menus;
    }
}
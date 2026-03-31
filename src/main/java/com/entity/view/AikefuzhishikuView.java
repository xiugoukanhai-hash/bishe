package com.entity.view;

import com.entity.AikefuzhishikuEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;

/**
 * AI客服知识库
 * 后端返回视图实体辅助类
 */
@TableName("aikefuzhishiku")
public class AikefuzhishikuView extends AikefuzhishikuEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public AikefuzhishikuView() {
    }

    public AikefuzhishikuView(AikefuzhishikuEntity aikefuzhishikuEntity) {
        try {
            BeanUtils.copyProperties(this, aikefuzhishikuEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

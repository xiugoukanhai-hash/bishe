package com.entity.view;

import com.entity.QiantairenyuanEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;

/**
 * 前台人员
 * 后端返回视图实体辅助类
 */
@TableName("qiantairenyuan")
public class QiantairenyuanView extends QiantairenyuanEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public QiantairenyuanView() {
    }

    public QiantairenyuanView(QiantairenyuanEntity qiantairenyuanEntity) {
        try {
            BeanUtils.copyProperties(this, qiantairenyuanEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

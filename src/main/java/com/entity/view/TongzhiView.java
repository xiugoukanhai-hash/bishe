package com.entity.view;

import com.entity.TongzhiEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;

/**
 * 通知
 * 后端返回视图实体辅助类
 */
@TableName("tongzhi")
public class TongzhiView extends TongzhiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public TongzhiView() {
    }

    public TongzhiView(TongzhiEntity tongzhiEntity) {
        try {
            BeanUtils.copyProperties(this, tongzhiEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

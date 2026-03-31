package com.entity.view;

import com.entity.FuwujiluEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;

/**
 * 服务记录
 * 后端返回视图实体辅助类
 */
@TableName("fuwujilu")
public class FuwujiluView extends FuwujiluEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public FuwujiluView() {
    }

    public FuwujiluView(FuwujiluEntity fuwujiluEntity) {
        try {
            BeanUtils.copyProperties(this, fuwujiluEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

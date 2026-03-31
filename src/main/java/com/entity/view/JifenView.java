package com.entity.view;

import com.entity.JifenEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;

/**
 * 积分记录
 * 后端返回视图实体辅助类
 */
@TableName("jifen")
public class JifenView extends JifenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public JifenView() {
    }

    public JifenView(JifenEntity jifenEntity) {
        try {
            BeanUtils.copyProperties(this, jifenEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

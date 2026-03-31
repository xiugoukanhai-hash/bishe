package com.entity.view;

import com.entity.PinglunEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;

/**
 * 客户评价 视图
 */
@TableName("pinglun")
public class PinglunView extends PinglunEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public PinglunView() {}

    public PinglunView(PinglunEntity pinglunEntity) {
        try {
            BeanUtils.copyProperties(this, pinglunEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

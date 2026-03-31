package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.PinglunEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.PinglunVO;
import com.entity.view.PinglunView;
import org.apache.ibatis.annotations.Param;

/**
 * 客户评价
 */
public interface PinglunService extends IService<PinglunEntity> {
    PageUtils queryPage(Map<String, Object> params);
    List<PinglunVO> selectListVO(Wrapper<PinglunEntity> wrapper);
    PinglunVO selectVO(Wrapper<PinglunEntity> wrapper);
    List<PinglunView> selectListView(Wrapper<PinglunEntity> wrapper);
    PinglunView selectView(Wrapper<PinglunEntity> wrapper);
    PageUtils queryPage(Map<String, Object> params, Wrapper<PinglunEntity> wrapper);
}

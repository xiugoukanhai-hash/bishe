package com.dao;

import com.entity.PinglunEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;
import com.entity.vo.PinglunVO;
import com.entity.view.PinglunView;

/**
 * 客户评价
 */
public interface PinglunDao extends BaseMapper<PinglunEntity> {
    List<PinglunVO> selectListVO(@Param("ew") Wrapper<PinglunEntity> wrapper);
    PinglunVO selectVO(@Param("ew") Wrapper<PinglunEntity> wrapper);
    List<PinglunView> selectListView(@Param("ew") Wrapper<PinglunEntity> wrapper);
    List<PinglunView> selectListView(Pagination page, @Param("ew") Wrapper<PinglunEntity> wrapper);
    PinglunView selectView(@Param("ew") Wrapper<PinglunEntity> wrapper);
}

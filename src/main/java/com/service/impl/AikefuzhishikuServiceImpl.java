package com.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;
import com.utils.DeepSeekUtil;
import com.dao.AikefuzhishikuDao;
import com.entity.AikefuzhishikuEntity;
import com.service.AikefuzhishikuService;
import com.constant.AiKefuConstant;
import com.config.DeepSeekConfig;

/**
 * AI客服知识库 服务实现类
 */
@Service("aikefuzhishikuService")
public class AikefuzhishikuServiceImpl extends ServiceImpl<AikefuzhishikuDao, AikefuzhishikuEntity> implements AikefuzhishikuService {

    private static final Logger logger = LoggerFactory.getLogger(AikefuzhishikuServiceImpl.class);

    @Autowired
    private DeepSeekConfig deepSeekConfig;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<AikefuzhishikuEntity> page = this.selectPage(
                new Query<AikefuzhishikuEntity>(params).getPage(),
                new EntityWrapper<AikefuzhishikuEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<AikefuzhishikuEntity> wrapper) {
        Page<AikefuzhishikuEntity> page = this.selectPage(
                new Query<AikefuzhishikuEntity>(params).getPage(),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public String smartAnswer(String question) {
        if (question == null || question.trim().isEmpty()) {
            return AiKefuConstant.DEFAULT_REPLY;
        }
        
        String questionLower = question.toLowerCase().trim();
        
        // 检查是否需要转人工
        if (needTransferToHuman(questionLower)) {
            return "好的，我这就为您转接人工客服。请稍等，前台工作人员将会尽快与您联系。\n\n如需紧急帮助，您也可以直接拨打前台电话：8888";
        }
        
        // 查询所有启用的知识库条目
        List<AikefuzhishikuEntity> list = this.selectList(
                new EntityWrapper<AikefuzhishikuEntity>()
                        .eq("zhuangtai", AiKefuConstant.KB_STATUS_ENABLED)
                        .orderBy("paixu", true)
        );
        
        // 关键词匹配
        for (AikefuzhishikuEntity item : list) {
            String keywords = item.getGuanjianci();
            if (keywords != null && !keywords.isEmpty()) {
                String[] keywordArray = keywords.split(",");
                for (String keyword : keywordArray) {
                    if (questionLower.contains(keyword.trim().toLowerCase())) {
                        this.incrementClickCount(item.getId());
                        return item.getDaan();
                    }
                }
            }
            
            String wenti = item.getWenti();
            if (wenti != null && questionLower.contains(wenti.toLowerCase())) {
                this.incrementClickCount(item.getId());
                return item.getDaan();
            }
        }
        
        // 知识库未匹配，尝试调用DeepSeek AI
        if (deepSeekConfig != null && deepSeekConfig.isEnabled()) {
            try {
                logger.info("知识库未匹配，调用DeepSeek AI回答问题: {}", question);
                String aiAnswer = DeepSeekUtil.hotelAssistantChat(deepSeekConfig.getApiKey(), question);
                if (aiAnswer != null && !aiAnswer.isEmpty()) {
                    return aiAnswer;
                }
            } catch (Exception e) {
                logger.error("DeepSeek AI调用失败: {}", e.getMessage());
            }
        }
        
        // 全部失败，返回默认回复
        return AiKefuConstant.DEFAULT_REPLY;
    }
    
    /**
     * 检查是否需要转人工客服
     */
    private boolean needTransferToHuman(String question) {
        for (String keyword : AiKefuConstant.TRANSFER_KEYWORDS) {
            if (question.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<AikefuzhishikuEntity> getHotQuestions(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = AiKefuConstant.HOT_QUESTION_COUNT;
        }
        return this.selectList(
                new EntityWrapper<AikefuzhishikuEntity>()
                        .eq("zhuangtai", AiKefuConstant.KB_STATUS_ENABLED)
                        .orderBy("clickcount", false)
                        .last("LIMIT " + limit)
        );
    }

    @Override
    public void incrementClickCount(Long id) {
        AikefuzhishikuEntity entity = this.selectById(id);
        if (entity != null) {
            Integer count = entity.getClickcount();
            entity.setClickcount(count == null ? 1 : count + 1);
            this.updateById(entity);
        }
    }
}

package com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * DeepSeek AI接口调用工具类
 * 用于AI客服智能问答功能
 */
public class DeepSeekUtil {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekUtil.class);

    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    private static final String MODEL = "deepseek-chat";
    private static final int TIMEOUT = 30000;

    private DeepSeekUtil() {
        throw new IllegalStateException("工具类不允许实例化");
    }

    /**
     * 调用DeepSeek API进行智能问答
     * @param apiKey API密钥
     * @param question 用户问题
     * @param systemPrompt 系统提示词
     * @return AI回复内容
     */
    public static String chat(String apiKey, String question, String systemPrompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("DeepSeek API Key未配置");
            return null;
        }

        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);
            conn.setDoOutput(true);

            JSONObject requestBody = buildRequestBody(question, systemPrompt);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
                return parseResponse(response.toString());
            } else {
                logger.error("DeepSeek API调用失败，状态码: {}", responseCode);
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                }
                logger.error("错误信息: {}", errorResponse.toString());
                return null;
            }
        } catch (Exception e) {
            logger.error("DeepSeek API调用异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 构建请求体
     */
    private static JSONObject buildRequestBody(String question, String systemPrompt) {
        JSONObject body = new JSONObject();
        body.put("model", MODEL);
        body.put("max_tokens", 1024);
        body.put("temperature", 0.7);

        JSONArray messages = new JSONArray();

        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);
        }

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", question);
        messages.add(userMessage);

        body.put("messages", messages);
        return body;
    }

    /**
     * 解析响应
     */
    private static String parseResponse(String responseJson) {
        try {
            JSONObject response = JSON.parseObject(responseJson);
            JSONArray choices = response.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                if (message != null) {
                    return message.getString("content");
                }
            }
        } catch (Exception e) {
            logger.error("解析DeepSeek响应失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 酒店客服专用问答
     * @param apiKey API密钥
     * @param question 用户问题
     * @return AI回复
     */
    public static String hotelAssistantChat(String apiKey, String question) {
        String systemPrompt = "你是一个专业的酒店智能客服助手，名叫小智。" +
                "你的职责是帮助客人解答关于酒店预订、入住、退房、服务设施等问题。" +
                "请用友好、专业的语气回答问题，回答要简洁明了。" +
                "如果问题超出你的能力范围，请建议客人联系前台人工客服。" +
                "注意：不要回答与酒店服务无关的问题，礼貌地引导用户回到酒店相关话题。";
        return chat(apiKey, question, systemPrompt);
    }
}

/*
 Navicat Premium Data Transfer

 Source Server         : work
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : springboot6alf1

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 05/03/2026 23:36:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for aikefuzhishiku
-- ----------------------------
DROP TABLE IF EXISTS `aikefuzhishiku`;
CREATE TABLE `aikefuzhishiku`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `wentileixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '问题类型',
  `guanjianci` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '关键词',
  `wenti` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '问题',
  `daan` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '答案',
  `paixu` int NULL DEFAULT 0 COMMENT '排序',
  `clickcount` int NULL DEFAULT 0 COMMENT '点击次数',
  `zhuangtai` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '启用' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_wentileixing`(`wentileixing` ASC) USING BTREE,
  INDEX `idx_zhuangtai`(`zhuangtai` ASC) USING BTREE,
  INDEX `idx_paixu`(`paixu` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 110 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'AI客服知识库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aikefuzhishiku
-- ----------------------------
INSERT INTO `aikefuzhishiku` VALUES (100, '2026-03-01 00:06:49', '订房流程', '预订,订房,怎么订,如何预订,预约,订酒店', '如何预订客房？', '您好！预订客房的流程如下：\n1. 在首页浏览可用客房信息\n2. 选择心仪的客房点击\"预订\"\n3. 填写入住时间和入住天数\n4. 确认订单信息并提交\n5. 等待管理员审核通过后进行支付\n6. 支付成功后凭订单到前台办理入住\n\n如有其他问题，请随时咨询。', 1, 3, '启用');
INSERT INTO `aikefuzhishiku` VALUES (101, '2026-03-01 00:06:49', '退房规则', '退房,退房时间,几点退房,checkout,离店', '退房时间是几点？', '您好！我们酒店的退房规则如下：\n\n【退房时间】\n- 标准退房时间：每日中午12:00\n\n【延时退房】\n- 12:00-14:00退房：加收半天房费\n- 14:00-18:00退房：加收全天房费\n- 18:00后退房：按续住一晚计算\n\n【温馨提示】\n- 如需延时退房，请提前联系前台\n- 会员可享受延时至14:00免费退房', 2, 3, '启用');
INSERT INTO `aikefuzhishiku` VALUES (102, '2026-03-01 00:06:49', '发票索取', '发票,开票,开发票,invoice,报销', '如何开具发票？', '您好！关于发票开具，请参考以下说明：\n\n【开票方式】\n1. 入住期间可在前台申请开具发票\n2. 退房时可一并开具住宿发票\n3. 退房后7天内可联系前台补开\n\n【发票类型】\n- 普通发票：即时开具\n- 增值税专用发票：需提供公司税号等信息\n\n【可开具项目】\n- 住宿费\n- 餐饮费\n- 其他消费项目\n\n请携带相关证件到前台办理。', 3, 1, '启用');
INSERT INTO `aikefuzhishiku` VALUES (103, '2026-03-01 00:06:49', '早餐服务', '早餐,早餐时间,早餐地点,breakfast,餐厅', '早餐时间和地点？', '您好！酒店早餐服务信息如下：\n\n【早餐时间】\n- 工作日：07:00 - 09:30\n- 周末及节假日：07:00 - 10:00\n\n【早餐地点】\n- 酒店一楼西餐厅\n\n【早餐类型】\n- 中西式自助早餐\n- 品种丰富，包含中式点心、西式面包、新鲜水果、饮品等\n\n【早餐费用】\n- 住店客人：38元/位（含房价则免费）\n- 非住店客人：58元/位\n\n祝您用餐愉快！', 4, 0, '启用');
INSERT INTO `aikefuzhishiku` VALUES (104, '2026-03-01 00:06:49', 'WiFi信息', 'wifi,无线网,网络,上网,密码,联网', 'WiFi密码是多少？', '您好！酒店WiFi信息如下：\n\n【WiFi名称】\n- 公共区域：Hotel_Guest\n- 客房内：Hotel_Room\n\n【连接方式】\n1. 选择对应WiFi网络\n2. 密码请在房间服务指南中查看\n3. 或拨打前台电话获取\n\n【网络覆盖】\n- 酒店全区域覆盖\n- 支持高速上网\n\n【温馨提示】\n- 如遇网络问题，请联系前台协助处理\n- 前台电话：8888', 5, 1, '启用');
INSERT INTO `aikefuzhishiku` VALUES (105, '2026-03-01 00:06:49', '停车信息', '停车,车位,停车场,parking,泊车', '酒店有停车场吗？', '您好！酒店停车信息如下：\n\n【停车场位置】\n- 酒店地下一层（B1）\n\n【车位数量】\n- 共50个车位\n\n【收费标准】\n- 住店客人：免费停车\n- 非住店客人：10元/小时，60元封顶/天\n\n【入口位置】\n- 酒店北门右侧\n\n【温馨提示】\n- 车位有限，建议提前预约\n- 大型车辆请提前告知前台\n- 贵重物品请随身携带', 6, 0, '启用');
INSERT INTO `aikefuzhishiku` VALUES (106, '2026-03-01 00:06:49', '周边推荐', '周边,附近,景点,美食,购物,玩,逛', '酒店周边有什么推荐？', '您好！酒店周边推荐如下：\n\n【景点推荐】\n- 市中心广场：步行约10分钟\n- 博物馆：驾车约15分钟\n- 公园：步行约5分钟\n\n【美食推荐】\n- 特色餐厅街：步行约5分钟\n- 夜市小吃街：步行约8分钟\n- 商场美食广场：步行约10分钟\n\n【购物推荐】\n- 大型商场：步行约10分钟\n- 便利店：酒店一楼即有\n- 特产店：步行约3分钟\n\n如需详细信息，请联系前台获取周边地图。', 7, 0, '启用');
INSERT INTO `aikefuzhishiku` VALUES (107, '2026-03-01 00:06:49', '客房信息', '房间,房型,客房,床,面积,设施', '酒店有哪些房型？', '您好！我们酒店提供以下房型：\n\n【标准单人间】\n- 面积：20平米\n- 床型：1.2米单人床\n- 价格：约200元/晚\n\n【标准双人间】\n- 面积：25平米\n- 床型：1.5米双人床\n- 价格：约280元/晚\n\n【豪华大床房】\n- 面积：30平米\n- 床型：1.8米大床\n- 价格：约380元/晚\n\n【家庭套房】\n- 面积：45平米\n- 床型：1.8米+1.2米\n- 价格：约500元/晚\n\n所有房间均配备：空调、电视、独立卫浴、免费WiFi等。', 8, 1, '启用');
INSERT INTO `aikefuzhishiku` VALUES (108, '2026-03-01 00:06:49', '价格咨询', '价格,多少钱,费用,房价,价钱', '房间价格是多少？', '您好！房间价格根据房型和入住日期有所不同：\n\n【参考价格】\n- 标准单人间：约200元/晚起\n- 标准双人间：约280元/晚起\n- 豪华大床房：约380元/晚起\n- 家庭套房：约500元/晚起\n\n【优惠信息】\n- 会员可享9折优惠\n- 连住3晚以上享8.5折\n- 节假日价格可能有所上浮\n\n【温馨提示】\n具体价格请以预订页面显示为准，或联系前台咨询最新优惠活动。', 9, 0, '启用');
INSERT INTO `aikefuzhishiku` VALUES (109, '2026-03-01 00:06:49', '服务咨询', '服务,叫醒,洗衣,行李,寄存,借', '酒店提供哪些服务？', '您好！酒店提供以下服务：\n\n【客房服务】\n- 叫醒服务（免费）\n- 客房清洁（每日一次）\n- 额外清洁（按需收费）\n\n【便民服务】\n- 行李寄存（免费）\n- 物品借用（雨伞、充电器等）\n- 叫车服务\n- 快递代收\n\n【增值服务】\n- 洗衣服务（收费）\n- 早餐服务\n- 商务中心\n- 会议室预订\n\n如需以上服务，请拨打前台电话8888或联系客房服务。', 10, 0, '启用');

-- ----------------------------
-- Table structure for chat
-- ----------------------------
DROP TABLE IF EXISTS `chat`;
CREATE TABLE `chat`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `userid` bigint NOT NULL COMMENT '用户id',
  `adminid` bigint NULL DEFAULT NULL COMMENT '管理员id',
  `ask` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '提问',
  `reply` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '回复',
  `isreply` int NULL DEFAULT NULL COMMENT '是否回复',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772444092125 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '在线客服' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat
-- ----------------------------
INSERT INTO `chat` VALUES (141, '2026-04-30 10:32:31', 1, 1, '提问1', '回复1', 1);
INSERT INTO `chat` VALUES (142, '2026-04-30 10:32:31', 2, 2, '提问2', '回复2', 2);
INSERT INTO `chat` VALUES (143, '2026-04-30 10:32:31', 3, 3, '提问3', '回复3', 3);
INSERT INTO `chat` VALUES (144, '2026-04-30 10:32:31', 4, 4, '提问4', '回复4', 4);
INSERT INTO `chat` VALUES (145, '2026-04-30 10:32:31', 5, 5, '提问5', '回复5', 5);
INSERT INTO `chat` VALUES (146, '2026-04-30 10:32:31', 6, 6, '提问6', '回复6', 6);
INSERT INTO `chat` VALUES (1619750435177, '2026-04-30 10:40:34', 1619750209173, NULL, '电饭锅电饭锅地方刚发的', NULL, 0);
INSERT INTO `chat` VALUES (1619750664017, '2026-04-30 10:44:23', 1619750209173, 1, NULL, '个地方工号规范化发给发给预约', 0);
INSERT INTO `chat` VALUES (1772444089854, '2026-03-02 17:34:48', 1619750209173, NULL, '1111', NULL, 0);
INSERT INTO `chat` VALUES (1772444092124, '2026-03-02 17:34:51', 1619750209173, NULL, '1111', NULL, 1);

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '配置参数名称',
  `value` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '配置参数值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '配置文件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES (1, 'picture1', 'http://localhost:8080/springboot6alf1/upload/1772528195077.png');
INSERT INTO `config` VALUES (2, 'picture2', 'upload/picture2.jpg');
INSERT INTO `config` VALUES (3, 'picture3', 'upload/picture3.jpg');
INSERT INTO `config` VALUES (4, 'hotelName', '海景假日酒店');
INSERT INTO `config` VALUES (5, 'hotelPhone', '400-888-9999');
INSERT INTO `config` VALUES (6, 'hotelDescription', 'Welcome to our hotel');

-- ----------------------------
-- Table structure for fuwujilu
-- ----------------------------
DROP TABLE IF EXISTS `fuwujilu`;
CREATE TABLE `fuwujilu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dingdanbianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `kefanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房号',
  `fuwuleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '服务类型',
  `fuwuxiangqing` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '服务详情',
  `fuwufeiyong` double NULL DEFAULT NULL COMMENT '服务费用',
  `beizhu` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '备注',
  `dengjirenzhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '登记人账号',
  `dengjishijian` datetime NULL DEFAULT NULL COMMENT '登记时间',
  `chulirenzhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '处理人账号',
  `chulishijian` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `zhuangtai` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '待处理' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dingdanbianhao`(`dingdanbianhao` ASC) USING BTREE,
  INDEX `idx_kefanghao`(`kefanghao` ASC) USING BTREE,
  INDEX `idx_zhuangtai`(`zhuangtai` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '服务记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fuwujilu
-- ----------------------------

-- ----------------------------
-- Table structure for huiyuan
-- ----------------------------
DROP TABLE IF EXISTS `huiyuan`;
CREATE TABLE `huiyuan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `zhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '账号',
  `mima` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `xingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `nianling` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '年龄',
  `xingbie` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '性别',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `shenfenzheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `zhaopian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '照片',
  `jifen` int NULL DEFAULT 0 COMMENT '积分',
  `yue` double NULL DEFAULT 0 COMMENT '璐︽埛浣欓',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `zhanghao`(`zhanghao` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1619750220664 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '会员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of huiyuan
-- ----------------------------
INSERT INTO `huiyuan` VALUES (1, '2026-03-03 13:36:52', 'vip_zhang', '123456', '张明', '35', '男', '13900139001', '440101198801011111', 'huiyuan_zhaopian1.jpg', 2580, 0);
INSERT INTO `huiyuan` VALUES (2, '2026-03-03 13:36:52', 'vip', '123456', 'VIP会员', '28', '男', '13900139005', '440101199605055555', 'huiyuan_zhaopian2.jpg', 3200, 0);

-- ----------------------------
-- Table structure for huiyuanquxiao
-- ----------------------------
DROP TABLE IF EXISTS `huiyuanquxiao`;
CREATE TABLE `huiyuanquxiao`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yuyuebianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '预约编号',
  `kefanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房号',
  `shifouquxiao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '是否取消',
  `jiage` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '价格',
  `tianshu` int NULL DEFAULT NULL COMMENT '天数',
  `zongjia` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '总价',
  `quxiaoyuanyin` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '取消原因',
  `quxiaoshijian` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `zhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `xingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `shenfenzheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `sfsh` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '否' COMMENT '是否审核',
  `shhf` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '审核回复',
  `ispay` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '未支付' COMMENT '是否支付',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1619750857712 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '会员取消' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of huiyuanquxiao
-- ----------------------------
INSERT INTO `huiyuanquxiao` VALUES (81, '2026-04-30 10:32:31', '预约编号1', '客房号1', '是', '价格1', 1, '总价1', '取消原因1', '2026-04-30 10:32:31', '账号1', '姓名1', '手机1', '身份证1', '是', '', '未支付');
INSERT INTO `huiyuanquxiao` VALUES (82, '2026-04-30 10:32:31', '预约编号2', '客房号2', '是', '价格2', 2, '总价2', '取消原因2', '2026-04-30 10:32:31', '账号2', '姓名2', '手机2', '身份证2', '是', '', '未支付');
INSERT INTO `huiyuanquxiao` VALUES (83, '2026-04-30 10:32:31', '预约编号3', '客房号3', '是', '价格3', 3, '总价3', '取消原因3', '2026-04-30 10:32:31', '账号3', '姓名3', '手机3', '身份证3', '是', '', '未支付');
INSERT INTO `huiyuanquxiao` VALUES (84, '2026-04-30 10:32:31', '预约编号4', '客房号4', '是', '价格4', 4, '总价4', '取消原因4', '2026-04-30 10:32:31', '账号4', '姓名4', '手机4', '身份证4', '是', '', '未支付');
INSERT INTO `huiyuanquxiao` VALUES (85, '2026-04-30 10:32:31', '预约编号5', '客房号5', '是', '价格5', 5, '总价5', '取消原因5', '2026-04-30 10:32:31', '账号5', '姓名5', '手机5', '身份证5', '是', '', '未支付');
INSERT INTO `huiyuanquxiao` VALUES (86, '2026-04-30 10:32:31', '预约编号6', '客房号6', '是', '价格6', 6, '总价6', '取消原因6', '2026-04-30 10:32:31', '账号6', '姓名6', '手机6', '身份证6', '是', '', '未支付');
INSERT INTO `huiyuanquxiao` VALUES (1619750857711, '2026-04-30 10:47:37', '202143010454285676861', '333', '是', '333', 11, '3663', '<p>士大夫士大夫</p>', '2026-04-30 10:45:51', '22', '电饭锅', '11122222222', '441421199001125847', '否', NULL, '未支付');

-- ----------------------------
-- Table structure for huiyuanruzhu
-- ----------------------------
DROP TABLE IF EXISTS `huiyuanruzhu`;
CREATE TABLE `huiyuanruzhu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dingdanbianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `kefanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房号',
  `kefangleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房类型',
  `suoshujiudian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '所属酒店',
  `zhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `xingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `shenfenzheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `kefangzhuangtai` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房状态',
  `ruzhuyajin` float NULL DEFAULT NULL COMMENT '入住押金',
  `zhifufangshi` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付方式',
  `ruzhushijian` datetime NULL DEFAULT NULL COMMENT '入住时间',
  `ispay` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '未支付' COMMENT '是否支付',
  `jiage` double NULL DEFAULT NULL COMMENT '浠锋牸',
  `yulifangshijian` datetime NULL DEFAULT NULL COMMENT '棰勭鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `dingdanbianhao`(`dingdanbianhao` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1619750893798 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '会员入住' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of huiyuanruzhu
-- ----------------------------
INSERT INTO `huiyuanruzhu` VALUES (103, '2026-04-30 10:32:31', '订单编号3', '客房号3', '客房类型3', '所属酒店3', '账号3', '姓名3', '身份证3', '手机3', '已入住', 3, '支付方式3', '2026-04-30 10:32:31', '未支付', NULL, NULL);
INSERT INTO `huiyuanruzhu` VALUES (104, '2026-04-30 10:32:31', '订单编号4', '客房号4', '客房类型4', '所属酒店4', '账号4', '姓名4', '身份证4', '手机4', '已入住', 4, '支付方式4', '2026-04-30 10:32:31', '未支付', NULL, NULL);
INSERT INTO `huiyuanruzhu` VALUES (105, '2026-04-30 10:32:31', '订单编号5', '客房号5', '客房类型5', '所属酒店5', '账号5', '姓名5', '身份证5', '手机5', '已入住', 5, '支付方式5', '2026-04-30 10:32:31', '未支付', NULL, NULL);
INSERT INTO `huiyuanruzhu` VALUES (106, '2026-04-30 10:32:31', '订单编号6', '客房号6', '客房类型6', '所属酒店6', '账号6', '姓名6', '身份证6', '手机6', '已入住', 6, '支付方式6', '2026-04-30 10:32:31', '未支付', NULL, NULL);
INSERT INTO `huiyuanruzhu` VALUES (1619750893797, '2026-04-30 10:48:13', '1619750780237', '333', '士大夫收到', '士大夫都是广东省房给对方地方', '22', '电饭锅', '441421199001125847', '11122222222', '已入住', 444, '44发给房东', '2026-04-30 10:46:20', '未支付', NULL, NULL);

-- ----------------------------
-- Table structure for huiyuantuifang
-- ----------------------------
DROP TABLE IF EXISTS `huiyuantuifang`;
CREATE TABLE `huiyuantuifang`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dingdanbianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `kefanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房号',
  `kefangleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房类型',
  `suoshujiudian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '所属酒店',
  `zhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `xingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `shenfenzheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `ruzhuyajin` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '入住押金',
  `tuifangshijian` datetime NULL DEFAULT NULL COMMENT '退房时间',
  `ispay` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '未支付' COMMENT '是否支付',
  `jiage` double NULL DEFAULT NULL,
  `chaoshifeiyong` double NULL DEFAULT NULL,
  `zongjine` double NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772446974082 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '会员退房' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of huiyuantuifang
-- ----------------------------
INSERT INTO `huiyuantuifang` VALUES (121, '2026-04-30 10:32:31', '订单编号1', '客房号1', '客房类型1', '所属酒店1', '账号1', '姓名1', '身份证1', '手机1', '入住押金1', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `huiyuantuifang` VALUES (122, '2026-04-30 10:32:31', '订单编号2', '客房号2', '客房类型2', '所属酒店2', '账号2', '姓名2', '身份证2', '手机2', '入住押金2', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `huiyuantuifang` VALUES (123, '2026-04-30 10:32:31', '订单编号3', '客房号3', '客房类型3', '所属酒店3', '账号3', '姓名3', '身份证3', '手机3', '入住押金3', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `huiyuantuifang` VALUES (124, '2026-04-30 10:32:31', '订单编号4', '客房号4', '客房类型4', '所属酒店4', '账号4', '姓名4', '身份证4', '手机4', '入住押金4', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `huiyuantuifang` VALUES (125, '2026-04-30 10:32:31', '订单编号5', '客房号5', '客房类型5', '所属酒店5', '账号5', '姓名5', '身份证5', '手机5', '入住押金5', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `huiyuantuifang` VALUES (126, '2026-04-30 10:32:31', '订单编号6', '客房号6', '客房类型6', '所属酒店6', '账号6', '姓名6', '身份证6', '手机6', '入住押金6', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `huiyuantuifang` VALUES (1619750898360, '2026-04-30 10:48:18', '1619750780237', '333', '士大夫收到', '士大夫都是广东省房给对方地方', '22', '电饭锅', '441421199001125847', '11122222222', '444', '2026-04-30 10:46:34', '已支付', NULL, NULL, NULL);
INSERT INTO `huiyuantuifang` VALUES (1772446216362, '2026-03-02 18:10:15', '订单编号6', '客房号6', '客房类型6', '所属酒店6', '账号6', '姓名6', '身份证6', '手机6', '6', '2026-03-02 18:10:14', '未支付', NULL, NULL, NULL);
INSERT INTO `huiyuantuifang` VALUES (1772446962517, '2026-03-02 18:22:42', '订单编号1', '客房号1', '客房类型1', '所属酒店1', '账号1', '姓名1', '身份证1', '手机1', '1', '2026-03-02 18:22:41', '未支付', NULL, NULL, NULL);
INSERT INTO `huiyuantuifang` VALUES (1772446974081, '2026-03-02 18:22:53', '订单编号2', '客房号2', '客房类型2', '所属酒店2', '账号2', '姓名2', '身份证2', '手机2', '2', '2026-03-02 18:22:53', '未支付', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for huiyuanyuyue
-- ----------------------------
DROP TABLE IF EXISTS `huiyuanyuyue`;
CREATE TABLE `huiyuanyuyue`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yuyuebianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '预约编号',
  `kefanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房号',
  `ruzhushijian` datetime NULL DEFAULT NULL COMMENT '入住时间',
  `jiage` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '价格',
  `tianshu` int NULL DEFAULT NULL COMMENT '天数',
  `zongjia` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '总价',
  `yuyueshijian` datetime NULL DEFAULT NULL COMMENT '预约时间',
  `zhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `xingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `shenfenzheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `sfsh` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '否' COMMENT '是否审核',
  `shhf` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '审核回复',
  `ispay` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '未支付' COMMENT '是否支付',
  `yuyuezhuangtai` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'pending' COMMENT 'pending/approved/rejected/paid/checkedin/cancelled',
  `ispingjia` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'no',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `yuyuebianhao`(`yuyuebianhao` ASC) USING BTREE,
  INDEX `idx_zhanghao`(`zhanghao` ASC) USING BTREE,
  INDEX `idx_kefanghao`(`kefanghao` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772447120281 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '会员预约' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of huiyuanyuyue
-- ----------------------------

-- ----------------------------
-- Table structure for jifen
-- ----------------------------
DROP TABLE IF EXISTS `jifen`;
CREATE TABLE `jifen`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `huiyuanid` bigint NOT NULL COMMENT '会员ID',
  `zhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `jifenleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '积分类型',
  `jifenshu` int NULL DEFAULT 0 COMMENT '积分数',
  `yue` int NULL DEFAULT 0 COMMENT '变动后余额',
  `shuoming` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '说明',
  `guanliandingdan` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '关联订单',
  `caozuoren` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '操作人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_huiyuanid`(`huiyuanid` ASC) USING BTREE,
  INDEX `idx_zhanghao`(`zhanghao` ASC) USING BTREE,
  INDEX `idx_jifenleixing`(`jifenleixing` ASC) USING BTREE,
  INDEX `idx_addtime`(`addtime` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '积分表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of jifen
-- ----------------------------

-- ----------------------------
-- Table structure for kefangxinxi
-- ----------------------------
DROP TABLE IF EXISTS `kefangxinxi`;
CREATE TABLE `kefangxinxi`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `kefanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '客房号',
  `kefangleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房类型',
  `chuangxing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '床型',
  `kefangtupian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房图片',
  `fangjianmianji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '房间面积',
  `jiage` int NULL DEFAULT NULL COMMENT '价格',
  `kefangzhuangtai` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房状态',
  `keyueshijian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '可约时间',
  `weishengqingkuang` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '卫生情况',
  `kefanghuanjing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房环境',
  `suoshujiudian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '所属酒店',
  `louceng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '楼层',
  `kefangjieshao` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '客房介绍',
  `clicktime` datetime NULL DEFAULT NULL COMMENT '最近点击时间',
  `clicknum` int NULL DEFAULT 0 COMMENT '点击次数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_kefangzhuangtai`(`kefangzhuangtai` ASC) USING BTREE,
  INDEX `idx_kefangleixing`(`kefangleixing` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1619750290378 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '客房信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kefangxinxi
-- ----------------------------
INSERT INTO `kefangxinxi` VALUES (1, '2026-03-03 13:36:52', 'A101', '豪华大床房', '1.8米大床', 'http://localhost:8080/springboot6alf1/upload/1772516556328.jpg', '45平方米', 388, '已预约', '全天可约', '已清扫', '海景/阳台', '海景假日酒店', '1层', '豪华大床房，配备独立卫浴、智能电视、高速WiFi', '2026-03-04 23:24:47', 163);
INSERT INTO `kefangxinxi` VALUES (2, '2026-03-03 13:36:52', 'A102', '豪华双床房', '1.5米双床', 'kefangxinxi_kefangtupian2.jpg', '50平方米', 428, '空闲', '全天可约', '已清扫', '城景/阳台', '海景假日酒店', '1层', '豪华双床房，适合家庭或好友出行', '2026-03-03 21:15:10', 125);
INSERT INTO `kefangxinxi` VALUES (3, '2026-03-03 13:36:52', 'A201', '行政套房', '2.0米特大床', 'kefangxinxi_kefangtupian3.jpg', '75平方米', 688, '空闲', '全天可约', '已清扫', '海景/独立客厅', '海景假日酒店', '2层', '行政套房，专为商务精英打造', '2026-03-03 21:48:25', 87);
INSERT INTO `kefangxinxi` VALUES (4, '2026-03-03 13:36:52', 'A202', '家庭套房', '1.8米+1.2米', 'kefangxinxi_kefangtupian4.jpg', '80平方米', 588, '空闲', '全天可约', '已清扫', '园景/儿童区', '海景假日酒店', '2层', '家庭套房，温馨舒适的亲子空间', '2026-03-03 22:58:59', 98);
INSERT INTO `kefangxinxi` VALUES (5, '2026-03-03 13:36:52', 'B101', '经济单人房', '1.5米单人床', 'kefangxinxi_kefangtupian5.jpg', '28平方米', 198, '空闲', '全天可约', '已清扫', '内景/简约', '海景假日酒店', '1层', '经济单人房，简约而不简单', '2026-03-03 13:36:52', 156);
INSERT INTO `kefangxinxi` VALUES (6, '2026-03-03 13:36:52', 'B102', '标准双人房', '1.5米双床', 'kefangxinxi_kefangtupian6.jpg', '35平方米', 268, '已预订', '需预约', '已清扫', '城景', '海景假日酒店', '1层', '标准双人房，性价比之选', '2026-03-03 21:48:22', 204);
INSERT INTO `kefangxinxi` VALUES (7, '2026-03-03 13:36:53', 'B201', '商务大床房', '1.8米大床', 'kefangxinxi_kefangtupian1.jpg', '40平方米', 328, '空闲', '全天可约', '已清扫', '城景/办公区', '海景假日酒店', '2层', '商务大床房，专为商旅人士设计', '2026-03-03 13:36:53', 118);
INSERT INTO `kefangxinxi` VALUES (8, '2026-03-03 13:36:53', 'C101', '总统套房', '2.2米帝王床', 'kefangxinxi_kefangtupian2.jpg', '150平方米', 1888, '空闲', '需预约', '已清扫', '360度全景/露台', '海景假日酒店', '顶层', '总统套房，至尊奢华体验', '2026-03-03 21:03:12', 50);

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `userid` bigint NOT NULL COMMENT '留言人id',
  `username` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '留言内容',
  `reply` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '回复内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772443338334 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '留言板' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (171, '2026-04-30 10:32:31', 1, '用户名1', '留言内容1', '回复内容1');
INSERT INTO `messages` VALUES (172, '2026-04-30 10:32:31', 2, '用户名2', '留言内容2', '回复内容2');
INSERT INTO `messages` VALUES (173, '2026-04-30 10:32:31', 3, '用户名3', '留言内容3', '回复内容3');
INSERT INTO `messages` VALUES (174, '2026-04-30 10:32:31', 4, '用户名4', '留言内容4', '回复内容4');
INSERT INTO `messages` VALUES (175, '2026-04-30 10:32:31', 5, '用户名5', '留言内容5', '回复内容5');
INSERT INTO `messages` VALUES (176, '2026-04-30 10:32:31', 6, '用户名6', '留言内容6', '回复内容6');
INSERT INTO `messages` VALUES (1619750407296, '2026-04-30 10:40:07', 1619750209173, '11', '发给的广泛地地方给对方', ' 恢复供货发给很反感很反感');
INSERT INTO `messages` VALUES (1772443338333, '2026-03-02 17:22:17', 1619750209173, '11', '1111', NULL);

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `title` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '标题',
  `introduction` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '简介',
  `picture` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '图片',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1619750325216 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '酒店资讯' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES (1, '2026-03-03 13:36:53', '春季特惠活动11', '预订享8折优惠', 'http://localhost:8080/springboot6alf1/upload/1772517485430.jpg', '<p>春暖花开，正是出游好时节！即日起预订任意房型，会员专享8折优惠！</p>');
INSERT INTO `news` VALUES (2, '2026-03-03 13:36:53', '新装修客房上线', '2层行政区域全面升级', 'kefangxinxi_kefangtupian2.jpg', '经过两个月的精心装修，2层行政区域已全新上线！');
INSERT INTO `news` VALUES (3, '2026-03-03 13:38:09', '会员积分兑换', '积分可兑换多种礼品', 'kefangxinxi_kefangtupian3.jpg', '会员积分兑换活动现已开启！');
INSERT INTO `news` VALUES (4, '2026-03-03 13:38:09', '酒店设施指南', '设施使用说明', 'kefangxinxi_kefangtupian4.jpg', '为方便您的入住，特整理酒店各项设施的使用说明。');

-- ----------------------------
-- Table structure for pinglun
-- ----------------------------
DROP TABLE IF EXISTS `pinglun`;
CREATE TABLE `pinglun`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `refid` bigint NULL DEFAULT NULL,
  `userid` bigint NULL DEFAULT NULL,
  `tablename` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `nickname` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `pingfen` int NULL DEFAULT 5,
  `dingdanbianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `reply` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `replytime` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_refid`(`refid` ASC) USING BTREE,
  INDEX `idx_userid`(`userid` ASC) USING BTREE,
  INDEX `idx_addtime`(`addtime` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772546659793 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pinglun
-- ----------------------------
INSERT INTO `pinglun` VALUES (1772546659792, '2026-03-03 22:04:19', NULL, 1, 'yonghu', 'zhangsan', NULL, 4, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for qiantairenyuan
-- ----------------------------
DROP TABLE IF EXISTS `qiantairenyuan`;
CREATE TABLE `qiantairenyuan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `qiantaizhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '前台账号',
  `mima` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `qiantaixingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '前台姓名',
  `xingbie` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '性别',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `ruzhishijian` date NULL DEFAULT NULL COMMENT '入职时间',
  `zhaopian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '照片',
  `zhuangtai` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '在职' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_qiantaizhanghao`(`qiantaizhanghao` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '前台人员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qiantairenyuan
-- ----------------------------
INSERT INTO `qiantairenyuan` VALUES (100, '2026-03-01 00:06:48', 'qiantai001', '123456', '李芳', '女', '13900001111', '2024-01-01', NULL, '在职');
INSERT INTO `qiantairenyuan` VALUES (101, '2026-03-01 00:06:48', 'qiantai002', '123456', '王敏', '女', '13900002222', '2024-03-15', NULL, '在职');
INSERT INTO `qiantairenyuan` VALUES (102, '2026-03-01 00:06:48', 'qiantai003', '123456', '张伟', '男', '13900003333', '2024-06-01', NULL, '在职');

-- ----------------------------
-- Table structure for qingjierenyuan
-- ----------------------------
DROP TABLE IF EXISTS `qingjierenyuan`;
CREATE TABLE `qingjierenyuan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `qingjiezhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '清洁账号',
  `mima` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `qingjiexingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '清洁姓名',
  `nianling` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '年龄',
  `xingbie` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '性别',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `zhaopian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '照片',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `qingjiezhanghao`(`qingjiezhanghao` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1619750230548 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清洁人员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qingjierenyuan
-- ----------------------------
INSERT INTO `qingjierenyuan` VALUES (32, '2026-04-30 10:32:31', '清洁人员2', '123456', '清洁姓名2', '年龄2', '男', '13823888882', 'http://localhost:8080/springboot6alf1/upload/qingjierenyuan_zhaopian2.jpg');
INSERT INTO `qingjierenyuan` VALUES (33, '2026-04-30 10:32:31', '清洁人员3', '123456', '清洁姓名3', '年龄3', '男', '13823888883', 'http://localhost:8080/springboot6alf1/upload/qingjierenyuan_zhaopian3.jpg');
INSERT INTO `qingjierenyuan` VALUES (34, '2026-04-30 10:32:31', '清洁人员4', '123456', '清洁姓名4', '年龄4', '男', '13823888884', 'http://localhost:8080/springboot6alf1/upload/qingjierenyuan_zhaopian4.jpg');
INSERT INTO `qingjierenyuan` VALUES (35, '2026-04-30 10:32:31', '清洁人员5', '123456', '清洁姓名5', '年龄5', '男', '13823888885', 'http://localhost:8080/springboot6alf1/upload/qingjierenyuan_zhaopian5.jpg');
INSERT INTO `qingjierenyuan` VALUES (36, '2026-04-30 10:32:31', '清洁人员6', '123456', '清洁姓名6', '年龄6', '男', '13823888886', 'http://localhost:8080/springboot6alf1/upload/qingjierenyuan_zhaopian6.jpg');
INSERT INTO `qingjierenyuan` VALUES (1619750230547, '2026-04-30 10:37:10', '33', '33', '电饭锅', '33', '男', '11122233333', 'http://localhost:8080/springboot6alf1/upload/1619750962831.jpg');

-- ----------------------------
-- Table structure for qingsaofangjian
-- ----------------------------
DROP TABLE IF EXISTS `qingsaofangjian`;
CREATE TABLE `qingsaofangjian`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `kefanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房号',
  `kefangleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房类型',
  `suoshujiudian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '所属酒店',
  `shifoudasao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '是否打扫',
  `dasaoshijian` datetime NULL DEFAULT NULL COMMENT '打扫时间',
  `qingjiezhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '清洁账号',
  `qingjiexingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '清洁姓名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772465512839 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清扫房间' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qingsaofangjian
-- ----------------------------
INSERT INTO `qingsaofangjian` VALUES (131, '2026-04-30 10:32:31', '客房号1', '客房类型1', '所属酒店1', '是', '2026-04-30 10:32:31', '清洁账号1', '清洁姓名1');
INSERT INTO `qingsaofangjian` VALUES (132, '2026-04-30 10:32:31', '客房号2', '客房类型2', '所属酒店2', '是', '2026-04-30 10:32:31', '清洁账号2', '清洁姓名2');
INSERT INTO `qingsaofangjian` VALUES (133, '2026-04-30 10:32:31', '客房号3', '客房类型3', '所属酒店3', '是', '2026-04-30 10:32:31', '清洁账号3', '清洁姓名3');
INSERT INTO `qingsaofangjian` VALUES (134, '2026-04-30 10:32:31', '客房号4', '客房类型4', '所属酒店4', '是', '2026-04-30 10:32:31', '清洁账号4', '清洁姓名4');
INSERT INTO `qingsaofangjian` VALUES (135, '2026-04-30 10:32:31', '客房号5', '客房类型5', '所属酒店5', '是', '2026-04-30 10:32:31', '清洁账号5', '清洁姓名5');
INSERT INTO `qingsaofangjian` VALUES (136, '2026-04-30 10:32:31', '客房号6', '客房类型6', '所属酒店6', '是', '2026-04-30 10:32:31', '清洁账号6', '清洁姓名6');
INSERT INTO `qingsaofangjian` VALUES (1619750993207, '2026-04-30 10:49:53', '333', '士大夫收到', '士大夫都是广东省房给对方地方', '是', '2026-04-30 10:48:07', '33', '电饭锅');
INSERT INTO `qingsaofangjian` VALUES (1772448443082, '2026-03-02 18:47:22', '客房号1', '客房类型1', '所属酒店1', NULL, '2026-03-02 18:47:21', '清洁人员4', '清洁姓名4');
INSERT INTO `qingsaofangjian` VALUES (1772448447526, '2026-03-02 18:47:26', '客房号1', '客房类型1', '所属酒店1', NULL, '2026-03-02 18:47:25', '清洁人员4', '清洁姓名4');
INSERT INTO `qingsaofangjian` VALUES (1772448587457, '2026-03-02 18:49:46', '客房号1', '客房类型1', '所属酒店1', NULL, '2026-03-02 18:49:45', '33', '电饭锅');
INSERT INTO `qingsaofangjian` VALUES (1772448593220, '2026-03-02 18:49:52', '客房号1', '客房类型1', '所属酒店1', '是', '2026-03-02 18:49:49', '33', '电饭锅');
INSERT INTO `qingsaofangjian` VALUES (1772448598710, '2026-03-02 18:49:58', '客房号1', '客房类型1', '所属酒店1', '是', '2026-03-02 18:49:56', '33', '电饭锅');
INSERT INTO `qingsaofangjian` VALUES (1772448604188, '2026-03-02 18:50:03', '客房号1', '客房类型1', '所属酒店1', '是', '2026-03-02 18:50:01', '33', '电饭锅');
INSERT INTO `qingsaofangjian` VALUES (1772448611480, '2026-03-02 18:50:11', '客房号2', '客房类型2', '所属酒店2', '是', '2026-03-02 18:50:08', '33', '电饭锅');
INSERT INTO `qingsaofangjian` VALUES (1772448620336, '2026-03-02 18:50:19', '客房号1', '客房类型1', '所属酒店1', '是', '2026-03-02 18:50:17', '33', '电饭锅');
INSERT INTO `qingsaofangjian` VALUES (1772465493042, '2026-03-02 23:31:32', '333', '士大夫收到', '士大夫都是广东省房给对方地方', '是', '2026-03-02 23:31:31', '33', '电饭锅');
INSERT INTO `qingsaofangjian` VALUES (1772465503309, '2026-03-02 23:31:43', '客房号1', '客房类型1', '所属酒店1', '是', '2026-03-02 23:31:43', '33', '电饭锅');
INSERT INTO `qingsaofangjian` VALUES (1772465506482, '2026-03-02 23:31:46', '客房号2', '客房类型2', '所属酒店2', '是', '2026-03-02 23:31:46', '33', '电饭锅');
INSERT INTO `qingsaofangjian` VALUES (1772465509130, '2026-03-02 23:31:49', '客房号3', '客房类型3', '所属酒店3', '是', '2026-03-02 23:31:49', '33', '电饭锅');
INSERT INTO `qingsaofangjian` VALUES (1772465512838, '2026-03-02 23:31:52', '客房号4', '客房类型4', '所属酒店4', '是', '2026-03-02 23:31:52', '33', '电饭锅');

-- ----------------------------
-- Table structure for storeup
-- ----------------------------
DROP TABLE IF EXISTS `storeup`;
CREATE TABLE `storeup`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `userid` bigint NOT NULL COMMENT '用户id',
  `refid` bigint NULL DEFAULT NULL COMMENT '收藏id',
  `tablename` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '表名',
  `name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收藏名称',
  `picture` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收藏图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772554249813 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of storeup
-- ----------------------------
INSERT INTO `storeup` VALUES (1772554249812, '2026-03-04 00:10:49', 1, 1, 'kefangxinxi', '豪华大床房 A101', 'http://localhost:8080/springboot6alf1/upload/1772516556328.jpg');

-- ----------------------------
-- Table structure for token
-- ----------------------------
DROP TABLE IF EXISTS `token`;
CREATE TABLE `token`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userid` bigint NOT NULL COMMENT '用户id',
  `username` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户名',
  `tablename` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '表名',
  `role` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '角色',
  `token` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `expiratedtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'token表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of token
-- ----------------------------
INSERT INTO `token` VALUES (1, 1, 'abo', 'users', '管理员', 'fifu2batnq82akfmyhcsckncsr3ptx88', '2026-04-30 10:37:17', '2026-03-04 00:41:16');
INSERT INTO `token` VALUES (2, 1619750209173, '11', 'yonghu', '用户', 'hg9qwsdvhwn8x8jjlby3rswyz27oq2vj', '2026-04-30 10:38:52', '2026-03-03 14:02:50');
INSERT INTO `token` VALUES (3, 1619750220663, '22', 'huiyuan', '会员', 'tv0fjmmqd9dh1lxgny435wt6jivhvd51', '2026-04-30 10:44:44', '2026-03-02 19:48:23');
INSERT INTO `token` VALUES (4, 1619750230547, '33', 'qingjierenyuan', '清洁人员', '2sosm0rlalx4wn3xsx1ffpcp6cn2cj2h', '2026-04-30 10:49:17', '2026-03-03 00:31:09');
INSERT INTO `token` VALUES (5, 32, '清洁人员2', 'qingjierenyuan', '清洁人员', '1bczqe656npuy74ukot73vnxc5akx4en', '2026-03-02 18:46:55', '2026-03-02 19:46:55');
INSERT INTO `token` VALUES (6, 33, '清洁人员3', 'qingjierenyuan', '清洁人员', '907skfwh4b90uue8ndmgfa9uvovtbemn', '2026-03-02 18:47:05', '2026-03-02 19:47:06');
INSERT INTO `token` VALUES (7, 34, '清洁人员4', 'qingjierenyuan', '清洁人员', 'hbdtx6rvb28dblj1fzasnh1ze9vf6s01', '2026-03-02 18:47:12', '2026-03-02 19:47:13');
INSERT INTO `token` VALUES (8, 1, 'zhangsan', 'yonghu', '用户', 'cck4iwbv6atgozg2br5ezel2cmymgkkx', '2026-03-03 13:49:11', '2026-03-04 00:56:26');
INSERT INTO `token` VALUES (9, 2, 'lisi', 'yonghu', '用户', 'm4uapub4gav8ojx6efamezwgb0g0i1th', '2026-03-03 17:23:53', '2026-03-05 16:22:53');

-- ----------------------------
-- Table structure for tongzhi
-- ----------------------------
DROP TABLE IF EXISTS `tongzhi`;
CREATE TABLE `tongzhi`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `userid` bigint NOT NULL COMMENT '接收用户ID',
  `tablename` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户表名',
  `title` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '通知标题',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '通知内容',
  `type` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '通知类型',
  `refid` bigint NULL DEFAULT NULL COMMENT '关联ID',
  `reftable` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '关联表名',
  `isread` int NULL DEFAULT 0 COMMENT '是否已读(0未读1已读)',
  `readtime` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_userid`(`userid` ASC) USING BTREE,
  INDEX `idx_tablename`(`tablename` ASC) USING BTREE,
  INDEX `idx_isread`(`isread` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_addtime`(`addtime` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772474361209 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tongzhi
-- ----------------------------
INSERT INTO `tongzhi` VALUES (1772474273660, '2026-03-03 01:57:53', 1619750209173, 'yonghu', '预约审核通过', '您的预约已审核通过，请尽快完成支付。', '预约审核', 1772469014238, 'yonghuyuyue', 0, NULL);
INSERT INTO `tongzhi` VALUES (1772474313188, '2026-03-03 01:58:33', 1619750209173, 'yonghu', '预约审核通过', '您的预约已审核通过，请尽快完成支付。', '预约审核', 1772469014238, 'yonghuyuyue', 0, NULL);
INSERT INTO `tongzhi` VALUES (1772474361208, '2026-03-03 01:59:21', 1619750209173, 'yonghu', '预约审核通过', '您的预约已审核通过，请尽快完成支付。', '预约审核', 1772469014238, 'yonghuyuyue', 0, NULL);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `role` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '管理员' COMMENT '角色',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'abo', 'abo', '管理员', '2026-04-30 10:32:31');

-- ----------------------------
-- Table structure for yonghu
-- ----------------------------
DROP TABLE IF EXISTS `yonghu`;
CREATE TABLE `yonghu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `zhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '账号',
  `mima` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `xingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `nianling` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '年龄',
  `xingbie` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '性别',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `shenfenzheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `zhaopian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '照片',
  `yue` double NULL DEFAULT 0 COMMENT '璐︽埛浣欓',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `zhanghao`(`zhanghao` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1619750209174 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yonghu
-- ----------------------------
INSERT INTO `yonghu` VALUES (1, '2026-03-03 13:36:52', 'zhangsan', '123456', '张三', '28', '男', '13599898989', '440101199501011234', '1772533419400.png', 0);
INSERT INTO `yonghu` VALUES (2, '2026-03-03 13:36:52', 'lisi', '123456', '李四', '32', '女', '13800138002', '440101199002025678', 'yonghu_zhaopian2.jpg', 1612);
INSERT INTO `yonghu` VALUES (3, '2026-03-03 13:36:52', 'test', '123456', '测试用户', '26', '男', '13800138005', '440101199705057890', 'yonghu_zhaopian3.jpg', 0);

-- ----------------------------
-- Table structure for yonghuquxiao
-- ----------------------------
DROP TABLE IF EXISTS `yonghuquxiao`;
CREATE TABLE `yonghuquxiao`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yuyuebianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '预约编号',
  `kefanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房号',
  `shifouquxiao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '是否取消',
  `jiage` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '价格',
  `tianshu` int NULL DEFAULT NULL COMMENT '天数',
  `zongjia` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '总价',
  `quxiaoyuanyin` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '取消原因',
  `quxiaoshijian` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `zhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `xingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `shenfenzheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `sfsh` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '否' COMMENT '是否审核',
  `shhf` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '审核回复',
  `ispay` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '未支付' COMMENT '是否支付',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772445177667 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户取消' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yonghuquxiao
-- ----------------------------
INSERT INTO `yonghuquxiao` VALUES (71, '2026-04-30 10:32:31', '预约编号1', '客房号1', '是', '价格1', 1, '总价1', '取消原因1', '2026-04-30 10:32:31', '账号1', '姓名1', '手机1', '身份证1', '是', '', '未支付');
INSERT INTO `yonghuquxiao` VALUES (72, '2026-04-30 10:32:31', '预约编号2', '客房号2', '是', '价格2', 2, '总价2', '取消原因2', '2026-04-30 10:32:31', '账号2', '姓名2', '手机2', '身份证2', '是', '', '未支付');
INSERT INTO `yonghuquxiao` VALUES (73, '2026-04-30 10:32:31', '预约编号3', '客房号3', '是', '价格3', 3, '总价3', '取消原因3', '2026-04-30 10:32:31', '账号3', '姓名3', '手机3', '身份证3', '是', '', '未支付');
INSERT INTO `yonghuquxiao` VALUES (74, '2026-04-30 10:32:31', '预约编号4', '客房号4', '是', '价格4', 4, '总价4', '取消原因4', '2026-04-30 10:32:31', '账号4', '姓名4', '手机4', '身份证4', '是', '', '未支付');
INSERT INTO `yonghuquxiao` VALUES (75, '2026-04-30 10:32:31', '预约编号5', '客房号5', '是', '价格5', 5, '总价5', '取消原因5', '2026-04-30 10:32:31', '账号5', '姓名5', '手机5', '身份证5', '是', '', '未支付');
INSERT INTO `yonghuquxiao` VALUES (76, '2026-04-30 10:32:31', '预约编号6', '客房号6', '是', '价格6', 6, '总价6', '取消原因6', '2026-04-30 10:32:31', '账号6', '姓名6', '手机6', '身份证6', '是', '', '未支付');
INSERT INTO `yonghuquxiao` VALUES (1772445151043, '2026-03-02 17:52:30', '20214301038790860824', '333', NULL, '333', 22, '7326', NULL, '2026-03-02 17:52:28', '11', '受访人', '11111111121', '441421199001125846', '否', NULL, '未支付');
INSERT INTO `yonghuquxiao` VALUES (1772445158871, '2026-03-02 17:52:38', '20214301038790860824', '333', NULL, '333', 22, '7326', NULL, '2026-03-02 17:52:36', '11', '受访人', '11111111121', '441421199001125846', '否', NULL, '未支付');
INSERT INTO `yonghuquxiao` VALUES (1772445177666, '2026-03-02 17:52:57', '20214301038790860824', '333', NULL, '333', 22, '7326', NULL, '2026-03-02 17:52:56', '11', '受访人', '11111111121', '441421199001125846', '否', NULL, '未支付');

-- ----------------------------
-- Table structure for yonghuruzhu
-- ----------------------------
DROP TABLE IF EXISTS `yonghuruzhu`;
CREATE TABLE `yonghuruzhu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dingdanbianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `kefanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房号',
  `kefangleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房类型',
  `suoshujiudian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '所属酒店',
  `zhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `xingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `shenfenzheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `kefangzhuangtai` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房状态',
  `ruzhuyajin` float NULL DEFAULT NULL COMMENT '入住押金',
  `zhifufangshi` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付方式',
  `ruzhushijian` datetime NULL DEFAULT NULL COMMENT '入住时间',
  `ispay` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '未支付' COMMENT '是否支付',
  `jiage` double NULL DEFAULT NULL COMMENT '浠锋牸',
  `yulifangshijian` datetime NULL DEFAULT NULL COMMENT '棰勭鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `dingdanbianhao`(`dingdanbianhao` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772474296263 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户入住' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yonghuruzhu
-- ----------------------------
INSERT INTO `yonghuruzhu` VALUES (91, '2026-04-30 10:32:31', '订单编号1', '客房号1', '客房类型1', '所属酒店1', '账号1', '姓名1', '身份证1', '手机1', '已入住', 1, '支付方式1', '2026-04-30 10:32:31', '未支付', NULL, NULL);
INSERT INTO `yonghuruzhu` VALUES (92, '2026-04-30 10:32:31', '订单编号2', '客房号2', '客房类型2', '所属酒店2', '账号2', '姓名2', '身份证2', '手机2', '已入住', 2, '支付方式2', '2026-04-30 10:32:31', '未支付', NULL, NULL);
INSERT INTO `yonghuruzhu` VALUES (93, '2026-04-30 10:32:31', '订单编号3', '客房号3', '客房类型3', '所属酒店3', '账号3', '姓名3', '身份证3', '手机3', '已入住', 3, '支付方式3', '2026-04-30 10:32:31', '未支付', NULL, NULL);
INSERT INTO `yonghuruzhu` VALUES (94, '2026-04-30 10:32:31', '订单编号4', '客房号4', '客房类型4', '所属酒店4', '账号4', '姓名4', '身份证4', '手机4', '已入住', 4, '支付方式4', '2026-04-30 10:32:31', '未支付', NULL, NULL);
INSERT INTO `yonghuruzhu` VALUES (95, '2026-04-30 10:32:31', '订单编号5', '客房号5', '客房类型5', '所属酒店5', '账号5', '姓名5', '身份证5', '手机5', '已入住', 5, '支付方式5', '2026-04-30 10:32:31', '未支付', NULL, NULL);
INSERT INTO `yonghuruzhu` VALUES (96, '2026-04-30 10:32:31', '订单编号6', '客房号6', '客房类型6', '所属酒店6', '账号6', '姓名6', '身份证6', '手机6', '已入住', 6, '支付方式6', '2026-04-30 10:32:31', '未支付', NULL, NULL);
INSERT INTO `yonghuruzhu` VALUES (1619750529756, '2026-04-30 10:42:09', '1619750415179', '333', '士大夫收到', '士大夫都是广东省房给对方地方', '11', '受访人', '441421199001125846', '11111111121', '已入住', 1111, '发双方各地方地方地方', '2026-04-30 10:40:15', '已支付', NULL, NULL);
INSERT INTO `yonghuruzhu` VALUES (1772474296262, '2026-03-03 01:58:16', '2026330301167375263', '客房号1', '客房类型1', '所属酒店1', '11', '受访人', '441421199001125846', '11111111121', NULL, NULL, NULL, '2026-03-03 01:58:16', '已支付', 1, '2026-03-04 12:00:01');

-- ----------------------------
-- Table structure for yonghutuifang
-- ----------------------------
DROP TABLE IF EXISTS `yonghutuifang`;
CREATE TABLE `yonghutuifang`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dingdanbianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `kefanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房号',
  `kefangleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房类型',
  `suoshujiudian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '所属酒店',
  `zhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `xingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `shenfenzheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `ruzhuyajin` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '入住押金',
  `tuifangshijian` datetime NULL DEFAULT NULL COMMENT '退房时间',
  `ispay` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '未支付' COMMENT '是否支付',
  `jiage` double NULL DEFAULT NULL COMMENT '鎴块棿浠锋牸',
  `chaoshifeiyong` double NULL DEFAULT NULL COMMENT '瓒呮椂璐圭敤',
  `zongjine` double NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772446157606 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户退房' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yonghutuifang
-- ----------------------------
INSERT INTO `yonghutuifang` VALUES (111, '2026-04-30 10:32:31', '订单编号1', '客房号1', '客房类型1', '所属酒店1', '账号1', '姓名1', '身份证1', '手机1', '入住押金1', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `yonghutuifang` VALUES (112, '2026-04-30 10:32:31', '订单编号2', '客房号2', '客房类型2', '所属酒店2', '账号2', '姓名2', '身份证2', '手机2', '入住押金2', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `yonghutuifang` VALUES (113, '2026-04-30 10:32:31', '订单编号3', '客房号3', '客房类型3', '所属酒店3', '账号3', '姓名3', '身份证3', '手机3', '入住押金3', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `yonghutuifang` VALUES (114, '2026-04-30 10:32:31', '订单编号4', '客房号4', '客房类型4', '所属酒店4', '账号4', '姓名4', '身份证4', '手机4', '入住押金4', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `yonghutuifang` VALUES (115, '2026-04-30 10:32:31', '订单编号5', '客房号5', '客房类型5', '所属酒店5', '账号5', '姓名5', '身份证5', '手机5', '入住押金5', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `yonghutuifang` VALUES (116, '2026-04-30 10:32:31', '订单编号6', '客房号6', '客房类型6', '所属酒店6', '账号6', '姓名6', '身份证6', '手机6', '入住押金6', '2026-04-30 10:32:31', '未支付', NULL, NULL, NULL);
INSERT INTO `yonghutuifang` VALUES (1619750577287, '2026-04-30 10:42:56', '1619750415179', '333', '士大夫收到', '士大夫都是广东省房给对方地方', '11', '受访人', '441421199001125846', '11111111121', '1111', '2026-05-26 10:41:08', '已支付', NULL, NULL, NULL);
INSERT INTO `yonghutuifang` VALUES (1772446135105, '2026-03-02 18:08:54', '1619750415179', '333', '士大夫收到', '士大夫都是广东省房给对方地方', '11', '受访人', '441421199001125846', '11111111121', '1111', '2026-03-02 18:08:53', '未支付', NULL, NULL, NULL);
INSERT INTO `yonghutuifang` VALUES (1772446142289, '2026-03-02 18:09:01', '订单编号1', '客房号1', '客房类型1', '所属酒店1', '账号1', '姓名1', '身份证1', '手机1', '1', '2026-03-02 18:09:00', '未支付', NULL, NULL, NULL);
INSERT INTO `yonghutuifang` VALUES (1772446157605, '2026-03-02 18:09:17', '订单编号1', '客房号1', '客房类型1', '所属酒店1', '账号1', '姓名1', '身份证1', '手机1', '1', '2026-03-02 18:09:15', '未支付', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for yonghuyuyue
-- ----------------------------
DROP TABLE IF EXISTS `yonghuyuyue`;
CREATE TABLE `yonghuyuyue`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yuyuebianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '预约编号',
  `kefanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '客房号',
  `ruzhushijian` datetime NULL DEFAULT NULL COMMENT '入住时间',
  `jiage` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '价格',
  `tianshu` int NULL DEFAULT NULL COMMENT '天数',
  `zongjia` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '总价',
  `yuyueshijian` datetime NULL DEFAULT NULL COMMENT '预约时间',
  `zhanghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `xingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `shouji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机',
  `shenfenzheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `sfsh` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '否' COMMENT '是否审核',
  `shhf` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '审核回复',
  `ispay` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '未支付' COMMENT '是否支付',
  `yuyuezhuangtai` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'pending' COMMENT 'pending/approved/rejected/paid/checkedin/cancelled',
  `ispingjia` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'no',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `yuyuebianhao`(`yuyuebianhao` ASC) USING BTREE,
  INDEX `idx_zhanghao`(`zhanghao` ASC) USING BTREE,
  INDEX `idx_kefanghao`(`kefanghao` ASC) USING BTREE,
  INDEX `idx_sfsh`(`sfsh` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1772637888383 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户预约' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yonghuyuyue
-- ----------------------------
INSERT INTO `yonghuyuyue` VALUES (1772545032456, '2026-03-03 21:37:11', 'YY1772545031741136', 'A101', '2026-03-03 14:00:00', '388', 1, '388', '2026-03-03 21:37:12', 'zhangsan', '张三', '13800138001', '440101199501011234', '待审核', NULL, '已退款', '已退款', '否');
INSERT INTO `yonghuyuyue` VALUES (1772549944889, '2026-03-03 22:59:03', 'YY177254994391015', 'A202', '2026-03-03 14:00:00', '588', 2, '1176', '2026-03-03 22:59:04', 'zhangsan', '张三', '13800138001', '440101199501011234', '待审核', NULL, '已支付', '申请退款', 'no');
INSERT INTO `yonghuyuyue` VALUES (1772637888382, '2026-03-04 23:24:48', 'YY1772637888057759', 'A101', '2026-03-04 14:00:00', '388', 1, '388', '2026-03-04 23:24:48', 'lisi', '李四', '13800138002', '440101199002025678', '是', NULL, '已支付', '待入住', 'no');

SET FOREIGN_KEY_CHECKS = 1;

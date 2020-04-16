/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.1.126-13307
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : 192.168.1.126:13307
 Source Schema         : keda_sys

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 16/04/2020 15:31:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gateway_dynamic_route
-- ----------------------------
DROP TABLE IF EXISTS `gateway_dynamic_route`;
CREATE TABLE `gateway_dynamic_route`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键自增Id',
  `route_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '路由Id',
  `uri` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '路由规则转发的uri',
  `order` int(11) NOT NULL DEFAULT 0 COMMENT '路由的执行顺序',
  `predicate_json` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '路由断言集合配置json串',
  `filter_json` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由过滤器集合配置json串',
  `enable` bit(1) NOT NULL COMMENT '状态：0,\"不可用\")；1,\"可用\")',
  `create_time` timestamp(0) NOT NULL COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL COMMENT '修改时间',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `last_user_id` bigint(20) DEFAULT NULL COMMENT '最后操作员的Id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `route_id`(`route_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 397112177056837634 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'gateway动态路由配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gateway_filter_url
-- ----------------------------
DROP TABLE IF EXISTS `gateway_filter_url`;
CREATE TABLE `gateway_filter_url`  (
  `id` bigint(20) NOT NULL,
  `filter_url_type` smallint(1) DEFAULT NULL COMMENT '过滤的url类型，1:直接跳过的前缀，2：跳过的前缀的前提、3：在前提满足后，跳过的后缀',
  `filter_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'url地址',
  `create_time` timestamp(3) DEFAULT NULL,
  `update_time` timestamp(3) DEFAULT NULL,
  `create_user_id` bigint(20) DEFAULT NULL,
  `last_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

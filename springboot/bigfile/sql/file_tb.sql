/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50556
Source Host           : localhost:3306
Source Database       : file_tb

Target Server Type    : MYSQL
Target Server Version : 50556
File Encoding         : 65001

Date: 2021-01-23 22:45:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `file_tb`
-- ----------------------------
DROP TABLE IF EXISTS `file_tb`;
CREATE TABLE `file_tb` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_key` varchar(255) DEFAULT NULL COMMENT '文件唯一标识',
  `f_index` bigint(20) DEFAULT NULL COMMENT '第几个分片',
  `f_total` int(11) DEFAULT NULL COMMENT '共有几个分片',
  `f_name` varchar(255) DEFAULT NULL COMMENT '文件名称，后面可以返回出去',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of file_tb
-- ----------------------------
INSERT INTO `file_tb` VALUES ('56', '8ac9a031fb4032565eb12d98f67d742c', '100', '100', '精通mysql调优大师班六.mp4');
INSERT INTO `file_tb` VALUES ('57', '478d2eb02a9b875e6f5d52d8d6e534a9', '100', '100', '精通mysql调优大师班六.mp4');
INSERT INTO `file_tb` VALUES ('58', '18c18f8991432126b4db4bc0b4b0aa53', '100', '100', '精通mysql调优大师班六.mp4');
INSERT INTO `file_tb` VALUES ('59', '8ea2dd62abacdcfdd0b8fab4569343cf', '100', '100', '精通mysql调优大师班六.mp4');

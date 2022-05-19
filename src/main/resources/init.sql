-- 使用容器时拷贝到 /opt/homebrew/var/mysql/init.sql
CREATE DATABASE IF NOT EXISTS trace;
    
USE trace;

DROP TABLE IF EXISTS `asset`;

CREATE TABLE `asset`
(
    `id`            int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `assetId`       varchar(100) NOT NULL,
    `assetCode`     varchar(100) NOT NULL,
    `assetName`     varchar(100) NOT NULL,
    `baseAssetCode` varchar(100) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
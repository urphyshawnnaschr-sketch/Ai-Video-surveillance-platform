`camera-ai-next`.wvp_device definition

CREATE TABLE `wvp_device` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `device_id` varchar(50) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `manufacturer` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `firmware` varchar(255) DEFAULT NULL,
  `transport` varchar(50) DEFAULT NULL,
  `stream_mode` varchar(50) DEFAULT NULL,
  `on_line` tinyint(1) DEFAULT '0',
  `register_time` varchar(50) DEFAULT NULL,
  `keepalive_time` varchar(50) DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `create_time` varchar(50) DEFAULT NULL,
  `update_time` varchar(50) DEFAULT NULL,
  `port` int DEFAULT NULL,
  `expires` int DEFAULT NULL,
  `subscribe_cycle_for_catalog` int DEFAULT '0',
  `subscribe_cycle_for_mobile_position` int DEFAULT '0',
  `mobile_position_submission_interval` int DEFAULT '5',
  `subscribe_cycle_for_alarm` int DEFAULT '0',
  `host_address` varchar(50) DEFAULT NULL,
  `charset` varchar(50) DEFAULT NULL,
  `ssrc_check` tinyint(1) DEFAULT '0',
  `geo_coord_sys` varchar(50) DEFAULT NULL,
  `media_server_id` varchar(50) DEFAULT NULL,
  `custom_name` varchar(255) DEFAULT NULL,
  `sdp_ip` varchar(50) DEFAULT NULL,
  `local_ip` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `as_message_channel` tinyint(1) DEFAULT '0',
  `keepalive_interval_time` int DEFAULT NULL,
  `broadcast_push_after_ack` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GB Standard Device table';

`camera-ai-next`.wvp_device_channel definition

CREATE TABLE `wvp_device_channel` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `channel_id` varchar(50) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `custom_name` varchar(255) DEFAULT NULL,
  `manufacture` varchar(50) DEFAULT NULL,
  `model` varchar(50) DEFAULT NULL,
  `owner` varchar(50) DEFAULT NULL,
  `civil_code` varchar(50) DEFAULT NULL,
  `block` varchar(50) DEFAULT NULL,
  `address` varchar(50) DEFAULT NULL,
  `parent_id` varchar(50) DEFAULT NULL,
  `safety_way` int DEFAULT NULL,
  `register_way` int DEFAULT NULL,
  `cert_num` varchar(50) DEFAULT NULL,
  `certifiable` int DEFAULT NULL,
  `err_code` int DEFAULT NULL,
  `end_time` varchar(50) DEFAULT NULL,
  `secrecy` varchar(50) DEFAULT NULL,
  `ip_address` varchar(50) DEFAULT NULL,
  `port` int DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `ptz_type` int DEFAULT NULL,
  `custom_ptz_type` int DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0',
  `longitude` double DEFAULT NULL,
  `custom_longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `custom_latitude` double DEFAULT NULL,
  `stream_id` varchar(255) DEFAULT NULL,
  `device_id` varchar(50) NOT NULL,
  `parental` varchar(50) DEFAULT NULL,
  `has_audio` tinyint(1) DEFAULT '0',
  `create_time` varchar(50) NOT NULL,
  `update_time` varchar(50) NOT NULL,
  `sub_count` int DEFAULT NULL,
  `longitude_gcj02` double DEFAULT NULL,
  `latitude_gcj02` double DEFAULT NULL,
  `longitude_wgs84` double DEFAULT NULL,
  `latitude_wgs84` double DEFAULT NULL,
  `business_group_id` varchar(50) DEFAULT NULL,
  `gps_time` varchar(50) DEFAULT NULL,
  `stream_identification` varchar(50) DEFAULT NULL,
  `camera_id` bigint(20) DEFAULT 0,
  `location_id` bigint DEFAULT 0,
  `err_msg` varchar(200),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GB Standard Channel table';

`camera-ai-next`.wvp_device_alarm definition

CREATE TABLE `wvp_device_alarm` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `device_id` varchar(50) NOT NULL,
  `channel_id` varchar(50) NOT NULL,
  `alarm_priority` varchar(50) DEFAULT NULL,
  `alarm_method` varchar(50) DEFAULT NULL,
  `alarm_time` varchar(50) DEFAULT NULL,
  `alarm_description` varchar(255) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `alarm_type` varchar(50) DEFAULT NULL,
  `create_time` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GB Standard Alarm table';

`camera-ai-next`.wvp_device_mobile_position definition

CREATE TABLE `wvp_device_mobile_position` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `device_id` varchar(50) NOT NULL,
  `channel_id` varchar(50) NOT NULL,
  `device_name` varchar(255) DEFAULT NULL,
  `time` varchar(50) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `altitude` double DEFAULT NULL,
  `speed` double DEFAULT NULL,
  `direction` double DEFAULT NULL,
  `report_source` varchar(50) DEFAULT NULL,
  `longitude_gcj02` double DEFAULT NULL,
  `latitude_gcj02` double DEFAULT NULL,
  `longitude_wgs84` double DEFAULT NULL,
  `latitude_wgs84` double DEFAULT NULL,
  `create_time` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GB Standard Channel Bit set table';

`camera-ai-next`.wvp_gb_stream definition

CREATE TABLE `wvp_gb_stream` (
  `gb_stream_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `app` varchar(255) NOT NULL,
  `stream` varchar(255) NOT NULL,
  `gb_id` varchar(50) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `stream_type` varchar(50) DEFAULT NULL,
  `media_server_id` varchar(50) DEFAULT NULL,
  `create_time` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`gb_stream_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GB Standard Stream table';

`camera-ai-next`.wvp_media_server definition

CREATE TABLE `wvp_media_server` (
  `id` varchar(255) NOT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `hook_ip` varchar(50) DEFAULT NULL,
  `sdp_ip` varchar(50) DEFAULT NULL,
  `stream_ip` varchar(50) DEFAULT NULL,
  `http_port` int DEFAULT NULL,
  `http_ssl_port` int DEFAULT NULL,
  `rtmp_port` int DEFAULT NULL,
  `rtmp_ssl_port` int DEFAULT NULL,
  `rtp_proxy_port` int DEFAULT NULL,
  `rtsp_port` int DEFAULT NULL,
  `rtsp_ssl_port` int DEFAULT NULL,
  `flv_port` int DEFAULT NULL,
  `flv_ssl_port` int DEFAULT NULL,
  `ws_flv_port` int DEFAULT NULL,
  `ws_flv_ssl_port` int DEFAULT NULL,
  `auto_config` tinyint(1) DEFAULT '0',
  `secret` varchar(50) DEFAULT NULL,
  `type` varchar(50) DEFAULT 'zlm',
  `rtp_enable` tinyint(1) DEFAULT '0',
  `rtp_port_range` varchar(50) DEFAULT NULL,
  `send_rtp_port_range` varchar(50) DEFAULT NULL,
  `record_assist_port` int DEFAULT NULL,
  `default_server` tinyint(1) DEFAULT '0',
  `create_time` varchar(50) DEFAULT NULL,
  `update_time` varchar(50) DEFAULT NULL,
  `hook_alive_interval` int DEFAULT NULL,
  `record_path` varchar(255) DEFAULT NULL,
  `record_day` int DEFAULT '7',
  `transcode_suffix` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Stream Media Service Node table';

`camera-ai-next`.wvp_platform definition

CREATE TABLE `wvp_platform` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `enable` tinyint(1) DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `server_gb_id` varchar(50) DEFAULT NULL,
  `server_gb_domain` varchar(50) DEFAULT NULL,
  `server_ip` varchar(50) DEFAULT NULL,
  `server_port` int DEFAULT NULL,
  `device_gb_id` varchar(50) DEFAULT NULL,
  `device_ip` varchar(50) DEFAULT NULL,
  `device_port` varchar(50) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `expires` varchar(50) DEFAULT NULL,
  `keep_timeout` varchar(50) DEFAULT NULL,
  `transport` varchar(50) DEFAULT NULL,
  `character_set` varchar(50) DEFAULT NULL,
  `catalog_id` varchar(50) DEFAULT NULL,
  `ptz` tinyint(1) DEFAULT '0',
  `rtcp` tinyint(1) DEFAULT '0',
  `status` tinyint(1) DEFAULT '0',
  `start_offline_push` tinyint(1) DEFAULT '0',
  `administrative_division` varchar(50) DEFAULT NULL,
  `catalog_group` int DEFAULT NULL,
  `create_time` varchar(50) DEFAULT NULL,
  `update_time` varchar(50) DEFAULT NULL,
  `as_message_channel` tinyint(1) DEFAULT '0',
  `auto_push_channel` tinyint(1) DEFAULT '0',
  `send_stream_ip` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='up Level GB Standard table';

`camera-ai-next`.wvp_platform_catalog definition

CREATE TABLE `wvp_platform_catalog` (
  `id` varchar(50) DEFAULT NULL,
  `platform_id` varchar(50) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent_id` varchar(50) DEFAULT NULL,
  `civil_code` varchar(50) DEFAULT NULL,
  `business_group_id` varchar(50) DEFAULT NULL,
  UNIQUE KEY `uk_platform_catalog_id_platform_id` (`id`,`platform_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GB Standard Directory table';

`camera-ai-next`.wvp_platform_gb_channel definition

CREATE TABLE `wvp_platform_gb_channel` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `platform_id` varchar(50) DEFAULT NULL,
  `catalog_id` varchar(50) DEFAULT NULL,
  `device_channel_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='up Level GB Standard Channel table';

`camera-ai-next`.wvp_platform_gb_stream definition

CREATE TABLE `wvp_platform_gb_stream` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `platform_id` varchar(50) DEFAULT NULL,
  `catalog_id` varchar(50) DEFAULT NULL,
  `gb_stream_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='up Level GB Standard Stream table';

`camera-ai-next`.wvp_resources_tree definition

CREATE TABLE `wvp_resources_tree` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `is_catalog` tinyint(1) DEFAULT '1',
  `device_channel_id` int DEFAULT NULL,
  `gb_stream_id` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parentId` int DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Resource result structure Tree table';

`camera-ai-next`.wvp_stream_proxy definition

CREATE TABLE `wvp_stream_proxy` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(50) DEFAULT NULL,
  `app` varchar(255) DEFAULT NULL,
  `stream` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `src_url` varchar(255) DEFAULT NULL,
  `dst_url` varchar(255) DEFAULT NULL,
  `timeout_ms` int DEFAULT NULL,
  `ffmpeg_cmd_key` varchar(255) DEFAULT NULL,
  `rtp_type` varchar(50) DEFAULT NULL,
  `media_server_id` varchar(50) DEFAULT NULL,
  `enable_audio` tinyint(1) DEFAULT '0',
  `enable_mp4` tinyint(1) DEFAULT '0',
  `enable` tinyint(1) DEFAULT '0',
  `status` tinyint(1) DEFAULT NULL,
  `enable_remove_none_reader` tinyint(1) DEFAULT '0',
  `create_time` varchar(50) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `update_time` varchar(50) DEFAULT NULL,
  `stream_key` varchar(255) DEFAULT NULL,
  `enable_disable_none_reader` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Stream Forward table';

`camera-ai-next`.wvp_stream_push definition

CREATE TABLE `wvp_stream_push` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `app` varchar(255) DEFAULT NULL,
  `stream` varchar(255) DEFAULT NULL,
  `total_reader_count` varchar(50) DEFAULT NULL,
  `origin_type` int DEFAULT NULL,
  `origin_type_str` varchar(50) DEFAULT NULL,
  `create_time` varchar(50) DEFAULT NULL,
  `alive_second` int DEFAULT NULL,
  `media_server_id` varchar(50) DEFAULT NULL,
  `server_id` varchar(50) DEFAULT NULL,
  `push_time` varchar(50) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0',
  `update_time` varchar(50) DEFAULT NULL,
  `push_ing` tinyint(1) DEFAULT '0',
  `self` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Pull Stream table';

`camera-ai-next`.wvp_cloud_record definition

CREATE TABLE `wvp_cloud_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `app` varchar(255) DEFAULT NULL,
  `stream` varchar(255) DEFAULT NULL,
  `call_id` varchar(255) DEFAULT NULL,
  `start_time` bigint DEFAULT NULL,
  `end_time` bigint DEFAULT NULL,
  `media_server_id` varchar(50) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `folder` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `collect` tinyint(1) DEFAULT '0',
  `file_size` bigint DEFAULT NULL,
  `time_len` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GB Standard Recording table';

Add Field create_time, Default Value for Current Time
ALTER TABLE `wvp_cloud_record` ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP;

`camera-ai-next`.wvp_user definition

CREATE TABLE `wvp_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role_id` int DEFAULT NULL,
  `create_time` varchar(50) DEFAULT NULL,
  `update_time` varchar(50) DEFAULT NULL,
  `push_key` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User table';

`camera-ai-next`.wvp_log definition

CREATE TABLE `wvp_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `uri` varchar(200) DEFAULT NULL,
  `address` varchar(50) DEFAULT NULL,
  `result` varchar(50) DEFAULT NULL,
  `timing` bigint DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `create_time` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Log table';

`camera-ai-next`.wvp_config definition

CREATE TABLE `wvp_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `sip_id` varchar(100) DEFAULT NULL COMMENT 'SIP Server ID',
  `sip_domain` varchar(100) DEFAULT NULL COMMENT 'SIP Server Domain',
  `sip_ip` varchar(100) DEFAULT NULL COMMENT 'SIP Server IP',
  `sip_port` int DEFAULT NULL COMMENT 'SIP Server Port',
  `sip_password` varchar(100) DEFAULT NULL COMMENT 'SIP Server Password',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GB Standard Config table';

`camera-ai-hongdu1`.tbl_biz_sys_log definition

CREATE TABLE `tbl_biz_sys_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `module_code` varchar(100) DEFAULT NULL COMMENT 'Module Code',
  `module_name` varchar(100) DEFAULT NULL COMMENT 'Module Name',
  `func_code` varchar(100) DEFAULT NULL COMMENT 'Function Code',
  `func_name` varchar(100) DEFAULT NULL COMMENT 'Function Name',
  `oper_detail` text COMMENT 'Operation Content',
  `created_at` datetime DEFAULT NULL COMMENT 'Create Time',
  `status` smallint DEFAULT NULL COMMENT 'Status,0- Success,1- Failed',
  `created_at_text` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2019300902880698370 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='System Log table';
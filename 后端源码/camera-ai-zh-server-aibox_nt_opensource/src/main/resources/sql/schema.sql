`camera-ai-next`.tbl_biz_account definition

CREATE TABLE `tbl_biz_account` (
  `id` bigint NOT NULL DEFAULT '0',
  `account` varchar(50) DEFAULT NULL COMMENT 'Login Account',
  `password` varchar(128) DEFAULT NULL COMMENT 'Login Password',
  `name` varchar(50) DEFAULT NULL COMMENT 'Nickname',
  `state` smallint DEFAULT NULL COMMENT 'Status 0- Normal 1- Disabled',
  `created_at` datetime DEFAULT NULL COMMENT 'Create Time',
  `updated_at` datetime DEFAULT NULL COMMENT 'Update Time',
  `push_cid` text COMMENT 'Push cid',
  `app_version` varchar(20) DEFAULT NULL COMMENT 'app Version',
  `is_super` smallint DEFAULT NULL COMMENT 'Whether super Level Management member,0- No,1- is',
  `phone` varchar(50) DEFAULT NULL COMMENT 'Phone code',
  `depart_id` bigint DEFAULT NULL COMMENT 'belong belong Department',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'User Info table';

`camera-ai-next`.tbl_biz_aibox_status definition

CREATE TABLE `tbl_biz_aibox_status` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `aibox_id` bigint DEFAULT NULL COMMENT 'Box ID',
  `aibox_sn` varchar(50) DEFAULT NULL COMMENT 'Box No',
  `disk_used` double(10,2) DEFAULT NULL COMMENT 'Disk make Use Rate',
  `memory_used` double(10,2) DEFAULT NULL COMMENT 'inner Store make Use Rate',
  `cpu_used` double(10,2) DEFAULT NULL COMMENT 'cpu make Use Rate',
  `apu_used` double(10,2) DEFAULT NULL COMMENT 'apu make Use Rate',
  `vic_used` double(10,2) DEFAULT NULL COMMENT 'vic make Use Rate',
  `ipe_used` double(10,2) DEFAULT NULL COMMENT 'ipe make Use Rate',
  `temperature_used` double(10,2) DEFAULT NULL COMMENT 'Chip Temperature',
  `created_at` datetime DEFAULT NULL COMMENT 'Create Time',
  `time_tag` varchar(20) DEFAULT NULL COMMENT 'Time Label, Format: Hour: part: s',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Edge Box Resource Status table';

`camera-ai-next`.tbl_biz_alarm_level definition

CREATE TABLE `tbl_biz_alarm_level` (
  `id` bigint NOT NULL DEFAULT '0',
  `name` varchar(50) DEFAULT NULL COMMENT 'Alert Level Name',
  `show_color` varchar(50) DEFAULT NULL COMMENT 'Alert Level Color',
  `show_color_alpha` varchar(50) DEFAULT NULL COMMENT 'Alert Level Color alpha',
  `show_types` json DEFAULT NULL COMMENT 'Alert show show Type',
  `account_id` bigint DEFAULT NULL COMMENT 'User ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Alert Level Config';

`camera-ai-next`.tbl_biz_algorithm_alarm_level definition

CREATE TABLE `tbl_biz_algorithm_alarm_level` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `algorithm_id` bigint DEFAULT NULL COMMENT 'Algorithm id',
  `level_id` bigint DEFAULT NULL COMMENT 'Alert Level id',
  `account_id` bigint DEFAULT NULL COMMENT 'User id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Algorithm Alert Level Relate table';

`camera-ai-next`.tbl_biz_algorithm_file definition

CREATE TABLE `tbl_biz_algorithm_file` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `name_en` varchar(100) NOT NULL COMMENT 'Algorithm English Name',
  `file_name` varchar(100) NOT NULL COMMENT 'File Name',
  `file_sha256` text COMMENT 'sha256',
  `platform` varchar(100) DEFAULT NULL COMMENT 'Hardware Platform',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Algorithm File table';

`camera-ai-next`.tbl_biz_algorithm_task definition

CREATE TABLE `tbl_biz_algorithm_task` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `name_en` varchar(100) NOT NULL COMMENT 'Algorithm English Name',
  `state` char(1) DEFAULT '0' COMMENT '0- In Progress Download, 1- Success, 2- Failed, 3- via is most New',
  `remark` varchar(100) DEFAULT NULL COMMENT 'Remark',
  `file_name` varchar(100) DEFAULT NULL COMMENT 'Algorithm File Package Name',
  `file_path` varchar(300) DEFAULT NULL COMMENT 'Algorithm File Package Temporary Storage Path',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Algorithm Download Task table';

`camera-ai-next`.tbl_biz_camera definition

CREATE TABLE `tbl_biz_camera` (
  `id` bigint NOT NULL DEFAULT '0',
  `name` varchar(50) DEFAULT NULL COMMENT 'Camera Name',
  `rtsp_url` text COMMENT 'Camera Stream Address',
  `action` smallint DEFAULT NULL COMMENT 'no Use',
  `created_at` date DEFAULT NULL COMMENT 'Create Time',
  `updated_at` date DEFAULT NULL COMMENT 'Update Time',
  `state` smallint DEFAULT NULL COMMENT 'Status,0- Valid,1- Invalid',
  `running` smallint DEFAULT NULL COMMENT 'Enabled 0- Close, 1- Enable',
  `interval_time` float(5,2) DEFAULT NULL COMMENT 'Algorithm Recognition Interval, s',
  `alarm_interval` float(5,2) DEFAULT '5.00' COMMENT 'Alert Interval, s',
  `frequency` int DEFAULT NULL COMMENT 'no Use',
  `file_name` varchar(500) DEFAULT NULL COMMENT 'Cover image',
  `file_width` int DEFAULT NULL COMMENT 'no Use',
  `file_height` int DEFAULT NULL COMMENT 'no Use',
  `canvas_width` int DEFAULT NULL COMMENT 'no Use',
  `canvas_height` int DEFAULT NULL COMMENT 'no Use',
  `scale_ratio` float(12,4) DEFAULT NULL COMMENT 'no Use',
  `params` text COMMENT 'no Use',
  `api_params` text COMMENT 'no Use',
  `warehouse_id` bigint DEFAULT NULL COMMENT 'no Use',
  `rtsp_type` smallint DEFAULT '0' COMMENT 'no Use',
  `location_id` bigint DEFAULT '0' COMMENT 'Region id',
  `location_ids` text COMMENT 'Region ids',
  `video_play` smallint DEFAULT '0' COMMENT 'Camera Play Status 0- not Push Stream 1- Push Stream',
  `location_type` varchar(2) DEFAULT '1' COMMENT '1 Camera 2 Box',
  `aibox_exec_status` int DEFAULT NULL COMMENT 'Camera Execute Status (1000 Inference in 2000 Download Algorithm in 3000 not make Use)',
  `aibox_exec_time` datetime DEFAULT NULL,
  `action_counter` int DEFAULT '0' COMMENT 'Camera Update Plan Number (Edge Box make Use)',
  `video_codec` varchar(10) DEFAULT NULL COMMENT 'Video Stream Code Format',
  `video_fps` int DEFAULT '25' COMMENT 'Video Stream Frame Rate',
  `sound_column_id` bigint DEFAULT NULL COMMENT 'IP Speaker Pole id',
  `playing_time` bigint DEFAULT '0' COMMENT 'most after Play Timestamp, 30s inner no Update, rule Call super Star Box Stop Play',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Camera Config table';

add
alter table `tbl_biz_camera` add column `aibox_exec_msg` varchar(200) COMMENT 'Algorithm Status Result';
alter table `tbl_biz_camera` add column `aibox_exec_send` bigint default 0 COMMENT 'Algorithm Call Send Timestamp';
alter table `tbl_biz_camera` add column `source_type` smallint(1) default 0 COMMENT 'Source Type,0- Direct connect Stream,1- GB Standard Stream';
alter table `tbl_biz_camera` add column `gb_id` bigint(20) default 0 COMMENT 'GB Standard Channel ID';
alter table `tbl_biz_camera` add column `media_server_id` bigint(20) default 0 COMMENT 'Media Node ID';
alter table `tbl_biz_camera` add column `algo_count` int(3) default 0 COMMENT 'Relate Camera Count';
alter table `tbl_biz_camera` add column `video_width` int default 0 COMMENT 'Video Width';
alter table `tbl_biz_camera` add column `video_height` int default 0 COMMENT 'Video high';

alter table tbl_biz_camera add column video_width int default 0;

`camera-ai-next`.tbl_biz_camera_algorithm definition

CREATE TABLE `tbl_biz_camera_algorithm` (
  `id` bigint NOT NULL DEFAULT '0',
  `camera_id` bigint DEFAULT NULL,
  `algorithm_id` bigint DEFAULT NULL,
  `confidence` float(12,2) DEFAULT '0.50' COMMENT 'Confidence',
  `mark_points` text COMMENT 'Region Mark',
  `image_points` text COMMENT 'true real Image Region Mark, mark_points Convert Complete true real Coordinate',
  `line_mark_points` text COMMENT 'Draw Line Mark',
  `line_image_points` text COMMENT 'true real Image Draw Line Mark, line_mark_points Convert Complete true real Coordinate',
  `algorithm_version` varchar(10) DEFAULT NULL COMMENT 'Algorithm Version',
  `box_update_status` smallint DEFAULT '0' COMMENT 'Box Algorithm Update Status 0 no Update Task 1 Wait Update 2 Update in 3 Update Complete Complete',
  `run_status` smallint DEFAULT '0' COMMENT 'Algorithm Run Status,0- not Run,1- In Progress Run',
  `run_time` date DEFAULT NULL COMMENT 'Algorithm most after Run Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Camera and Algorithm Relate table';

updated
alter table `tbl_biz_camera_algorithm` modify column `run_time` datetime;
alter table `tbl_biz_camera_algorithm` add column `auto_push` smallint(1) default 0 COMMENT 'Whether self Dynamic Push, 0- is,1- No';

`camera-ai-next`.tbl_biz_camera_import definition

CREATE TABLE `tbl_biz_camera_import` (
  `id` bigint NOT NULL DEFAULT '0',
  `name` varchar(50) DEFAULT NULL COMMENT 'Camera Name',
  `brand` varchar(50) DEFAULT NULL COMMENT 'Brand',
  `rtsp_url` varchar(300) DEFAULT NULL COMMENT 'Video Address',
  `ipHost` varchar(50) DEFAULT NULL COMMENT 'ipHost',
  `account` varchar(50) DEFAULT NULL COMMENT 'Account',
  `password` varchar(50) DEFAULT NULL COMMENT 'Password',
  `channel` varchar(50) DEFAULT NULL COMMENT 'Channel',
  `check_state` smallint DEFAULT '0' COMMENT 'Status 0 not Validate 1 Validate in 2 Validation failed 3 Validation success',
  `import_state` smallint DEFAULT '0' COMMENT 'Status 0 not Import 1 Import in 2 Import failed 3 Import success',
  `algorithms` text COMMENT 'Relate Algorithm',
  `location` varchar(255) DEFAULT NULL COMMENT 'Point Bit belong belong',
  `interval_time` float(5,2) DEFAULT NULL COMMENT 'Algorithm Recognition Interval (Unit: second)',
  `alarm_interval` float(5,2) DEFAULT '5.00' COMMENT 'Alert Interval (Unit: second)',
  `account_id` bigint DEFAULT NULL COMMENT 'User id',
  `mistake` text COMMENT 'Error Message',
  `location_type` varchar(10) DEFAULT NULL COMMENT 'Region Type 1- Server Version 2- Box Version',
  `created_at` date DEFAULT NULL COMMENT 'Create Time',
  `algorithm_ids` varchar(255) DEFAULT NULL COMMENT 'Algorithm ids, For example:1,2,3',
  `location_id` bigint DEFAULT NULL COMMENT 'Region id',
  `location_ids` varchar(255) DEFAULT NULL COMMENT 'Region ids',
  `file_name` varchar(255) DEFAULT NULL COMMENT 'Import File Name',
  `tag` varchar(255) DEFAULT NULL COMMENT 'Import Batch Label',
  `mistake_desc` text COMMENT 'Import Error Note',
  `port` varchar(20) DEFAULT NULL COMMENT 'Port',
  `rtsp_url2` varchar(255) DEFAULT NULL COMMENT 'customer account Manual input in Stream Address',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Camera Batch Import Temporary table';

`camera-ai-next`.tbl_biz_config definition

CREATE TABLE `tbl_biz_config` (
  `id` bigint NOT NULL DEFAULT '0',
  `name` varchar(100) DEFAULT NULL COMMENT 'Config Name',
  `tag` varchar(100) DEFAULT NULL COMMENT 'Config ID',
  `val` varchar(200) DEFAULT NULL COMMENT 'Config Value',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Config table';

`camera-ai-next`.tbl_biz_face_group definition

CREATE TABLE `tbl_biz_face_group` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `name` varchar(100) DEFAULT NULL COMMENT 'Group Name Name',
  `deleted` smallint DEFAULT '0' COMMENT 'Whether Deleted 0- Normal 1- Delete',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Face Group table';

`camera-ai-next`.tbl_biz_face_image definition

CREATE TABLE `tbl_biz_face_image` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `user_id` bigint DEFAULT NULL COMMENT 'Complete member ID',
  `img_url` varchar(300) DEFAULT NULL COMMENT 'Image Address',
  `is_avatar` smallint DEFAULT '0' COMMENT 'Whether Image 0- No 1- is',
  `deleted` smallint DEFAULT '0' COMMENT 'Whether Deleted 0- Normal 1- Delete',
  `created_at` datetime DEFAULT NULL COMMENT 'Create Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Face Complete member Image table';

`camera-ai-next`.tbl_biz_face_report definition

CREATE TABLE `tbl_biz_face_report` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `camera_id` bigint DEFAULT NULL COMMENT 'Camera ID',
  `has_stranger` smallint DEFAULT '0' COMMENT 'Whether Contain Stranger Person 0- No 1- is',
  `result_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Recognition Result JSON',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Recognition Image Address',
  `created_at` datetime DEFAULT NULL COMMENT 'Recognition Time',
  `created_mills` bigint DEFAULT '0' COMMENT 'Recognition Time (ms Value)',
  `group_id` bigint DEFAULT NULL COMMENT 'Group id',
  `face_id` bigint DEFAULT NULL COMMENT 'Face Image id',
  `user_id` bigint DEFAULT NULL COMMENT 'Face User id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Face Recognition Result table';

add at 2025-07-05
alter table `tbl_biz_face_report` add column source_file varchar(300) COMMENT 'original image File Path';
alter table `tbl_biz_face_report` add column similarity float(2,2) COMMENT 'Similarity';



`camera-ai-next`.tbl_biz_face_user definition

CREATE TABLE `tbl_biz_face_user` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `name` varchar(100) DEFAULT NULL COMMENT 'Complete member Name',
  `tel` varchar(100) DEFAULT NULL COMMENT 'Contact Mode',
  `remark` varchar(100) DEFAULT NULL COMMENT 'Remark',
  `group_id` bigint DEFAULT NULL COMMENT 'Group ID',
  `deleted` smallint DEFAULT '0' COMMENT 'Whether Deleted 0- Normal 1- Delete',
  `created_at` datetime DEFAULT NULL COMMENT 'Create Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Face Complete member table';

`camera-ai-next`.tbl_biz_location definition

CREATE TABLE `tbl_biz_location` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `name` varchar(50) DEFAULT NULL COMMENT 'Bit set Name',
  `sort` int DEFAULT NULL COMMENT 'Sort Value',
  `parent_id` bigint DEFAULT NULL COMMENT 'up Level Region',
  `parent_names` text COMMENT 'up Level Bit set Name',
  `parent_ids` text COMMENT 'up Level Region ids',
  `latitude` float(12,4) DEFAULT NULL COMMENT 'Latitude',
  `longitude` float(12,4) DEFAULT NULL COMMENT 'Longitude',
  `ip_addr` varchar(255) DEFAULT NULL COMMENT 'ip Address',
  `remark` varchar(255) DEFAULT NULL COMMENT 'Remark',
  `type` varchar(255) DEFAULT NULL COMMENT '1 Region 2 Box',
  `location_type` varchar(2) DEFAULT '1' COMMENT '1 Camera 2 Box',
  `box_no` varchar(100) DEFAULT '' COMMENT 'Box Code',
  `box_heart_time` bigint DEFAULT '0' COMMENT 'Box most after Heartbeat Time',
  `active_sn` varchar(200) DEFAULT NULL COMMENT 'Machine code',
  `active_code` varchar(300) DEFAULT NULL COMMENT 'Activate code',
  `active_status` smallint DEFAULT '0' COMMENT 'Activate Status,0- not Activate,1- Activate',
  `active_date` datetime DEFAULT NULL COMMENT 'Activate Time',
  `is_def` smallint DEFAULT '0' COMMENT 'Whether Default 0- No 1- is',
  `makers` varchar(100) DEFAULT NULL COMMENT 'make build Factory merchant',
  `device_mode` varchar(100) DEFAULT NULL COMMENT 'Device Mode',
  `cpu_version` varchar(100) DEFAULT NULL COMMENT 'cpu Version',
  `kernel_version` varchar(100) DEFAULT NULL COMMENT 'inner Core Version',
  `os_version` varchar(100) DEFAULT NULL COMMENT 'os Version',
  `disk_total` bigint DEFAULT NULL COMMENT 'Disk Director Quantity',
  `memory_total` bigint DEFAULT NULL COMMENT 'inner Store Director Quantity',
  `lyndriver_version` varchar(100) DEFAULT NULL,
  `lynsdk_version` varchar(100) DEFAULT NULL,
  `box_version` varchar(10) DEFAULT NULL COMMENT 'Box Version',
  `box_file` varchar(200) DEFAULT NULL COMMENT 'Box most New Version File Address',
  `depart_id` bigint DEFAULT NULL COMMENT 'belong belong Organization or Department',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Camera Region Node < Tree result structure > table';

add at 2025-04-15
alter table `tbl_biz_location` add column `platform` varchar(10) COMMENT 'Hardware Platform';
alter table `tbl_biz_location` add column `use_type` smallint(1) default 0 COMMENT 'make Use Use Path,0- Inference Use Path,1- Face Use Path,2- Mix combine Use Path';
alter table `tbl_biz_location` add column `use_num` int(3) default 0 COMMENT 'make Use sub Number, Each sub Box Request One sub Face Box rule +1';

`camera-ai-next`.tbl_biz_record definition

CREATE TABLE `tbl_biz_record` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `media_server_id` varchar(50) DEFAULT NULL COMMENT 'Stream Media id',
  `app` varchar(100) DEFAULT NULL COMMENT 'Stream ID',
  `stream` varchar(100) DEFAULT NULL COMMENT 'Stream Code',
  `file_name` varchar(100) DEFAULT NULL COMMENT 'File Name',
  `file_size` bigint DEFAULT '0' COMMENT 'File Size',
  `start_time` bigint DEFAULT '0' COMMENT 'Start Time Stamp',
  `end_time` bigint DEFAULT '0' COMMENT 'End Time Stamp',
  `time_len` bigint DEFAULT '0' COMMENT 'Hour long',
  `file_url` varchar(300) DEFAULT NULL COMMENT 'File Path',
  `flag` smallint DEFAULT '0' COMMENT 'Valid table show',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Recording Record table';

alter table `tbl_biz_record` add column `upload_flag` smallint(1) default 0 COMMENT 'Whether Upload,0- not Upload,1- Upload';
alter table `tbl_biz_record` add column `location_id` bigint default 0 COMMENT 'Box ID';
alter table `tbl_biz_record` add column `record_path` varchar(300) COMMENT 'Recording Path';
ALTER TABLE `tbl_biz_record` ADD COLUMN `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP;

`camera-ai-next`.tbl_biz_report definition

CREATE TABLE `tbl_biz_report` (
  `id` bigint NOT NULL DEFAULT '0',
  `camera_id` bigint DEFAULT NULL COMMENT 'Camera id',
  `algorithm_id` bigint DEFAULT NULL COMMENT 'Algorithm id',
  `file_name` varchar(128) DEFAULT NULL COMMENT 'Detect picture file name',
  `params` text COMMENT 'Detection coordinate point',
  `type` smallint DEFAULT NULL COMMENT 'Alarm Type 1-algorithm 2-stream',
  `display` smallint DEFAULT NULL COMMENT 'Whether to display 0-show 1-hide',
  `created_at` datetime DEFAULT NULL COMMENT 'Create date',
  `created_mills` bigint DEFAULT NULL COMMENT 'Create Millisecond time',
  `audit_state` smallint DEFAULT '0' COMMENT 'Review Status (0,1), 5 - Create Annotation Project, 11 - Deleted File',
  `audit_result` smallint DEFAULT '0',
  `project_id` bigint NOT NULL DEFAULT '0' COMMENT 'Annotation Project ID',
  `record_id` bigint DEFAULT '0' COMMENT 'Recording ID',
  `rois` varchar(500) DEFAULT NULL COMMENT 'Frame ROI',
  `lines` varchar(500) DEFAULT NULL COMMENT 'Draw Line ROI',
  `audit_at` datetime DEFAULT NULL COMMENT 'Process Time',
  `video_path` varchar(255) DEFAULT NULL COMMENT 'Video File Path',
  `depart_id` bigint DEFAULT NULL COMMENT 'belong belong Department ID',
  `box_id` bigint DEFAULT NULL COMMENT 'belong belong Box ID',
  PRIMARY KEY (`id`),
  KEY `idx_camera_algorithm` (`camera_id`,`algorithm_id`,`created_mills`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Alarm Record table';


alter table `tbl_biz_report` add column pushed smallint(1) default 0 COMMENT 'Push ID,0- not Push,1- Push';
alter table `tbl_biz_report` add column audit_type smallint(1) default 0 COMMENT 'Process Type,0- self Dynamic Process,1- Manual Process';
alter table `tbl_biz_report` add column audit_remark varchar(300) default NULL COMMENT 'Process Note';
alter table `tbl_biz_report` add column push_msg varchar(300) default NULL COMMENT 'Push Result Message';
alter table `tbl_biz_report` add column push_time datetime default NULL COMMENT 'Push Time';
alter table `tbl_biz_report` add column handle_time bigint default 0 COMMENT 'Process Hour long (s)';

`camera-ai-next`.tbl_biz_report_period definition

CREATE TABLE `tbl_biz_report_period` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `algorithm_id` bigint DEFAULT NULL COMMENT 'Algorithm id',
  `camera_id` bigint DEFAULT NULL COMMENT 'camera id',
  `start_time` int DEFAULT NULL,
  `end_time` int DEFAULT NULL,
  `start_text` varchar(20) DEFAULT NULL,
  `end_text` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT 'Alarm Hour Segment Config table';

`camera-ai-next`.tbl_biz_sms_phone definition

CREATE TABLE `tbl_biz_sms_phone` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `phone` varchar(20) DEFAULT NULL COMMENT 'Phone code',
  `account_id` bigint DEFAULT NULL COMMENT 'User ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SMS Push Phone code';

`camera-ai-next`.tbl_biz_sound_column definition

CREATE TABLE `tbl_biz_sound_column` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `server` varchar(50) DEFAULT NULL COMMENT 'Speaker Pole Server,ip:port',
  `sn` varchar(128) DEFAULT NULL COMMENT 'Speaker Pole order Column',
  `vol` int DEFAULT '20' COMMENT 'Speaker Pole Audio Quantity',
  `type` varchar(50) DEFAULT NULL COMMENT 'IP Speaker Pole type No',
  `user_name` varchar(50) DEFAULT NULL COMMENT 'IP Speaker Pole Account',
  `password` varchar(50) DEFAULT NULL COMMENT 'IP Speaker Pole Account Password',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IP Speaker Pole Management';

`camera-ai-next`.tbl_biz_tag definition

CREATE TABLE `tbl_biz_tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `type` int NOT NULL DEFAULT '0' COMMENT 'Label Type,0- User Custom Label,1- Line Industry Category',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT 'Tag Name Name',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7878 DEFAULT CHARSET=utf8mb4 COMMENT='Label Config table';

`camera-ai-next`.tbl_biz_tracker_report definition

CREATE TABLE `tbl_biz_tracker_report` (
  `id` bigint NOT NULL DEFAULT '0',
  `camera_id` bigint DEFAULT NULL COMMENT 'Camera id',
  `timestamp` varchar(50) DEFAULT NULL COMMENT 'Report Time',
  `enter_count` int DEFAULT '0' COMMENT 'in in Headcount',
  `leave_count` int DEFAULT '0' COMMENT 'Away open Headcount',
  `extend_data_1` int DEFAULT '0' COMMENT 'Expand Data 1',
  `extend_data_2` int DEFAULT '0' COMMENT 'Expand Data 2',
  `extend_str_1` varchar(50) DEFAULT NULL COMMENT 'Expand Data 1',
  `extend_str_2` varchar(50) DEFAULT NULL COMMENT 'Expand Data 2',
  `created_at` datetime DEFAULT NULL COMMENT 'Create Time',
  `created_mills` bigint DEFAULT NULL COMMENT 'Create Time Stamp',
  `depart_id` bigint DEFAULT NULL COMMENT 'belong belong Department ID',
  `box_id` bigint DEFAULT NULL COMMENT 'belong belong Box ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Person Stream Quantity Report Data table';

`camera-ai-next`.tbl_biz_voice_phone definition

CREATE TABLE `tbl_biz_voice_phone` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `phone` varchar(20) DEFAULT NULL COMMENT 'Phone code',
  `level_id` bigint DEFAULT NULL COMMENT 'Alert Level',
  `account_id` bigint DEFAULT NULL COMMENT 'User ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Voice Push Phone code';

`camera-ai-next`.tbl_biz_algorithm definition

CREATE TABLE `tbl_biz_algorithm` (
  `id` bigint NOT NULL DEFAULT '0',
  `name` varchar(50) DEFAULT NULL COMMENT 'Algorithm Name',
  `frequency` int DEFAULT NULL COMMENT 'no Use',
  `interval_time` int DEFAULT NULL COMMENT 'no Use',
  `params` text COMMENT 'no Use',
  `created_at` datetime DEFAULT NULL COMMENT 'Create Time',
  `updated_at` datetime DEFAULT NULL COMMENT 'Update Time',
  `file_name` varchar(500) DEFAULT NULL COMMENT 'no Use',
  `file_width` int DEFAULT NULL COMMENT 'no Use',
  `file_height` int DEFAULT NULL COMMENT 'no Use',
  `canvas_width` int DEFAULT NULL COMMENT 'no Use',
  `canvas_height` int DEFAULT NULL COMMENT 'no Use',
  `scale_ratio` float(12,2) DEFAULT NULL COMMENT 'no Use',
  `statics_flag` smallint DEFAULT '0' COMMENT 'Relate Video Stream Count 0- not Relate 1- Relate',
  `name_en` varchar(50) DEFAULT '' COMMENT 'Algorithm Code',
  `model_path` varchar(200) DEFAULT '' COMMENT 'Model Path',
  `tag_ids` json DEFAULT NULL COMMENT 'Label, For example Line Industry',
  `alarm_level_id` bigint DEFAULT '0' COMMENT 'Alert Level id',
  `image` varchar(255) DEFAULT '' COMMENT 'Cover Image Address',
  `marks` varchar(255) DEFAULT '' COMMENT 'Simple Description',
  `description` text COMMENT 'Detail Detail Description',
  `detail` text COMMENT 'Detail Detail Description',
  `push_enable` smallint DEFAULT '0' COMMENT 'Third Party Data Push, 0- not Push,1- Push',
  `platform` varchar(100) DEFAULT NULL COMMENT 'Hardware Platform',
  `sort` int DEFAULT '0' COMMENT 'Sort',
  `sound_file` varchar(255) DEFAULT NULL COMMENT 'Audio File',
  `share_mode` smallint DEFAULT '0' COMMENT 'Share Type, 1- Share,0- Single Share',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Algorithm Config table';

alter table `tbl_biz_algorithm` add column `collect_flag` smallint default 0 COMMENT 'Collect ID,0- Close,1- Enable';
alter table `tbl_biz_algorithm` add column `collect_confidence` float(5,2)  default 0.5 COMMENT 'Collect Confidence';
alter table `tbl_biz_algorithm` add column `collect_start_time` bigint default 0 COMMENT 'Data Collect Start Time';
alter table `tbl_biz_algorithm` add column `collect_end_time` bigint default 0 COMMENT 'Data Collect End Time';
alter table `tbl_biz_algorithm` add column `extras` text default NULL COMMENT 'amount outer Expand Param,json String';

`camera-ai-next`.tbl_biz_algorithm_box definition

CREATE TABLE `tbl_biz_algorithm_box` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `algo_id` bigint DEFAULT NULL COMMENT 'Algorithm ID',
  `algo_name` varchar(100) DEFAULT NULL COMMENT 'Algorithm Name',
  `algo_code` varchar(100) DEFAULT NULL COMMENT 'Algorithm Code',
  `state` smallint DEFAULT '0' COMMENT 'Process Status,0- Wait Process,1- Processing,2- Process success,3- Process failed',
  `msg` varchar(100) DEFAULT NULL COMMENT 'Message',
  `created_at` datetime DEFAULT NULL COMMENT 'Delete Time',
  `exec_at` datetime DEFAULT NULL COMMENT 'Execute Time',
  `box_id` bigint DEFAULT NULL COMMENT 'Box ID',
  `type` smallint DEFAULT NULL COMMENT 'Process Type, 0- Delete Algorithm File,1- Update Expand Param',
  `time_mills` bigint DEFAULT NULL COMMENT 'Timestamp',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Algorithm Uninstall or Delete Task table';

`camera-ai-nt-farm`.tbl_biz_box_algorithm definition

CREATE TABLE `tbl_biz_box_version` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `box_id` bigint DEFAULT NULL COMMENT 'Box ID',
  `algorithm_id` bigint DEFAULT NULL COMMENT 'Algorithm ID',
  `version_num` varchar(10) DEFAULT NULL COMMENT 'Algorithm Version',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Box and Algorithm Version Management';

`camera-ai-next`.tbl_biz_media_server definition

CREATE TABLE `tbl_biz_media_server` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `name` varchar(100) DEFAULT NULL COMMENT 'ID Name',
  `secret` varchar(100) DEFAULT NULL COMMENT 'Password',
  `ip` varchar(30) DEFAULT NULL COMMENT 'ip Address',
  `http_port` int DEFAULT NULL COMMENT 'http Port',
  `rtsp_port` int DEFAULT NULL COMMENT 'rtsp Port',
  `rtc_port` int DEFAULT NULL COMMENT 'webrtc Port',
  `rtp_port_range` varchar(20) DEFAULT NULL COMMENT 'rtp Receive Port',
  `send_rtp_port_range` varchar(30) DEFAULT NULL COMMENT 'rtp Send Port',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='zlm Config table';

`camera-ai-next`.tbl_biz_map_config definition

CREATE TABLE `tbl_biz_map_config` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `name` varchar(100) DEFAULT NULL COMMENT 'Ground image or image Layer Name',
  `cover` varchar(100) DEFAULT NULL COMMENT 'Cover image',
  `sort` int DEFAULT NULL COMMENT 'Sort Value',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Ground image or image Layer table';

`camera-ai-next`.tbl_biz_map_object definition

CREATE TABLE `tbl_biz_map_object` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `type` smallint DEFAULT '0' COMMENT 'Type,0- Box,1- Camera',
  `map_id` bigint DEFAULT NULL COMMENT 'Ground image or image Layer ID',
  `object_id` bigint DEFAULT NULL COMMENT 'Camera ID or Box ID',
  `position` json DEFAULT NULL COMMENT 'Mapper Coordinate, Format [x1,y1,x2,y2]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Ground image and Camera or Box Relate';

`camera-ai-next`.tbl_biz_map_rule definition

CREATE TABLE `tbl_biz_map_rule` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `svg_name` varchar(100) DEFAULT NULL COMMENT 'svg File Name Name',
  `calc_day` int DEFAULT '0' COMMENT 'Calculate day Number,0- Only Calculate when day',
  `type` smallint DEFAULT '0' COMMENT 'Type,0- Box,1- Camera',
  `rules` json DEFAULT NULL COMMENT 'Calculate Rule, Format [{name:', min:0, max:0, color:'}]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Ground image Style Rule';

`camera-ai-next`.tbl_biz_social_hook definition

CREATE TABLE `tbl_biz_social_hook` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `name` varchar(100) NOT NULL COMMENT 'Group Name',
  `webhook` varchar(200) DEFAULT NULL COMMENT 'webhook Address',
  `signature` varchar(100) DEFAULT NULL COMMENT 'Signature Info',
  `remark` varchar(300) DEFAULT NULL COMMENT 'Remark',
  `state` smallint DEFAULT '1' COMMENT 'Whether Enable,0- not Enable,1- Enable',
  `type` smallint DEFAULT '0' COMMENT 'Type,0- Feishu,1- WeWork,2- DingTalk',
  `app_id` varchar(100) DEFAULT NULL COMMENT 'app_id',
  `app_secret` varchar(100) DEFAULT NULL COMMENT 'app_secret',
  `proxy_addr` varchar(100) DEFAULT NULL COMMENT 'http instead Reason',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Social Platform Group Push table';

`camera-ai-next`.tbl_biz_social_result definition

CREATE TABLE `tbl_biz_social_result` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `social_id` bigint DEFAULT NULL COMMENT 'SocialId',
  `state` smallint DEFAULT '0' COMMENT 'Status,0- Success,1- Failed',
  `created_at` datetime DEFAULT NULL COMMENT 'Create Time',
  `camera_name` varchar(100) DEFAULT NULL COMMENT 'Camera Name',
  `algorithm_name` varchar(100) DEFAULT NULL COMMENT 'Algorithm Name',
  `send_text` text COMMENT 'Send Content',
  `img_url` text COMMENT 'Image Address',
  `error_detail` text COMMENT 'Error Detail',
  `report_id` bigint DEFAULT NULL COMMENT 'Alarm ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Social Platform Push Result';

`camera-ai-next`.tbl_biz_social_config definition

CREATE TABLE `tbl_biz_social_config` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `camera_id` bigint NOT NULL COMMENT 'Camera ID',
  `social_id` bigint DEFAULT NULL COMMENT 'Social Platform ID',
  `algorithm_id` bigint NOT NULL COMMENT 'Algorithm ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Camera and Social Platform Relate table';

`camera-ai-next`.tbl_biz_camera_group definition

CREATE TABLE IF NOT EXISTS `tbl_biz_camera_group` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `name` varchar(100) DEFAULT NULL COMMENT 'Group Name Name',
  `parent_id` bigint DEFAULT '0' COMMENT 'up Level Group ID',
  `sort` int DEFAULT '1' COMMENT 'Sort Value',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Camera Group';


alter table `tbl_biz_camera_group` add column `level` int default 1 COMMENT 'Hierarchy, from 1 Start';

`camera-ai-next`.tbl_biz_camera_group_item definition

CREATE TABLE IF NOT EXISTS `tbl_biz_camera_group_item` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `group_id` bigint NOT NULL COMMENT 'Group ID',
  `camera_id` bigint NOT NULL COMMENT 'Camera ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Camera Group Detail table';

`camera-ai-next`.tbl_biz_face_track_camera definition

CREATE TABLE IF NOT EXISTS `tbl_biz_face_track_camera` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `config_id` bigint NOT NULL COMMENT 'Tracking Config ID',
  `camera_id` bigint NOT NULL COMMENT 'Camera ID',
  `position` json NOT NULL COMMENT 'Insert Point Bit set',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Face Tracking Camera Config';

`camera-ai-next`.tbl_biz_face_track_config definition

CREATE TABLE IF NOT EXISTS `tbl_biz_face_track_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `name` varchar(100) NOT NULL COMMENT 'Template Name',
  `filename` varchar(300) NOT NULL COMMENT 'Base image File Name Name',
  `state` smallint DEFAULT '0' COMMENT 'Valid Status,0- Invalid,1- Valid',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Face Tracking Config';

`camera-ai-next`.tbl_biz_report_target definition

CREATE TABLE IF NOT EXISTS `tbl_biz_report_target` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `t_month` varchar(100) NOT NULL COMMENT 'Month copy',
  `t_val` int NOT NULL DEFAULT '0' COMMENT 'Target Value, form Bit (min)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Alarm Month Degree Target Value Config table';

`camera-ai-next`.tbl_biz_face_track_search definition

CREATE TABLE IF NOT EXISTS `tbl_biz_face_track_search` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `filename` varchar(300) NOT NULL COMMENT 'Search Image',
  `group_ids` json DEFAULT NULL COMMENT 'Camera Group ID List',
  `start_date` datetime DEFAULT NULL COMMENT 'Start Time',
  `end_date` datetime DEFAULT NULL COMMENT 'End Time',
  `created_at` datetime DEFAULT NULL COMMENT 'Search Date',
  `status` smallint NOT NULL DEFAULT '0' COMMENT 'Search Status, 0- not Search,1- Search in,2- Search Success,3- Search Failed',
  `result_msg` text COMMENT 'Search Result Message',
  `result_json` text COMMENT 'Search Result JSON',
  `result_user_id` bigint DEFAULT '0' COMMENT 'Search Similarity most high User ID',
  `result_at` datetime DEFAULT NULL COMMENT 'Search Result Time',
  `result_num` int NOT NULL DEFAULT '0' COMMENT 'Search Result Headcount',
  `result_group_id` bigint DEFAULT '0' COMMENT 'Search Similarity most high Group ID',
  `result_image_id` bigint DEFAULT '0' COMMENT 'Search Similarity most high Image ID',
  `result_start_date` datetime DEFAULT NULL COMMENT 'first Record Time',
  `result_end_date` datetime DEFAULT NULL COMMENT 'final Record Time',
  `result_is_stranger` smallint DEFAULT '0' COMMENT 'Search Result Whether for Stranger Person,0- No,1- is, Face Result Similarity low at Refer Fixed Threshold',
  `result_similarity` float(4,2) DEFAULT '0.00' COMMENT 'Search Result Face Similarity',
  `result_start_camera_id` bigint DEFAULT '0' COMMENT 'Search Result Start Camera ID',
  `result_end_camera_id` bigint DEFAULT '0' COMMENT 'Search Result End Camera ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Face Search History table';

`camera-ai-next`.tbl_biz_face_track_flow definition

CREATE TABLE IF NOT EXISTS `tbl_biz_face_track_flow` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `report_id` bigint NOT NULL COMMENT 'Alarm ID',
  `search_id` bigint NOT NULL COMMENT 'Search ID',
  `camera_id` bigint DEFAULT '0' COMMENT 'Camera ID',
  `report_at` datetime DEFAULT NULL COMMENT 'Alarm Time, same face_report.created_at',
  `similarity` float(4,2) DEFAULT '0.00' COMMENT 'Similarity',
  `user_id` bigint DEFAULT '0' COMMENT 'User ID, same face_report.user_id',
  `group_id` bigint DEFAULT '0' COMMENT 'Group ID, same face_report.group_id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Face Search History and Alarm table';

`camera-ai-next`.tbl_biz_face_sync_object definition

CREATE TABLE IF NOT EXISTS `tbl_biz_face_sync_object` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `op_type` varchar(20) NOT NULL COMMENT 'Operation Type, can select Value for"clear_face","clear_user","clear_group","query_group_list","query_user_list".',
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `group_id` bigint NOT NULL COMMENT 'Group ID',
  `image_id` bigint NOT NULL COMMENT 'Face ID_ Image ID',
  `created_at` datetime NOT NULL COMMENT 'Create Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Face Sync Object table, Involve and Face Info, Image Info, Group Info, Modify Group etc';

`camera-ai-next`.tbl_biz_face_sync_box definition

CREATE TABLE IF NOT EXISTS `tbl_biz_face_sync_box` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `box_id` bigint NOT NULL COMMENT 'Box ID',
  `sync_id` bigint NOT NULL COMMENT 'Sync ID',
  `result_status` smallint NOT NULL DEFAULT '0' COMMENT 'Sync Result Status,0- not Sync,1- Success,2- Failed',
  `sync_num` smallint NOT NULL DEFAULT '0' COMMENT 'Sync sub Number, like Result super over 3 sub rule Put Abandon',
  `sync_status` smallint NOT NULL DEFAULT '0' COMMENT 'Sync Status, 0- not Sync,1-- Sync Complete Complete',
  `result_msg` text COMMENT 'Sync Result Note',
  `result_at` datetime DEFAULT NULL COMMENT 'Sync Result Time',
  `created_at` datetime NOT NULL COMMENT 'Create Time',
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `group_id` bigint NOT NULL COMMENT 'Group ID',
  `image_id` bigint DEFAULT NULL COMMENT 'Image ID',
  `op_type` varchar(20) NOT NULL COMMENT 'Operation Type, can select Value for add_face,clear_face,clear_user,clear_group,query_group_list,query_user_list',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Face and Box Sync Record table';

`camera-ai-next`.tbl_biz_login_log definition

CREATE TABLE IF NOT EXISTS `tbl_biz_login_log` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `account_id` bigint NOT NULL COMMENT 'Account account ID',
  `login_at` datetime NOT NULL COMMENT 'Login Time',
  `login_state` smallint NOT NULL COMMENT 'Login Status,0- Success,1- Failed',
  `login_ip` varchar(20) DEFAULT NULL COMMENT 'Login IP',
  `login_mills` bigint NOT NULL COMMENT 'Login Timestamp',
  `account` varchar(100) DEFAULT NULL COMMENT 'Account Name',
  `login_error` varchar(100) DEFAULT NULL COMMENT 'Login Error',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Login Log table';

`camera-ai-next`.tbl_biz_report_summary definition

CREATE TABLE `tbl_biz_report_summary` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `s_year` int NOT NULL COMMENT 'Year',
  `s_month` int NOT NULL COMMENT 'Month',
  `s_day` int NOT NULL COMMENT 'Day',
  `report_total` int DEFAULT '0' COMMENT 'Alarm Total',
  `report_handled` int DEFAULT '0' COMMENT 'Alarm Process Number',
  `report_closed` int DEFAULT '0' COMMENT 'Alarm Close Number',
  `report_unhandle` int DEFAULT '0' COMMENT 'Alarm not Process Number',
  `report_handle_time` int DEFAULT '0' COMMENT 'Alarm Process Hour long (min)',
  `s_date` int DEFAULT NULL COMMENT 'Date, Format:yyyyMMdd',
  `report_handle_rate` int DEFAULT '0' COMMENT 'Process Rate',
  `report_auto_handled` int DEFAULT NULL COMMENT 'self Dynamic Process Count',
  `update_at` datetime DEFAULT NULL COMMENT 'Data Update Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Alarm Data Day Count table';

`camera-ai-next`.tbl_biz_report_summary_task definition

CREATE TABLE `tbl_biz_report_summary_task` (
  `id` bigint NOT NULL COMMENT 'Primary Key',
  `task_date` int NOT NULL COMMENT 'Task Time,YYYYMMDD',
  `task_year` int NOT NULL COMMENT 'Count Year',
  `task_month` int NOT NULL COMMENT 'Count Month',
  `task_day` int NOT NULL COMMENT 'Count Day',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Count Alarm Day Data Update Task table, Hand Work Process Need Update Process Hour long';

Default Algorithm List
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1696809711436365825, 'Line Person Detection', '2025-03-07 15:47:19', '2025-03-07 15:47:19', 1, 'people', '/data/models/people', '[]', '/cover_image/people.png', 'should Use at Garden Area, merchant City, Factory, Hand via etc Lead Domain, real current for Line Person Real-time Recognition and Fixed Bit', 'should Use at Garden Area, merchant City, Factory and Hand via etc Lead Domain, real current for Line Person Real-time Recognition and Fixed Bit', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1697225577508438018, 'Fire Smoke Recognition', '2025-03-07 15:47:19', '2025-03-07 15:47:19', 1, 'fire', '/data/models/fire', '[]', '/cover_image/fire.png', 'Real-time Monitor Flame, Smoke, fast Speed Alarm Report Fire hide Hazard, Fit Use at each class Scene The Room inner outer reply Misc Environment', 'Recognition merchant Scene, Factory etc Scene Scene down Whether has Fire Sprout, Smoke Situation out current; like Result Detection to Fire Sprout, Smoke Stand Moment Alert, Remind Phase Close Personnel and Hour Process.', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1697236629289762818, 'Fall Detection', '2025-03-07 15:47:19', '2025-03-07 15:47:19', 0, 'fall', '/data/models/fall', '[]', '/cover_image/fall.png', 'self Dynamic Monitor Home Old Person, Nursing Home, Ground Iron and Public Region Personnel Fall Fall Behavior', 'Fall Fall Recognition Base at Calculate Machine Recognition Technology, allocate combine current Scene Camera, self Dynamic Recognition like Home Old Person, Nursing Home, Ground Iron Hand Support Ladder / Stairs, Old Young Work Dynamic Area etc Public Scene The Personnel Fall Fall Behavior', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1700413094181273602, 'Playing Phone Detection', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'callphone', '/data/models/callphone', '[]', '/cover_image/callphone.png', 'self Dynamic Recognition In Special Fixed Region inner Personnel make Use Hand Machine Behavior', 'Playing Phone Detection Base at Person Work Smart can Calculate Machine View Sense Technology, can should Use at Add Oil site, Campus, Warehouse, Handle public Region etc Scene Scene, like Result Detection to Region inner Personnel Hold Hand Machine, Immediate can Detection to Target Object Body (Person + Hand Machine), and in Line Alert.', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1718802526433247233, 'Sleeping On Duty Detection', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'dozing', '/data/models/dozing', '[]', '/cover_image/dozing.png', 'Recognition Personnel Sleep Type Doze Behavior,, super over One Timing between inner Coordinate Bit set Protect Hold not change Immediate Alarm', 'Sleeping On Duty Detection Base at Person Work Smart can View Sense Analyze Technology, main need Use at Monitor and Distinguish Other Work Make Personnel Whether In Work Make Post Bit up Trap in Sleep or Place at not Normal Rest Status, One Dawn Detection to Personnel Close Eye Time over long, rule send out Alert Letter No.', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1723226302428790785, 'not Wear Wear Safe all Cap', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'helmet', '/data/models/helmet', '[]', '/cover_image/helmet.png', 'result combine current Scene Camera self Dynamic Detection Job Personnel Safe all Cap Wear Wear Condition Condition, Raise high current Scene Safe all Management Effect Rate', 'Recognition Work Ground, Mine Area etc Scene Scene down Phase Close Personnel Whether Wear Wear Safe all Cap; Safe all Cap Detection Color Color Support: Yellow Color, Red, White Color; like Result Detection to has Person not Wear Wear Safe all Cap Stand Moment Alert, Remind Phase Close Personnel and Hour whole modify.', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1723607618085240833, 'Vehicle Detection', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'car', '/data/models/car', '[]', '/cover_image/car.png', 'Recognition public Path, stop Car Scene, Hand via Path Mouth etc Scene Scene in Vehicle', 'Vehicle Detection Technology Pass Calculate Machine View Sense Algorithm and current Scene Camera, self Dynamic Monitor Monitor Region inner Whether has Vehicle Exist, real current for Vehicle Type Real-time Recognition and Vehicle Coordinate Fixed Bit.', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1734860489178546178, 'Away Post Detection', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'absent', '/data/models/absent', '[]', '/cover_image/absent.png', '24 h Monitor Mine Shaft, Monitor Room etc Work Make Scene Scene Personnel Off Post Behavior', 'Away Post Recognition Base at Calculate Machine View Sense Algorithm Technology, allocate combine current Scene Camera, self Dynamic Recognition Monitor Room Off Post Behavior, Protect proof 24h Monitor Safe all', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1739161770283687938, 'Electric child Enclose Bar', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'electricFencing', '/data/models/electricFencing', '[]', '/cover_image/electricFencing.png', 'Electric child Enclose Bar should Use at Factory, Coal Mine, Hand via etc Special Fixed Region, Recognition Break In in Region inner Line Person etc', 'allocate combine current Scene Camera, self Dynamic Recognition Electric child Enclose Bar inner Personnel, Ensure Person Body Wealth produce Safe all, Danger Region can Package Include like input Coal Skin Belt, Crush Coal Machine, Violate Forbid Region etc.', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1752273771557494786, 'Smoking Detection', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'smoke', '/data/models/smoke', '[]', '/cover_image/smoke.png', 'In has Easy Burn Easy Explode Product Scene Scene fast Speed Recognition Personnel Smoking Behavior, Trigger Alarm and and Hour Notification Management Personnel', 'Smoking Recognition Base at Person Work Smart can View Sense Analyze Technology, Pass Camera Recognition Scene Scene in Smoking Behavior; like Scene Scene in out current Line Person Smoking Behavior, rule Stand Immediate Alarm with Help Phase Close form Bit Control Smoking Behavior.', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1761943104013791234, 'Fatigued Driving', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'fatigued', '/data/models/fatigued', '[]', '/cover_image/fatigued.png', 'Monitor Drive member Eye part and Surface part Action, Determine Whether Exist Fatigued Driving Behavior', 'make Use Camera Monitor Drive member Eyes and Surface part Action, Detection to Blink Frequency, Doze, Type Ha Owe etc Refer Mark with Determine Drive member Whether Fatigued Driving.', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1761943104013791235, 'Face Recognition', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'face_recognize', '/data/models/face_recognize', '[]', '/cover_image/face_recognize.png', 'should Use at Public Scene The, real current Non Invade in style fast Speed Core check Personnel Body copy, summary Director Target Personnel Trajectory', 'Base at First in Face Recognition Technology, high Effect Core real Line Person Body copy and for not Authorize Personnel Trigger Alarm Report. can Wide Broad should Use at Public Scene The like Silver Line, Learn School, Machine Scene, Factory, for customer account Safe Prevent Body System Provide Smart can Change Solve Decide Method Case.', 1, '1684x', 1);
INSERT IGNORE INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1761943104013791239, 'Person Stream Quantity Count', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'peopleTrack', '/data/models/peopleTrack', '[]', '/cover_image/peopleTrack.png', 'real current Object Track, Count Real-time Line Headcount Quantity, can Valid Raise high Personnel Safe all, Optimize current Scene Work Make Stream Process', 'Use at Monitor and Count Special Fixed Region inner Person Stream Situation. can Wide Broad should Use at Garden Area, Handle public Building, Buy Object merchant Scene, Expo Object Hall, Public show will, Car site etc Scene Scene, can Enough for Management Provide Important Person Stream Data Support, has assist at Optimize empty between Canvas Bureau, Raise up Service Quality and Protect Barrier Public Safe all.', 1, '1684x', 0);

alter table `tbl_biz_map_config` add column `english_name` varchar(200) COMMENT 'Ground image or image Layer English Name';
alter table `tbl_biz_algorithm` add column `english_name` varchar(200) COMMENT 'English Name';
alter table `ap_menus` add column `english_name` varchar(200) COMMENT 'Menu English Name';
alter table `tbl_biz_config` add column `english_val` varchar(200) COMMENT 'English Name';
alter table `tbl_biz_camera` add column `english_name` varchar(200) COMMENT 'English Name';
alter table `tbl_biz_camera` add column `rtsp_url2` text COMMENT 'Camera Stream Address 2';
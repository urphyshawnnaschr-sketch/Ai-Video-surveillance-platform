CREATE TABLE `ap_project` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',

  `project_name` varchar(255) NOT NULL DEFAULT '' COMMENT 'Project Name',
  `project_type` int NOT NULL DEFAULT '0' COMMENT 'Project Type, 1- Image Category, 2- Target Detection, 3-OCR Detection, 4- increase Quantity Training',
  `project_detail` varchar(512) DEFAULT NULL COMMENT 'Project Description',

  `labels` json DEFAULT NULL COMMENT 'Label Tree',

  `user_group_id` json DEFAULT NULL COMMENT 'Annotation group id',
  `need_review` tinyint DEFAULT '0' COMMENT 'Whether Need Quality Check',
  `review_ratio` int NOT NULL DEFAULT '0' COMMENT 'Quality Check than Example %',
  `label_task_time_out` int NOT NULL DEFAULT '0' COMMENT 'Annotation Timeout Time (min)',
  `need_auxiliary_box` tinyint NOT NULL DEFAULT '0' COMMENT 'Whether Need Aux assist Box',
  `auxiliary_box` json DEFAULT NULL COMMENT 'Aux assist Box Size',

  `data_file_id` bigint NOT NULL DEFAULT '0' COMMENT 'Data Set id',
  `doc_file_id` bigint NOT NULL DEFAULT '0' COMMENT 'Annotation Example Document id',
  `algorithm_ids` json DEFAULT NULL COMMENT 'Algorithm id',
  `status` int NOT NULL DEFAULT '0' COMMENT 'Project Status,1- Data Read in,2- Smart can Annotation in,11- Annotation in,99- Deliver',
  `item_count` int DEFAULT NULL COMMENT 'Image Count',
  `labeled` int DEFAULT NULL COMMENT 'Annotation Count',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT 'Annotation Project';


CREATE TABLE `ap_file` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',

  `user_id` bigint NOT NULL COMMENT 'User ID',
  `file_type` tinyint DEFAULT '0' COMMENT 'File Type, 1-zip Data Set,2-excel table Grid,3-doc',
  `raw_data` VARCHAR(255) DEFAULT NULL COMMENT 'File Address',
  `status` VARCHAR(16) NOT NULL DEFAULT 'created' COMMENT 'File Status',
  PRIMARY KEY (`id`)
) ENGINE=INNODB  DEFAULT CHARSET=utf8mb4 COMMENT 'Annotation Project File';


CREATE TABLE `ap_images` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',

  `project_id` bigint NOT NULL DEFAULT '0' COMMENT 'Project id',
  `data_file_id` bigint NOT NULL DEFAULT '0' COMMENT 'Data Set id',
  `storage_path` VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Storage Address',
  `width` INT(11) DEFAULT NULL COMMENT 'Image Width',
  `height` INT(11) DEFAULT NULL COMMENT 'Image high Degree',
  `depth` INT(11) DEFAULT NULL COMMENT 'Image Deep Degree',
  `md5` VARCHAR(32) DEFAULT NULL COMMENT 'Image md5',

  `status` int DEFAULT '0'  COMMENT '0- not Annotation, 1- Smart can Annotation, 4- Person Work Annotation Lock,5- Person Work Annotation,11- Quality Check Lock,15- Quality Check Pass,16- Type return',
  `need_review` tinyint DEFAULT '0' COMMENT 'Whether is Quality Check Get sample',

  `label_user_id` bigint NOT NULL DEFAULT '0' COMMENT 'Annotation member id',
  `review_user_id` bigint NOT NULL DEFAULT '0' COMMENT 'Quality Check member id',
  `assigned_at` datetime DEFAULT NULL COMMENT 'Claim Time',
  `expired_at` datetime DEFAULT NULL COMMENT 'Expire Time',
  `commit_id` bigint NOT NULL DEFAULT '0' COMMENT 'most after One sub Annotation Submit id',
  `version` int(11) NOT NULL COMMENT 'Happy View Lock' ,

  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Annotation Project Image';


CREATE TABLE `ap_commit` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',

  `project_id` bigint NOT NULL DEFAULT '0' COMMENT 'Project id',
  `image_id` bigint NOT NULL DEFAULT '0'  COMMENT 'Image id',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT 'Annotation id',

  `annotation_count` int(11) NOT NULL COMMENT 'Annotation Box Count',
  `is_valid` tinyint NOT NULL COMMENT 'Whether Valid',

  PRIMARY KEY (`id`),
  KEY `idx_project_image` (`project_id`, `image_id`),
  KEY `idx_user_id` ( `user_id`)
) ENGINE=INNODB  DEFAULT CHARSET=utf8mb4 COMMENT 'Submit Annotation';

CREATE TABLE `ap_annotation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',

  `project_id` bigint NOT NULL DEFAULT '0' COMMENT 'Project id',
  `image_id` bigint NOT NULL DEFAULT '0'  COMMENT 'Image id',
  `commit_id` bigint NOT NULL DEFAULT '0' COMMENT 'Submit id',

  `tag_name` varchar(32) DEFAULT NULL COMMENT 'Tag Name',
  `annotation_type` int(11) DEFAULT NULL COMMENT 'Annotation Type, 1 Image Category 2 Rectangle Box 5 form Point 7 Polygon',
  `annotation` json DEFAULT NULL COMMENT 'Box',

  PRIMARY KEY (`id`),
  KEY `idx_project_image` (`project_id`, `image_id`),
  KEY `idx_commit_id` ( `commit_id`)
) ENGINE=INNODB  DEFAULT CHARSET=utf8mb4 COMMENT 'Annotation Box, One Box One Record';

CREATE TABLE `ap_reviews` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',

  `project_id` bigint NOT NULL COMMENT 'Project id',
  `image_id` bigint NOT NULL COMMENT 'Image id',
  `commit_id` bigint NOT NULL DEFAULT '0' COMMENT 'Submit id',
  `review_user_id` int NOT NULL COMMENT 'Quality Check id',
  `review_action` int NOT NULL COMMENT 'Quality Check Result, 1 Pass,3 Rejected,7 Modify',
  `comment` VARCHAR(128) NOT NULL DEFAULT '' COMMENT 'Quality Check Meaning View',
  PRIMARY KEY (`id`),
  KEY `idx_project_image` (`project_id`, `image_id`)

) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT 'Quality Check';

CREATE TABLE `ap_export` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',

  `project_id` bigint NOT NULL DEFAULT '0' ,
  `storage_path` VARCHAR(255) DEFAULT NULL COMMENT 'Storage Path',
  `type` INT DEFAULT NULL COMMENT 'Export Format 1 VOC 2COCO',
  `user_id` bigint NOT NULL DEFAULT '0' ,
  `status` INT DEFAULT NULL COMMENT 'Export Status',
  `export_image` INT DEFAULT NULL COMMENT 'Whether Export Image 1 for is 2 for No',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT 'Export Data';

CREATE TABLE `ap_menus` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT  'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',
  `name` varchar(255) DEFAULT NULL COMMENT 'Menu Name',
  `parent` bigint(20) DEFAULT NULL COMMENT 'parent id',
  `path` varchar(255) DEFAULT NULL COMMENT 'Corresponding url Path',
  `auth` varchar(255) DEFAULT NULL COMMENT 'Unique ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT 'Menu Bar';


CREATE TABLE `ap_role` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',
  `name_ch` varchar(255) DEFAULT NULL COMMENT 'Role Name',
  `name_en` varchar(255) DEFAULT NULL COMMENT 'English Name',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Role table';

pre set Role
INSERT IGNORE INTO `ap_role` VALUES (10000,'2023-10-10 19:25:10','2023-10-10 19:25:13', NULL,'Platform Management member','admin');
INSERT IGNORE INTO `ap_role` VALUES (10001,'2023-10-10 19:25:42','2023-10-10 19:25:44', NULL,'Annotation Management member','ap_admin');
INSERT IGNORE INTO `ap_role` VALUES (10003,'2023-10-10 19:26:11','2023-10-10 19:26:13', NULL,'Annotation member','ap_mark');
INSERT IGNORE INTO `ap_role` VALUES (10004,'2023-10-10 19:26:32','2023-10-10 19:26:34', NULL,'Quality Check member','ap_review');

CREATE TABLE `ap_role_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `role_id` bigint NOT NULL DEFAULT '0' COMMENT 'Role id',
  `menu_id` bigint NOT NULL DEFAULT '0' COMMENT 'Menu id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Role Permission';


CREATE TABLE `ap_user_group` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',
  `name` varchar(32) DEFAULT NULL COMMENT 'Annotation group Name',
  `description` varchar(255) DEFAULT NULL COMMENT 'Annotation group Description',
  `team_id` bigint NOT NULL DEFAULT '0' COMMENT 'Team id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Annotation group table';


CREATE TABLE `ap_user_group_relationships` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT 'User id',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT 'Annotation group id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'User Annotation group table';


CREATE TABLE `ap_user_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
  `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',
  `user_id` bigint NOT NULL DEFAULT '0'  COMMENT 'User id',
  `role_id` bigint NOT NULL DEFAULT '0' COMMENT 'Role id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'User Role table';

CREATE TABLE `ap_user_team`  (
 `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
 `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
 `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
 `name` varchar(32) DEFAULT NULL COMMENT 'Team Name',
 `description` varchar(255) DEFAULT NULL COMMENT 'Team Description',
 `user_id` bigint NOT NULL DEFAULT '0'  COMMENT 'Creator User id',
 `deleted_at` datetime DEFAULT NULL COMMENT 'Delete Time',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Team table';

CREATE TABLE `ap_user_team_relationships`  (
   `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
   `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify Time',
   `user_id` bigint NOT NULL DEFAULT '0'  COMMENT 'User id',
   `team_id` bigint NOT NULL DEFAULT '0' COMMENT 'Team id',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'User Team table';

CREATE TABLE `ap_user_group_project` (
  `project_id` bigint NOT NULL DEFAULT '0' COMMENT 'Project id',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT 'Annotation group id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Project Annotation group table';

Department table
CREATE TABLE IF NOT EXISTS `ap_depart`  (
    `id` bigint(20) NOT NULL COMMENT 'Primary Key',
    `name` varchar(255) DEFAULT NULL COMMENT 'Department Name',
    `parent_id` bigint(20) DEFAULT NULL COMMENT 'up Level Department ID',
    `created_by` bigint(20) DEFAULT NULL COMMENT 'Creator member',
    `created_at` datetime DEFAULT NULL COMMENT 'Create Time',
    `updated_by` bigint(20) DEFAULT NULL COMMENT 'Modify Personnel',
    `updated_at` datetime DEFAULT NULL COMMENT 'Modify Time',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Department table';

add by zhoumingxing
alter table `ap_menus` add column `type` smallint(1) comment 'Type (0- Directory, 1- Menu, 2- Button)';
alter table `ap_menus` add column `is_hidden` smallint(1) default '0' comment 'Whether Hide Menu';
alter table `ap_menus` add column `icon` varchar(255) comment 'icon Path';
alter table `ap_menus` add column `file_path` varchar(255) comment 'File Path';

Role location
CREATE TABLE `ap_role_location` (
                                      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
                                      `role_id` bigint NOT NULL DEFAULT '0' COMMENT 'Role id',
                                      `location_id` bigint NOT NULL DEFAULT '0' COMMENT 'Region id',
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Role Region';

alter table `ap_user_group` add column `user_id` bigint NOT NULL DEFAULT '0' COMMENT 'User id';
alter table `ap_menus` add column `english_name` varchar(200) COMMENT 'Menu English Name';


Default Insert in super Level Management member
insert ignore into tbl_biz_account(`id`, `account`, `password`, `name`, `state`, `created_at`, `updated_at`, `is_super`)
values(1, 'admin', '5aac43e4518629d67e15985959b874fc10758b45ddaecca482bc71efa7829d09', 'admin', 0, now(), now(), 1);

Algorithm Scene Scene Category
insert ignore into tbl_biz_tag (`id`, `type`, `name`) values (7811, 1, "Basic Algorithm"),
                                                             (7822, 1, "Smart Wisdom Coal Mine"),
                                                             (7833, 1, "Smart Wisdom can source"),
                                                             (7844, 1, "Smart Wisdom Campus"),
                                                             (7855, 1, "Smart Wisdom Port Mouth"),
                                                             (7866, 1, "Smart Wisdom Garden Area"),
                                                             (7877, 1, "Smart Wisdom Work Ground") ;

Default Face Group
insert ignore into tbl_biz_face_group (`id`, `name`, `deleted`) values (1718922509339394049, "Black Name form", 0),
                                                             (1718922509339394048, "White Name form", 0);
                                                             
Default Algorithm List
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1696809711436365825, 'Line Person Detection', '2025-03-07 15:47:19', '2025-03-07 15:47:19', 1, 'people', '/data/models/people', '[]', '/cover_image/people.png', 'should Use at Garden Area, merchant City, Factory, Hand via etc Lead Domain, real current for Line Person Real-time Recognition and Fixed Bit', 'should Use at Garden Area, merchant City, Factory and Hand via etc Lead Domain, real current for Line Person Real-time Recognition and Fixed Bit', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1697225577508438018, 'Fire Smoke Recognition', '2025-03-07 15:47:19', '2025-03-07 15:47:19', 1, 'fire', '/data/models/fire', '[]', '/cover_image/fire.png', 'Real-time Monitor Flame, Smoke, fast Speed Alarm Report Fire hide Hazard, Fit Use at each class Scene The Room inner outer reply Misc Environment', 'Recognition merchant Scene, Factory etc Scene Scene down Whether has Fire Sprout, Smoke Situation out current; like Result Detection to Fire Sprout, Smoke Stand Moment Alert, Remind Phase Close Personnel and Hour Process.', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1697236629289762818, 'Fall Detection', '2025-03-07 15:47:19', '2025-03-07 15:47:19', 0, 'fall', '/data/models/fall', '[]', '/cover_image/fall.png', 'self Dynamic Monitor Home Old Person, Nursing Home, Ground Iron and Public Region Personnel Fall Fall Behavior', 'Fall Fall Recognition Base at Calculate Machine Recognition Technology, allocate combine current Scene Camera, self Dynamic Recognition like Home Old Person, Nursing Home, Ground Iron Hand Support Ladder / Stairs, Old Young Work Dynamic Area etc Public Scene The Personnel Fall Fall Behavior', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1700413094181273602, 'Playing Phone Detection', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'callphone', '/data/models/callphone', '[]', '/cover_image/callphone.png', 'self Dynamic Recognition In Special Fixed Region inner Personnel make Use Hand Machine Behavior', 'Playing Phone Detection Base at Person Work Smart can Calculate Machine View Sense Technology, can should Use at Add Oil site, Campus, Warehouse, Handle public Region etc Scene Scene, like Result Detection to Region inner Personnel Hold Hand Machine, Immediate can Detection to Target Object Body (Person + Hand Machine), and in Line Alert.', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1718802526433247233, 'Sleeping On Duty Detection', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'dozing', '/data/models/dozing', '[]', '/cover_image/dozing.png', 'Recognition Personnel Sleep Type Doze Behavior,, super over One Timing between inner Coordinate Bit set Protect Hold not change Immediate Alarm', 'Sleeping On Duty Detection Base at Person Work Smart can View Sense Analyze Technology, main need Use at Monitor and Distinguish Other Work Make Personnel Whether In Work Make Post Bit up Trap in Sleep or Place at not Normal Rest Status, One Dawn Detection to Personnel Close Eye Time over long, rule send out Alert Letter No.', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1723226302428790785, 'not Wear Wear Safe all Cap', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'helmet', '/data/models/helmet', '[]', '/cover_image/helmet.png', 'result combine current Scene Camera self Dynamic Detection Job Personnel Safe all Cap Wear Wear Condition Condition, Raise high current Scene Safe all Management Effect Rate', 'Recognition Work Ground, Mine Area etc Scene Scene down Phase Close Personnel Whether Wear Wear Safe all Cap; Safe all Cap Detection Color Color Support: Yellow Color, Red, White Color; like Result Detection to has Person not Wear Wear Safe all Cap Stand Moment Alert, Remind Phase Close Personnel and Hour whole modify.', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1723607618085240833, 'Vehicle Detection', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'car', '/data/models/car', '[]', '/cover_image/car.png', 'Recognition public Path, stop Car Scene, Hand via Path Mouth etc Scene Scene in Vehicle', 'Vehicle Detection Technology Pass Calculate Machine View Sense Algorithm and current Scene Camera, self Dynamic Monitor Monitor Region inner Whether has Vehicle Exist, real current for Vehicle Type Real-time Recognition and Vehicle Coordinate Fixed Bit.', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1734860489178546178, 'Away Post Detection', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'absent', '/data/models/absent', '[]', '/cover_image/absent.png', '24 h Monitor Mine Shaft, Monitor Room etc Work Make Scene Scene Personnel Off Post Behavior', 'Away Post Recognition Base at Calculate Machine View Sense Algorithm Technology, allocate combine current Scene Camera, self Dynamic Recognition Monitor Room Off Post Behavior, Protect proof 24h Monitor Safe all', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1739161770283687938, 'Electric child Enclose Bar', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'electricFencing', '/data/models/electricFencing', '[]', '/cover_image/electricFencing.png', 'Electric child Enclose Bar should Use at Factory, Coal Mine, Hand via etc Special Fixed Region, Recognition Break In in Region inner Line Person etc', 'allocate combine current Scene Camera, self Dynamic Recognition Electric child Enclose Bar inner Personnel, Ensure Person Body Wealth produce Safe all, Danger Region can Package Include like input Coal Skin Belt, Crush Coal Machine, Violate Forbid Region etc.', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1752273771557494786, 'Smoking Detection', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'smoke', '/data/models/smoke', '[]', '/cover_image/smoke.png', 'In has Easy Burn Easy Explode Product Scene Scene fast Speed Recognition Personnel Smoking Behavior, Trigger Alarm and and Hour Notification Management Personnel', 'Smoking Recognition Base at Person Work Smart can View Sense Analyze Technology, Pass Camera Recognition Scene Scene in Smoking Behavior; like Scene Scene in out current Line Person Smoking Behavior, rule Stand Immediate Alarm with Help Phase Close form Bit Control Smoking Behavior.', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1761943104013791234, 'Fatigued Driving', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'fatigued', '/data/models/fatigued', '[]', '/cover_image/fatigued.png', 'Monitor Drive member Eye part and Surface part Action, Determine Whether Exist Fatigued Driving Behavior', 'make Use Camera Monitor Drive member Eyes and Surface part Action, Detection to Blink Frequency, Doze, Type Ha Owe etc Refer Mark with Determine Drive member Whether Fatigued Driving.', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1761943104013791235, 'Face Recognition', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'face_recognize', '/data/models/face_recognize', '[]', '/cover_image/face_recognize.png', 'should Use at Public Scene The, real current Non Invade in style fast Speed Core check Personnel Body copy, summary Director Target Personnel Trajectory', 'Base at First in Face Recognition Technology, high Effect Core real Line Person Body copy and for not Authorize Personnel Trigger Alarm Report. can Wide Broad should Use at Public Scene The like Silver Line, Learn School, Machine Scene, Factory, for customer account Safe Prevent Body System Provide Smart can Change Solve Decide Method Case.', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1761943104013791239, 'Person Stream Quantity Count', '2025-03-07 15:47:20', '2025-03-07 15:47:20', 0, 'peopleTrack', '/data/models/peopleTrack', '[]', '/cover_image/peopleTrack.png', 'real current Object Track, Count Real-time Line Headcount Quantity, can Valid Raise high Personnel Safe all, Optimize current Scene Work Make Stream Process', 'Use at Monitor and Count Special Fixed Region inner Person Stream Situation. can Wide Broad should Use at Garden Area, Handle public Building, Buy Object merchant Scene, Expo Object Hall, Public show will, Car site etc Scene Scene, can Enough for Management Provide Important Person Stream Data Support, has assist at Optimize empty between Canvas Bureau, Raise up Service Quality and Protect Barrier Public Safe all.', 0, '1684x', 0);
INSERT INTO tbl_biz_algorithm (id, name, created_at, updated_at, statics_flag, name_en, model_path, tag_ids, image, marks, description, push_enable, platform, share_mode) VALUES(1950468822978035714,'Temperature Alarm','2025-03-07 15:47:20','2025-03-07 15:47:20', 0,'temperatureAlarm','/data/models/temperatureAlarm','[]','/cover_image/temperatureAlarm.png','Monitor Camera Temperature Alarm','', 0,'1684x', 0);
delete from tbl_biz_algorithm where id = 1950468822978035714;

update tbl_biz_algorithm set extras = '[{"name":"min_detect_frame_num","label":"connect Continue Frame Detection sub Number","type":"number","value":"1","element":"input"}]' where extras is null or extras = '' or extras = '[]';

Delete Temp Control Alarm Algorithm
delete fromn tbl_biz_algorithm where id = 1950468822978035714;

Menu

INSERT INTO `ap_menus` VALUES (1805851191219134465, '2024-06-26 14:30:50', '2024-07-03 17:56:07', NULL, 'Algorithm merchant City', 0, '/algorithmManagement/modelTesting', 'algorithmManagement', 1, 0, 'el-icon-s-grid', '/applicationMonitoring/modelTesting/modelTesting', '');
INSERT INTO `ap_menus` VALUES (1805852154491379714, '2024-06-26 14:34:39', '2024-06-26 14:34:39', NULL, 'Algorithm Detail', 1805851191219134465, '/algorithmManagement/modelDetail', 'detail', 1, 1, NULL, '/applicationMonitoring/modelTesting/modelDetail', '');
INSERT INTO `ap_menus` VALUES (1805852601373626370, '2024-06-26 14:36:26', '2024-07-03 17:56:10', NULL, 'Alert Level Management', 1805851191219134465, '', 'algorithm-alarm-level', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1807661745424142338, '2024-07-01 14:25:19', '2024-07-01 14:25:19', NULL, 'Edge Platform', 0, '/edgePlatform', 'edgePlatform', 0, 0, 'el-icon-s-cooperation', '/applicationMonitoring/index', '');
INSERT INTO `ap_menus` VALUES (1807662054389157889, '2024-07-01 14:26:33', '2024-07-01 14:26:33', NULL, 'Point Bit Management', 1807661745424142338, '/edgePlatform/boxManagement', 'edgePlatform-boxManagement', 1, 0, NULL, '/applicationMonitoring/boxManagement', '');
INSERT INTO `ap_menus` VALUES (1807662242671464450, '2024-07-01 14:27:18', '2024-07-01 14:27:18', NULL, 'Box Management', 1807661745424142338, '/edgePlatform/casketManagement', 'edgePlatform-casketManagement', 1, 0, NULL, '/applicationMonitoring/casketManagement/index', '');
INSERT INTO `ap_menus` VALUES (1807662403548188674, '2024-07-01 14:27:56', '2024-07-01 14:27:56', NULL, 'Video Preview', 1807661745424142338, '/edgePlatform/newVideoPreview', 'edgePlatform-videoPreview', 1, 0, NULL, '/applicationMonitoring/edgePlatform/newVideoPreview', '');
INSERT INTO `ap_menus` VALUES (1807662561191104513, '2024-07-01 14:28:34', '2024-07-01 14:28:34', NULL, 'Program Upgrade', 1807661745424142338, '/edgePlatform/updata', 'edgePlatform-updata', 1, 0, NULL, '/applicationMonitoring/casketManagement/edgePlatformUpdata', '');
INSERT INTO `ap_menus` VALUES (1807662771388649473,'2024-07-01 14:29:24','2024-07-01 14:29:24', NULL,'Log Query', 1807661745424142338,'/systemManagement/logInfo','edgePlatform-logInfo', 1, 0, NULL,'/applicationMonitoring/logInfo');
INSERT INTO `ap_menus` VALUES (1807663645489991681, '2024-07-01 14:32:53', '2024-07-01 14:32:53', NULL, 'Speaker Pole Management', 0, '/dataManagement/soundColumnManagement', 'soundColumnManagement', 1, 0, 'el-icon-microphone', '/applicationMonitoring/soundColumnManagement', '');
INSERT INTO `ap_menus` VALUES (1807663786074673153, '2024-07-01 14:33:26', '2024-07-01 14:33:26', NULL, 'Push Management', 0, '/noticeManagement', 'noticeManagement', 0, 0, 'el-icon-s-comment', '/applicationMonitoring/index', '');
INSERT INTO `ap_menus` VALUES (1807663982804307970, '2024-07-01 14:34:13', '2024-07-01 14:34:13', NULL, 'SMS Push', 1807663786074673153, '/noticeManagement/messageManagement', 'noticeManagement-message', 1, 0, NULL, '/applicationMonitoring/noticeManagement/messageManagement', '');
INSERT INTO `ap_menus` VALUES (1807664150073151490, '2024-07-01 14:34:53', '2024-07-01 14:34:53', NULL, 'Handle public Push', 1807663786074673153, '/noticeManagement/wxManagement', 'noticeManagement-wx', 1, 0, NULL, '/applicationMonitoring/noticeManagement/wxManagement', '');
INSERT INTO `ap_menus` VALUES (1807664378763382785, '2024-07-01 14:35:47', '2024-07-01 14:35:47', NULL, 'API Push', 1807663786074673153, '/noticeManagement/apiManagement', 'noticeManagement-api', 1, 0, NULL, '/applicationMonitoring/noticeManagement/apiManagement', '');
INSERT INTO `ap_menus` VALUES (1807664553732968449, '2024-07-01 14:36:29', '2024-07-01 14:36:29', NULL, 'Voice Push', 1807663786074673153, '/noticeManagement/voiceManagement', 'noticeManagement-voice', 1, 0, NULL, '/applicationMonitoring/noticeManagement/voiceManagement', '');
INSERT INTO `ap_menus` VALUES (1807664766547759106,'2024-07-01 14:37:20','2024-07-01 14:37:20', NULL,'IP Speaker Pole Play Report', 1807663786074673153,'/noticeManagement/IPSoundColumnManagement','noticeManagement-IPSoundColumn', 1, 0, NULL,'/applicationMonitoring/noticeManagement/IPSoundColumnManagement');
INSERT INTO `ap_menus` VALUES (1807665727253090306, '2024-07-01 14:41:09', '2024-07-01 14:41:09', NULL, 'System Management', 0, '/systemManagement', 'systemManagement', 0, 0, 'el-icon-s-tools', '/applicationMonitoring/index', '');
INSERT INTO `ap_menus` VALUES (1807665909382352897, '2024-07-01 14:41:52', '2024-07-01 14:41:52', NULL, 'Basic Config', 1807665727253090306, '/systemManagement/baseManagement', 'systemManagement-baseManagement', 1, 0, NULL, '/applicationMonitoring/systemManagement', '');
INSERT INTO `ap_menus` VALUES (1807666212039135234, '2024-07-01 14:43:04', '2024-07-01 14:43:04', NULL, 'Organization Account', 1807665727253090306, '/systemManagement/organizationalManagement', 'systemManagement-organizational', 1, 0, NULL, '/applicationMonitoring/systemManage/organizationalManagement/index', '');
INSERT INTO `ap_menus` VALUES (1807666499655143426, '2024-07-01 14:44:13', '2024-07-01 14:44:13', NULL, 'Role Management', 1807665727253090306, '/systemManagement/roleManagement', 'systemManagement-role', 1, 0, NULL, '/applicationMonitoring/systemManage/roleManagement/index', '');
INSERT INTO `ap_menus` VALUES (1807666613626966018, '2024-07-01 14:44:40', '2024-07-03 16:53:42', NULL, 'Menu Management', 1807665727253090306, '/systemManagement/menuManagement', 'systemManagement-menu', 1, 0, '', '/applicationMonitoring/systemManage/menuManagement/index', '');
INSERT INTO `ap_menus` VALUES (1807667443054776321, '2024-07-01 14:47:58', '2024-07-01 14:47:58', NULL, 'Face Deployment', 0, '/faceControl', 'faceControl', 0, 0, 'el-icon-s-cooperation', '/faceControl/index', '');
INSERT INTO `ap_menus` VALUES (1807667578560155649, '2024-07-01 14:48:30', '2024-07-01 14:48:30', NULL, 'Real-time Recognition', 1807667443054776321, '/faceControl/faceRecognition', 'faceControl-faceRecognition', 1, 0, NULL, '/faceControl/faceRecognition', '');
INSERT INTO `ap_menus` VALUES (1807667707782467585, '2024-07-01 14:49:01', '2024-07-01 14:49:01', NULL, 'Face Management', 1807667443054776321, '/faceControl/faceManagent', 'faceControl-faceManagent', 1, 0, NULL, '/faceControl/faceManagent/index', '');
INSERT INTO `ap_menus` VALUES (1807667841752731649, '2024-07-01 14:49:33', '2024-07-01 14:49:33', NULL, 'Recognition Record', 1807667443054776321, '/faceControl/faceHistory', 'faceControl-faceHistory', 1, 0, NULL, '/faceControl/faceHistory/index', '');
INSERT INTO `ap_menus` VALUES (1807668294506876929, '2024-07-01 14:51:21', '2024-07-01 14:51:21', NULL, 'Face for than', 1807667443054776321, '/faceControl/faceCompare', 'faceControl-faceCompare', 1, 0, NULL, '/faceControl/faceCompare/index', '');
INSERT INTO `ap_menus` VALUES (1808437901693915137, '2024-07-03 17:49:30', '2024-07-03 17:51:21', NULL, 'Alert Voice Management', 1805851191219134465, '', 'algorithm-alarm-voice', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808438037841022977, '2024-07-03 17:50:02', '2024-07-03 17:50:02', NULL, 'Algorithm Edit', 1805851191219134465, NULL, 'algorithm-edit', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808439697011216386, '2024-07-03 17:56:38', '2024-07-03 17:56:38', NULL, 'Version Management', 1805851191219134465, NULL, 'algorithm-version', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808770969952686081, '2024-07-04 15:52:59', '2024-07-04 16:01:10', NULL, 'Add', 1807662054389157889, '', 'box-add', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808771082888515586, '2024-07-04 15:53:26', '2024-07-04 16:01:14', NULL, 'Edit', 1807662054389157889, '', 'box-edit', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808771939507998721, '2024-07-04 15:56:50', '2024-07-04 16:01:18', NULL, 'Hour Segment Config', 1807662054389157889, '', 'box-time-config', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808772045951045634, '2024-07-04 15:57:16', '2024-07-04 16:01:23', NULL, 'Delete', 1807662054389157889, '', 'box-delete', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808772185415847937, '2024-07-04 15:57:49', '2024-07-04 16:01:27', NULL, 'Batch Add', 1807662054389157889, '', 'box-batch-add', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808772330115141633, '2024-07-04 15:58:24', '2024-07-04 16:01:31', NULL, 'Run Status', 1807662054389157889, '', 'box-run-state', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808772502832386050, '2024-07-04 15:59:05', '2024-07-04 16:01:35', NULL, 'Local Algorithm Director View', 1807662054389157889, '', 'box-algorithm-overview', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808772658768220162, '2024-07-04 15:59:42', '2024-07-04 16:00:48', NULL, 'check View', 1807662242671464450, '', 'caske-view', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808772787906646018, '2024-07-04 16:00:13', '2024-07-04 16:00:57', NULL, 'Edit', 1807662242671464450, '', 'caske-edit', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808773225682931714,'2024-07-04 16:01:57','2024-07-04 16:01:57', NULL,'Detail', 1807662242671464450, NULL,'caske-detail', 2, 1, NULL, NULL);
INSERT INTO `ap_menus` VALUES (1808773363352571906, '2024-07-04 16:02:30', '2024-07-04 16:02:50', NULL, 'Add Box', 1807662242671464450, '', 'caske-add', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808773567531290626, '2024-07-04 16:03:19', '2024-07-04 16:03:19', NULL, 'Restart', 1807662242671464450, NULL, 'caske-restart', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808773812851937282, '2024-07-04 16:04:17', '2024-07-04 16:04:31', NULL, 'Export original image', 1807663319714205697, '', 'alarm-export-image', 2, 1, '', '', '');
INSERT INTO `ap_menus` VALUES (1808774048752177154, '2024-07-04 16:05:13', '2024-07-04 16:05:13', NULL, 'Export Report Report Excel', 1807663319714205697, NULL, 'alarm-export-data', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1942879718253809666, '2024-07-04 16:05:13', '2024-07-04 16:05:13', NULL, 'Storage Set', 1807663319714205697, NULL, 'alarm-clear-report', 2, 1, NULL, NULL, '');


INSERT INTO `ap_menus` VALUES (1808775306946908162, '2024-07-04 16:10:13', '2024-07-04 16:10:13', NULL, 'Organization Edit', 1807666212039135234, NULL, 'organizational-edit', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808775389595668481, '2024-07-04 16:10:33', '2024-07-04 16:10:33', NULL, 'Organization Add', 1807666212039135234, NULL, 'organizational-add', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808775546445860866, '2024-07-04 16:11:10', '2024-07-04 16:11:10', NULL, 'Organization Delete', 1807666212039135234, NULL, 'organizational-delete', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808775624355057665, '2024-07-04 16:11:29', '2024-07-04 16:11:29', NULL, 'User Add', 1807666212039135234, NULL, 'user-add', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808775722883452929, '2024-07-04 16:11:52', '2024-07-04 16:11:52', NULL, 'User Edit', 1807666212039135234, NULL, 'user-edit', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808775843486470146, '2024-07-04 16:12:21', '2024-07-04 16:12:21', NULL, 'User Delete', 1807666212039135234, NULL, 'user-delete', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808775945416445953, '2024-07-04 16:12:45', '2024-07-04 16:12:45', NULL, 'Create', 1807666499655143426, NULL, 'role-add', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808776032095932418, '2024-07-04 16:13:06', '2024-07-04 16:13:06', NULL, 'Edit', 1807666499655143426, NULL, 'role-edit', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808776085107740674, '2024-07-04 16:13:19', '2024-07-04 16:13:19', NULL, 'Delete', 1807666499655143426, NULL, 'role-delete', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808776341744619521, '2024-07-04 16:14:20', '2024-07-04 16:14:20', NULL, 'Group Management', 1807667707782467585, NULL, 'face-group', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808776427379724290, '2024-07-04 16:14:40', '2024-07-04 16:14:40', NULL, 'Add Face', 1807667707782467585, NULL, 'face-add', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808776427379722290, '2024-07-04 16:14:40', '2024-07-04 16:14:40', NULL, 'Delete Face', 1807667707782467585, NULL, 'face-delete', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808776584645152769, '2024-07-04 16:15:18', '2024-07-04 16:15:18', NULL, 'Batch Upload', 1807667707782467585, NULL, 'face-batch-upload', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808776668699004929, '2024-07-04 16:15:38', '2024-07-04 16:15:38', NULL, 'Edit', 1807667707782467585, NULL, 'face-edit', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808776777289535489, '2024-07-04 16:16:04', '2024-07-04 16:16:04', NULL, 'check View', 1807667707782467585, NULL, 'face-view', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1808776914434887682, '2024-07-04 16:16:37', '2024-07-04 16:16:37', NULL, 'Delete', 1807667707782467585, NULL, 'face-detele', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1833049157994610690, '2024-07-04 16:16:45', '2024-07-04 16:16:45', NULL, 'Delete', 1807662242671464450, NULL, 'caske-delete', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1833049157994610699, '2024-07-04 16:16:45', '2024-07-04 16:16:45', NULL, 'Person Stream Detection', 0, '/flowDsetection/flowSee', 'flowDsetection', 1, 0, 'el-icon-view', '/flowDsetection/flowSeeNew', '');
INSERT INTO `ap_menus` VALUES (1844995864839786498, '2024-10-12 14:57:47', '2024-10-12 14:57:47', NULL, 'Add Algorithm', 1805851191219134465, NULL, 'algorithm-add', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1844995948126081025, '2024-10-12 14:58:07', '2024-10-12 14:58:07', NULL, 'Uninstall Algorithm', 1805851191219134465, NULL, 'algorithm-unload', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1844996057706467330, '2024-10-12 14:58:33', '2024-10-12 14:58:33', NULL, 'Import Model File', 1805851191219134465, NULL, 'algorithm-import', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1844996143786168322, '2024-10-12 14:58:54', '2024-10-12 14:58:54', NULL, 'Delete Card', 1805851191219134465, NULL, 'algorithm-delete', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1807663319714305697, '2024-07-04 16:14:40', '2024-07-04 16:14:40', NULL, 'Alarm Process', 1807663319714205697, NULL, 'report-audit', 2, 1, NULL, NULL, '');
INSERT INTO `ap_menus` VALUES (1807664378763332785, '2024-07-04 16:14:40', '2024-07-04 16:14:40', NULL, 'SMS Notification', 1807664378763382785, NULL, 'noticeManagement-message', 2, 1, NULL, NULL, '');


delete from `ap_menus` where id in (1807663319700005697, 1807663319711115697, 1807663319714205697);
INSERT INTO `ap_menus` VALUES (1807663319700005697, '2024-07-01 14:31:35', '2024-07-01 14:31:35', NULL, 'Alarm Management', 0, '/dataManagement', 'alarmManagement', 0, 0, 'el-icon-s-data', '/applicationMonitoring/index', '');
INSERT INTO `ap_menus` VALUES (1807663319711115697, '2024-07-01 14:31:35', '2024-07-01 14:31:35', NULL, 'Alert Analyze', 1807663319700005697, '/dataManagement/alarmAnalysis', 'alarmAnalysis', 1, 0, '', '/applicationMonitoring/alarmAnalysis', '');
INSERT INTO `ap_menus` VALUES (1807663319714205697, '2024-07-01 14:31:35', '2024-07-01 14:31:35', NULL, 'Alert Data', 1807663319700005697, '/dataManagement/alarmManagement', 'alarmData', 1, 0, '', '/applicationMonitoring/alarmManagement', '');

Delete SMS Push
DELETE FROM `ap_menus` WHERE id = 1807663982804307970;
DELETE FROM `ap_menus` WHERE parent = 1807663982804307970;

Delete Voice Push
DELETE FROM `ap_menus` WHERE id = 1807664553732968449;
DELETE FROM `ap_menus` WHERE parent = 1807664553732968449;


delete from ap_menus where id = 1788591303976218629;
delete from ap_menus where id = 1788591303976218630;
GB Standard Menu
INSERT INTO ap_menus (id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path) VALUES(1788591303976218629, '2024-12-01 16:44:31', '2024-12-01 16:44:31', NULL, 'GB Standard Device', 1807661745424142338, '/GBManagement/deviceList', 'device:gbdevice', 1, 0, '', '/applicationMonitoring/GBManagement/deviceList');
INSERT INTO ap_menus (id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path) VALUES(1788591303976218630, '2024-12-01 16:44:31', '2024-12-01 16:44:31', NULL, 'GB Standard Access', 1807661745424142338, '/GBManagement/channelList', 'device:gbchannel', 1, 0, '', '/applicationMonitoring/GBManagement/channelList');

GB Standard Menu Permission
INSERT INTO ap_menus (id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path) VALUES(1788591303976218631, '2024-12-01 16:44:31', '2024-12-01 16:44:31', NULL, 'check View List', 1788591303976218629, '', 'gb-device-list', 2, 1, '', '');
INSERT INTO ap_menus (id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path) VALUES(1788591303976218632, '2024-12-01 16:44:31', '2024-12-01 16:44:31', NULL, 'check View Platform', 1788591303976218629, '', 'gb-platform-info', 2, 1, '', '');
INSERT INTO ap_menus (id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path) VALUES(1788591303976218633, '2024-12-01 16:44:31', '2024-12-01 16:44:31', NULL, 'check View Platform', 1788591303976218630, '', 'gb-channel-list', 2, 1, '', '');

GB Standard Menu
INSERT INTO ap_menus (id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path) VALUES(11111, '2024-12-01 16:44:31', '2024-12-01 16:44:31', NULL, 'Media Node', 1807661745424142338, '/mediaServer/list', 'mediaServer', 1, 0, '', '/applicationMonitoring/mediaServer/index');

Camera Group Menu
INSERT INTO ap_menus (id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path) VALUES(1934965607394299906, '2025-06-17 21:25:25', '2025-06-17 21:25:25', NULL, 'Group View image', 1807661745424142338, '/edgePlatform/groupView', 'edgePlatform-groupView', 1, 0, NULL, '/applicationMonitoring/groupView/index');

update ap_menus set parent = 0 where id = 1805852154491379714;

Handle public Push, Modify Address
update ap_menus set file_path = '/applicationMonitoring/noticeManagement/socialHookManagement' where id = 1807664150073151490;

Ground image Management Menu
INSERT INTO ap_menus (id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path) VALUES(1924751464145911809, '2024-12-01 16:44:31', '2024-12-01 16:44:31', NULL, 'Ground image Management', 1807665727253090306, '/systemManage/mapImageManagent', 'map:mgr', 1, 0, '', '/applicationMonitoring/systemManage/mapImageManagent/index');

Reset Camera Relate Algorithm Count
update tbl_biz_camera t set algo_count = (select count(1) from tbl_biz_camera_algorithm b where b.camera_id = t.id ) where algo_count = 0;

Face Deployment _ Stranger Person Search
INSERT INTO `ap_menus` VALUES (1945308112883130369, '2024-07-01 14:48:30', '2024-07-01 14:48:30', NULL, 'Stranger Person Search', 1807667443054776321, '/faceControl/stranger', 'faceControl-stranger', 1, 0, NULL, '/faceControl/stranger', '');

Face Deployment _ Real-time Recognition
delete from `ap_menus` where id = 1807667578560155649;

Built-in Default Alarm Month Degree Value
insert into `tbl_biz_report_target`(id, t_month, t_val) values(1, 1, 10);
insert into `tbl_biz_report_target`(id, t_month, t_val) values(2, 2, 10);
insert into `tbl_biz_report_target`(id, t_month, t_val) values(3, 3, 10);
insert into `tbl_biz_report_target`(id, t_month, t_val) values(4, 4, 10);
insert into `tbl_biz_report_target`(id, t_month, t_val) values(5, 5, 10);
insert into `tbl_biz_report_target`(id, t_month, t_val) values(6, 6, 10);
insert into `tbl_biz_report_target`(id, t_month, t_val) values(7, 7, 10);
insert into `tbl_biz_report_target`(id, t_month, t_val) values(8, 8, 10);
insert into `tbl_biz_report_target`(id, t_month, t_val) values(9, 9, 10);
insert into `tbl_biz_report_target`(id, t_month, t_val) values(10, 10, 10);
insert into `tbl_biz_report_target`(id, t_month, t_val) values(11, 11, 10);
insert into `tbl_biz_report_target`(id, t_month, t_val) values(12, 12, 10);


UPDATE `tbl_biz_map_config` SET english_name='Park map'  WHERE id=1928057154985627649;
UPDATE `tbl_biz_map_config` SET english_name='Camera Layer'  WHERE id=1933402400586530817;
UPDATE `ap_menus` SET english_name='Media Node'  WHERE id=11111;
UPDATE `ap_menus` SET english_name='‌Algorithm Details'  WHERE id=1805851191219134465;
UPDATE `ap_menus` SET english_name='‌Alert Severity Management'  WHERE id=1805852601373626370;
UPDATE `ap_menus` SET english_name='‌Edge Platform'  WHERE id=1807661745424142338;
UPDATE `ap_menus` SET english_name='‌‌Point Management‌'  WHERE id=1807662054389157889;
UPDATE `ap_menus` SET english_name='‌‌Box Management‌'  WHERE id=1807662242671464450;
UPDATE `ap_menus` SET english_name='‌‌Video Preview‌'  WHERE id=1807662403548188674;
UPDATE `ap_menus` SET english_name='‌‌Alarm Management‌'  WHERE id=1807663319700005697;
UPDATE ap_menus SET english_name = 'Alarm Analysis' WHERE id = 1807663319711115697;
UPDATE ap_menus SET english_name = 'Alarm Data' WHERE id = 1807663319714205697;
UPDATE ap_menus SET english_name = 'Alarm Processing' WHERE id = 1807663319714305697;
UPDATE ap_menus SET english_name = 'Notification Management' WHERE id = 1807663786074673153;
UPDATE ap_menus SET english_name = 'SMS Notification' WHERE id = 1807663982804307970;
UPDATE ap_menus SET english_name = 'Office Notification' WHERE id = 1807664150073151490;
UPDATE ap_menus SET english_name = 'Alarm Processing' WHERE id = 1807664378763332785;
UPDATE ap_menus SET english_name = 'API Notification' WHERE id = 1807664378763382785;
UPDATE ap_menus SET english_name = 'Voice Notification' WHERE id = 1807664553732968449;
UPDATE ap_menus SET english_name = 'System Management' WHERE id = 1807665727253090306;
UPDATE ap_menus SET english_name = 'Basic Configuration' WHERE id = 1807665909382352897;
UPDATE ap_menus SET english_name = 'Organization Account Management' WHERE id = 1807666212039135234;
UPDATE ap_menus SET english_name = 'Role Management' WHERE id = 1807666499655143426;
UPDATE ap_menus SET english_name = 'Menu Management' WHERE id = 1807666613626966018;
UPDATE ap_menus SET english_name = 'Face Surveillance' WHERE id = 1807667443054776321;
UPDATE ap_menus SET english_name = 'Real-time Recognition' WHERE id = 1807667578560155649;
UPDATE ap_menus SET english_name = 'Face Management' WHERE id = 1807667707782467585;
UPDATE ap_menus SET english_name = 'Recognition Records' WHERE id = 1807667841752731649;
UPDATE ap_menus SET english_name = 'Face Comparison' WHERE id = 1807668294506876929;
UPDATE ap_menus SET english_name = 'Alarm Voice Management' WHERE id = 1808437901693915137;
UPDATE ap_menus SET english_name = 'Algorithm Edit' WHERE id = 1808438037841022977;
UPDATE ap_menus SET english_name = 'Version Management' WHERE id = 1808439697011216386;
UPDATE ap_menus SET english_name = 'Add' WHERE id = 1808770969952686081;
UPDATE ap_menus SET english_name = 'Edit' WHERE id = 1808771082888515586;
UPDATE ap_menus SET english_name = 'Time Configuration' WHERE id = 1808771939507998721;
UPDATE ap_menus SET english_name = 'Delete Device' WHERE id = 1808772045951045634;
UPDATE ap_menus SET english_name = 'Batch Add Devices' WHERE id = 1808772185415847937;
UPDATE ap_menus SET english_name = 'Device Status' WHERE id = 1808772330115141633;
UPDATE ap_menus SET english_name = 'Local Algorithm Overview' WHERE id = 1808772502832386050;
UPDATE ap_menus SET english_name = 'View Device' WHERE id = 1808772658768220162;
UPDATE ap_menus SET english_name = 'Edit Device' WHERE id = 1808772787906646018;
UPDATE ap_menus SET english_name = 'Add Device' WHERE id = 1808773363352571906;
UPDATE ap_menus SET english_name = 'Restart Device' WHERE id = 1808773567531290626;
UPDATE ap_menus SET english_name = 'Export Original Image' WHERE id = 1808773812851937282;
UPDATE ap_menus SET english_name = 'Export Excel Report' WHERE id = 1808774048752177154;
UPDATE ap_menus SET english_name = 'Edit Organization' WHERE id = 1808775306946908162;
UPDATE ap_menus SET english_name = 'Add Organization' WHERE id = 1808775389595668481;
UPDATE ap_menus SET english_name = 'Delete Organization' WHERE id = 1808775546445860866;
UPDATE ap_menus SET english_name = 'Add User' WHERE id = 1808775624355057665;
UPDATE ap_menus SET english_name = 'Edit User' WHERE id = 1808775722883452929;
UPDATE ap_menus SET english_name = 'Delete User' WHERE id = 1808775843486470146;
UPDATE ap_menus SET english_name = 'Create Role' WHERE id = 1808775945416445953;
UPDATE ap_menus SET english_name = 'Edit Role' WHERE id = 1808776032095932418;
UPDATE ap_menus SET english_name = 'Delete Role' WHERE id = 1808776085107740674;
UPDATE ap_menus SET english_name = 'Group Management' WHERE id = 1808776341744619521;
UPDATE ap_menus SET english_name = 'Delete Face' WHERE id = 1808776427379722290;
UPDATE ap_menus SET english_name = 'Add Face' WHERE id = 1808776427379724290;
UPDATE ap_menus SET english_name = 'Batch Upload' WHERE id = 1808776584645152769;
UPDATE ap_menus SET english_name = 'Edit Face' WHERE id = 1808776668699004929;
UPDATE ap_menus SET english_name = 'View Face' WHERE id = 1808776777289535489;
UPDATE ap_menus SET english_name = 'Delete Face' WHERE id = 1808776914434887682;
UPDATE ap_menus SET english_name = 'Delete Device' WHERE id = 1833049157994610690;
UPDATE ap_menus SET english_name = 'Add Algorithm' WHERE id = 1844995864839786498;
UPDATE ap_menus SET english_name = 'Unload Algorithm' WHERE id = 1844995948126081025;
UPDATE ap_menus SET english_name = 'Import Model File' WHERE id = 1844996057706467330;
UPDATE ap_menus SET english_name = 'Delete Card' WHERE id = 1844996143786168322;
UPDATE ap_menus SET english_name = 'Map Management' WHERE id = 1924751464145911809;
UPDATE ap_menus SET english_name = 'Group View' WHERE id = 1934965607394299906;
UPDATE ap_menus SET english_name = 'Flow Detection' WHERE id = 1833049157994610699;
UPDATE ap_menus SET auth = 'mediaServer' WHERE id = 11111;
UPDATE ap_menus SET auth = 'Stranger Search' WHERE id = 1945308112883130369;
UPDATE tbl_biz_config SET english_val = 'Ai Security Platform' WHERE id = 1923391270677483522;
UPDATE tbl_biz_config SET english_val = 'Ai Security Platform' WHERE id = 1923391270698455042;

Modify Stranger Person Search Permission Code
UPDATE ap_menus SET auth = 'faceControl-stranger' WHERE id = 1945308112883130369;

Modify Algorithm Detail page Sort Value
update tbl_biz_algorithm set sort = 99 where id = 1805851191219134465;

Delete not need Algorithm
delete from tbl_biz_algorithm where name_en ='car';
delete from tbl_biz_algorithm where name_en ='fatigued';
delete from tbl_biz_algorithm where name_en ='dozing';
delete from tbl_biz_algorithm where name_en ='electricFencing';
Delete not need Menu
delete from ap_menus where id in (1788591303976218629, 1788591303976218630);
delete from ap_menus where parent in (1788591303976218629, 1788591303976218630);
INSERT INTO `ap_menus` VALUES (1808439697011216387,'2025-09-12 11:05:13','2025-09-12 11:05:13', NULL,'Alert History Data', 1807663319700005697,'/dataManagement/alramHistoricalData','alramHistoricalData', 1, 0, NULL,'/applicationMonitoring/alramHistoricalData/index','Alarm History Data query');
delete from ap_menus where parent in (1808439697011216387);

Box Offline Push Feishu Push Result Add Field
ALTER TABLE tbl_biz_social_result ADD COLUMN business_type TINYINT(1) COMMENT 'Business Type (1- Alarm,2- Box,3- Camera)';

Create Group Push Config table
CREATE TABLE `group_push_config`  (
                                      `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Primary Key',
                                      `group_id` bigint NOT NULL COMMENT 'Camera group id',
                                      `group_level` int NOT NULL DEFAULT 1 COMMENT 'Hierarchy, from 1 Start',
                                      `algorithm_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'multi Algorithm id, Use English Comma No Separate open',
                                      `responsible_person` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'multi Duty Task Person Name, Use English Comma No Separate open',
                                      `responsible_person_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'multi Duty Task Person Work No, Use English Comma No Separate open',
                                      `social_hook_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'multi Push Group id, Use English Comma No Separate open, when Hierarchy for 3 Hour Wait Required',
                                      `created_at` datetime NULL DEFAULT NULL COMMENT 'Create Time',
                                      `updated_at` datetime NULL DEFAULT NULL COMMENT 'Update Time',
                                      `algorithm_names` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'multi Algorithm Name, Use English Comma No Separate open',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Group Push Config' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE tbl_biz_report ADD COLUMN mark TINYINT(1) DEFAULT 0 COMMENT 'Annotation 0- Pending Fixed, 1- Correct Report, 2- wrong Report';


ALTER TABLE tbl_biz_camera ADD COLUMN is_external_rtsp TINYINT(1) DEFAULT 0 COMMENT 'Whether Need from External Get rtsp Stream,0- No,1- is';
ALTER TABLE tbl_biz_camera ADD COLUMN equipment_code VARCHAR(500) DEFAULT null COMMENT 'Device No';
ALTER TABLE tbl_biz_camera ADD COLUMN bitstream_type TINYINT(1) DEFAULT null COMMENT 'code Stream Type 0- main code Stream,1- child code Stream';

Annotation Phase Close Menu

DELETE FROM `ap_menus` where id in (1962806212761554947, 1962806212761554957, 1962806212761554956, 1962806212761554955, 1962806212761554954, 1962806212761554953, 1962806212761554950);
DELETE FROM `ap_menus` where parent in (1962806212761554945);

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554945,now(),now(),NULL,'Annotation Platform',0,'/annotationPlatform','apmgr',0,0,'el-icon-s-claim','/annotationPlatform/index', 'Annotation Platform');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554946,now(),now(),NULL,'Project Management',1962806212761554945,'/annotationPlatform/projectManagement','apmgr-project',1,0,NULL,'/annotationPlatform/projectManagement','Project Management');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554948,now(),now(),NULL,'Annotation group Management',1962806212761554945,'/annotationPlatform/annotationGroupManagement','apmgr-group',1,0,NULL,'/annotationPlatform/annotationGroupManagement','Labeling Group Management');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554949,now(),now(),NULL,'Team Management',1962806212761554945,'/annotationPlatform/teamManagement','apmgr-team',1,0,NULL,'/annotationPlatform/teamManagement','Team Management');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554951,now(),now(),NULL,'Count page Surface',1962806212761554945,'/annotationPlatform/statisticsPage','apmgr-summary',1,0,NULL,'/annotationPlatform/statisticsPage','Statistics Page');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554952,now(),now(),NULL,'increase Quantity Training',1962806212761554945,'/annotationPlatform/incrementalRraining','apmgr-train',1,0,NULL,'/annotationPlatform/incrementalRraining','Incremental Training');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554947,now(),now(),NULL,'Project Detail',1962806212761554945,'/annotationPlatform/projectManagement/projectDetail','apmgr-project',1,1,NULL,'/annotationPlatform/projectManagement/projectDetail','Project Details');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554950,now(),now(),NULL,'Team Detail',1962806212761554945,'/annotationPlatform/teamManagement/teamDetail','apmgr-team',1,1,NULL,'/annotationPlatform/teamManagement/teamDetail','Team Details');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554953,now(),now(),NULL,'Target Detection',1962806212761554945,'/annotationPlatform/projectManagement/markTool/annotate','apmgr-train',1,1,NULL,'/annotationPlatform/markTool/annotate','Object Detection');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554954,now(),now(),NULL,'Image Category - Batch',1962806212761554945,'/annotationPlatform/projectManagement/markTool/classifyBatch','apmgr-train',1,1,NULL,'/annotationPlatform/markTool/classifyBatch','Image Classification - Batch');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554955,now(),now(),NULL,'Image Category',1962806212761554945,'/annotationPlatform/projectManagement/markTool/classify','apmgr-train',1,1,NULL,'/annotationPlatform/markTool/classify','Image Classification');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554956,now(),now(),NULL,'ocr',1962806212761554945,'/annotationPlatform/markTool/ocr','apmgr-train',1,1,NULL,'/annotationPlatform/markTool/annotate','Ocr');

INSERT INTO ap_menus(id, created_at, updated_at, deleted_at, name, parent, `path`, auth, `type`, is_hidden, icon, file_path, english_name)
VALUES(1962806212761554957,now(),now(),NULL,'increase Quantity Training',1962806212761554945,'/annotationPlatform/markTool/incrementalRraining','apmgr-train',1,1,NULL,'/annotationPlatform/markTool/incrementalRraining','Incremental Training');

Integrate hr System User Info Add Field
ALTER TABLE tbl_biz_face_user
    ADD COLUMN work_code VARCHAR(100) COMMENT 'Work No',
ADD COLUMN dept_id VARCHAR(50) COMMENT 'Department ID',
ADD COLUMN dept_code VARCHAR(200) COMMENT 'Department Code',
ADD COLUMN dept_name VARCHAR(200) COMMENT 'Department Name',
ADD COLUMN position_id VARCHAR(50) COMMENT 'Job Bit ID',
ADD COLUMN position_name VARCHAR(200) COMMENT 'Job Bit Name',
ADD COLUMN user_status CHAR(1) COMMENT 'User Status (1- In Job 5- Away Job)',
ADD COLUMN `source` VARCHAR(20) DEFAULT 'aicv' COMMENT 'User Source:hr, aicv)';
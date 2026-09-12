# Ai-Video-surveillance-platform
YihCode is an AI video surveillance platform built for construction sites, industrial parks, mines, factories and campuses. It combines edge AI boxes (which pull camera streams and run inference locally) with a cloud management platform .  The system ingests camera streams over RTSP or GB/T 28181

## 导入源码说明

本仓库来源于用户提供的“翻译项目_前后端源码(1).zip”，保留前后端目录结构。

- 前端：`前端源码/camera-cx-vue-cxBox_next_opensource`，Vue 2。
- 后端：`后端源码/camera-ai-zh-server-aibox_nt_opensource`，Java / Spring Boot。

## 上传前凭据处理

原代码中的下列硬编码凭据已改为读取环境变量。原始压缩包未修改。

- `OSS_ACCESS_KEY_ID`、`OSS_ACCESS_KEY_SECRET`
- `SOURCE_GIT_PASSWORD`
- `SIP_PASSWORD`
- `application-dev.yml` 和 `application-prod.yml` 的 password / secret 字段：使用字段所在位置标注的 `APP_DEV_*`、`APP_PROD_*` 环境变量。

部署前必须填写实际配置。本次仅整理并上传源码，未执行项目脚本、安装依赖、构建或运行验证。子目录原有 README 和 LICENSE 按原样保留，不代表已确认其描述或授权内容。
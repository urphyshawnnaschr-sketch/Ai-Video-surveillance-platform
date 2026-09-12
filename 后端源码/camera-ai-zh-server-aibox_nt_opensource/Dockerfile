FROM registry.cn-hangzhou.aliyuncs.com/yihecode/images:openjdk-8-jre
MAINTAINER '465769438@qq.com'
ADD ./camera-ai-zh-server-0.0.1-SNAPSHOT.jar /app-zh.jar
#RUN echo 'LANG="zh_CN.UTF-8"' >/etc/locale.conf
#RUN echo "Asia/Shanghai" > /etc/timezone
#RUN apt-get update && apt-get install -y git
EXPOSE 8021
#ENTRYPOINT exec java -Xmx16g -Xms2g -Xmn1g -jar app-zh.jar
CMD ["java", "-jar", "/app-zh.jar"]

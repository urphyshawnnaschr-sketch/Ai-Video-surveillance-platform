FROM registry.cn-hangzhou.aliyuncs.com/yihecode/images:nginx-1.18

MAINTAINER HHL

ADD default.conf /etc/nginx/conf.d/

COPY dist/ /usr/share/nginx/html/

EXPOSE 80

CMD ["nginx","-g","daemon off;"]

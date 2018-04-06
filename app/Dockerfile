# 为自己的应用程序打一个基础镜像，把基础的软件安装好，并且包括启动的entrypoint
FROM registry.cn-hangzhou.aliyuncs.com/bevol/app_api:base
# 上面提到了，云效会把container-app.tgz放到Dockerfile的同级目录，所以可以这么写，把云效打出来的软件包拷贝到镜像中
COPY target/app-0.0.1-SNAPSHOT.jar /usr/local/www/jar/app-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/usr/local/www/jar/app-0.0.1-SNAPSHOT.jar"]

#!/bin/bash

# 运维人员应该在虚拟机里设置 ACTIVE_PROFILE 环境变量
# 针对不同的环境，ACTIVE_PROFILE 有以下选择
#
# development.env   开发人员的本地开发环境
# production.env    生产环境
#

PACKAGE_NAME="coin-manager"
VERSION="1.0.0-SNAPSHOT"
SIGN="_xDk83j_"
if [ -z "$ACTIVE_PROFILE" ]; then
    ACTIVE_PROFILE=development.env
fi

INIT_HEAPSIZE=1g
MAX_HEAPSIZE=2g
INIT_PERMSIZE=128m
MAX_PERMSIZE=256m

GC_LOG_DIR=./gc
HEAPDUMP_DIR=./gc

if [ "production.env" == ${ACTIVE_PROFILE} ]; then
  INIT_HEAPSIZE=2g
  MAX_HEAPSIZE=4g
  INIT_PERMSIZE=256m
  MAX_PERMSIZE=512m
fi

## 设置gc log目录
export GC_LOG_DIR
export HEAPDUMP_DIR
mkdir -p ${GC_LOG_DIR}
mkdir -p ${HEAPDUMP_DIR}

## 设置jvm参数
jdk_verbose_version=`java -version 2>&1 | grep version | awk -F"\"" '{print $2}'`
echo "JDK version: ${jdk_verbose_version}"

jdk_major_version=`java -version 2>&1 | grep version | awk -F"." '{print $2}'`
if [ ${jdk_major_version} -gt 7 ]
then
  ### jdk7 之后的版本
  JAVA_OPTS="${JAVA_OPTS} -XX:MetaspaceSize=${INIT_PERMSIZE} -XX:MaxMetaspaceSize=${MAX_PERMSIZE}"
else
  ### jdk7 以及之前的版本
  JAVA_OPTS="${JAVA_OPTS} -XX:PermSize=${INIT_PERMSIZE} -XX:MaxPermSize=${MAX_PERMSIZE}"
  JAVA_OPTS="${JAVA_OPTS} -XX:+UseConcMarkSweepGC -XX:CMSMaxAbortablePrecleanTime=5000"
fi

JAVA_OPTS="${JAVA_OPTS} -Xms${INIT_HEAPSIZE} -Xmx${MAX_HEAPSIZE}"
JAVA_OPTS="${JAVA_OPTS} -Xloggc:${GC_LOG_DIR}/gc.log_$(date +%F) -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
JAVA_OPTS="${JAVA_OPTS} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${HEAPDUMP_DIR}/heapdump_$(date +%m-%d_%H:%M:%S).hprof"
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"

export JAVA_OPTS

## 具体操作
function start() {
  nohup java $JAVA_OPTS -jar \
    -Dlog.path=${LOG_PATH} \
    -Dapp.name=${PACKAGE_NAME} \
    -Djava.security.egd=file:/dev/./urandom ${PACKAGE_NAME}-${VERSION}.jar \
    --spring.profiles.active=$ACTIVE_PROFILE $SIGN > nohup.log &
}

function stop() {
  pid=`ps -ef | grep $PACKAGE_NAME-$VERSION | grep "$SIGN" | grep -v "grep" | awk '{print $2}'`
  kill -9 $pid
  echo "killed process: $pid"
}

function restart() {
  stop
  start
}

function status() {
  ps -ef | grep $PACKAGE_NAME | grep "$SIGN" | grep -v "grep"
}

case $1 in
  start)
    start
    ;;

  stop)
    stop
    ;;

  status)
    status
    ;;

  restart)
    restart
    ;;

  *)
    echo -e "Usage $0 { start | stop | restart | status }"
    ;;
esac


#!/bin/sh
RESOURCE_NAME_MSG=ksh5-0.0.1-SNAPSHOT.jar
cd run/h5

tpidplatformMsg=`ps -ef|grep "$RESOURCE_NAME_MSG" | grep -v grep|grep -v kill|awk '{print $2}'`
if [ "${tpidplatformMsg}" ]; then
echo 'Stop Process...'
kill -15 "$tpidplatformMsg"
fi
sleep 2
tpidplatformMsg=`ps -ef|grep "$RESOURCE_NAME_MSG" |grep -v grep|grep -v kill|awk '{print $2}'`
if [ "${tpidplatformMsg}" ]; then
echo 'Kill Process!'
kill -9 "$tpidplatformMsg"
else
echo 'Stop Success!'
fi

tpidplatformMsg=`ps -ef|grep "$RESOURCE_NAME_MSG" |grep -v grep|grep -v kill|awk '{print $2}'`
if [ "${tpidplatformMsg}" ]; then
    echo 'App is running.'    
else
    echo 'App is NOT running.'
fi

rm -f tpidplatformMsg
nohup /usr/local/java/jdk1.8.0_251/bin/java -Dfile.encoding=utf-8 -jar "$RESOURCE_NAME_MSG"  >nohup.log 2>&1 & # >/dev/null 2>&1 &
echo $! > tpidplatformMsg
echo Start Success! 


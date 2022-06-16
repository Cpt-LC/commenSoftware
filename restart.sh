#!/bin/sh
RESOURCE_NAME=management-service-0.0.1.jar

cd run/mgmt

tpidplatform=`ps -ef|grep "$RESOURCE_NAME" | grep -v grep|grep -v kill|grep -v sudo|awk '{print $2}'`
echo process id is: ${tpidplatform}
if [ "${tpidplatform}" ]; then
echo 'Stop Process...'
kill -15 "${tpidplatform}"
fi
sleep 2
tpidplatform=`ps -ef|grep "$RESOURCE_NAME" |grep -v grep|grep -v kill|grep -v sudo|awk '{print $2}'`
if [ "${tpidplatform}" ]; then
echo 'Kill Process!'
kill -9 "${tpidplatform}"
else
echo 'Stop Success!'
fi

tpidplatform=`ps -ef|grep "$RESOURCE_NAME" |grep -v grep|grep -v kill|grep -v sudo|awk '{print $2}'`
if [ "${tpidplatform}" ]; then
    echo 'App is running.'
else
    echo 'App is NOT running.'
fi

rm -f tpidplatform
nohup /usr/local/java/jdk1.8.0_251/bin/java -Dfile.encoding=utf-8 -jar "$RESOURCE_NAME"  >nohup.log 2>&1 & # >/dev/null 2>&1 &
echo $! > tpidplatform
echo Start Success!



#!/bin/sh
RESOURCE_NAME_MSG=com.lianzheng.message-2.6.1.jar


tpidplatformMsg=`ps -ef|grep "$RESOURCE_NAME_MSG" | grep -v grep|grep -v kill|awk '{print $2}'`
if [ "${tpidplatformMsg}" ]; then
echo 'Stop Process...'
kill -15 "$tpidplatformMsg"
fi
sleep 2
tpidplatformMsg=`ps -ef|grep "$RESOURCE_NAME_MSG" |grep -v grep|grep -v kill|awk '{print $2}'`
if [ "${tpidplatformMsg}" ]; then
echo 'Kill Process!'
kill -9 "${tpidplatformMsg}"
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
nohup /usr/local/java/jdk1.8.0_251/bin/java -Dfile.encoding=utf-8 -jar "$RESOURCE_NAME_MSG"  >nohupMsg.log 2>&1 & # >/dev/null 2>&1 &
echo $! > tpidplatformMsg
echo Start Success! 


echo checking the log ...
sleep 3
tail -n 100 nohupMsg.log

sleep 3
tail -n 100 nohup.log

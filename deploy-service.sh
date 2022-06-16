echo git pull
git pull

echo --------------starting to build----------------------
pwd
/usr/share/maven-3.8.3/apache-maven-3.8.3/bin/mvn clean package -e -DskipTests #-X
cp com.lianzheng.management.service/target/management-service-0.0.1.jar run/mgmt
cp -r templates run/mgmt
cp com.lianzheng.message/target/com.lianzheng.message-2.6.1.jar run/mgmt


echo --------------starting service------------------------
./restart.sh

# pause 5 seconds
sleep 5 

echo -------------starting log--------------------------------
tail -n 100 run/mgmt/nohup.log
#echo copying to 49.233.58.123
#scp jeecg-boot-module-system/target/jeecg-boot-module-system-2.4.6.jar root@49.233.58.123:/root/ningbo/service
#scp jeecg-boot-module-system/target/classes/application-prod.yml root@49.233.58.123:/root/ningbo/service
#scp -r jeecg-boot/templates/* root@49.233.58.123:/root/ningbo/service/templates

#echo restarting ...
#echo cd /root/ningbo/service
#echo ./restart.sh
#echo -------------test url---------------------
#echo curl -X POST  -F 'username=18913510279' -F 'password=123456'  https://tianyi.lianzhenglink.com/jeecg-boot/api/user/login
#ssh root@49.233.58.123

echo git pull
git pull

echo --------------starting to build----------------------
cd ks_certified_server
pwd

/usr/share/maven-3.8.3/apache-maven-3.8.3/bin/mvn clean package -e -DskipTests #-X
cp target/ksh5-0.0.1-SNAPSHOT.jar run/h5
#cp -r templates run/mgmt
#cp com.lianzheng.message/target/com.lianzheng.message-2.6.1.jar run/mgmt


echo --------------starting service------------------------
./restart-h5.sh

# pause 5 seconds
sleep 5 

echo -------------starting log--------------------------------
tail -n 100 run/h5/nohup.log


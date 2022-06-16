echo 生成私钥
openssl genrsa -out jwtRS256.key
echo 生成公钥
openssl rsa -in jwtRS256.key -pubout -outform PEM -out jwtRS256.key.pub
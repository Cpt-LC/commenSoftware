const settings = require('./conf.js')

settings.redis.host='127.0.0.1'
settings.redis.auth_pass='Gjxhcd8L'
settings.redis.port=63794
settings.redis.db=0
settings.upload.imge_root='/home/pubcert/policy_certificate_service/run/h5/fileUpload/'
settings.upload.pdf_root='/home/pubcert/policy_certificate_service/run/mgmt/notices/'
settings.upload.upload_root='/home/pubcert/policy_certificate_service/run/mgmt/uploads/'
settings.upload.imge_api='/home/jhservice/policy_certificate_service/run/jhApi/uploads/'
settings.upload.pdf_root_jh='/home/jhservice/policy_certificate_service/run/jhservice/notices/'
settings.upload.upload_root_jh='/home/jhservice/policy_certificate_service/run/jhservice/uploads/'
// settings.redis.host
module.exports = settings

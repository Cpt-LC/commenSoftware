const _settings = {
    server:{
        port:3005,
        name:"static-files"
    },
    redis: {
        host : "192.168.2.52",
        port : 63794,
        auth_pass : "Gjxhcd8L",
        db : 0,
        ttl : 600, //seconds,
        max_attempts : 3
    },
    upload:{
        imge_root:"../../jeecg-boot/uploads",

        pdf_root:"../../jeecg-boot/notices"
    }
}

module.exports = _settings

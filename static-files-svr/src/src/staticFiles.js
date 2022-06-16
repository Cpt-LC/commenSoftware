//must load after global.settings

const redisAsync = require('./redisAsync')
const md5 = require('md5')
const assert = require('http-assert')
const path=require('path')

let files = [{
    name: "/download",
    dir: global.settings.upload.imge_root,
    validate: async (ctx) => {
        const token = ctx.query['token'];
        let fileName = path.basename(decodeURIComponent(ctx.path.replace('/download/','')));
	fileName = fileName.replace(path.extname(fileName),'')
        let content = await redisAsync.redisClients.app.getAsync(token)
	content = (content || "").replace(/"/g,'');
	const md5Str = `${md5(fileName.replace('-thumnail',''))}`
	console.log("md5=",md5Str)
        console.log("fileName=",fileName);
        console.log("token=",token);
        console.log("content=",content);
        assert(content, 403, {message: "所持令牌超时，请重新请求", expose:true})
        assert(fileName, 403, {message: "所持令牌验证不通过", expose:true})
        assert(content == md5Str, 403, {message: "所持令牌验证不通过", expose:true})
        return content && content == md5Str
    }
},
{
    name: "/pdf",
    dir: global.settings.upload.pdf_root,
    validate: async (ctx) => {
        const token = ctx.query['token'];
        let fileName = path.basename(decodeURIComponent(ctx.path.replace('/pdf/','')));
        fileName = fileName.replace(path.extname(fileName),'')
        let content = await redisAsync.redisClients.app.getAsync(token)
        content = (content || "").replace(/"/g,'');
        const md5Str = `${md5(fileName.replace('-thumnail',''))}`
        console.log("md5=",md5Str)
        console.log("fileName=",fileName);
        console.log("token=",token);
        console.log("content=",content);
        assert(content, 403, {message: "所持令牌超时，请重新请求", expose:true})
        assert(fileName, 403, {message: "所持令牌验证不通过", expose:true})
        assert(content == md5Str, 403, {message: "所持令牌验证不通过", expose:true})
        return content && content == md5Str
    }
},
{
    name: "/upload",
    dir: global.settings.upload.upload_root,
    validate: async (ctx) => {
        const token = ctx.query['token'];
        let fileName = path.basename(decodeURIComponent(ctx.path.replace('/upload/','')));
        fileName = fileName.replace(path.extname(fileName),'')
        let content = await redisAsync.redisClients.app.getAsync(token)
        content = (content || "").replace(/"/g,'');
        const md5Str = `${md5(fileName.replace('-thumnail',''))}`
        console.log("md5=",md5Str)
        console.log("fileName=",fileName);
        console.log("token=",token);
        console.log("content=",content);
        assert(content, 403, {message: "所持令牌超时，请重新请求", expose:true})
        assert(fileName, 403, {message: "所持令牌验证不通过", expose:true})
        assert(content == md5Str, 403, {message: "所持令牌验证不通过", expose:true})
        return content && content == md5Str
    }
},
{
    name: "/uploadJh",
        dir: global.settings.upload.upload_root_jh,
    validate: async (ctx) => {
    const token = ctx.query['token'];
    let fileName = path.basename(decodeURIComponent(ctx.path.replace('/uploadJh/','')));
    fileName = fileName.replace(path.extname(fileName),'')
    let content = await redisAsync.redisClients.app.getAsync(token)
    content = (content || "").replace(/"/g,'');
    const md5Str = `${md5(fileName.replace('-thumnail',''))}`
    console.log("md5=",md5Str)
    console.log("fileName=",fileName);
    console.log("token=",token);
    console.log("content=",content);
    assert(content, 403, {message: "所持令牌超时，请重新请求", expose:true})
    assert(fileName, 403, {message: "所持令牌验证不通过", expose:true})
    assert(content == md5Str, 403, {message: "所持令牌验证不通过", expose:true})
    return content && content == md5Str
}
},
{
    name: "/pdfJh",
        dir: global.settings.upload.pdf_root_jh,
    validate: async (ctx) => {
    const token = ctx.query['token'];
    let fileName = path.basename(decodeURIComponent(ctx.path.replace('/pdfJh/','')));
    fileName = fileName.replace(path.extname(fileName),'')
    let content = await redisAsync.redisClients.app.getAsync(token)
    content = (content || "").replace(/"/g,'');
    const md5Str = `${md5(fileName.replace('-thumnail',''))}`
    console.log("md5=",md5Str)
    console.log("fileName=",fileName);
    console.log("token=",token);
    console.log("content=",content);
    assert(content, 403, {message: "所持令牌超时，请重新请求", expose:true})
    assert(fileName, 403, {message: "所持令牌验证不通过", expose:true})
    assert(content == md5Str, 403, {message: "所持令牌验证不通过", expose:true})
    return content && content == md5Str
}
},
{
    name: "/downloadJh",
        dir: global.settings.upload.imge_api,
    validate: async (ctx) => {
    const token = ctx.query['token'];
    let fileName = path.basename(decodeURIComponent(ctx.path.replace('/downloadJh/','')));
    fileName = fileName.replace(path.extname(fileName),'')
    let content = await redisAsync.redisClients.app.getAsync(token)
    content = (content || "").replace(/"/g,'');
    const md5Str = `${md5(fileName.replace('-thumnail',''))}`
    console.log("md5=",md5Str)
    console.log("fileName=",fileName);
    console.log("token=",token);
    console.log("content=",content);
    assert(content, 403, {message: "所持令牌超时，请重新请求", expose:true})
    assert(fileName, 403, {message: "所持令牌验证不通过", expose:true})
    assert(content == md5Str, 403, {message: "所持令牌验证不通过", expose:true})
    return content && content == md5Str
}
},
]

module.exports = files

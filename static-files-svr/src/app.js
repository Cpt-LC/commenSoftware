
const bunyanDebugStream = require('bunyan-debug-stream');
const koa = require('koa');
const bunyan = require('bunyan');
const Static = require('koa-static');
const path = require('path');
const colors = require('colors/safe');
const dayjs = require('dayjs');
const mount = require('koa-mount');
const onerror = require('koa-onerror');
const http = require('http');

const redisAsync = require('./redisAsync')
const env = process.env.NODE_ENV || 'development';
global.settings = env == 'prd' ? require('../conf.prd') : require('../conf');
const _isDev = env == 'development'

//must load after global.settings
const statiFiles = require('./staticFiles')
const defaultConsoleStream = require('./colorConsoleStream');

//--------------start-----------------
const _app = new koa();

_app.name = global.settings.server.name;
_app.port = global.settings.server.port;

//异常处理
onerror(_app, {
    all: (err, that) => {
        //http-assert会把err.expose=status<500        
        let message = formatError(err);

        global.logger.error(message)

        let returnedMessage = ((_isDev || err.expose) && err.message) ?
            err.message :
            http.STATUS_CODES[this.status];

        that.body = returnedMessage


    }
})

async function start() {
    let debugStream = {
        type: "raw",
        stream: new bunyanDebugStream({
            basepath: __dirname, // this should be the root folder of your project.
            forceColor: true,
            stringifiers: {
                'start_time': function (start_time) {
                    return new Date(start_time);
                },
            }
        }),
        formatter: "short"
    }
    let consoleStream = {
        name: 'console',
        stream: defaultConsoleStream,
        type: "raw"
    }
    opts = {
        name: "static-files",
        streams: [debugStream, consoleStream]
    }
    global.logger = bunyan.createLogger(opts);

    await redisAsync.init({
        app: global.settings.redis
    })

    loadStaticFiles(_app, statiFiles)

    _app.listen(_app.port);
    const message = `${_app.name} service is running on ${_app.port} with ${env} configuration at ${dayjs().format("YYYY-MM-DD HH:mm:ss.SSS")}`;
    global.logger.info(message);
}
/**加载静态资源，必须放在router之后
 * 访问http://127.0.0.1:3014/folder.name/file,比如http://192.168.2.52:3014/doc/schema/sample/custom/index.html
 * folders: [{
    name: "/download",
    dir: '../../uploadTemp',
    validate: (ctx) => {
        var params = ctx.params
        console.log(params.token);
        //todo, check the token
        return true
    }
}]
 */
function loadStaticFiles(app, folders) {
    const baseDir = __dirname;
    for (const folder of folders) {
        app.use(async (ctx, next) => {
            const url = (ctx.url || "");
            var isDir = url.startsWith(folder.name)
	
            if (isDir && folder.validate) {
	
                var isValid = await folder.validate(ctx);
	
                if (!isValid) {
                    this.status = 404
                    return next();
                }

                const isAbsolute = path.isAbsolute(folder.dir);
                const filePath = isAbsolute ? folder.dir : path.join(baseDir, folder.dir);
                return mount(folder.name, Static(filePath))(ctx, next)
            }
            this.status = 404
            return next();
        })
    }


}

function formatError(err) {
    if (!(err instanceof Error)) {
        return null;
    }
    let errMsg = typeof (err.message) == "string" ? err.message : JSON.stringify(err.message);
    let message = `message: ${errMsg}
stack:${err.stack}`;
    return message;
}
process.on('uncaughtException', function (err) {
    try {
        let message = formatError(err);

        global.logger.fatal("process.uncaughtException - " + message);
    } catch (e) {
        console.log(colors.fatal("process.uncaughtException 1: %s"), `message: ${err.message}
stack:${err.stack}`);
        console.log(colors.fatal("process.uncaughtException 2: %s"), e.message);
    }
});

process.on("unhandledRejection", function (reason, promise) {
    let message;
    try {
        let message = formatError(reason);
        global.logger.fatal("process.unhandledRejection - " + message);
        // console.log("process.unhandledRejection - " + message);

    } catch (e) {
        message = formatError(e);
        console.log(colors.fatal("process.unhandledRejection 1: %s"), reason);
        console.log(colors.fatal("process.unhandledRejection 2: %s"), message);
    }
});

start()


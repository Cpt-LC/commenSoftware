'use strict'
const colors = require('colors/safe');
const stream = require('stream');
const bunyan = require('bunyan');

let _defaultConsoleStream = new stream();
_defaultConsoleStream.writable = true;

// set theme of console 
colors.setTheme({
    silly: 'rainbow',
    input: 'grey',
    verbose: 'cyan',
    prompt: 'grey',
    info: 'green',
    data: 'grey',
    help: 'cyan',
    warn: ['black', 'bgYellow'],
    debug: 'blue',
    error: 'red',
    fatal: ['red', 'bgYellow']
});
_defaultConsoleStream.write = function (obj) {
    // pretty-printing your message
    /**
     * level:
     * debug
     * info = 30
     * error = 50
     * fatal = 60
     */

    let message = obj.msg || obj.__msg;
    if (obj.req && !obj.res && obj.req.headers) {
        message = `${obj.msg || obj.__msg}, x-request-id : ${obj.req.headers["x-request-id"]}`;
    } else if (obj.res && obj.res._headers) {
        message = `${obj.msg || obj.__msg}, x-request-id : ${obj.res._headers["x-request-id"]}`;
    } else { }

    if (obj.level >= bunyan.FATAL) {
        console.log(colors.fatal(message));
    } else if (obj.level >= bunyan.ERROR) {
        console.log(colors.error(message));
    } else if (obj.level >= bunyan.WARN) {
        console.log(colors.warn(message));
    } else {
        console.log(colors.info(message));
    }
}

module.exports = _defaultConsoleStream;
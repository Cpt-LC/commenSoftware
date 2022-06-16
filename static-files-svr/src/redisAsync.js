'use strict'

const colors = require('colors/safe');
const bluebird = require('bluebird');
const redis = require('redis');
bluebird.promisifyAll(redis.RedisClient.prototype);
bluebird.promisifyAll(redis.Multi.prototype);

if(!process.env.REDIS_MAX_ATTEMPT){
    process.env.REDIS_MAX_ATTEMPT = 3;
}

const _clients = {}
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
async function createClient(config) {
    if (config == null) {
        return null;
    }

    let client = redis.createClient(config.port, config.host, {
        auth_pass: config.auth_pass,
        retry_strategy: function (options) {
            if (options.error && options.error.code === 'ECONNREFUSED') {
                // End reconnecting on a specific error and flush all commands with
                // a individual error
                return new Error('The server refused the connection');
            }
            // if (options.total_retry_time > config.max_retry_time) {
            //     // End reconnecting after a specific timeout and flush all commands
            //     // with a individual error
            //     return new Error('Retry time exhausted');
            // }
            if (options.attempt > (config.max_attempts || process.env.REDIS_MAX_ATTEMPT)) {
                // End reconnecting with built in error
                return undefined;
            }
            // reconnect after
            return Math.min(options.attempt * 100, 3000);
        }
    });
    await client.selectAsync(config.db);

    client.on("error", function (err) {
        const log = (global.currentApp || {}).logger || console;
        log.error(err);
    });
    return client;
}

async function init(confList) {
    if (!confList) {
        return;
    }
    for (const key in confList) {
        if (confList.hasOwnProperty(key)) {
            const conf = confList[key];
            _clients[key] = await createClient(conf);
        }
    }
}

module.exports = {
    init,
    redisClients: _clients
};
const http = require('http');
const app = require('./app');
const cronJob = require('./CallBackCron');

const normalizePort = val => {
    const port = parseInt(val, 10);

    if (isNaN(port))
        return val;
    if (port >= 10)
        return port;
    return false;
};

const port = normalizePort(process.env.PORT || '8080');
app.set('port', port);

const errorHandler = error => {
    if (error.syscall !== 'listen')
        throw error;
    const adress = server.address();
    const bind = typeof adress == 'string' ? 'pipe ' + adress : 'port: ' + port;
    switch(error.code) {
        case 'EACCES':
            console.error(bind);
            process.exit(1);
            break;
        case 'EADDRINUSE':
            console.error(bind + ' is already in use.');
            process.exit(1);
            break;
        default:
            throw error;
    }
};

const server = http.createServer(app);
server.on('error', errorHandler);
server.on('listening', () => {
  const address = server.address();
  const bind = typeof address === 'string' ? 'pipe ' + address : 'port ' + port;
  console.log('Listening on ' + bind);
});

cronJob.runCron();

server.listen(process.env.PORT || 8080);

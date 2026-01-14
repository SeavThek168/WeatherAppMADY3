// Simple proxy server for OpenWeatherMap API
// This allows the Android emulator to access the API through the host machine
// when direct internet access is blocked by Hyper-V/WSL conflicts

const http = require('http');
const https = require('https');
const url = require('url');

const PORT = 8888;
const OPENWEATHERMAP_HOST = 'api.openweathermap.org';

const server = http.createServer((req, res) => {
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);
    
    // Parse the incoming request URL
    const parsedUrl = url.parse(req.url, true);
    
    // Set CORS headers for the response
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
    
    if (req.method === 'OPTIONS') {
        res.writeHead(200);
        res.end();
        return;
    }
    
    // Forward the request to OpenWeatherMap
    const options = {
        hostname: OPENWEATHERMAP_HOST,
        port: 443,
        path: parsedUrl.path,
        method: req.method,
        headers: {
            'Host': OPENWEATHERMAP_HOST,
            'Accept': 'application/json'
        }
    };
    
    const proxyReq = https.request(options, (proxyRes) => {
        console.log(`  -> Response: ${proxyRes.statusCode}`);
        
        res.writeHead(proxyRes.statusCode, proxyRes.headers);
        proxyRes.pipe(res);
    });
    
    proxyReq.on('error', (err) => {
        console.error(`  -> Error: ${err.message}`);
        res.writeHead(502);
        res.end(JSON.stringify({ error: 'Proxy error', message: err.message }));
    });
    
    req.pipe(proxyReq);
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`\n===========================================`);
    console.log(`  Weather API Proxy Server`);
    console.log(`===========================================`);
    console.log(`  Listening on: http://0.0.0.0:${PORT}`);
    console.log(`  Forwarding to: https://${OPENWEATHERMAP_HOST}`);
    console.log(`\n  From Android emulator, use:`);
    console.log(`  http://10.0.2.2:${PORT}/data/2.5/weather?...`);
    console.log(`===========================================\n`);
});

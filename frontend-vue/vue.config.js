const { defineConfig } = require('@vue/cli-service')
const path = require('path')
const webpack = require('webpack')

module.exports = defineConfig({
  transpileDependencies: true,
  
  configureWebpack: {
    resolve: {
      fallback: {
        "path": require.resolve("path-browserify"),
        "fs": false,
        "crypto": require.resolve("crypto-browserify"),
        "stream": require.resolve("stream-browserify"),
        "util": require.resolve("util/"),
        "buffer": require.resolve("buffer/"),
        "process": require.resolve("process/browser"),
        "zlib": require.resolve("browserify-zlib"),
        "http": require.resolve("stream-http"),
        "https": require.resolve("https-browserify"),
        "url": require.resolve("url/"),
        "assert": require.resolve("assert/")
      }
    },
    plugins: [
      new webpack.ProvidePlugin({
        process: 'process/browser',
        Buffer: ['buffer', 'Buffer']
      })
    ]
  },

  devServer: {
    host: 'localhost', // 修改为localhost
    port: 8081,
    allowedHosts: 'all',
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端地址
        onProxyReq(proxyReq){
         proxyReq.removeHeader('origin')
        },
        changeOrigin: true,
      }
    }
  }
})
const path = require('path');
const webpack = require('webpack');

module.exports = {
    entry: ['babel-polyfill', './src/main/client/local.js'],
    devtool: 'eval-source-map',
    cache: true,
    output: {
        path: path.resolve(__dirname, './src/main/resources/static'),
        publicPath: '/static',
        filename: 'built/bundle.js'
    },

    //exclude empty dependencies, require for Joi
    node: {
        net: 'empty',
        tls: 'empty',
        dns: 'empty'
    },

    plugins: [
        new webpack.ContextReplacementPlugin(/moment[\/\\]locale$/, /en|de/),
        new webpack.NoEmitOnErrorsPlugin()
    ],

    resolve: {
        modules: [process.env.NODE_PATH, 'node_modules'],
        extensions: ['.js']
    },

    resolveLoader: {
        modules: [process.env.NODE_PATH, 'node_modules'],
        extensions: ['.js']
    },

    module: {
        rules: [
            {
                test: /\.css$/,
                loader: "style-loader!css-loader"
            },
            {
                test: /\.less$/,
                loader: 'style-loader!css-loader!less-loader'
            },
            {
                test: /.jsx?$/,
                include: [
                    path.join(__dirname, 'local'),
                    path.join(__dirname, 'src')
                ],
                loader: 'babel-loader',
                options: {
                    compact: false,
                    presets: [
                        ['env', { 'targets': { 'node': 8, 'uglify': true }, 'modules': false }],
                        'stage-0',
                        'react'
                    ],
                    plugins: ['transform-decorators-legacy']
                }
            }
        ]
    }
};
require.config({
    baseUrl: "app",
    out: "build.js",
    optimize: "uglify",
    optimizeCss: "standard.keepLines.keepWhitespace",
    inlineText: true,
    useStrict: true,
    name: "calendar",
    paths: {
        jquery: "../bower_components/jquery/dist/jquery",
        knockout: "../bower_components/knockout.js/knockout",
        underscore: "../bower_components/underscore/underscore",
        requirejs: "../bower_components/requirejs/require",
        "requirejs-plugins": "../bower_components/requirejs-plugins/index",
        "text": "../bower_components/requirejs-text/text"
    },
    packages: [

    ]
});
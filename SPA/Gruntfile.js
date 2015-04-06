module.exports = function(grunt) {

    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        requirejs: {
            compile: {
                options: {
                    mainConfigFile: "config.js",
                    done: function(done, output) {
                        var duplicates = require('rjs-build-analysis').duplicates(output);

                        if (Object.keys(duplicates).length > 0) {
                            grunt.log.subhead('Duplicates found in requirejs build:');
                            for (var key in duplicates) {
                                grunt.log.error(duplicates[key] + ": " + key);
                            }
                            return done(new Error('r.js built duplicate modules, please check the excludes option.'));
                        } else {
                            grunt.log.success("No duplicates found!");
                        }

                        done();
                    }
                }
            }
        },
        jasmine:{
            specs: {

            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-requirejs');
    grunt.loadNpmTasks('grunt-contrib-jasmine');

    // Default task(s).
    grunt.registerTask('default', ['jasmine','requirejs']);

};
const gulp = require('gulp');
const sass = require('gulp-sass');
const sourceMaps = require('gulp-sourcemaps');
const autoprefixer = require('gulp-autoprefixer');
const browserSync = require('browser-sync')

//SCSS

function style() {
    return gulp.src('./*.scss')
    .pipe(sourceMaps.init())
    .pipe(sass().on('error', sass.logError))
    .pipe(autoprefixer())
    .pipe(sourceMaps.write('./'))
    .pipe(gulp.dest('./css'))
    .pipe(browserSync.stream())
}

function watch() {
    browserSync.init({
        server: {
            baseDir: './',
        },
        startPath: './index.html',
        ghostMode: false,
        notify: false
    });
    gulp.watch('./*.scss', style);
    gulp.watch('.src/main/resources/templates/*.html').on('change', browserSync.reload);
    gulp.watch('./*.js').on('change', browserSync.reload);

}

exports.style = style;
exports.watch = watch;

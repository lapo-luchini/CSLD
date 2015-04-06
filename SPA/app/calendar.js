requirejs.config({
    paths: {
        jquery: "../bower_components/jquery/dist/jquery",
        knockout: "../bower_components/knockout.js/knockout",
        underscore: "../bower_components/underscore/underscore",
        requirejs: "../bower_components/requirejs/require",
        "requirejs-plugins": "../bower_components/requirejs-plugins/index",
        "text": "../bower_components/requirejs-text/text"
    }
});

define(['jquery','knockout','underscore', 'service/maps','viewmodels/calendar','text!views/calendar.html'],
    function($, ko, _, GoogleMaps, CalendarViewModel, calendarMarkup){
        $("body").append(calendarMarkup);

        GoogleMaps.done(function(GoogleMaps){
            var calendar = new CalendarViewModel(GoogleMaps);
            ko.applyBindings(calendar, $(".calendar")[0]);
            calendar.activate();
        }).fail(function(){
            console.error("ERROR: Google maps library failed to load.");
        });
});
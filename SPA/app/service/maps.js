define(['jquery'], function ($) {
    var googleMapsLoaded = null;

    if (!googleMapsLoaded) {
        googleMapsLoaded = $.Deferred();

        window.googleMapsLoaded = function () {
            googleMapsLoaded.resolve(google.maps);
        };

        require(['http://maps.googleapis.com/maps/api/js?key=AIzaSyAbMwSPyGMaXWqlAGNoFCy67VyO93RpPfo&sensor=false&callback=googleMapsLoaded'],
            function () {
            }, function (err) {
                googleMapsLoaded.reject();
            });
    }

    return googleMapsLoaded.promise();
});
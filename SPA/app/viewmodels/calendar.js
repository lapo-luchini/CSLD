define(['jquery','knockout','lib/store','record/event'], function($, ko, store, Event){
    function CalendarViewModel(Maps){
        this.maps = Maps;
        this.events(store.findAll(Event));
        setTimeout(function(){
            var defaultElement = window.frames['dataKalendar'].document.getElementById('page');
        }, 1000);
    }

    CalendarViewModel.prototype = {
        constructor: CalendarViewModel,

        events: ko.observableArray(),
        maps: null,
        map: null,

        activate: function(){
            if(this.maps == null) throw new TypeError('CalendarViewModel needs maps to be initialized.');
            var self = this;
            var mapProp = {
                center:new self.maps.LatLng(51.508742,-0.120850),
                zoom:5,
                mapTypeId: self.maps.MapTypeId.ROADMAP
            };
            self.map = new self.maps.Map($("#googleMap")[0], mapProp);
        }
    };

    return CalendarViewModel;
});
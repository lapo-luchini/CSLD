define(['underscore','knockout','lib/record'], function(_, ko, Record){
    function Event(){

    }

    Event.prototype = _.extend(Object.create(Record.prototype), {
        constructor: Event,

        properties: {
            from: ko.observable(),
            to: ko.observable(),
            name: ko.observable(),
            web: ko.observable()
        }
    });

    return Event;
});
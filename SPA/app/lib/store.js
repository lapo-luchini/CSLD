define([], function(){
    function Store(){

    }

    Store.prototype = {
        findAll: function(){
            return [];
        }
    };

    var store = new Store();

    return store;
});
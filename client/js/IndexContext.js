/**
 * Created by fabrizio on 5/20/14.
 */
define(["export"], function( Export ) {

    var exportModule;

    function IndexContext(){
        exportModule = new Export;
    };

    IndexContext.prototype.init = function() {
        exportModule.init() ;
    };

    return IndexContext;



});

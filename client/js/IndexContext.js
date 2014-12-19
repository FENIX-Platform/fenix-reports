define(["testfakeClient"], function ( FakeClient) {

    'use strict'

    var fakeClient;

    function IndexContext() {
        fakeClient = new FakeClient;
    };

    IndexContext.prototype.init = function () {

        console.log('indexcontext.init')
        fakeClient.init();
    };

    return IndexContext;


});

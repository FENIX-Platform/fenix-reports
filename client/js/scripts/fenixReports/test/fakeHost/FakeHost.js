define(['jquery',
    'fenixExport',
    'text!testEnv/payload/md/metadataSimpleConf.json'],
    function($, FenixExport, Payload){
    'use strict';

    var fenixExport, payload;


    var URL = "http://localhost:8080";


    function FakeHost(){
        fenixExport = new FenixExport;
        payload = JSON.parse(Payload);
    }


    FakeHost.prototype.init = function(){

        console.log('fakeHost.init');

        fenixExport.init("metadataExport");

        console.log(payload);

        $('#btn').on('click', function(e){
            fenixExport.exportData(payload,URL);
        });
    };

    return FakeHost;
})

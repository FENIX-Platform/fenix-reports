define(['jquery','fenixExport', 'text!testEnv/payload/config1.json'], function($, FenixExport, Payload){

    'use strict'

    var fenixExport, payload;


    function FakeHost(){
        fenixExport = new FenixExport;
        payload = JSON.parse(Payload);
    }



    FakeHost.prototype.init = function(){

        console.log('fakeHost.init')

        var url = "http://localhost:8080/fenix/export2"

        console.log(payload)


        $('#btn').on('click', function(e){
            fenixExport.exportData(payload,url)
        })

    }

    return FakeHost;
})

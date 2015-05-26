define(['jquery',  "fx-rp-config"], function ($,  PluginCOFIG) {

    'use strict';


    var  pluginChosen;


    var EXPORT_ACCESS_POINT = '/fenix/export';


    var o = {
        "success":""
    };


    function FenixReports() {

        this.o = o;

        this.o.success = function(Plugin) {
            pluginChosen = new Plugin;
        };

        this.o.error = function() {
          console.error("Something went wrong on plugin creation");
        };
    };


    FenixReports.prototype.init = function(plugin) {

        if(typeof plugin!== 'undefined' && plugin!== null && plugin !== '' &&
            typeof  PluginCOFIG[plugin] !== 'undefined' && PluginCOFIG[plugin]){
            require([''+PluginCOFIG[plugin]], o.success, o.error);
        }
        else  {
            throw new Error('please define a valid plugin name');
        }
    };


    FenixReports.prototype.exportData = function (config, url,successCallBack, errorCallback) {

        var payload = pluginChosen.process(config);

        url += EXPORT_ACCESS_POINT;

        var self = this;
        $.ajax({
            url: url,
            crossDomain: true,
            type: 'POST',
            data: JSON.stringify(payload),
            contentType: 'application/json',
            success: function (data) {
                window.location = data;
                self.onSuccess(successCallBack);

            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("error occurred");
                var errors = {
                    'jqXHR': jqXHR,
                    'textStatus': textStatus,
                    'errorThrown': errorThrown
                };
                self.onError(errorCallback, errors);
            }
    });
};


    FenixReports.prototype.onSuccess = function(callback) {
        console.log('onSuccess');
        if(callback && callback!== null) {
            callback();
        }
    };


    FenixReports.prototype.onError = function(callback, errors) {
        console.log('onSuccess');
        if(callback && callback!== null) {
            callback(errors);
        }
    };


    return FenixReports;
})
define(['jquery', 'validator', "fx-rp-config"], function ($, Validator, PluginCOFIG) {

    'use strict'

    var validator, pluginChosen

    var o = {
        "success":""
    }


    function FenixReports() {

        this.o = o;

        this.o.success = function(Plugin) {
            pluginChosen = new Plugin;
        }

        this.o.error = function() {
          console.error("Something went wrong on plugin creation")
        }

        validator = new Validator
    }


    FenixReports.prototype.init = function(plugin) {

        if(typeof plugin!== 'undefined' && plugin!== null && plugin !== '' &&
            typeof  PluginCOFIG[plugin] !== 'undefined' && PluginCOFIG[plugin]){
            require([''+PluginCOFIG[plugin]], o.success, o.error)
        }
        else  {
            throw new Error('please define a valid plugin name')
        }
    }


    // second version
    FenixReports.prototype.exportData = function (config, url) {

        var payload = pluginChosen.process(config)

/*
        validator.checkPayload(payload, url)
*/

        var that = this;

        $.ajax({
            url: url,
            crossDomain: true,
            type: 'POST',
            data: JSON.stringify(payload),
            contentType: 'application/json',
            /* beforeSend: that.loadAnimatedGif(),*/
            success: function (data) {
                window.location = data;
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("error occurred");
                console.log('jqXHR error:')
                console.log(jqXHR)
                console.log('textStatus error:')
                console.log(textStatus)
                console.log('errorThrown error:')
                console.log(errorThrown)
            }
    })

}



    return FenixReports;
})
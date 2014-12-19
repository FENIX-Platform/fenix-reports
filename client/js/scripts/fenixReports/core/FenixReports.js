define(['jquery', 'validator'], function ($, Validator) {

    'use strict'


    var validator


    function FenixReports() {
        validator = new Validator
    }


    FenixReports.prototype.exportData = function (payload, url) {

        validator.checkPayload(payload, url)

        $.ajax({
            url: url,
            crossDomain: true,
            type: 'POST',
            data: JSON.stringify(payload),
            contentType: 'application/json',
            success: function (data) {
                console.log('success export')
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
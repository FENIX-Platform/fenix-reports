define(['jquery', 'validator'], function ($, Validator) {

    var validator

    'use strict'


    function FenixReports() {
        validator = new Validator
    }


    FenixReports.prototype.exportData = function (resource, input, output, url) {
        validator.checkAll(resource, input, output, url)

        var payload = {}
        payload['input'] = input;
        payload['output'] = input;
        payload['resource'] = resource;

        $.ajax({
            url: url,
            crossDomain: true,

            dataType: "json",
            type: 'POST',
            data: JSON.stringify(payload),
            contentType: 'application/json',
            mimeType: 'application/json',
            success: function (data) {

                alert('Hi');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("error occurred");
            }

        })
    }


    FenixReports.prototype.exportDataPayload = function (payload, url) {

        validator.checkPayload(payload, url)

        console.log('qqqqq')

        $.ajax({
            url: url,
            crossDomain: true,

            dataType: "json",
            type: 'POST',
            data: JSON.stringify(payload),
            contentType: 'application/json',
            mimeType: 'application/json',
            success: function (data) {

                alert('Hi');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("error occurred");
            }
        })

        console.log('asdasdas')
    }


    return FenixReports;
})
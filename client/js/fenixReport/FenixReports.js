define(['jquery', 'validator', 'text!fenixForm/formExport.html', 'jquery.fileDownloader'],
    function ($, Validator, Form) {

    var validator, formFenixDOM

    'use strict'


    function FenixReports() {
        validator = new Validator

       $('#toAppendForm').append(Form)

       // formFenixDOM = new DOMParser().parseFromString(Form, "text/xml");

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
            success: function() {
                window.location = url;
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("error occurred");
            }

        })
    }


    FenixReports.prototype.exportDataPayload = function (payload, url) {

        this.onSubmitForm();

        validator.checkPayload(payload, url)

        debugger;

        document.getElementById('formStandardExportFenix').action = url
        document.getElementById('payloadExportStandardFenix').value =JSON.stringify(payload);





        /*

                $.ajax({
                    url: url,
                    crossDomain: true,
                    type: 'POST',
                    data: JSON.stringify(payload),
                    contentType: 'application/json',
                    success: function (data) {

                        alert('Hi');
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        alert("error occurred");
                        console.log(jqXHR)
                        console.log(textStatus)
                        console.log(errorThrown)
                        debugger;


                    }
                })
        */
        console.log('asdasdas')
    }


  FenixReports.prototype.onSubmitForm = function () {

      $(document).on("submit", "form.fileDownloadForm", function (e) {
          $.fileDownload($(this).prop('action'), {
              preparingMessageHtml: "We are preparing your report, please wait...",
              failMessageHtml: "There was a problem generating your report, please try again.",
              httpMethod: "POST",
              data: $(this).serialize()
          });
          e.preventDefault(); //otherwise a normal form submit would occur
      });

  }


    return FenixReports;
})
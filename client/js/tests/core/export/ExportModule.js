define(['jquery', 'text!form/form.html', 'loaderData', 'configurator'],
	function($, Form, DataLoader, Configurator){

        'use strict'
	
		var dataLoader, configurator, downloader;


		function ExportModule(){
			dataLoader = new DataLoader;
			configurator = new Configurator;

		}


		ExportModule.prototype.init = function(){
			console.log('ExportModule.init()')
			var that = this;

		

			$("#exportBtn").on("click", function(event){
				event.preventDefault();
				that.createForm();

			})

		}

		ExportModule.prototype.createForm = function(){

            /*

			$('#toAppendForm').append(Form)
			this.fillFields();

			document.getElementById('submitButton').click(function(e){
	            e.preventDefault();
	            e.stopImmediatePropagation();
	        });
	        */

            var jsonObj = dataLoader.getPayload()
            var url = configurator.getUrlExport()
            debugger;
            $.ajax({
                url: url,
                crossDomain: true,

                dataType: "json",
                type: 'POST',
                data: jsonObj,
                contentType: 'application/json',
                mimeType: 'application/json',
                success: function(data) {

                    alert('Hi');
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    alert("error occurred");
                }

            })
        }

		ExportModule.prototype.fillFields = function(){

			document.getElementById('inputConfiguration').value = dataLoader.getInput();
			document.getElementById('outputConfiguration').value = dataLoader.getOutput();
			document.getElementById('data').value = dataLoader.getData();
			document.getElementById('metadata').value = dataLoader.getMetaData();

			var url = configurator.getUrlExport()


			document.getElementById('fx-exportForm').setAttribute('action',url)

		}



	return ExportModule;
})
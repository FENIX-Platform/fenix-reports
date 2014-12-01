define(['jquery', 'text!form/form.html', 'text!config/olap/data/data.json'], function($, Form){
	

	function ExportModule(){}

	ExportModule.prototype.init = function(){
		console.log('ExportModule.init()')
		var that = this;

		$("#exportBtn").on("click", function(event){
			event.preventDefault();
			that.createForm();

		})

	}

	ExportModule.prototype.createForm = function(){
		$("#toAppendForm").append(Form);

	}

	return ExportModule;
})
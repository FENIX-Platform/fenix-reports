define(['jquery','text!services/servicesLocal.json'], function($, Services){
	
	'use strict'

	function Configurator(){}

	Configurator.prototype.getUrlExport = function(){
		return $.parseJSON(Services).exportUrl

	}


	return Configurator;
})
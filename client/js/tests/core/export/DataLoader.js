define(['jquery', 'text!olapInput', 'text!olapOutput', 'text!olapData', 'text!olapMetadata'], 
	function($, Input, Output,Data,Metadata){

		'use strict'

		function DataLoader(){}

		DataLoader.prototype.getInput = function(){
			if(Input){
				return Input;
			}

		}

		DataLoader.prototype.getOutput = function(){
			if(Output){
				return Output;
			}

		}


		DataLoader.prototype.getData = function(){
			if(Data){
				return Data;
			}

		}


		DataLoader.prototype.getMetaData = function(){
			if(Metadata){
				return Metadata;
			}

		}

		return DataLoader;

})
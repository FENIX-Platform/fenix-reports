define(['jquery', 'text!tableInput', 'text!tableOutput', 'text!tableData', 'text!tableMetadata', 'text!tablePayload'],
	function($, Input, Output,Data,Metadata, Payload){

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
            debugger;
			if(Data){
				return Data;
			}

		}


		DataLoader.prototype.getMetaData = function(){
			if(Metadata){
				return Metadata;
			}

		}


        DataLoader.prototype.getPayload = function(){
            if(Payload){
                return Payload;
            }

        }

		return DataLoader;

})
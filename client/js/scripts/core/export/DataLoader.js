define(['jquery', 'olapInput', 'olapOutput', 'olapData', 'olapMetadata'], 
	function($, Input, Output,Data,Metadata){

	function DataLoader(){}

	DataLoader.prototype.getInput = function(Input){
		if(Input){
			return Input;
		}

	}

	DataLoader.prototype.getOutput = function(Output){
		if(Output){
			return Output;
		}

	}


	DataLoader.prototype.getData = function(Data){
		if(Data){
			return Data;
		}

	}


	DataLoader.prototype.getMetaData = function(Metadata){
		if(Metadata){
			return Metadata;
		}

	}

	return DataLoader;

})
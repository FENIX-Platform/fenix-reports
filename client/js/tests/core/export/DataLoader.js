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

		return DataLoader;

})

/**
 * Created by fabrizio on 12/15/14.
 */
define(['jquery'], function($){

    'use strict'


    var errors = {
        "output":"Error on output configuration ",
        "input":"Error on Input configuration ",
        "input-Plugin":"Error on Plugin field  in input file",
        "output-Plugin":"Error on Plugin field  in output file",
        "data":"Error on Data field  in resource file",
        "metadata":"Error on Metadata field  in resource file",
        "url":"Error on Url service"
    }

    function Validator(){}

    Validator.prototype.checkAll = function(resource, url){

        var outputCondition = output && output!=null && output!='';
        var inputCondition =  input && input!=null && input!='';
        var inputPluginCondition = input['plugin'] && input['plugin']!=null && input['plugin']!='';
        var outputPluginCondition= output['plugin'] && output['plugin']!=null && output['plugin']!='';
        var dataCondition  = resource['data'] && resource['data']!=null && resource['data']!='';
        var metadataCondition = resource['metadata'] && resource['metadata']!=null && resource['metadata']!='';
        var urlCondition =  url && url!=null && url!='';

        switch (false){

            case outputCondition:
                throw errors['output'];
                break;

            case inputCondition:
                throw errors['input'];
                break;

            case inputPluginCondition:
                throw errors['input-Plugin'];
                break;

            case outputPluginCondition:
                throw errors['output-Plugin'];
                break;

            case dataCondition:
                throw errors['data'];
                break;

            case metadataCondition:
                throw errors['metadata'];
                break;

            case urlCondition:
                throw errors['url'];
                break;
        }

    }

    Validator.prototype.checkEveryField = function(resource, url){

        var outputCondition = output && output!=null && output!='';
        var inputCondition =  input && input!=null && input!='';
        var inputPluginCondition = input['plugin'] && input['plugin']!=null && input['plugin']!='';
        var outputPluginCondition= output['plugin'] && output['plugin']!=null && output['plugin']!='';
        var dataCondition  = resource['data'] && resource['data']!=null && resource['data']!='';
        var metadataCondition = resource['metadata'] && resource['metadata']!=null && resource['metadata']!='';
        var urlCondition =  url && url!=null && url!='';

        var outputCondition = resource['output'] && resource['output']!=null && resource['output']!='';
        var inputCondition =  resource['input'] && resource['input']!=null && resource['input']!='';
        var inputPluginCondition = resource['input']['plugin'] && resource['input']['plugin']!=null && resource['input']['plugin']!='';
        var outputPluginCondition= resource['output']['plugin'] && resource['output']['plugin']!=null && resource['output']['plugin']!='';
        var dataCondition  = resource['data'] && resource['data']!=null && resource['data']!='';
        var metadataCondition = resource['metadata'] && resource['metadata']!=null && resource['metadata']!='';
        var urlCondition =  resource['url'] && resource['url']!=null && resource['url']!='';


        switch (false){

            case outputCondition:
                throw errors['output'];
                break;

            case inputCondition:
                throw errors['input'];
                break;

            case inputPluginCondition:
                throw errors['input-Plugin'];
                break;

            case outputPluginCondition:
                throw errors['output-Plugin'];
                break;

            case dataCondition:
                throw errors['data'];
                break;

            case metadataCondition:
                throw errors['metadata'];
                break;

            case urlCondition:
                throw errors['url'];
                break;
        }

    }




    return Validator;
})

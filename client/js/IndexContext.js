/**
 * Created by fabrizio on 5/20/14.
 */
define(["export", "fenixExport"], function( Export, Report ) {

    'use strict'

    var exportModule, report;

    function IndexContext(){
        report = new Report
        exportModule = new Export;
    };

    IndexContext.prototype.init = function() {
       // exportModule.init() ;
        var url  ="http://localhost:8080/fenix/export2"
        var payload = {     "resource" : {         "metadata" : {             "uid" : "test1",             "version" : "1",              "dsd" : {                 "columns" : [                     {                         "id" : "col1",                         "dataType" : "code"                     },                     {                         "id" : "col2",                         "dataType" : "year"                     }                 ]             }         },          "data" : [             [1,2,3],             [4,5,6],             [7,8,9]         ]     },      "input" : {         "plugin" : "inputTable",         "config" : {             "a" : "1",             "b" : 2,             "c" : [                 1,2,3             ]         }     },      "output" : {         "plugin" : "outputTable",         "config" : {             "a" : "1",             "b" : 2,             "c" : [                 1,2,3             ]         }     } }

        console.log(report)
        report.exportDataPayload(payload,url )
    };

    return IndexContext;



});

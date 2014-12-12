var olapData = ""

// Place third party dependencies in the lib folder
requirejs.config({
    "baseUrl":              "js/tests/libs",
    "paths": {
        jquery            :  "jquery",
        downloader        :  "jquery.fileDownloader",
        loaderData        :  "../core/export/DataLoader",
        export            :  "../core/export/ExportModule",
        form              :  "../core/viewForm", 
        config            :  "../configurations",
        services          :  "../configurations/services",
        configurator      :  "../configurations/services/configurator/Configurator",
        olapData          :  "../configurations/olap/data/data.json",
        olapInput         :  "../configurations/olap/input/input.json",
        olapMetadata      :  "../configurations/olap/metadata/metadata.json",
        olapOutput        :  "../configurations/olap/output/output.json",
        tableData         :  "../configurations/table/data/data.json",
        tableInput        :  "../configurations/table/input/input.json",
        tableMetadata     :  "../configurations/table/metadata/metadata.json",
        tableOutput       :  "../configurations/table/output/output.json",
        tablePayload      :  "../configurations/table/entireJSON/payload.json"
    },
    "shim": {
        "bootstrap": {
            deps: ["jquery"]
        },
        "jquery.fileDownloader":{
            deps:["jquery"]
        }
    }

});


require(["../../IndexContext", "domReady!", "bootstrap"], function(IndexContext) {
    console.log("index.js() - require() on domReady!");

   var indexContext = new IndexContext;
    indexContext.init();

});








var olapData = ""

// Place third party dependencies in the lib folder
requirejs.config({
    "baseUrl":              "js/tests/libs",
    "paths": {
        jquery            :  "jquery",
        loaderData        :  "../core/export/DataLoader",
        export            :  "../core/export/ExportModule",
        form              :  "../core/viewForm", 
        config            :  "../configurations",
        validator         :  "../../fenixReport/validator/Validator",
        fenixExport       :  "../../fenixReport/FenixReports",
        services          :  "../configurations/services",
        configurator      :  "../configurations/services/configurator/Configurator",
        olapData          :  "../configurations/olap/data/data.json",
        olapInput         :  "../configurations/olap/input/input.json",
        olapMetadata      :  "../configurations/olap/metadata/metadata.json",
        olapOutput        :  "../configurations/olap/output/output.json",
        tableData          :  "../configurations/table/data/data.json",
        tableInput         :  "../configurations/table/input/input.json",
        tableMetadata      :  "../configurations/table/metadata/metadata.json",
        tableOutput        :  "../configurations/table/output/output.json"
    },
    "shim": {
        "bootstrap": {
            deps: ["jquery"]
          }
      }
});


require(["../../IndexContext", "domReady!", "bootstrap"], function(IndexContext) {
    console.log("index.js() - require() on domReady!");

   var indexContext = new IndexContext;
    indexContext.init();

});








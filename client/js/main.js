var olapData = ""

// Place third party dependencies in the lib folder
requirejs.config({
    "baseUrl":              "js/scripts/libs",
    "paths": {
        jquery            :  "jquery",
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
        gridData          :  "../configurations/grid/data/data.json",
        gridInput         :  "../configurations/grid/input/input.json",
        gridMetadata      :  "../configurations/grid/metadata/metadata.json",
        gridOutput        :  "../configurations/grid/output/output.json",
      
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








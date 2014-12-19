requirejs.config({
    "baseUrl":              "js/libs",
    "paths": {
        jquery            :  "jquery",
        testEnv           :  "../scripts/fenixReports/test",
        testfakeClient    :  "../scripts/fenixReports/test/fakeHost/FakeHost",
        validator         :  "../scripts/fenixReports/core/validator/Validator",
        fenixExport       :  "../scripts/fenixReports/core/FenixReports"
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








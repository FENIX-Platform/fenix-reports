// Place third party dependencies in the lib folder
requirejs.config({
    "baseUrl":              "js/scripts/libs",
    "paths": {
        jquery            :  "jquery",
        testEnv           :  "../fenixReports/test",
        validator         :  "../fenixReports/core/validator/Validator",
        fenixExport       :  "../fenixReports/core/FenixReports",
        testfakeClient    :  "../fenixReports/test/fakeHost/FakeHost"
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















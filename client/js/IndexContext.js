/**
 * Created by fabrizio on 5/20/14.
 */
define(["export", "fenixExport"], function (Export, Report) {

    'use strict'

    var exportModule, report;

    function IndexContext() {
        report = new Report
        exportModule = new Export;
    };

    IndexContext.prototype.init = function () {
        // exportModule.init() ;
        var url = "http://localhost:8080/fenix/export2"
        var payload =
        {
            "resource": {
                "metadata": {
                    "uid": "test1",
                    "version": "1",

                    "dsd": {
                        "columns": [
                            {
                                "id": "col1",
                                "dataType": "code",
                                "key": true,
                                "values": {
                                    "codes": [
                                        {
                                            "codes": [
                                                {
                                                    "code": "1",
                                                    "label": {
                                                        "EN": "Algeria"
                                                    }
                                                },
                                                {
                                                    "code": "2",
                                                    "label": {
                                                        "EN": "Angola"
                                                    }
                                                },
                                                {
                                                    "code": "3",
                                                    "label": {
                                                        "EN": "Angola"
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                },
                                "title": {
                                    "EN": "NAtions",
                                    "FR": ""
                                }
                            },
                            {
                                "id": "col2",
                                "dataType": "year",
                                "key": true,
                                "title": {
                                    "EN": "date",
                                    "FR": ""
                                }

                            },
                            {
                                "id": "col2",
                                "dataType": "number",
                                "key": true,
                                "title": {
                                    "EN": "value",
                                    "FR": ""
                                }

                            }
                        ]
                    }
                },

                "data": [
                    ["1", 2001, 3],
                    ["2", 2003, 6],
                    ["3", 2006, 9]
                ]
            },

            "input": {
                "plugin": "inputTable",
                "config": {
                    "a": "1",
                    "b": 2,
                    "c": [
                        1, 2, 3
                    ]
                }
            },

            "output": {
                "plugin": "outputTable",
                "config": {
                    "columns": [
                        {
                            "formatDate": "",
                            "formatValue": "",
                            "formatCode": ""
                        },
                        {
                            "formatDate": "",
                            "formatValue": "",
                            "formatCode": ""
                        },
                        {
                            "formatDate": "",
                            "formatValue": "",
                            "formatCode": ""
                        }
                    ]

                }
            }
        }
        console.log(report)
        report.exportDataPayload(payload, url)
    };

    return IndexContext;


});

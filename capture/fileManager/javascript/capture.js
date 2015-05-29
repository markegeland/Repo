/* 
==========================================================================================================================
       Name: capture.js
     Author: John Palubinskas
Create date: May 2015
Description: JavaScript file intended to be used across all pages of the application
             including Login and Admin screens.

Updates
20150527 John Palubinskas - #609 Moved the Environment label routine here since it is called on all pages, including login.

==========================================================================================================================
*/
require([], function() {
    require.ready(function() {
        
        // Set the Environment label (using the td.nav-left element since it exists on all pages)
        var environmentName = "Environment: ";
        if (_BM_HOST_COMPANY == "devrepublicservices") { environmentName += "Dev"; }
        if (_BM_HOST_COMPANY == "dev2republicservices") { environmentName += "Dev 2"; }
        if (_BM_HOST_COMPANY == "testrepublicservices") { environmentName += "Test"; }
        if (_BM_HOST_COMPANY == "testsfdcrepublicservices") { environmentName += "Test SFDC"; }
        if (_BM_HOST_COMPANY == "trainrepublic") { environmentName += "Training"; }
        if (_BM_HOST_COMPANY == "republicservices") { environmentName = ""; }  // Prod should be blank

        var navLeft = document.getElementById("environment");
        navLeft.innerHTML = environmentName;

        var favorite = document.createElement("link");
        favorite.type = "image/x-icon";
        favorite.rel = "shortcut icon";
        favorite.href = "http://republicservices.com/PublishingImages/favicon.ico";
        document.getElementsByTagName("head")[0].appendChild(favorite);

    });
});


  ( bingMap.tryCreateLocation = function(){
	var searchManager = null;
 var directionsManager = null;
 var map = null;
var latitudeCustomerSite ;
var longitudeCustomerSite;

 var abcd = {};
	//bingMapsKey_quote
 //abcd.tryCreateLocation= function(){
	var requiredAttr = document.getElementById('bingMapCustomerSiteHTML_quote');
	if (!requiredAttr){
		return;
	}
	map = new Microsoft.Maps.Map(document.getElementById('bingMapCustomerSiteHTML_quote'), {credentials: 'bingMapsKey', width: 650, height: 450});
	Microsoft.Maps.loadModule('Microsoft.Maps.Search', { callback: reverseGeocodeRequest });
	Microsoft.Maps.loadModule('Microsoft.Maps.Directions', { callback: createDrivingRoute });	
//}
 createSearchManager = function() 
{
    if (!searchManager) 
    {
        map.addComponent('searchManager', new Microsoft.Maps.Search.SearchManager(map)); 
        searchManager = map.getComponent('searchManager'); 
    }
}

 reverseGeocodeRequest=function() 
{ 
    createSearchManager(); 
	
	 latitudeCustomerSite = $('input[name=_siteAddress_quote_company_name]').val();
	 longitudeCustomerSite = $('input[name=_siteAddress_quote_company_name2]').val();
	
	console.log(latitudeCustomerSite + " , " + longitudeCustomerSite);
    var userData = { name: 'Anitha Majji', id: 'bingMapsKey'};  
    var request = 
    { 
        location: new Microsoft.Maps.Location(latitudeCustomerSite, longitudeCustomerSite), //Home
        count: 5, 
        bounds: map.getBounds(), 
        callback: onReverseGeocodeSuccess, 
        errorCallback: onReverseGeocodeFailed, 
        userData: userData 
    };
	searchManager.reverseGeocode(request);	
	
		
} 
onReverseGeocodeSuccess=function(result, userData) 
{ 
    if (result) { 
        //map.entities.clear(); 
        var topResult = result.results && result.results[0]; 
        //if (topResult) { 
            var pushpin = new Microsoft.Maps.Pushpin(result.location, null); 
            map.setView({ center: result.location, zoom: 10 }); 
            map.entities.push(pushpin); 
       // } 
    } 
} 

 onReverseGeocodeFailed=function(result, userData) { 
    console.log('Rev geocode failed'); 
} 
if (searchManager) 
{ 
    reverseGeocodeRequest(); 
} 
else 
{ 
    Microsoft.Maps.loadModule('Microsoft.Maps.Search', { callback: reverseGeocodeRequest }); 
}
/*
function createDirectionsManager() {
    var displayMessage = "";
    if (!directionsManager) {
        directionsManager = new Microsoft.Maps.Directions.DirectionsManager(map);
        displayMessage = 'Directions Module loaded<BR>';
        displayMessage += 'Directions Manager loaded';
    }
    directionsManager.resetDirections();
    directionsErrorEventObj = Microsoft.Maps.Events.addHandler(directionsManager, 'directionsError', function (arg) { handleDirectionsError(arg) });
    //directionsUpdatedEventObj = Microsoft.Maps.Events.addHandler(directionsManager, 'directionsUpdated'});
}
function createDrivingRoute()
{
if (!directionsManager) { createDirectionsManager(); }
directionsManager.resetDirections();
// Set Route Mode to driving 
directionsManager.setRequestOptions({ routeMode: Microsoft.Maps.Directions.RouteMode.driving });
//var sourcepoint = new Microsoft.Maps.Directions.Waypoint({ address: 'Phoenix, AZ' });
newDisposalLatitude = latitudeDispSite1;
newDisposalLongitude = longitudeDispSite1;
if($('#alternateSite_l').val() == '2'){
	newDisposalLatitude = latitudeDispSite2;
	newDisposalLongitude = longitudeDispSite2;
}
if($('#alternateSite_l').val() == '3'){
	newDisposalLatitude = latitudeDispSite3;
	newDisposalLongitude = longitudeDispSite3;
}
console.log(latitudeDispSite1 + " , " + latitudeDispSite2 + " , " + latitudeDispSite3);
console.log(newDisposalLatitude+ " , " + newDisposalLatitude + " , " + newDisposalLongitude);
var sourcepoint = new Microsoft.Maps.Directions.Waypoint({location: new Microsoft.Maps.Location(latitudeCustomerSite, longitudeCustomerSite)}); //Home
directionsManager.addWaypoint(sourcepoint);
//var tacomaWaypoint = new Microsoft.Maps.Directions.Waypoint({ address: 'Scottsdale, AZ'});//, location: new Microsoft.Maps.Location(47.255134, -122.441650) });
var destinationpoint = new Microsoft.Maps.Directions.Waypoint({location: new Microsoft.Maps.Location(newDisposalLatitude, newDisposalLongitude)}); //Office
directionsManager.addWaypoint(destinationpoint);
// Set the element in which the itinerary will be rendered
directionsManager.setRenderOptions({ itineraryContainer: document.getElementById('directionsItinerary') });
directionsManager.calculateDirections();
}

if (!directionsManager)
{
Microsoft.Maps.loadModule('Microsoft.Maps.Directions', { callback: createDrivingRoute });
}
else
{
createDrivingRoute();
}*/
 } );
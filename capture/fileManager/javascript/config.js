/**
 * @param dependencies {Array} name of modules this code depends on. Can exclude ".js"
 * @param callback {Function} function containing this module's functionality.
 * @version Fri Feb 25 18:44:56 2011
 */
  
require([], function(rtq) {
  /*
   * Put all functions for homepage here
   */

  //this function runs when the page loads
  
  require.ready(function(){
	var searchManager = null;
 var directionsManager = null;
 var map = null;
var latitudeCustomerSite ;
var longitudeCustomerSite;
var latitudeDispSite1 ;
var longitudeDispSite1;
				$('#return_to_quote').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');

//Get the BingMapKey -  License
var bingMapKey = $('input[name=bingMapsKey]').val();
//Get the selected index of Disposable site that need to be routed
var selectedIndex = $('#alternateSite_l').val();

//Highlight the Row Selected - applying background color to the table cell
var rowSelector = "#disposalSitesArraySet > table > tbody > tr:nth-child("+selectedIndex+") > td";
$(rowSelector).css("border","1pt solid black");

//Construct dynamically selected index latitude & longitude field id's
var latitudeDispSite1Id = "#latitude_l-"+(selectedIndex-1);
var longitudeDispSite1Id = "#longitude_l-"+(selectedIndex-1);

latitudeDispSite1 = $(latitudeDispSite1Id).val();
longitudeDispSite1 = $(longitudeDispSite1Id).val();

var disposalSitesSize = $('input[name=disposalSitesSize]').val();

//Only for debugging
var d = new Date();
var start = d.getMilliseconds();

//Create Map interface with valid credentials
//function tryCreateLocation(){
	var requiredAttr = document.getElementById('bingMap_html');
	if (!requiredAttr){
		return;
	}
	map = new Microsoft.Maps.Map(document.getElementById('bingMap_html'), {credentials: bingMapKey, width: 650, height: 450});
	var locs = [];
	Microsoft.Maps.loadModule('Microsoft.Maps.Search', { callback: reverseGeocodeRequest });
	Microsoft.Maps.loadModule('Microsoft.Maps.Directions', { callback: createDrivingRoute });	
	
	var bestview = Microsoft.Maps.LocationRect.fromLocations(locs);
	map.setView({bounds:bestview });
	
//}
function createSearchManager() 
{
    if (!searchManager) 
    {
        map.addComponent('searchManager', new Microsoft.Maps.Search.SearchManager(map)); 
        searchManager = map.getComponent('searchManager'); 
    }
}

//Set customer & disposal site locations on the map
function reverseGeocodeRequest() 
{ 
    createSearchManager(); 
	
	latitudeCustomerSite = $('input[name=latitudeCustomerSite]').val();
	longitudeCustomerSite = $('input[name=longitudeCustomerSite]').val();
	
    var userData = { name: 'Republic', id: bingMapKey};  
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
	
	//Iterate on all available disposal sites for current configuration
	for(var i=1; i <= disposalSitesSize; i++){
		var latitudeDispSiteId = "#latitude_l-"+(i-1);
		var longitudeDispSiteId = "#longitude_l-"+(i-1);
		var latitudeDispSite = $(latitudeDispSiteId).val();
		var longitudeDispSite = $(longitudeDispSiteId).val();
		
		var request1 =  
		{
			location: new Microsoft.Maps.Location(latitudeDispSite, longitudeDispSite), //Office
			count: 5, 
			bounds: map.getBounds(), 
			callback: onReverseGeocodeSuccess, 
			errorCallback: onReverseGeocodeFailed, 
			userData: userData 
		};
		
		searchManager.reverseGeocode(request1);
	}
} 
//Set Map zoom level & add push pins at locations
function onReverseGeocodeSuccess(result, userData) 
{ 
    if (result) { 
        //map.entities.clear(); 
        var topResult = result.results && result.results[0]; 
        //if (topResult) { 
            var pushpin = new Microsoft.Maps.Pushpin(result.location, null); 
			arr.push(result.location);
            map.setView({ center: result.location, zoom: Number($('input[name=bingMapsConfigZoomLevel_quote]').val()) }); 
            map.entities.push(pushpin); 
       // } 
    } 
} 

function onReverseGeocodeFailed(result, userData) { 
    //console.log('Rev geocode failed'); 
} 
if (searchManager) 
{ 
    reverseGeocodeRequest(); 
} 
else 
{ 
    Microsoft.Maps.loadModule('Microsoft.Maps.Search', { callback: reverseGeocodeRequest }); 
}

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
function handleDirectionsError(arg) {
	if (arg.responseCode == 12) {
	  showDisambiguation(arg);
	}
	else {
	  showDirectionsError(arg.message);
	}
}

function showDirectionsError(message){
	var displayString = "Event Info: Directions Error\n Message: " + message;
	//console.log(displayString);
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

	var sourcepoint = new Microsoft.Maps.Directions.Waypoint({location: new Microsoft.Maps.Location(latitudeCustomerSite, longitudeCustomerSite)}); //Home
	directionsManager.addWaypoint(sourcepoint);
	
	var destinationpoint = new Microsoft.Maps.Directions.Waypoint({location: new Microsoft.Maps.Location(newDisposalLatitude, newDisposalLongitude)}); //Office
	directionsManager.addWaypoint(destinationpoint);
	// Set the element in which the itinerary will be rendered
	//directionsManager.setRenderOptions({ itineraryContainer: document.getElementById('directionsItinerary') });
	directionsManager.calculateDirections();
}


if (!directionsManager)
{
Microsoft.Maps.loadModule('Microsoft.Maps.Directions', { callback: createDrivingRoute });
}
else
{
createDrivingRoute();
}
 } );
});
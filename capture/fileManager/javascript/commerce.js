/**
 * @param dependencies {Array} name of modules this code depends on. Can exclude ".js"
 * @param callback {Function} function containing this module's functionality.
 * @version Fri Feb 25 18:44:56 2011
 */
require(["return_to_quote_button", "commerce_ids", "excanvas", "jquery_cookie", "highcharts"], function (rtq) {
	/*
     * Put all functions for commerce here
     */

    //this function runs when the page loads
    require.ready(function () {
        rtq.set_cookie_in_commerce();
		
		/* Make sure to comment all console.log commands, IE8 doesn't like them. It sucks :(*/
		// console.log('in chart');
		//Chart Rendering begins here 
		var chartWrapper = $("#attr_wrapper_1_chartContainer");
		//If chart container exists, render chart image
		if(chartWrapper.length > 0){	
			chart = new Highcharts.Chart({
				chart: { //Basic options for a chart
					renderTo: 'myChart',
					type: 'column',
					//width: 30,
					inverted: false,
					backgroundColor: null,
					borderWidth: 0,
					borderRadius: 10,
					spacingLeft : 40,
					//plotBackgroundColor: '#ffffff',
					plotShadow: false,
					plotBorderWidth: 0
				},
				plotOptions: {
					column: {
						pointPadding: 0,
						groupPadding: 0
					}
				},
				credits: { // Credits disabled for Highcharts.com
					enabled: false
				},
				tooltip: { // Tool Tip disabled
					enabled: false
				},
				title: {
					text: '', 
					align: 'right'	
				},
				legend: {
					enabled: false
				},
				exporting: { // Exporting this graph as a JPG, png has been disabled
					 enabled: false
				},
				xAxis: {
					max: 4,
					labels:
					{
						enabled: false
					}
				},
				yAxis: {
					min: Math.min(Number($('input[name=grandTotalBase_quote]').val().replace(/[^0-9\.]+/g, "")), Number($('input[name=grandTotalTarget_quote]').val().replace(/[^0-9\.]+/g, "")), Number($('input[name=grandTotalStretch_quote]').val().replace(/[^0-9\.]+/g, "")), Number($('input[name=grandTotalSell_quote]').val().replace(/[^0-9\.]+/g, ""))) - 20,
					labels: {
						enabled: true,
						format: '${value}'
					}, // Minimum of the four prices has to be taken so that Y axis starts from an appropriate number.
					title: {
						text: 'Prices'
					},
					gridLineWidth: 0,
					plotLines: [{
						value: Number($('input[name=grandTotalTarget_quote]').val().replace(/[^0-9\.]+/g, "")),
						dashStyle: 'Dash',
						color: '#000000',
						width: 1,
						zIndex: 4,
						label: {
							text: 'Average',
							align: 'right',
							 x: -10,
							y: 16
						}
					}, {
						value: Number($('input[name=grandTotalStretch_quote]').val().replace(/[^0-9\.]+/g, "")),
						dashStyle: 'Dash',
						color: '#000000',
						width: 1,
						zIndex: 2,
						label: {
							text: 'Target',
							align: 'right',
							 x: -10,
							y: 16
						}
					}, {
						value: Number($('input[name=grandTotalSell_quote]').val().replace(/[^0-9\.]+/g, "")),
						dashStyle: 'Solid',
						color: '#000000',
						width: 1,
						zIndex: 4,
						label: {
							text: 'Proposed',
							align: 'left',
							style: {
								color: 'black',
								fontWeight: 'bold'
							},
							 x: -10,
							y: 16
						}
					}, {
						value: Number($('input[name=grandTotalBase_quote]').val().replace(/[^0-9\.]+/g, "")),
						dashStyle: 'Dash',
						color: '#000000',
						width: 1,
						zIndex: 4,
						label: {
							text: 'Floor',
							align: 'right',
							 x: -10,
							y: 16
						}
					}] //Plot lines for each of the four prices
				},

				series: [{
					name: 'Text',
					marker: {
						enabled: false
					},
					color: {
						linearGradient: [0, 0, 0, 400],
						stops: [
							[0, '#0000A0'],
							[0.1, '#0000D0'],
							[0.2, '#0000FF'],
							[0.3, '#7070FF'],
							[0.4, '#ffffff'],
							[0.5, '#FFC0C0'],
							[0.6, '#FFA0A0'],
							[0.7, '#FF7070'],
							[0.8, '#FF4040'],
							[0.9, '#FF2020'],
							[0.95, '#ff0000'],
							[0.1, '#ff0000']
						] // This is a very important part of the graph where we define how the color changes from blue to white to red.
					},
					borderWidth: 1,
					shadow: false,
					data: [ [0,0],
						[2, Math.max(Number($('input[name=grandTotalBase_quote]').val().replace(/[^0-9\.]+/g, "")), Number($('input[name=grandTotalTarget_quote]').val().replace(/[^0-9\.]+/g, "")), Number($('input[name=grandTotalStretch_quote]').val().replace(/[^0-9\.]+/g, "")), Number($('input[name=grandTotalSell_quote]').val().replace(/[^0-9\.]+/g, "")) + 20)]
					] // make sure this is in descending order to show up, We are displaying the graph at point 2 of the X axis.

				}]
			});
			
		}//Chart Rendering ends here 
		
		
        //Hide Next Action in Select Services step when there are no products on the quote
        //console.log("start hide function");

        //if ($('input[name=_step_id]').val() == '5711925') { 
        if ($('input[name=_step_varname]').val() == 'selectServices') {  //Use step var name instead of id to keep it consistent across sites
            smallContainerExists = $('input[name=commercialExists_quote]').val().toLowerCase();
            largeContainerExists = $('input[name=industrialExists_quote]').val().toLowerCase();
            moveToAdjustPricing = $('input[name=moveFromselectServicesToAdjustPricing_quote]').val().toLowerCase();
            //console.log(smallContainerExists);
            if (moveToAdjustPricing == 'false') {
                if (smallContainerExists == 'false' && largeContainerExists == 'false') {
                    //console.log("here?");
                    $("#next").hide();
                }
            }
        }
		
		if ($('input[name=_step_varname]').val() == 'adjustPricing' || $('input[name=_step_varname]').val() == 'underManagerReview_process') {  //Use step var name instead of id to keep it consistent across sites
            //largeContainerExists = $('input[name=industrialExists_quote]').val().toLowerCase();
            //console.log(smallContainerExists);
			//if (largeContainerExists == 'true') {
			//console.log("here1");
			$('.child-line-item').each(function(index, element){
				if ($(element).find('input[name*=rateType_line]').val() == 'Installation'){
					//console.log("here in installation");
					cellToChange = $(element).find('input[name*=sellPrice_line]');
					newCell = $("<input type='hidden' class='form-input' />");
					$(newCell).attr({id : $(cellToChange).attr("id"), name : $(cellToChange).attr("name"), size : $(cellToChange).attr("size"), maxlength : $(cellToChange).attr("maxlength"), value : $(cellToChange).attr("value")});
					
					thisVal = $(cellToChange).val();
					//console.log("value");
					//console.log(thisVal);
					spanCell = $("<span class='readonly-wrapper'/>");
					$(spanCell).text(thisVal);

					$(newCell).insertBefore($(cellToChange));
					$(spanCell).insertBefore($(newCell));
					$(cellToChange).remove();
					
					$(spanCell).parent().attr("class", "field");
					$(spanCell).parent().parent().attr("class", "field-wrapper");

				}
			});
		}

        //Hide Goal Seek Action when there exists Large Container on quote
        var industrialExists = $("input[name='industrialExists_quote']").val();
		var adHocExists = $("input[name='adHocExists_quote']").val();
        if ((typeof industrialExists != "undefined" && industrialExists != null && industrialExists.toLowerCase() === "true") || (typeof adHocExists != "undefined" && adHocExists != null && adHocExists.toLowerCase() === "true")) {
            $('#recalculate_total').hide();
			$('#calculate_total_price').hide();
			$("label[for='goalSeekHTML_quote']").parent().css("visibility","hidden"); //Hide Desired Total column heading
        }
        sellPrice = Number($('input[name=grandTotalSell_quote]').val().replace(/[^0-9\.]+/g, ""));
        var ticks = ['Floor', 'Base', 'Average', 'Target', 'Sell'];

        //Bing Maps Rendering begins here
		var searchManager = null;
        var directionsManager = null;
        var map = null;
		var nearbySalesMapHTML = null;
		//Set default coordinates for North America
        var latitudeCustomerSite = "48.1667";
        var longitudeCustomerSite = "-100.1667";
		//var zoomLevel = 2; ZS 3/19/14 - Update to make this value dynamic
		var zoomLevel = Number($('input[name=bingMapsDefaultZoomLevel_quote]').val());
		var locs = [];
		
        bingMapsKey = $('input[name=bingMapsKey_quote]').val();
		var requiredAttr = $('#readonly_1_bingMapCustomerSiteHTML_quote');
		
		if($('input[name=_siteAddress_quote_company_name]').val() != ""){
			latitudeCustomerSite = $('input[name=_siteAddress_quote_company_name]').val();
			//zoomLevel = 15; ZS 3/19/14 - Update to make this value dynamic
			zoomLevel = Number($('input[name=bingMapsZoomLevelSite_quote]').val());
		}
		if($('input[name=_siteAddress_quote_company_name_2]').val() != ""){
			longitudeCustomerSite = $('input[name=_siteAddress_quote_company_name_2]').val();
		}
		
		//Render Map when the container exists on the page
		//Invoke Bing Map API only when longitude & latitude are available - check this, if RS agree then enable it to save on Bing API # transactions
        //if(requiredAttr.length > 0 && latitudeCustomerSite != "" &&  longitudeCustomerSite != "") {
        if(requiredAttr.length > 0) {
			$("#attr_wrapper_1_bingMapCustomerSiteHTML_quote").parent().css("width", "50%");
			//bingMapsKey_quote
			//abcd.tryCreateLocation= function(){
        
			map = new Microsoft.Maps.Map(document.getElementById('readonly_1_bingMapCustomerSiteHTML_quote'), {
				credentials: bingMapsKey,
				width: 550,
				height: 450
			});
			
			/*$(requiredAttr).css("width","500px !important");
			$(requiredAttr).css("height","450px !important");*/
			createSearchManager = function () {
				if (!searchManager) {
					map.addComponent('searchManager', new Microsoft.Maps.Search.SearchManager(map));
					searchManager = map.getComponent('searchManager');
				}					
			}
			
			function reverseGeocodeRequest() {
				createSearchManager();
				//console.log(latitudeCustomerSite + " , " + longitudeCustomerSite);
				var userData = {
					name: 'Republic Services',
					id: bingMapsKey
				};
				
				var request = {
					location: new Microsoft.Maps.Location(latitudeCustomerSite, longitudeCustomerSite), //Home
					mapTypeId: Microsoft.Maps.MapTypeId.road,
					count: 5,
					bounds: map.getBounds(),
					callback: onReverseGeocodeSuccess,
					errorCallback: onReverseGeocodeFailed,
					userData: userData
				};
				locs.push(new Microsoft.Maps.Location(latitudeCustomerSite, longitudeCustomerSite));
				searchManager.reverseGeocode(request);
				
			}
			
			Microsoft.Maps.loadModule('Microsoft.Maps.Search', {
				callback: reverseGeocodeRequest
			});
			
			function onReverseGeocodeSuccess(result, userData) {
				if (result) {
					//map.entities.clear(); 
					var topResult = result.results && result.results[0];
					//if (topResult) { 
					var pushpin = new Microsoft.Maps.Pushpin(result.location, null);
					map.setView({
						center: result.location,
						zoom: zoomLevel
						
					});
					map.entities.push(pushpin);					
					// } 
				}
			}

			function onReverseGeocodeFailed(result, userData) {
				//console.log('Rev geocode failed');
			}
			if (searchManager) {
				reverseGeocodeRequest();
			} else {
				Microsoft.Maps.loadModule('Microsoft.Maps.Search', {
					callback: reverseGeocodeRequest
				});
			}
			
		}//Ends if Map container exists	
		
		var requiredAttr1 = $('#readonly_1_nearbySalesMapHTML_quote');
		
		if(typeof requiredAttr1 != "undefined" && requiredAttr1 != null && requiredAttr1.length > 0){
			$("#readonly_1_nearbySalesMapHTML_quote").parent().css("width", "100%");
			
			nearbyServicesCustomerLocations = $('input[name=nearbyServicesCustomerLocations_quote]').val();//"Site 1@@09/09/2010@@$400@@$100.0@@10/11/2013@@33.91645@@-84.370336$$Site 2@@09/09/2012@@$400@@$100.0@@10/12/2013@@33.786821@@-84.113139"; //
			
			var dict = {}; // create an empty array
			
			customerLocationsArr = nearbyServicesCustomerLocations.split("$$");
			//These are nearby customer locations
			for ( var i = 0; i < customerLocationsArr.length; i = i + 1 ) {
				eachCustLocation = customerLocationsArr[i].split("@@");
				tempValue ="<div class='nearbyCustomerBalloon'><div>Account Name:" + eachCustLocation[0] + "</div>"
							 + "<div>Original Open Date :" + eachCustLocation[1] + "</div>"
							+ "<div> Current Rate:" + eachCustLocation[2] + "</div>"
							+ "<div>Total Revenue:" + eachCustLocation[3] + "</div>"
							+ "<div>Rate Effective Date:" + eachCustLocation[4] + "</div>"
							+ "<div>Admin Fee:" + eachCustLocation[7] + "</div>"
							+ "<div>ERF:" + eachCustLocation[8] + "</div>"
							+ "<div>FRF:" + eachCustLocation[9] + "</div></div>";
//				tempValue = "Account Name: " + eachCustLocation[0] + ", Original Open Date : " + eachCustLocation[1] + ", Current Rate : " + eachCustLocation[2] +", Total Revenue : " + eachCustLocation[3]+ ", Rate Effective Date: " + eachCustLocation[4] ;
				dict[eachCustLocation[5] + "," + eachCustLocation[6]] = tempValue;
			}
			
			//This is customer actual location
			dict[latitudeCustomerSite + "," + longitudeCustomerSite] = "<table><tr><td>Account Name:" + $('[name="siteName_quote"]').val() + "</td></tr>"
							 + "<tr><td>Original Open Date :" + "" + "</td></tr>"
							+ "<tr><td> Current Rate:" + $("#grandTotalSell_quote").val() + "</td></tr>"
							+ "<tr><td>Total Revenue:" + $("#grandTotalSell_quote").val()+ "</td></tr>"
							+ "<tr><td>Rate Effective Date:" + $('[name="effectiveServiceDate_quote"]').val() + "</td></tr></table>";
			
			nearbySalesMapHTML = new Microsoft.Maps.Map(document.getElementById('readonly_1_nearbySalesMapHTML_quote'), {
				credentials: bingMapsKey,
				width: 550,
				height: 450
			});
			var infoboxLayer = new Microsoft.Maps.EntityCollection();
			nearbySalesMapHTML.entities.push(infoboxLayer);

			infobox = new Microsoft.Maps.Infobox(new Microsoft.Maps.Location(0, 0), { visible: false, offset: new Microsoft.Maps.Point(0, 30) });
			infoboxLayer.push(infobox);
			
			createSearchManager = function () {
				if (!searchManager) {					
					nearbySalesMapHTML.addComponent('searchManager', new Microsoft.Maps.Search.SearchManager(nearbySalesMapHTML));
					searchManager = nearbySalesMapHTML.getComponent('searchManager');					
				}					
			}
			
			function reverseGeocodeRequest1() {
				createSearchManager();
				var userData = {
					name: 'Republic Services',
					id: bingMapsKey
				};
				
				var requestNew = {
					location: new Microsoft.Maps.Location(latitudeCustomerSite, longitudeCustomerSite,"test"), //Home
					mapTypeId: Microsoft.Maps.MapTypeId.road,
					count: 5,
					bounds: nearbySalesMapHTML.getBounds(),
					callback: onReverseGeocodeSuccess1,
					errorCallback: onReverseGeocodeFailed,
					userData: userData
				};
				locs.push(new Microsoft.Maps.Location(latitudeCustomerSite, longitudeCustomerSite));
				searchManager.reverseGeocode(requestNew);
				for ( var i = 0; i < customerLocationsArr.length; i = i + 1 ) {
					eachCustLocation = customerLocationsArr[i].split("@@");
					var request1 = {
						location: new Microsoft.Maps.Location(eachCustLocation[5], eachCustLocation[6]), 
						mapTypeId: Microsoft.Maps.MapTypeId.road,
						count: 5,
						bounds: nearbySalesMapHTML.getBounds(),
						callback: onReverseGeocodeSuccess1,
						errorCallback: onReverseGeocodeFailed,
						userData: userData
					};
					locs.push(new Microsoft.Maps.Location(eachCustLocation[5], eachCustLocation[6]));
					searchManager.reverseGeocode(request1);
					
				}
				bestview = Microsoft.Maps.LocationRect.fromLocations(locs)
				nearbySalesMapHTML.setView({bounds:bestview });
			}
			
			Microsoft.Maps.loadModule('Microsoft.Maps.Search', {
				callback: reverseGeocodeRequest1
			});
			
			function onReverseGeocodeSuccess1(result, userData) {
				if (result) {	
					var topResult = result.results && result.results[0];
					var supplierName = $("[name='supplierCompanyName_quote']").val();
					var imageUrl = "/bmfsweb/"+supplierName+"/image/images/locationMarker.png";
					var pushpin = new Microsoft.Maps.Pushpin(result.location, {icon: imageUrl});
					
					if(result.location.latitude == latitudeCustomerSite  && longitudeCustomerSite == result.location.longitude){
						var pushpin = new Microsoft.Maps.Pushpin(result.location, null);
					}
					
					//pushpin.Title = "Just title";
					pushpin.Description = dict[result.location.latitude + "," + result.location.longitude];
					pushpinClick= Microsoft.Maps.Events.addHandler(pushpin, 'click', displayInfobox);  					
					nearbySalesMapHTML.entities.push(pushpin);
				}
			}
			function displayInfobox(e,value) {
              if (e.targetType == 'pushpin') {
                  infobox.setLocation(e.target.getLocation());
                  infobox.setOptions({ visible: true,  description: e.target.Description });
              }
			}  

			function onReverseGeocodeFailed(result, userData) {
				//console.log('Rev geocode failed');
			}
			if (searchManager) {
				reverseGeocodeRequest1();
				
			} else {
				Microsoft.Maps.loadModule('Microsoft.Maps.Search', {
					callback: reverseGeocodeRequest1
				});
			}
		}
		
		//Bing Maps Rendering ends here
		
		//Remove tabindex property on address fields to follow attribute ordering
		$('input,select').removeAttr("tabindex");
		
		//Restrict users hitting Enter Key
		$('input,select').keypress(function(event) { return event.keyCode != 13; });
		
		// Based on Select Container group , we will update the container information into containerDetailsString_quote attribute to use it in the backend
		//var containerDetailsvalue = $("#containerDetailsDropdown").val();	
		var containerDetailsvalue = $("#availableContainers").val();	
		if(typeof containerDetailsvalue != "undefined"){
			//var selectdValue = val.options[val.selectedIndex].value;
			document.getElementsByName("containerDetailsString_quote")[0].value =containerDetailsvalue;	
			//$("[name='containerDetailsString_quote']").val(containerDetailsvalue);
			//$("#containerDetailsDropdown").change(function() {
			$("#availableContainers").change(function() {
				//console.log("on change called");
				//document.getElementsByName("containerDetailsString_quote")[0].value =$("#containerDetailsDropdown").val();	
				document.getElementsByName("containerDetailsString_quote")[0].value =$("#availableContainers").val();	
				//$("[name='containerDetailsString_quote']").val($("#availableContainers").val());
			});
		}
		//Select Services - Container Drop Down
		//$("select[name='pricingModel_quote']").change(function() {
		
		
		var containerGroupToDisplay = $("#containerGroupToDisplayDropdown");
		if(typeof containerGroupToDisplay != "undefined" && containerGroupToDisplay != null){
			var selectedOptn = $("#containerGroupToDisplayDropdown option:selected").text();
			if(selectedOptn != null && typeof selectedOptn !="undefined" && selectedOptn.length > 0){
				$("[name='containerGroupForTransaction_quote']").val(selectedOptn);
				//console.log("inside if not null=="+selectedOptn);
				tdId = "#grp"+selectedOptn;
				$(tdId).parent().css("border", "2px solid black");
				$(tdId).parent().css("text-align", "center");
				$(tdId).parent().css("font-weight", "bold");
				
			}
			
			$("#containerGroupToDisplayDropdown").change(function() {
				var selectedOptn = $("#containerGroupToDisplayDropdown option:selected").text();
				if(selectedOptn != null && typeof selectedOptn !="undefined" && selectedOptn.length > 0){
					$("[name='containerGroupForTransaction_quote']").val(selectedOptn);
					tdId = "#grp"+selectedOptn;
					$(tdId).parent().css("border", "2px solid black");
					$(tdId).parent().css("text-align", "center");
					$(tdId).parent().css("font-weight", "bold");
					//console.log("on change called");
				}
			});
		}
		
		//Show loading mask on Reconfigure
		$("div[actionid='4654410']").click(function(){showLoadingDialog();	});

    });
});
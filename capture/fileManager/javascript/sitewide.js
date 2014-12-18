/**
 * @param dependencies {Array} name of modules this code depends on. Can exclude ".js"
 * @param callback {Function} function containing this module's functionality.
 * @version Fri Feb 25 18:44:56 2011
 */
require([], function() {
  /*
   * Put all functions for sitewide context here
   */

  //this function runs when the page loads
  require.ready(function() {
	
		var formName = document.forms[0].name;
		
		//header-wrapper element exists on all pages
		var header = document.getElementById("header-wrapper");
		if(typeof(header) != "undefined" && header != null){
			var spanEle = document.createElement("span");
			header.appendChild(spanEle);
		}
		
		/* Make sure to comment all console.log commands - they are not supported in IE8, it sucks :(*/	
		//Start - used When User returns to commerce from Config(Returns to quote via 'Add To Quote' Action)
		//Following code will run in Commerce only
		function configure(model){
			//console.log(model);
			//Set the model name to configure	
			showLoadingDialog();
			$('input[name=modelToConfigure_quote]').val(model);			
			if(model == "containers_m"){
				$('#small_container').closest("table").click();
			}
			else if(model == "largeContainers"){
				$('#large_container').closest("table").click();
			}
			else{
				$('#additional_items').closest("table").click();
			}
			
		}
		
		//Hide Next Action in Select Services step when there are no products on the quote
		if(typeof(formName) !== "undefined" && formName == "bmDocForm"){
			//Remove tabindex property on address fields to follow attribute ordering
			$('input,select').removeAttr("tabindex");
		
			//Hide sticky action bar on top and add it at the bottom for Small containers
			if($('[name="sysUserType_quote"]').val() == 'SalesAgent'){
				$("#sticky-actions").hide();
			}
			//END

			var small = document.getElementById("small");
			var large = document.getElementById("large");
			var adhoc = document.getElementById("adhoc");
			
			if((typeof(small) !== "undefined" && small != null)|| (typeof(large) !== "undefined" && large != null)|| (typeof(adhoc) !== "undefined" && adhoc != null)){
				$('#small').click(function () { configure('containers_m'); });
				$('#large').click(function () { configure('largeContainers'); });
				$('#adhoc').click(function () { configure('adHocLineItems'); });
			}
		
			var stepVarName = document.getElementsByName('_step_varname'); //Use Step Var Name instead of Step Id to keep it consistent across sites
			if(typeof(stepVarName) !== "undefined" && stepVarName != null){
				//Start of All "Select Services" steps
				if($("input[name=_step_varname]").val() == 'selectServices' || $('input[name=_step_varname]').val() == 'selectServices_bmClone_1' || $('input[name=_step_varname]').val() == 'selectServices_bmClone_2' || $('input[name=_step_varname]').val() == 'selectServices_bmClone_3'){
					//Hide hidden action always
					$('#hidden_action').closest("table").hide();
					
					//Hiding Small container, Large Container and Additional Items action if PriceIncrease already exists on the quote
					var priceIncreaseExists = $('input[name=priceIncreaseQuote_quote]').val().toLowerCase();
					if(priceIncreaseExists == 'true'){
						$('#small_container').closest("table").hide();
						$('#large_container').closest("table").hide();
						$('#additional_items').closest("table").hide();
					}
				
					var smallContainerExists = $('input[name=commercialExists_quote]').val().toLowerCase();
					var largeContainerExists = $('input[name=industrialExists_quote]').val().toLowerCase();
					moveToAdjustPricing = $('input[name=moveFromselectServicesToAdjustPricing_quote]').val().toLowerCase();
					
					if(moveToAdjustPricing == 'false'){
						if(smallContainerExists == 'false' && largeContainerExists == 'false'){
							$("#return_to_pricing").hide();
						}
					}
					//Invoke 'Save and Price' action when a new line is added to the quote because step transition on add from catalogue is not working
					if(moveToAdjustPricing == 'true'){
						//Lock the screen before quote transitions
						showLoadingDialog();
						//Invoke step transition on Hidden action
						//setDocFormIds(4653823, 1, 5257278); 04/06/2014 - Not invoking Save and Price anymore from here, instead invoking Hidden Action which has similar behavior like Save and Price except that it does not accept any validations. This is required because Save and Price might have some validations in future and hence cannot be invoked from JS
						$('#hidden_action').closest("table").click();
					}
				}
				//End of "Select Services" steps
				if(typeof(largeContainerExists) != "undefined" && largeContainerExists === "true"){
					$("#calculate_total_price").closest("table").hide();
				}
				
				//Start of Adjust Pricing step
				if($("input[name=_step_varname]").val() == 'adjustPricing'){
					//Hide "View History" action if SalesActivity is New/New or New from competitor
					var salesActivity = $('input[name=salesActivity_RO_quote]').val().toLowerCase();
					if(salesActivity === "new/new" || salesActivity === "new from competitor"){
						$('#view_history').closest("table").hide();
					}	
				}
				if($("input[name=_step_varname]").val() == 'newCustomerAndSite_bmClone_1'){
					// If customer has already been selected, this can be grey
					$('#select_customer').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');

				}
				
				
				//End of Adjust Pricing step
								$('#delete').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#find_on_map').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#assign_quote').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#previous').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#recalculate_fees').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#calculate_total_price').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#select_additional_services').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#delete_selected').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#calculate_price').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#print_documents').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#email_documents').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#revise').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#view_history').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#create_site').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				
			}
		}
		//End  - script runs when user returns from config to commerce 
		
		
		/* Updating Button Labels for Configuration , Also swapping the positions of "Return To Quote" & "Add To Quote" buttons..."Previous" & "Next" Respectively */
		//Following code will run in Configuration only
		if(typeof(formName) != "undefined" && formName != null && formName == "configurationForm"){
			var return_to_quote = document.getElementById("return_to_quote");
			if(typeof(return_to_quote) !== "undefined" && return_to_quote !== null){
				$('#return_to_quote').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$('#previous').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
				$("#return_to_quote").html("Previous");
				
			}	
			var addTo_quote = document.getElementById("add_to_quote");
			if(typeof(addTo_quote) !== "undefined" && addTo_quote !== null){
				//console.log("in condition?");
				$("#add_to_quote").html("Next");
			}
			//change 'Save' label to 'Next'
			var saveAction = document.getElementById("save");
			if(typeof(saveAction) !== "undefined" && saveAction !== null){
				$("#save").html("Next");	
			}
		$('#update').closest("td.button-middle").css('backgroundColor', '#979FA2 !important');
			
		}
		

		//Swap Cancel & Next Buttons
		/*
		if( $($("table.button-invoke-add"))[0].length != 0 && $("table.button-invoke-return") != 0 ){	
			$($("table.button-invoke-add")[0]).insertAfter($($("table.button-invoke-return")[0]));
		}*/	
		//Reconfigure actions
		//Change 'update' label to 'Save'
		var updateAction = document.getElementById("update");
		if(typeof(updateAction) !== "undefined" && updateAction !== null){
			$("#update").html("Save");	
		}
		//Change 'Cancel' to 'Previous'
		var cancelAction = document.getElementById("cancel");
		if(typeof(cancelAction) !== "undefined" && cancelAction !== null){
			//$("#cancel").html("Previous");	
			cancelAction.innerHTML = "Previous";
		}
  });
});
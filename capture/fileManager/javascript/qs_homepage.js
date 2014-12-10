require(["jquery_cookie"], function() {
	// Added because variable isn't declared on the homepage
	var loginform = document.loginform;
	
	 
	// ------------------ HOVER INTENT START ------------------ //
	(function($){
		/* hoverIntent by Brian Cherne */
		$.fn.hoverIntent = function(f,g) {
			// default configuration options
			var cfg = {
				sensitivity: 7,
				interval: 100,
				timeout: 0
			};
			// override configuration options with user supplied object
			cfg = $.extend(cfg, g ? { over: f, out: g } : f );
	 
			// instantiate variables
			// cX, cY = current X and Y position of mouse, updated by mousemove event
			// pX, pY = previous X and Y position of mouse, set by mouseover and polling interval
			var cX, cY, pX, pY;
	 
			// A private function for getting mouse position
			var track = function(ev) {
				cX = ev.pageX;
				cY = ev.pageY;
			};
	 
			// A private function for comparing current and previous mouse position
			var compare = function(ev,ob) {
				ob.hoverIntent_t = clearTimeout(ob.hoverIntent_t);
				// compare mouse positions to see if they've crossed the threshold
				if ( ( Math.abs(pX-cX) + Math.abs(pY-cY) ) < cfg.sensitivity ) {
					$(ob).unbind("mousemove",track);
					// set hoverIntent state to true (so mouseOut can be called)
					ob.hoverIntent_s = 1;
					return cfg.over.apply(ob,[ev]);
				} else {
					// set previous coordinates for next time
					pX = cX; pY = cY;
					// use self-calling timeout, guarantees intervals are spaced out properly (avoids JavaScript timer bugs)
					ob.hoverIntent_t = setTimeout( function(){compare(ev, ob);} , cfg.interval );
				}
			};
	 
			// A private function for delaying the mouseOut function
			var delay = function(ev,ob) {
				ob.hoverIntent_t = clearTimeout(ob.hoverIntent_t);
				ob.hoverIntent_s = 0;
				return cfg.out.apply(ob,[ev]);
			};
	 
			// A private function for handling mouse 'hovering'
			var handleHover = function(e) {
				// next three lines copied from jQuery.hover, ignore children onMouseOver/onMouseOut
				var p = (e.type == "mouseover" ? e.fromElement : e.toElement) || e.relatedTarget;
				while ( p && p != this ) { try { p = p.parentNode; } catch(e) { p = this; } }
				if ( p == this ) { return false; }
	 
				// copy objects to be passed into t (required for event object to be passed in IE)
				var ev = jQuery.extend({},e);
				var ob = this;
	 
				// cancel hoverIntent timer if it exists
				if (ob.hoverIntent_t) { ob.hoverIntent_t = clearTimeout(ob.hoverIntent_t); }
	 
				// else e.type == "onmouseover"
				if (e.type == "mouseover") {
					// set "previous" X and Y position based on initial entry point
					pX = ev.pageX; pY = ev.pageY;
					// update "current" X and Y position based on mousemove
					$(ob).bind("mousemove",track);
					// start polling interval (self-calling timeout) to compare mouse coordinates over time
					if (ob.hoverIntent_s != 1) { ob.hoverIntent_t = setTimeout( function(){compare(ev,ob);} , cfg.interval );}
	 
				// else e.type == "onmouseout"
				} else {
					// unbind expensive mousemove event
					$(ob).unbind("mousemove",track);
					// if hoverIntent state is true, then call the mouseOut function after the specified delay
					if (ob.hoverIntent_s == 1) { ob.hoverIntent_t = setTimeout( function(){delay(ev,ob);} , cfg.timeout );}
				}
			};
	 
			// bind the function to the two event listeners
			return this.mouseover(handleHover).mouseout(handleHover);
		};
		
	})(jQuery);
	// ------------------ HOVER INTENT END ------------------ //
	 
	// ------------------ DROPDOWN START ------------------ //
	(function($){
	 
		var config = {    
			 sensitivity: 3, // number = sensitivity threshold (must be 1 or higher)    
			 interval: 110,  // number = milliseconds for onMouseOver polling interval    
			 over: doOpen,   // function = onMouseOver callback (REQUIRED)    
			 timeout: 110,   // number = milliseconds delay before onMouseOut    
			 out: doClose    // function = onMouseOut callback (REQUIRED)    
		};
		
		function doOpen() {
			$(this).addClass("hover");
			$('ul:first',this).css('visibility', 'visible');
		}
	 
		function doClose() {
			$(this).removeClass("hover");
			$('ul:first',this).css('visibility', 'hidden');
		}
	 
		$("ul.dropdown li").hoverIntent(config);    
		$("ul.dropdown li ul li:has(ul)").find("a:first").append(" &raquo; ");
	 
	})(jQuery);
	// ------------------ DROPDOWN END ------------------ //
	
	// ------------------ LAYOUT START ------------------ //
	var qs_layoutLogic = function (){		
		(function($){
			//IE7 Temp Fix
			// adds class if not logged in and Products show (guest access)
			var productCount = $('#family-nav').length;
			if ((_BM_USER_LOGIN == 'guest1' || _BM_USER_COMPANY == 'GuestCompany') && productCount > 0 ) {
				$('body').addClass('guest-access');
				$('#login-form-wrap').prepend('<div class="login-toggle"></div>');// adds close button for login-toggle
			}
			else if(_BM_USER_LOGIN != "" || productCount == 0){
				$('body').addClass('login-view');
			}
			// ---------- Parts Search  ----------------//
			// show/hide Search
			$('#search-login-toggle-wrapper .parts-search-toggle').click(function () {	    
				$('#search-login-toggle-wrapper').slideUp(100, function() {
					// if parts hide 
					$('.parts-search-toggle').hide();   //IE 7 Fix
					$('.parts-search-toggle').show();	//IE 7 Fix
					$('#search-outer-wrapper').slideDown(100);
				});
			});
			
			$('#search-outer-wrapper .parts-search-toggle').click(function () {	    
				$('#search-outer-wrapper').slideUp(100, function() {
					$('#search-login-toggle-wrapper').slideDown(100);
				});
			});
			
			// ---------- Serial Number Search  ----------------///
			// show/hide Serial Number Search
			$('#search-login-toggle-wrapper .serial-num-search-toggle').click(function () {	    
				$('#search-login-toggle-wrapper').slideUp(100, function() {
					$('.serial-num-search-toggle').hide();   //IE 7 Fix
					$('.serial-num-search-toggle').show();	//IE 7 Fix
					$('#serial-search-outer-wrapper').slideDown(100);
				});
			});
			$('#serial-search-outer-wrapper .serial-num-search-toggle').click(function () {	    
				$('#serial-search-outer-wrapper').slideUp(100, function() {
					$('#search-login-toggle-wrapper').slideDown(100);
				});
			});
			
			// ---------- Login Form ----------------///
			// show/hide login
			$('#search-login-toggle-wrapper .login-toggle').click(function () {	    
				$('#search-login-toggle-wrapper').slideUp(100, function() {
					$('#login-form-wrap').slideDown(100);
				});
			});
			
			$('#login-form-wrap .login-toggle').click(function () {	    
				$('#login-form-wrap').slideUp(100, function() {
					$('#search-login-toggle-wrapper').slideDown(100);
				});
			});
		})(jQuery);
	};
	
	
	// ------------------ CONTENT START ------------------ //
	var qs_setMenu = {
		selectProductLineNav : function(el){
			(function($){ 
				//Correctly shows the pl for the selected pf 
				// hides last product line nav and makes last product family inactive
				$(".product-line-nav").hide();
				$("#family-nav .active").removeClass("active");
				
				// makes current product familty list active and selects specific product line nav to show
				var currentId = $(el).attr('id');
				$('#product-nav-'+currentId).show();
				$(el).addClass("active");  // makes product family active
				$.cookie('hp-'+_BM_USER_LOGIN+'-pf', currentId); // sets pf cookie
				//return true;
			})(jQuery);
		},
		// correctly shows the description for the selected product line
		selectProductContent : function(el){
			(function($){ 
				
				// hides last product line content and makes last product line inactive
				$(".product-content").hide();
				$(".product-line-nav .active").removeClass("active");
				
				// makes current product line content visible and makes product line list active
				var currentId = $(el).attr('id');
				$('#content-'+currentId).show();
				$(el).addClass("active"); 
				$.cookie('hp-'+_BM_USER_LOGIN+'-pl', currentId); // sets pl cookie
				//return true;
			})(jQuery);
		}
	};
		
	var qs_contentLogic = function(){
		(function($){
			// ******** Start Click Events *******//
			//only shows relevant navs
			$("#family-nav li").click(function() {
				qs_setMenu.selectProductLineNav(this);
				// always clicks the first product line in list
				var currentId = $(this).attr('id');
				firstChild = $('#product-nav-'+currentId+' li:first-child');
				qs_setMenu.selectProductContent(firstChild);
			
			});
			
			//only shows relevant content
			
			$(".product-line-nav li").click(function() {
				qs_setMenu.selectProductContent(this);
			});	
			
			// ******** End Click Events *******//
			
			
			// if just parts takes user to the parts search results page and not logged in as services
			if($("#parts-only").length && _BM_USER_COMPANY != "BigMachines.com"){
				document.parts_simple.submit();
			}
		
			// if not logged in, removes cookie
			if(_BM_USER_LOGIN =="" || _BM_USER_LOGIN == 'guest1' || _BM_USER_COMPANY == 'GuestCompany'){
				$.cookie("last_transaction", null, {path: '/'});
			}
			
			var hp_pf = $.cookie('hp-'+_BM_USER_LOGIN+'-pf'); // gets cookie for PF
			var hp_pl = $.cookie('hp-'+_BM_USER_LOGIN+'-pl'); // gets cookie for PL

			if(hp_pf && hp_pl){ //if cookie exists for both
				qs_setMenu.selectProductLineNav($('#'+hp_pf));
				$('#'+hp_pl).click();
			}
			else if(hp_pf){
				$('#'+hp_pf).click();
			}
			else{ // if no cookie
				$("#family-nav li:first-child").click();
			}
			
		})(jQuery);
	};
	(function($){
		$(document).ready(function(){
			// call layout logic
			qs_layoutLogic();
			// call content logic
			qs_contentLogic();
			
		});
	})(jQuery);
	// ------------------ CONTENT END ------------------ //
});
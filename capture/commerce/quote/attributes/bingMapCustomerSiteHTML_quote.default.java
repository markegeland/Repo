// 20150504 - John Palubinskas - #518 CRM integration, check sourceSystem_quote

if(sourceSystem_quote == "SFDC") {
    return "";
} 
else {
    return "<script src='https://ecn.dev.virtualearth.net/mapcontrol/mapcontrol.ashx?v=7.0&s=1' type='text/javascript'></script>";  
}
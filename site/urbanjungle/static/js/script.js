/**
 * Create and configure the map
 */
function createGmap() {
    var latlng = new google.maps.LatLng(43.611005,3.878989);
    var myOptions = {
        zoom: 12,
        center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

    google.maps.event.addListener(map, 'idle', function() {
         addMarkers(map);
    });

    return map
}

/**
 * Download the json marker file, and insert markers in the map
 */
function addMarkers(map) {
    markersUri = '/map/markers/';

    bounds = map.getBounds();
    ne = bounds.getNorthEast();
    sw = bounds.getSouthWest();

    markersUri += ne.lat() + ',' + ne.lng() + ',' + sw.lat() + ',' + sw.lng() + '.json';

    $.getJSON(markersUri, function(data) {
        $.each(data['markers'], function(k, v) {
            createMarker(map, v['latitude'], v['longitude']);
        });
    });
}

/**
 * Add a marker on the map
 */
function createMarker(map, latitude, longitude) {
    m = new google.maps.Marker({
        position: new google.maps.LatLng(latitude, longitude),
        map: map
    });
}

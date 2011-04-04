/**
 * Create and configure the map
 */
function createGmap() {
    var latlng = new google.maps.LatLng(46.227638,2.213749);
    var myOptions = {
        zoom: 6,
        center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

    google.maps.event.addListener(map, 'idle', function() {
        map.clearMarkers();
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

    info = new google.maps.InfoWindow();
    $.getJSON(markersUri, function(data) {
        $.each(data['markers'], function(k, v) {
            createMarker(map, v['id'], v['latitude'], v['longitude']);
        });
    });
}

/**
 * Add a marker on the map
 */
function createMarker(map, id, latitude, longitude) {
    var m = new google.maps.Marker({
        position: new google.maps.LatLng(latitude, longitude),
        map: map,
    });
    m.setId(id);
    google.maps.event.addListener(m, 'click', function() {
        openInfoWindow(map, m);
    });
}

function openInfoWindow(map, m) {
    info.setContent('<div class="infoWindow"><img src="/report/thumbnail/' + m.getId() + '.jpg" /></div>');
    info.open(map, m);
}

/**
 * Use the geolocation html5 api to center map on current user position
 */
function centerOnCurrentPosition(map) {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            map.setCenter(new google.maps.LatLng(position.coords.latitude, position.coords.longitude));
            map.setZoom(13);
        });
    }
}

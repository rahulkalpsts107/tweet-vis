<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.amazonaws.*" %>
<%@ page import="com.amazonaws.auth.*" %>
<%@ page import="com.amazonaws.services.ec2.*" %>
<%@ page import="com.amazonaws.services.ec2.model.*" %>
<%@ page import="com.amazonaws.services.s3.*" %>
<%@ page import="com.amazonaws.services.s3.model.*" %>
<%@ page import="com.amazonaws.services.dynamodbv2.*" %>
<%@ page import="com.amazonaws.services.dynamodbv2.model.*" %>

<%! // Share the client objects across threads to
    // avoid creating new clients for each web request
    private AmazonEC2         ec2;
    private AmazonS3           s3;
    private AmazonDynamoDB dynamo;
 %>

<%
    /*
     * AWS Elastic Beanstalk checks your application's health by periodically
     * sending an HTTP HEAD request to a resource in your application. By
     * default, this is the root or default resource in your application,
     * but can be configured for each environment.
     *
     * Here, we report success as long as the app server is up, but skip
     * generating the whole page since this is a HEAD request only. You
     * can employ more sophisticated health checks in your application.
     */
    if (request.getMethod().equals("HEAD")) return;
%>

<%
    if (ec2 == null) {
        AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
        ec2    = new AmazonEC2Client(credentialsProvider);
        s3     = new AmazonS3Client(credentialsProvider);
        dynamo = new AmazonDynamoDBClient(credentialsProvider);
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8">
    <title>Twitter Map!</title>
    <link rel="stylesheet" href="styles/styles.css" type="text/css" media="screen">
    <style>
            html, body { height: 100%; margin: 0; padding: 0; }
            #maintitle{
                background-color: black;
                color: white;
                font-family: sans-serif
            }
            #map {
                width: 100%;
                height: 100%;
            }
            #loader {
              position: absolute;
              left: 50%;
              top: 50%;
              z-index: 1;
              width: 150px;
              height: 150px;
              margin: -75px 0 0 -75px;
              border: 16px solid #f3f3f3;
              border-radius: 50%;
              border-top: 16px solid #3498db;
              width: 120px;
              height: 120px;
              -webkit-animation: spin 2s linear infinite;
              animation: spin 2s linear infinite;
            }
            #tweettype{
                float: left;
            }
        </style>
    <script src="http://maps.google.com/maps/api/js?sensor=false" type="text/javascript"></script>
        <script>
            function initMap()
            {
                var locations = [
                    ['Bondi Beach', -33.890542, 151.274856, 4],
                    ['Coogee Beach', 31.200959,28.963851, 31.200959,30.228647, 5],
                    ['Cronulla Beach', -34.028249, 151.157507, 3],
                    ['Manly Beach', -33.80010128657071, 151.28747820854187, 2],
                    ['Maroubra Beach', -33.950198, 151.259302, 1]
                ];
                var bounds = new google.maps.LatLngBounds();
                var map = new google.maps.Map(document.getElementById('map'), {
                    mapTypeId: google.maps.MapTypeId.ROADMAP,
                    center: new google.maps.LatLng(-33.92, 151.25),
                    zoom:1
                });
                //map.setTilt(45);
                var infowindow = new google.maps.InfoWindow();
                var marker, i;
                for (i = 0; i < locations.length; i++) 
                {
                    var position = new google.maps.LatLng(locations[i][1], locations[i][2]);
                    marker = new google.maps.Marker({
                        position: position,
                        icon: {
                          path: google.maps.SymbolPath.CIRCLE,
                          scale: 4
                        },
                        map: map
                    });
                    
                    bounds.extend(position);
                    
                    google.maps.event.addListener(marker, 'click', (function (marker, i) {
                        return function () {
                            infowindow.setContent(locations[i][0]);
                            infowindow.open(map, marker);
                        }
                    })(marker, i));
                    
                    map.fitBounds(bounds);
                }    
                // Override our map zoom level once our fitBounds function runs (Make sure it only runs once)
                var boundsListener = google.maps.event.addListener((map), 'idle', function(event) {
                    this.setZoom(1);
                    google.maps.event.removeListener(boundsListener);
                });
            }
            google.maps.event.addDomListener(window, "load", initMap);
            

        </script>
</head>
<body>
    <h1 id="maintitle">Sports Tweets Geo Tagger</h1>
    <div id="tweettype">
        <p>Show</p>
        <select>
          <option value="F1">Formula 1</option>
          <option value="NASCAR">NASCAR</option>
          <option value="SBK">Super Bike</option>
          <option value="INDY">Indy Car</option>
          <option value="WRC">World Rally Championship</option>
        </select>
        <p> Geo Tagged Tweets On World Map</p>
    </div>
    <div id="map" style="width: 500px; height: 400px;">
        <div id="loader"></div>
    </div>
</body>
</html>
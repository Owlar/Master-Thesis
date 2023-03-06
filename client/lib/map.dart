import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import 'increment.dart';
import 'message.dart';


class Map extends StatefulWidget {
  const Map({Key? key}) : super(key: key);

  @override
  _MapState createState() => _MapState();
}

class _MapState extends State<Map> {
  final Completer<GoogleMapController> _controller = Completer();
  final Set<Polygon> _polygon = {};
  List<LatLng> _roomCorners = [];
  // Set to room @ IFI by default
  LatLng _smartphonePosition = const LatLng(59.944174, 10.719388);
  final double _zoomLevel = 15.0;
  Set<Message> _messages = {};

  late Socket _socket;

  @override
  void initState() {
    super.initState();
    _createPolygon();
  }

  void _createPolygon() {
    // Room @ IFI
    _roomCorners = [
      const LatLng(59.944211697376325, 10.719388984143734),
      const LatLng(59.944176937772774, 10.719477161765099),
      const LatLng(59.944117493728605, 10.71938395500183),
      const LatLng(59.94415074210543, 10.71929544210434)
    ];

    _polygon.add(
        Polygon(
            polygonId: const PolygonId("1"),
            points: _roomCorners,
            fillColor: Colors.redAccent
        )
    );

  }

  Future<void> _createSocket() async {
    // This is currently the public IP of the machine running the server, and it
    // is used to establish a connection from a physical device (smartphone) to it.
    String serverPublicIp = "172.20.36.207";

    String ip = "10.0.2.2";

    final deviceInfo = DeviceInfoPlugin();
    final androidInfo = await deviceInfo.androidInfo;
    final iOSInfo = await deviceInfo.iosInfo;

    if (androidInfo.isPhysicalDevice || iOSInfo.isPhysicalDevice) {
      ip = serverPublicIp;
    }

    _socket = await Socket.connect(ip, 8080);
    _socket.listen((event) {
      _sendData();
    });
  }

  Future<void> _sendData() async {
    final id = Increment.id;
    final message = Message(
        id: id,
        position: _smartphonePosition.latitude.toString() + "," + _smartphonePosition.longitude.toString(),
        status: _isInsidePolygon(_smartphonePosition, _roomCorners) ? "Inside critical area" : "Outside critical area",
        dateTime: DateTime.now()
      //dateTime: _times[i]
    );
    //For recording on/off
    _messages.add(message);

    _socket.add(utf8.encode(message.toString()));
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        Scaffold(
          body: GoogleMap(
            onTap: (LatLng pos) {
              print("Position is: " + pos.toString());
              print("Inside critical area: " + _isInsidePolygon(pos, _roomCorners).toString());
            },
            onMapCreated: (GoogleMapController googleMapController) {
              _controller.complete(googleMapController);
            },
            myLocationEnabled: true,
            polygons: _polygon,
            myLocationButtonEnabled: false,
            compassEnabled: true,
            zoomGesturesEnabled: true,
            zoomControlsEnabled: false,
            initialCameraPosition: CameraPosition(
                target: _smartphonePosition,
                zoom: _zoomLevel
            ),
          ),
          floatingActionButton: _messages.isEmpty ? FloatingActionButton.large (
              onPressed: () => _animateToPosition(),
              child: const Icon(Icons.location_on_outlined, size: 60),
          ) : FloatingActionButton.large (
              onPressed: () => _stopSharing(),
              backgroundColor: Colors.redAccent,
              child: const Icon(Icons.location_off_outlined, size: 60)
          ),
        )
      ]
    );
  }

  Future<void> _animateToPosition() async {
    await _createSocket();

    final position = await _getPosition();
    setState(() {
      _smartphonePosition = LatLng(position.latitude, position.longitude);
    });

    final GoogleMapController controller = await _controller.future;
    controller.animateCamera(CameraUpdate.newCameraPosition(
      CameraPosition(
          target: _smartphonePosition,
          zoom: _zoomLevel
      )
    ));
    _showSnackBar(_smartphonePosition.toString());
    _streamPosition();
  }

  Future<Position> _getPosition() async {
    // Source: https://pub.dev/packages/geolocator (26.01.2023)
    bool isServiceEnabled;
    LocationPermission permission;

    isServiceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!isServiceEnabled) return Future.error("Location is not enabled!");

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return Future.error("Location permissions were denied!");
      }
    }

    if (permission == LocationPermission.deniedForever) {
      return Future.error("Location permissions are denied forever, check settings!");
    }

    return await Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.best);
  }

  bool _isInsidePolygon(LatLng point, List<LatLng> polygon) {
    int intersectCount = 0;

    for (int i = 0; i < polygon.length - 1; i++) {
      if (rayCasting(point, polygon[i], polygon[i + 1])) {
        intersectCount++;
      }
    }
    // If odd is returned, the point is inside the polygon
    return ((intersectCount % 2) == 1);
  }

  bool rayCasting(LatLng point, LatLng a, LatLng b) {
    double aY = a.latitude;
    double aX = a.longitude;

    double bY = b.latitude;
    double bX = b.longitude;

    double pY = point.latitude;
    double pX = point.longitude;

    if ((aY > pY && bY > pY) || (aY < pY && bY < pY) || (aX < pX && bX < pX)) {
      return false;
    }

    double m = (aY - bY) / (aX - bX);
    double n = (-aX) * m + aY;

    double x = (pY - n) / m;

    return x > pX;
  }

  void _showSnackBar(String text) {
    ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
            content: Text(text)
        )
    );
  }

  Future<void> _streamPosition() async {
    const locationSettings = LocationSettings(
        accuracy: LocationAccuracy.high,
        distanceFilter: 0
    );
    Geolocator.getPositionStream(locationSettings: locationSettings).listen((Position pos) {
      _smartphonePosition = LatLng(pos.latitude, pos.longitude);
    });
  }

  Future<void> _stopSharing() async {
    _socket.close();
    setState(() {
      _messages = {};
    });
  }

}


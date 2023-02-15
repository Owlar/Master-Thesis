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
  final Set<Message> _messages = {};

  late final Socket _socket;

  @override
  void initState() {
    super.initState();
    _createPolygon();
    _createSocket();
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
      print(utf8.decode(event));
    });
  }

  Future<void> _sendData() async {
    final id = Increment.id;
    final message = Message(
        id: id,
        position: _smartphonePosition,
        status: _isInsidePolygon(_smartphonePosition, _roomCorners) ? "Inside dangerous space" : "Outside dangerous space",
        dateTime: DateTime.now()
    );
    _messages.add(message);

    _socket.add(utf8.encode(message.toString()));

    _socket.close();
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        Scaffold(
          body: GoogleMap(
            onTap: (LatLng pos) {
              print("Position is: " + pos.toString());
              print("Inside critial area: " + _isInsidePolygon(pos, _roomCorners).toString());
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
          ) : const SizedBox.shrink(),
        )
      ]
    );
  }

  Future<void> _animateToPosition() async {
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
    _sendData();
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

}


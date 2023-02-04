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
  final Set<Polyline> _polylines = {};
  LatLng _smartphonePosition = const LatLng(59.913868, 10.752245); //Set to Oslo by default
  final double _zoomLevel = 15.0;
  final Set<Message> _messages = {};

  late final Socket _socket;

  @override
  void initState() {
    super.initState();
    _createPolylines();
    _createSocket();
  }

  void _createPolylines() {
    const LatLng startPosition = LatLng(59.930566, 10.715629);
    List<LatLng> locations = [
      startPosition,
      const LatLng(59.930417, 10.715865),
      const LatLng(59.930729,10.716638),
      const LatLng(59.930878, 10.716406),
      startPosition
    ];

    _polylines.add(
        Polyline(
            polylineId: const PolylineId("1"),
            points: locations
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
    final message = Message(id: id, position: _smartphonePosition, status: "Outside dangerous space", dateTime: DateTime.now());
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
            onMapCreated: (GoogleMapController googleMapController) {
              _controller.complete(googleMapController);
            },
            myLocationEnabled: true,
            polylines: _polylines,
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

}


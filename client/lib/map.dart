import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:client/service.dart';
import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import 'status.dart';


class Map extends StatefulWidget {
  const Map({Key? key}) : super(key: key);

  @override
  _MapState createState() => _MapState();
}

class _MapState extends State<Map> {
  final Service _service = Service();
  final Completer<GoogleMapController> _controller = Completer();
  final double _zoomLevel = 14.0;

  late GoogleMapController _googleMapController;
  late LatLng _smartphonePosition;
  late Socket _socket;

  Set<Status> _messages = {};
  bool _warned = false;


  @override
  void initState() {
    super.initState();
  }


  Future<void> _stop() async {
    _socket.close();
    setState(() {
      _messages = {};
    });
  }


  @override
  Widget build(BuildContext context) {
    return Container(
        decoration: BoxDecoration(
            border: Border.all(
                color: _warned ? Colors.redAccent : Colors.greenAccent,
                width: 7.0
            )
        ),
        child: Stack(
            children: [
              Scaffold(
                body: GoogleMap(
                  onTap: (LatLng pos) {
                    print(pos.toString());
                  },
                  onMapCreated: (GoogleMapController googleMapController) {
                    _controller.complete(googleMapController);
                    _googleMapController = googleMapController;
                  },
                  myLocationEnabled: true,
                  myLocationButtonEnabled: false,
                  compassEnabled: true,
                  zoomGesturesEnabled: true,
                  zoomControlsEnabled: false,
                  initialCameraPosition: CameraPosition(
                      target: const LatLng(59.94416434370449, 10.719385296106339),
                      zoom: _zoomLevel
                  ),
                ),
                floatingActionButton: _messages.isEmpty ? FloatingActionButton.large (
                  onPressed: () => _start(),
                  child: const Icon(Icons.location_on_outlined, size: 60),
                ) : FloatingActionButton.large (
                    onPressed: () => _stop(),
                    backgroundColor: Colors.redAccent,
                    child: const Icon(Icons.location_off_outlined, size: 60)
                ),
              )
            ]
        )
    );
  }


  Future<void> _start() async {
    final position = await _getPosition();
    setState(() {
      _smartphonePosition = LatLng(position.latitude, position.longitude);
    });
    await _createSocket().then((value) {
      _listenOnSocket();
      _animateCamera();
      _showSnackBar(_smartphonePosition.toString());
      _updateLatestPosition();
    }, onError: (e) => _showSnackBar("Could not connect to server!"));
  }


  // Source: https://pub.dev/packages/geolocator (26.01.2023)
  Future<Position> _getPosition() async {
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
    _socket = await Socket.connect(ip, 8080)
        .timeout(const Duration(seconds: 2));
  }



  Future<void> _listenOnSocket() async {
    _socket.listen((event) {
      String received = utf8.decode(event).trim();
      if (received == "-1") {
        _warn();
      }
      else {
        _sendData(received);
        // Test with realtime database
        _warned = false;
        setState(() {
          _googleMapController.setMapStyle(null);
        });
      }
    });
  }


  void _warn() {
    _showSnackBar("You are currently inside a critical area!");
    _googleMapController.moveCamera(CameraUpdate.zoomIn());
    // Safe zones are set to medical areas, such as hospitals
    rootBundle.loadString("assets/safe_zones.txt").then((str) {
      _googleMapController.setMapStyle(str);
    });
    setState(() {
      _warned = true;
    });
  }


  void _showSnackBar(String text) {
    ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(text))
    );
  }


  Future<void> _sendData(String id) async {
    final status = Status(
      id: id,
      position: _smartphonePosition.latitude.toString() + "," + _smartphonePosition.longitude.toString(),
    );
    // To keep track of position recording toggle
    setState(() {
      _messages.add(status);
    });
    // Testing with realtime database
    _service.addStatus(status);
    _socket.add(utf8.encode(status.toString()));
  }



  Future<void> _animateCamera() async {
    final GoogleMapController controller = await _controller.future;
    controller.animateCamera(CameraUpdate.newCameraPosition(
        CameraPosition(
            target: _smartphonePosition,
            zoom: _zoomLevel
        )
    ));
  }


  Future<void> _updateLatestPosition() async {
    const locationSettings = LocationSettings(
        accuracy: LocationAccuracy.best,
        distanceFilter: 1
    );
    Geolocator.getPositionStream(locationSettings: locationSettings).listen((Position pos) {
      _smartphonePosition = LatLng(pos.latitude, pos.longitude);
    });
  }


}


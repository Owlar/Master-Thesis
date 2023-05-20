import 'dart:async';

import 'package:client/service/service.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import '../model/status.dart';


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
  late Status _currentStatus;
  late int _id = 0;

  Set<Status> _messages = {};
  bool _warned = false;

  @override
  void initState() {
    super.initState();
  }



  @override
  Widget build(BuildContext context) {
    return Container(
        decoration: BoxDecoration(
            border: Border.all(
                color: _warned ? Theme.of(context).colorScheme.secondary : Theme.of(context).colorScheme.primary,
                width: 7.0
            )
        ),
        child: Stack(
            children: [
              Scaffold(
                body: GoogleMap(
                  onTap: (LatLng pos) {
                    //print(pos.toString());
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
                  rotateGesturesEnabled: true,
                  initialCameraPosition: CameraPosition(
                      target: const LatLng(59.94416434370449, 10.719385296106339),
                      zoom: _zoomLevel
                  ),
                ),
                floatingActionButton: _messages.isEmpty ? FloatingActionButton.large (
                    onPressed: () => _start(),
                    backgroundColor: Theme.of(context).colorScheme.primary,
                    child: const Icon(Icons.location_on_outlined, size: 60),
                ) : FloatingActionButton.large (
                    onPressed: () => _stop(),
                    backgroundColor: Theme.of(context).colorScheme.secondary,
                    child: const Icon(Icons.location_off_outlined, size: 60)
                ),
              )
            ]
        )
    );
  }



  Future<void> _stop() async {
    _showDecision(_currentStatus.id);
    setState(() {
      _messages = {};
    });
  }



  Future<void> _start() async {
    _assignClient();
    final position = await _getPosition();
    setState(() {
      _smartphonePosition = LatLng(position.latitude, position.longitude);
    });
    _createStatus().then((value) async => {
      await _service.addStatus(_currentStatus).then((value) {
        _resetState();
        _animateCamera();
        _showSnackBar(_smartphonePosition.toString());
        _updateLatestPosition();
        }, onError: (e) => _showSnackBar("Could not send updated status!"))
    }, onError: (e) => _showSnackBar("Could not create a status!"));
  }



  Future<void> _createStatus() async {
    _currentStatus = Status(
        id: _id.toString(),
        latitude: _smartphonePosition.latitude.toString(),
        longitude: _smartphonePosition.longitude.toString(),
        endangered: false
    );
  }



  void _resetState() {
    setState(() {
      _messages.add(_currentStatus);
      _googleMapController.setMapStyle(null);
    });
    _warned = false;
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



  Future<void> _showDecision(String id) async {
    bool isEndangered = await _service.isEndangered(id);
    if (isEndangered) {
      //_showSnackBar("You are currently inside a critical area! Medical zones are shown in blue.");
      _warn();
    } else {
      //_showSnackBar("You are safe!");
    }
  }



  void _warn() {
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



  Future<void> _assignClient() async {
    ++_id;
  }

}


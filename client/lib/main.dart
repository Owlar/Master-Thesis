import 'package:client/map.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        theme: _theme,
        home: const Map()
    );
  }

  final ThemeData _theme = ThemeData(
    colorScheme: ColorScheme.fromSwatch().copyWith(
        secondary: Colors.green,
        primary: Colors.blue
    )
  );

}

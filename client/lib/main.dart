import 'package:client/ui/map.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';

import 'service/firebase_options.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
      options: DefaultFirebaseOptions.currentPlatform
  );
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
        primary: const Color(0xFF2A3282),
        secondary: const Color(0xFFE81313),
    )
  );

}

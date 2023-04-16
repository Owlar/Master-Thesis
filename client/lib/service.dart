import 'package:client/status.dart';
import 'package:firebase_database/firebase_database.dart';

class Service {
  Future<void> addStatus(Status status) async {
    DatabaseReference reference = FirebaseDatabase.instance.ref();
    await reference.push().set({
      "id": status.id,
      "position": status.position
    }).onError((error, stackTrace) => null);
  }
}
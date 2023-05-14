import 'package:client/model/status.dart';
import 'package:firebase_database/firebase_database.dart';

class Service {

  Future<int> getId() async {
    DatabaseReference reference = FirebaseDatabase.instance.ref("mobiles");
    DataSnapshot snapshot = await reference.get();
    return snapshot.children.length + 1;
  }


  Future<void> addStatus(Status status) async {
    DatabaseReference reference = FirebaseDatabase.instance.ref("mobiles/${status.id}");
    await reference.set({
      "id": status.id,
      "latitude": status.latitude,
      "longitude": status.longitude
    }).onError((error, stackTrace) => null);
  }


  Future<bool> isEndangered(String id) async {
    DatabaseReference reference = FirebaseDatabase.instance.ref("endangered");
    DataSnapshot snapshot = await reference.get();
    Iterable<DataSnapshot> children = snapshot.children.where((item) => item.child(id).value == id);
    if (children.isEmpty) return false;

    Object? child = children.first.child(id).value;
    if (child == null) return false;

    return true;
  }
}
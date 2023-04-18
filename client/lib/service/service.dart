import 'package:client/model/status.dart';
import 'package:firebase_database/firebase_database.dart';

class Service {

  Future<int> getId() async {
    DatabaseReference reference = FirebaseDatabase.instance.ref("mobiles");
    DataSnapshot snapshot = await reference.get();
    return snapshot.children.length + 1;
  }


  Future<void> addStatus(Status status) async {
    DatabaseReference reference = FirebaseDatabase.instance.ref("mobiles");
    await reference.push().set({
      "id": status.id,
      "latitude": status.latitude,
      "longitude": status.longitude
    }).onError((error, stackTrace) => null);
  }


  Future<bool> isEndangered(int id) async {
    DatabaseReference reference = FirebaseDatabase.instance.ref("mobiles");
    DataSnapshot snapshot = await reference.get();
    Iterable<DataSnapshot> children = snapshot.children.where((item) => item.child("id").value == id);
    Object? child = children.first.child("endangered").value;
    if (child == null) return false;
    return child as bool;
  }
}

class Message {
  final int? id;
  final String? position;
  final String? status;
  final DateTime? dateTime;

  Message({
    required this.id,
    required this.position,
    required this.status,
    required this.dateTime
  });

  @override
  String toString() {
    return "$id;$position;$status;$dateTime";
  }
}
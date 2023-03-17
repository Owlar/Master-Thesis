
class Status {
  final String? id;
  final String? position;
  final DateTime? dateTime;

  Status({
    required this.id,
    required this.position,
    required this.dateTime
  });

  @override
  String toString() {
    return "$id;$position;$dateTime";
  }
}
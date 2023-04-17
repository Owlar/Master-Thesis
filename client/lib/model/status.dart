
class Status {
  final String? id;
  final String? position;

  Status({
    required this.id,
    required this.position
  });

  @override
  String toString() {
    return "$id;$position";
  }
}
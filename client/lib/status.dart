
class Status {
  final String? id;
  final String? position;
  final String? instant;

  Status({
    required this.id,
    required this.position,
    required this.instant
  });

  @override
  String toString() {
    return "$id;$position;$instant";
  }
}

class Status {
  final int id;
  final String latitude;
  final String longitude;
  late final bool? endangered;

  Status({
    required this.id,
    required this.latitude,
    required this.longitude,
    this.endangered
  });

  @override
  String toString() {
    return "$id: ($latitude,$longitude) - $endangered";
  }
}
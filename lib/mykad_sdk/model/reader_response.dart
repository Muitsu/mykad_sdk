class ReaderResponse {
  final String data;
  final String message;

  ReaderResponse({required this.data, required this.message});

  Map<String, dynamic> toJson() {
    return {
      "data": data,
      "message": message,
    };
  }

  factory ReaderResponse.fromJson(Map<String, dynamic> json) {
    return ReaderResponse(data: json["data"], message: json["message"]);
  }

  ReaderResponse copyWith({String? data, String? message}) {
    return ReaderResponse(
      data: data ?? this.data,
      message: message ?? this.message,
    );
  }
}

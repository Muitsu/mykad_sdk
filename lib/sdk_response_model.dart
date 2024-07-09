class SdkResponseModel {
  String? status;
  String? message;
  String? data;
  SdkResponseModel({this.status, this.message, this.data});

  factory SdkResponseModel.fromJson(Map<String, dynamic> json) {
    return SdkResponseModel(
      status: json["status"],
      message: json["message"],
      data: json["data"],
    );
  }
  bool isDataMykad() {
    return message!.toLowerCase().contains("mykad");
  }
}

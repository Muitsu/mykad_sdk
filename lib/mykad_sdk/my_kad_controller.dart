import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:mykad_sdk/mykad_sdk/my_kad_reader.dart';

import 'model/reader_response.dart';

class MyKadController {
  // Private constructor
  MyKadController._privateConstructor();

  // Singleton instance
  static final MyKadController _instance =
      MyKadController._privateConstructor();

  // Factory constructor to return the singleton instance
  factory MyKadController() {
    return _instance;
  }

  StreamController<ReaderResponse>? controller;
  MyKadModel? _myKadModel;
  MyKidModel? _myKidModel;

  // Get the stream
  Stream<ReaderResponse>? get stream => controller?.stream;
  MyKadModel? get getMyKad => _myKadModel;
  MyKidModel? get getMyKid => _myKidModel;

  // Initialize the StreamController & MyKadReader
  void init({required bool verifyFP, required BuildContext context}) {
    if (controller == null) {
      controller = StreamController<ReaderResponse>.broadcast();
      MyKadReader.callSDK();
      MyKadReader.sdkListener(
        context: context,
        onIdle: () {
          //Please insert card
          setMessage(msg: "Please insert card");
        },
        onReadCard: () {
          //Loading ...
          setMyKadData(null);
          setMessage(msg: "Loading read card...");
        },
        onSuccessCard: (data) async {
          setMyKadData(data);
          //Success read card
          setMessage(msg: "Read card successful", data: data);
          if (verifyFP) {
            await MyKadReader.turnOnFP();
            await addDelay(milisec: 2500);
            setMessage(msg: "Initialize Fingerprint Hardware...");
            await MyKadReader.getFPDeviceList();
            await addDelay(milisec: 2000);
            await connectAndScanFP();
          }
        },
        onErrorCard: () {
          //Remove card and try again
          setMessage(msg: "Remove card and try again");
        },
        onVerifyFP: () {
          //Verifying Fingerprint
          setMessage(msg: "Please place your fingerprint at the scanner");
        },
        onSuccessFP: () {
          //Success verify fingerprint
          setMessage(msg: "User verification successful");
        },
        onErrorFP: () async {
          //Please try again
          setMessage(msg: "Error: Please try again");
        },
      );
    }
  }

  Future addDelay({int milisec = 1500}) =>
      Future.delayed(Duration(milliseconds: milisec));

  void addData(ReaderResponse data) => controller?.sink.add(data);

  Future connectAndScanFP() async {
    await MyKadReader.disconnectFPScanner();
    await addDelay();
    await MyKadReader.connectFPScanner();
    await addDelay();
    await MyKadReader.readFingerprint();
  }

  Future tryAgain() async {
    setMessage(msg: "Initialize Fingerprint Hardware...");
    await MyKadReader.disconnectFPScanner();
    await addDelay();
    await MyKadReader.connectFPScanner();
    await addDelay();
    await MyKadReader.readFingerprint();
  }

  void setMessage({SdkResponseModel? data, required String msg}) {
    var resp =
        ReaderResponse(data: data?.toJson().toString() ?? "", message: msg);
    addData(resp);
  }

  void setMyKadData(SdkResponseModel? data) {
    if (data == null) {
      _myKadModel = null;
      _myKidModel = null;
      return;
    }
    final json = jsonDecode(data.data!);
    if (data.isDataMykad()) {
      _myKadModel = MyKadModel.fromJson(json);
    } else {
      _myKidModel = MyKidModel.fromJson(json);
    }
  }

  // Close the StreamController
  Future<void> close() async {
    controller?.close();
    controller = null;
    setMyKadData(null);
    await MyKadReader.disconnectFPScanner();
    await addDelay();
    await MyKadReader.turnOffFP();
    await addDelay();
    await MyKadReader.disposeListener();
  }
}

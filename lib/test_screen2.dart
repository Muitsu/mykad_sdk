// ignore_for_file: constant_identifier_names

import 'package:flutter/material.dart';
import 'package:mykad_sdk/mykad_sdk/my_kad_reader.dart';

class TestScreen2 extends StatefulWidget {
  const TestScreen2({super.key});

  @override
  State<TestScreen2> createState() => _TestScreen2State();
}

class _TestScreen2State extends State<TestScreen2> with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    _sdkListener();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.paused ||
        state == AppLifecycleState.detached) {
      MyKadReader.turnOffFP();
    }
  }

  _sdkListener() {
    MyKadReader.sdkListener(
      context: context,
      isDebug: false,
      onIdle: () {
        //Please insert card
        setStatus("Please insert card");
      },
      onReadCard: () {
        //Loading ...
        setStatus("Loading read card...");
      },
      onSuccessCard: (data) async {
        //Success read card
        setStatus("Read card successful");
      },
      onErrorCard: () {
        //Remove card and try again
        setStatus("Remove card and try again");
      },
      onVerifyFP: () {
        //Verifying Fingerprint
        setStatus("Verifying Fingerprint");
      },
      onSuccessFP: () {
        //User verification successful
        setStatus("User verification successful");
      },
      onErrorFP: () async {
        //Please try again in 3 second
        setStatus("Please try again");
      },
    );
  }

  Future addDelay({int milisec = 1500}) =>
      Future.delayed(Duration(milliseconds: milisec));

  Future connectAndScanFP() async {
    await MyKadReader.disconnectFPScanner();
    await addDelay();
    await MyKadReader.connectFPScanner();
    await addDelay();
    await MyKadReader.readFingerprint();
  }

  String stats = "Initial";
  setStatus(String msg) {
    setState(() => stats = msg);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: const Text("Test Screen 2"),
      ),
      body: Center(
        child: SingleChildScrollView(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text(stats),
              ElevatedButton(
                  onPressed: () {
                    MyKadReader.callSDK(usingFP: false);
                  },
                  child: const Text("Call SDK")),
              ElevatedButton(
                  onPressed: () {
                    MyKadReader.turnOnFP();
                  },
                  child: const Text("Turn ON FP")),
              ElevatedButton(
                  onPressed: () {
                    MyKadReader.getFPDeviceList(context: context);
                  },
                  child: const Text("Get FP Device list")),
              ElevatedButton(
                  onPressed: () {
                    MyKadReader.connectFPScanner();
                  },
                  child: const Text("Connect Scanner")),
              ElevatedButton(
                  onPressed: () {
                    MyKadReader.readFingerprint();
                  },
                  child: const Text("Read Fingerprint")),
              ElevatedButton(
                  onPressed: () {
                    MyKadReader.disconnectFPScanner();
                  },
                  child: const Text("Disconnect Scanner")),
              ElevatedButton(
                  onPressed: () {
                    MyKadReader.turnOffFP();
                  },
                  child: const Text("Turn Off FP")),
              ElevatedButton(
                  onPressed: () {
                    MyKadReader.disposeListener();
                  },
                  child: const Text("Dispose")),
            ],
          ),
        ),
      ),
    );
  }
}

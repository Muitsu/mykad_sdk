// ignore_for_file: constant_identifier_names

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'dart:developer' as dev;

import 'package:mykad_sdk/sdk_response_model.dart';

import 'my_kad_model.dart';
import 'my_kid_model.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'MyKad SDK',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

enum CardReaderStatus {
  READER_INSERTED,
  CARD_INSERTED,
  CARD_SUCCESS,
  CARD_INSERTED_ERROR,
  CARD_REMOVE,
  READER_REMOVED,
  VERIFY_FP_FAILED
}

class _MyHomePageState extends State<MyHomePage> with WidgetsBindingObserver {
  int _counter = 0;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  static const platform = MethodChannel('com.myKad/fingerprint');

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
      _turnedOffFP();
    }
  }

  _sdkListener() {
    platform.setMethodCallHandler((call) async {
      final String data = call.arguments;
      final state = CardReaderStatus.values.byName(call.method);
      dev.log("TEST: ${call.method}");
      if (state == CardReaderStatus.READER_INSERTED) {
        dev.log("${state.name}: $data");
      } else if (state == CardReaderStatus.CARD_INSERTED) {
        dev.log("${state.name}: $data");
        Fluttertoast.showToast(msg: "CARD INSERTED");
      } else if (state == CardReaderStatus.CARD_SUCCESS) {
        final sdkresponse = SdkResponseModel.fromJson(jsonDecode(data));
        final json = jsonDecode(sdkresponse.data!);
        Fluttertoast.showToast(msg: "CARD SUCCESS");
        if (sdkresponse.isDataMykad()) {
          final model = MyKadModel.fromJson(json);
          dev.log("Nama: ${model.name}");
        } else {
          final model = MyKidModel.fromJson(json);
          dev.log("Nama: ${model.name}");
        }
      } else if (state == CardReaderStatus.CARD_INSERTED_ERROR) {
        dev.log("${state.name}: $data");
        _debugErrorDialog(data);
        Fluttertoast.showToast(msg: "CARD INSERTED ERROR $data");
      } else if (state == CardReaderStatus.CARD_REMOVE) {
        dev.log("${state.name}: $data");
        Fluttertoast.showToast(msg: "CARD REMOVE");
      } else if (state == CardReaderStatus.VERIFY_FP_FAILED) {
        dev.log("${state.name}: $data");
        Fluttertoast.showToast(msg: "VERIFY_FP_FAILED $data");
      } else {
        dev.log(data);
      }
    });
  }

  Future<void> _printHello() async {
    try {
      await platform.invokeMethod('printHello');
    } on PlatformException catch (e) {
      debugPrint("Failed to print hello: '${e.message}'.");
    }
  }

  Future<void> _callSDK({bool usingFP = false}) async {
    try {
      var args = {"usingFP": usingFP};
      await platform.invokeMethod('myKadSDK', args);
    } on PlatformException catch (e) {
      debugPrint("Failed to call SDK: '${e.message}'.");
    }
  }

  Future<void> _tunrOnFP() async {
    try {
      final isConnected =
          await platform.invokeMethod<bool>('turnOnFP') ?? false;
      Fluttertoast.showToast(msg: "FP Turned on: $isConnected");
    } on PlatformException catch (e) {
      debugPrint("Failed to turn on: '${e.message}'.");
    }
  }

  Future<void> _turnedOffFP() async {
    try {
      final isDisconnected = await platform.invokeMethod('turnOffFP');
      Fluttertoast.showToast(msg: "FP turned off: $isDisconnected");
    } on PlatformException catch (e) {
      debugPrint("Failed to turn off: '${e.message}'.");
    }
  }

  Future<void> _conenctFPScanner() async {
    try {
      final isDisconnected = await platform.invokeMethod('connectScanner');
      Fluttertoast.showToast(msg: "Scanner connected: $isDisconnected");
    } on PlatformException catch (e) {
      debugPrint("Failed to connect: '${e.message}'.");
    }
  }

  Future<void> _readFingerprint() async {
    try {
      await platform.invokeMethod('readFingerprint');
    } on PlatformException catch (e) {
      debugPrint("Failed to print hello: '${e.message}'.");
    }
  }

  Future<void> _disconnectFPScanner() async {
    try {
      final isDisconnected = await platform.invokeMethod('disconnectScanner');
      Fluttertoast.showToast(msg: "Scanner disconnected: $isDisconnected");
    } on PlatformException catch (e) {
      debugPrint("Failed to disconnect: '${e.message}'.");
    }
  }

  Future<void> _getFPDeviceList() async {
    try {
      final data =
          await platform.invokeMethod<String>('getFPDeviceList') ?? "-";
      _debugErrorDialog(data);
      if (!mounted) return;
    } on PlatformException catch (e) {
      debugPrint("Failed to get device list: '${e.message}'.");
    }
  }

  _debugErrorDialog(String data) {
    return showDialog(
        context: context,
        builder: (context) => Dialog(
              child: Text(data),
            ));
  }

  Future<void> _disposeListener() async {
    try {
      await platform.invokeMethod('dispose');
    } on PlatformException catch (e) {
      debugPrint("Failed to dispose: '${e.message}'.");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: SingleChildScrollView(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              const Text(
                'You have pushed the button this many times:',
              ),
              Text(
                '$_counter',
                style: Theme.of(context).textTheme.headlineMedium,
              ),
              ElevatedButton(
                  onPressed: () {
                    _printHello();
                  },
                  child: const Text("Call method channel")),
              ElevatedButton(
                  onPressed: () {
                    _callSDK(usingFP: true);
                  },
                  child: const Text("Call SDK")),
              ElevatedButton(
                  onPressed: () {
                    _tunrOnFP();
                  },
                  child: const Text("Turn ON FP")),
              ElevatedButton(
                  onPressed: () {
                    _getFPDeviceList();
                  },
                  child: const Text("Get FP Device list")),
              ElevatedButton(
                  onPressed: () {
                    _conenctFPScanner();
                  },
                  child: const Text("Connect Scanner")),
              ElevatedButton(
                  onPressed: () {
                    _readFingerprint();
                  },
                  child: const Text("Read Fingerprint")),
              ElevatedButton(
                  onPressed: () {
                    _disconnectFPScanner();
                  },
                  child: const Text("Disconnect Scanner")),
              ElevatedButton(
                  onPressed: () {
                    _turnedOffFP();
                  },
                  child: const Text("Turn Off FP")),
              ElevatedButton(
                  onPressed: () {
                    _disposeListener();
                  },
                  child: const Text("Dispose")),
            ],
          ),
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
    );
  }
}

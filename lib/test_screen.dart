import 'package:flutter/material.dart';
import 'package:mykad_sdk/mykad_sdk/my_kad_widget.dart';

import 'mykad_sdk/my_kad_controller.dart';

class TestScreen extends StatefulWidget {
  const TestScreen({super.key});

  @override
  State<TestScreen> createState() => _TestScreenState();
}

class _TestScreenState extends State<TestScreen> with WidgetsBindingObserver {
  final MyKadController myKadController = MyKadController();
  @override
  void initState() {
    super.initState();
    myKadController.init(verifyFP: true, context: context);
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.paused ||
        state == AppLifecycleState.detached) {
      myKadController.close();
    }
  }

  @override
  void dispose() {
    myKadController.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Test Stream Controller"),
        leading: IconButton(
            onPressed: () async {
              showLoading(context);
              myKadController.close().then((val) {
                Navigator.pop(context);
                Navigator.pop(context);
              });
            },
            icon: const Icon(Icons.chevron_left)),
        actions: [
          IconButton(
              onPressed: () async {
                await myKadController.close();
              },
              icon: const Icon(Icons.power))
        ],
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            MyKadWidget(
              controller: myKadController,
              onCardSuccess: (val) {},
              onVerifyFP: (val) {},
              builder: (context, msg) {
                return Text(msg);
              },
            )
          ],
        ),
      ),
    );
  }

  Future<dynamic> showLoading(BuildContext context) {
    return showDialog(
      context: context,
      barrierColor: Colors.black.withOpacity(0.3),
      builder: (context) => PopScope(
        canPop: false,
        onPopInvoked: (val) async {},
        child: GestureDetector(
          onTap: () {},
          child: Material(
              color: Colors.black.withOpacity(0.6),
              child: Center(
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(30),
                  child: Container(
                    height: 100,
                    width: 100,
                    padding: const EdgeInsets.all(15),
                    color: Colors.white,
                    child: const CircularProgressIndicator(),
                  ),
                ),
              )),
        ),
      ),
    );
  }
}

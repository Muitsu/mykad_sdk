import 'package:flutter/material.dart';
import 'package:mykad_sdk/mykad_sdk/my_kad_widget.dart';

import 'blinking_text.dart';
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
      body: LayoutBuilder(builder: (context, constraint) {
        return SingleChildScrollView(
          child: Column(
            children: [
              MyKadWidget(
                controller: myKadController,
                onCardSuccess: (val) {},
                onVerifyFP: (val) {},
                builder: (context, msg) {
                  final readerStatus = ReaderStatus.queryStatus(msg);

                  return Center(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        SizedBox(height: constraint.maxHeight * .12),
                        readerStatus.isLoading
                            ? Padding(
                                padding: EdgeInsets.only(
                                    top: constraint.maxHeight * .1),
                                child: const CircularProgressIndicator(),
                              )
                            : FittedBox(
                                child: Image.asset(
                                  readerStatus.imgSrc,
                                  fit: BoxFit.contain,
                                  height: constraint.maxHeight * .3,
                                  width: constraint.maxWidth * .8,
                                ),
                              ),
                        SizedBox(height: constraint.maxHeight * .04),
                        readerStatus.isNotBlink
                            ? Text(
                                readerStatus.title,
                                textAlign: TextAlign.center,
                                style: const TextStyle(
                                    fontSize: 24, fontWeight: FontWeight.w700),
                              )
                            : BlinkingText(
                                readerStatus.title,
                                textAlign: TextAlign.center,
                                style: const TextStyle(
                                    fontSize: 24, fontWeight: FontWeight.w700),
                              ),
                      ],
                    ),
                  );
                },
              )
            ],
          ),
        );
      }),
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

enum ReaderStatus {
  insert(
      title: "Please insert card",
      imgSrc: "assets/images/insert_card.png",
      query: "insert card"),
  cardSuccess(
      title: "Read card successful",
      imgSrc: "assets/images/success_card.png",
      query: "card successful"),
  cardFailed(
      title: "Remove card and try again",
      imgSrc: "assets/images/failed_card.png",
      query: "and try again"),
  remove(
      title: "Remove card",
      imgSrc: "assets/images/remove_card.png",
      query: "remove card"),
  insertFP(
      title: "Please place your fingerprint at the scanner",
      imgSrc: "assets/images/fp_scan.png",
      query: "place your fingerprint"),
  successFP(
      title: "User verification successful",
      imgSrc: "assets/images/fp_success.png",
      query: "verification successful"),
  failedFP(
      title: "Error: Please try again in 3 second",
      imgSrc: "assets/images/fp_failed.png",
      query: "Error: Please"),
  loading(title: "Loading ...", imgSrc: "", query: "loading"),
  loadingFP(
      title: "Verifying Fingerprint...",
      imgSrc: "",
      query: "verifying fingerprint"),
  ;

  final String title;
  final String imgSrc;
  final String query;

  bool get isLoading =>
      this == ReaderStatus.loading || this == ReaderStatus.loadingFP;

  bool get isNotBlink =>
      this == ReaderStatus.cardSuccess || this == ReaderStatus.successFP;

  static ReaderStatus queryStatus(String query) {
    final result = ReaderStatus.values
        .where((stat) => query.toLowerCase().contains(stat.query.toLowerCase()))
        .toList();
    return result.isNotEmpty ? result.first : ReaderStatus.loading;
  }

  const ReaderStatus(
      {required this.title, required this.imgSrc, required this.query});
}

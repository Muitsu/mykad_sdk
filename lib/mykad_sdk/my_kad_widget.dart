import 'package:flutter/material.dart';
import 'package:mykad_sdk/mykad_sdk/my_kad_controller.dart';
import 'dart:developer' as dev;

class MyKadWidget extends StatefulWidget {
  final MyKadController controller;
  final Widget Function(BuildContext context, String msg)? builder;
  final void Function(String val)? onListen;
  final void Function(bool val) onCardSuccess;
  final void Function(bool val) onVerifyFP;
  const MyKadWidget(
      {super.key,
      required this.controller,
      this.builder,
      this.onListen,
      required this.onCardSuccess,
      required this.onVerifyFP});

  @override
  State<MyKadWidget> createState() => _MyKadWidgetState();
}

class _MyKadWidgetState extends State<MyKadWidget> {
  @override
  Widget build(BuildContext context) {
    return StreamBuilder(
      stream: widget.controller.stream,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Text('Waiting for hardware...');
        } else if (snapshot.hasError) {
          dev.log("[MyKadWidget] Error");
          return Text('Error: ${snapshot.error}');
        } else if (snapshot.hasData) {
          final msg = snapshot.data?.message ?? "";
          setFunction(msg.toLowerCase());
          if (widget.onListen != null) {
            widget.onListen!(msg);
          }
          return widget.builder != null
              ? widget.builder!(context, msg)
              : const SizedBox();
        } else {
          dev.log("[MyKadWidget] Controller not initialize ");
          return const Text('Please initialize controller');
        }
      },
    );
  }

  setFunction(String msg) {
    if (msg.contains("remove card") || msg.contains("insert card")) {
      widget.onCardSuccess(false);
      widget.onVerifyFP(false);
    } else if (msg.contains("read card successful")) {
      widget.onCardSuccess(true);
    }

    if (msg.contains("verification successful")) {
      widget.onVerifyFP(true);
    } else if (msg.contains("try again")) {
      widget.onVerifyFP(false);
    }
  }
}

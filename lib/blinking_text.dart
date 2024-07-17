import 'package:flutter/material.dart';
import 'dart:async';

class BlinkingText extends StatefulWidget {
  final String text;
  final TextStyle? style;
  final Duration duration;
  final TextAlign? textAlign;
  const BlinkingText(
    this.text, {
    super.key,
    this.style,
    this.duration = const Duration(milliseconds: 500),
    this.textAlign,
  });

  @override
  BlinkingTextState createState() => BlinkingTextState();
}

class BlinkingTextState extends State<BlinkingText> {
  bool _isVisible = true;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _startBlinking();
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  void _startBlinking() {
    _timer = Timer.periodic(widget.duration, (timer) {
      setState(() {
        _isVisible = !_isVisible;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedOpacity(
      opacity: _isVisible ? 1.0 : 0.0,
      duration: widget.duration,
      child: Text(
        widget.text,
        textAlign: widget.textAlign,
        style: widget.style,
      ),
    );
  }
}

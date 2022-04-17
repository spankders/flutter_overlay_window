import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter_overlay_window/flutter_overlay_window.dart';

class OverlayWindow extends StatefulWidget {
  const OverlayWindow({Key? key}) : super(key: key);

  @override
  State<OverlayWindow> createState() => _OverlayWindowState();
}

class _OverlayWindowState extends State<OverlayWindow> {
  @override
  void initState() {
    super.initState();
    FlutterOverlayWindow.overlayListener.listen((event) {
      log("Current Event: $event");
    });
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      elevation: 0.0,
      child: Container(
        height: MediaQuery.of(context).size.height,
        color: const Color(0xFFFFD580).withOpacity(0.45),
        child: SingleChildScrollView(
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text("Hello am an overlay"),
                const SizedBox(height: 20.0),
                ElevatedButton(
                  onPressed: () async {
                    await FlutterOverlayWindow.closeOverlay();
                  },
                  child: const Text("Close Me"),
                ),
                ElevatedButton(
                  onPressed: () async {
                    await FlutterOverlayWindow.shareData("It's what it is");
                  },
                  child: const Text("Send data"),
                ),
                const TextField(),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

import 'dart:async';
import 'dart:developer';

import 'package:flutter/services.dart';

class FlutterOverlayWindow {
  FlutterOverlayWindow._();

  static const MethodChannel _channel =
      MethodChannel("x-slayer/overlay_channel");
  static const MethodChannel _overlayChannel =
      MethodChannel("x-slayer/overlay");
  static const BasicMessageChannel _overlayMessageChannel =
      BasicMessageChannel("x-slayer/overlay_messenger", JSONMessageCodec());

  /// Open overLay content
  /// Takes optional;
  ///   - int [height] default is [overlaySizeFill]
  ///   - int [width] default is [overlaySizeFill]
  ///   - OverlayAlignment [width] default is [alignment] [OverlayAlignment.center]
  static Future<void> showOverlay({
    int height = -1,
    int width = -1,
    OverlayAlignment alignment = OverlayAlignment.center,
  }) async {
    await _channel.invokeMethod('showOverlay', {
      "height": height,
      "width": width,
      "alignment": alignment.name,
    });
  }

  static Future<bool> isPermissionGranted() async {
    try {
      return await _channel.invokeMethod<bool>('checkPermission') ?? false;
    } on PlatformException catch (error) {
      log("$error");
      return Future.value(false);
    }
  }

  static Future<bool?> requestPermession() async {
    try {
      return await _channel.invokeMethod<bool?>('requestPermission');
    } on PlatformException catch (error) {
      log("Error requestPermession: $error");
      rethrow;
    }
  }

  /// Closes overlay if open
  static Future<bool?> closeOverlay() async {
    final bool? _res = await _overlayChannel.invokeMethod('close');
    return _res;
  }

  /// broadcast data to and from overlay app
  /// the supported data type are;
  ///   - [int], [double], [bool], [String], null
  ///   - [List] of supported types
  ///   - [Map] of supported types
  static Future sendDataToAndFromOverlay(dynamic data) async {
    return await _overlayMessageChannel.send(data);
  }

  /// Streams message shared between overlay and main app
  static final StreamController _controller = StreamController();
  static Stream<dynamic> overlayListener() {
    _overlayMessageChannel.setMessageHandler((message) async {
      _controller.add(message);
      return message;
    });
    return _controller.stream;
  }

  /// dispose overlay controller
  static void disposeOverlayListener() {
    _controller.close();
  }
}

/// Overlay alignment on screen
enum OverlayAlignment {
  topLeft,
  topCenter,
  topRight,
  centerLeft,
  center,
  centerRight,
  bottomLeft,
  bottomCenter,
  bottomRight
}

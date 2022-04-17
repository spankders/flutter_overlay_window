# flutter_overlay_window

Android plugin for displaying flutter app over other apps

## Usage

Add dependency to pubspec.yaml file


### Android
You'll need to add the `SYSTEM_ALERT_WINDOW` permission and `OverlayService` to your Android Manifest.
```XML
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

    <application>
        ...
        <service android:name="flutter.overlay.window.flutter_overlay_window.OverlayService" android:exported="false" />
    </application>
```

### Entry point

Inside `main.dart` create an entry point for your Overlay widget;
```dart
// overlay entry point
@pragma("vm:entry-point")
void showOverlay() {
  runApp(const MaterialApp(
    debugShowCheckedModeBanner: false,
    home: Material(child: Text("My overlay"))
  ));
}
```


### Methods
To open an overlay, call `FlutterOverlayApps.showOverlay()`. 
Default `height` & `width` is fill screen

### USAGE


```dart
 /// check if overlay permission is granted
 final bool status = await FlutterOverlayWindow.isPermissionGranted();
 
 /// request overlay permission
 /// it will open the overlay settings page and return `true` once the permission granted.
 final bool status = await FlutterOverlayWindow.requestPermession();
 
 /// Open overLay content
 await FlutterOverlayWindow.showOverlay();

 /// closes overlay if open
 await FlutterOverlayWindow.closeOverlay();
 
 /// broadcast data to and from overlay app
 await FlutterOverlayWindow.shareData("Hello from the other side");
 
 /// streams message shared between overlay and main app
  FlutterOverlayWindow.overlayListener.listen((event) {
      log("Current Event: $event");
    });
```

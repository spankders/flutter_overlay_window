package flutter.overlay.window.flutter_overlay_window;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.android.FlutterTextureView;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.android.TransparencyMode;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.JSONMessageCodec;
import io.flutter.plugin.common.MethodChannel;

public class OverlayService extends Service {

    private WindowManager windowManager = null;
    private FlutterView flutterView;
    private MethodChannel flutterChannel = new MethodChannel(FlutterEngineCache.getInstance().get("my_engine_id").getDartExecutor(), FlutterOverlayWindowPlugin.OVERLAY_TAG);
    private BasicMessageChannel overlayMessageChannel = new BasicMessageChannel(FlutterEngineCache.getInstance().get("my_engine_id").getDartExecutor(), FlutterOverlayWindowPlugin.OVERLAY_TAG, JSONMessageCodec.INSTANCE);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        FlutterEngine engine = FlutterEngineCache.getInstance().get("my_engine_id");
        engine.getLifecycleChannel().appIsResumed();

        flutterView = new FlutterView(getApplicationContext(), new FlutterTextureView(getApplicationContext()));

        flutterView.attachToFlutterEngine(FlutterEngineCache.getInstance().get("my_engine_id"));
        flutterView.setFitsSystemWindows(true);
        flutterView.setBackgroundColor(Color.TRANSPARENT);

        flutterChannel.setMethodCallHandler((call, result) -> {
            Log.i("X-S-S-S", "onCreate: Called");
            if (call.method == "close") {
                boolean closed = stopService(new Intent(getBaseContext(), OverlayService.class));
                result.success(closed);
            }
        });
        overlayMessageChannel.setMessageHandler(new MyHandler());

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        int type =  WindowManager.LayoutParams.TYPE_PHONE;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowSetup.width,
                WindowSetup.height,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = WindowSetup.gravity;
        windowManager.addView(flutterView, params);
    }
}

class MyHandler implements BasicMessageChannel.MessageHandler {

    @Override
    public void onMessage(@Nullable Object message, @NonNull BasicMessageChannel.Reply reply) {
        WindowSetup.messenger.send(message);
    }
}

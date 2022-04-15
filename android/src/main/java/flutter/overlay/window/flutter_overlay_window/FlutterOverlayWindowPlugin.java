package flutter.overlay.window.flutter_overlay_window;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.FlutterEngineGroup;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.JSONMessageCodec;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;


public class FlutterOverlayWindowPlugin implements FlutterPlugin, ActivityAware, BasicMessageChannel.MessageHandler, MethodCallHandler, PluginRegistry.ActivityResultListener {


    private MethodChannel channel;
    private Context context;
    private Activity mActivity;
    private BasicMessageChannel messenger;
    private Result pendingResult;
    final int REQUEST_CODE_FOR_OVERLAY_PERMISSION = 19999;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.context = flutterPluginBinding.getApplicationContext();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), OverlayConstants.CHANNEL_TAG);
        channel.setMethodCallHandler(this);

        messenger = new BasicMessageChannel(flutterPluginBinding.getBinaryMessenger(), OverlayConstants.MESSENGER_TAG, JSONMessageCodec.INSTANCE);
        messenger.setMessageHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        pendingResult = result;
        if (call.method.equals("checkPermission")) {
            result.success(checkOverlayPermission());
        } else if (call.method.equals("requestPermission")) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + mActivity.getPackageName()));
            mActivity.startActivityForResult(intent, REQUEST_CODE_FOR_OVERLAY_PERMISSION);
        } else if (call.method.equals("showOverlay")) {
            if (!checkOverlayPermission()) {
                result.error("PERMISSION", "overlay permission is not enabled", null);
                return;
            }
            Integer height = call.argument("height");
            Integer width = call.argument("width");
            String alignment = call.argument("alignment").toString();

            WindowSetup.width = width != null ? width : -1;
            WindowSetup.height = height != null ? width : -1;
            WindowSetup.setGravityFromAlignment(alignment != null ? alignment : "center");
            mActivity.startService(new Intent(context, OverlayService.class));
            result.success(null);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        WindowSetup.messenger = messenger;
        WindowSetup.messenger.setMessageHandler(this);
        mActivity = binding.getActivity();
        FlutterEngineGroup enn = new FlutterEngineGroup(context);
        DartExecutor.DartEntrypoint dEntry = new DartExecutor.DartEntrypoint(
                FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                "showOverlay");
        FlutterEngine engine = enn.createAndRunEngine(context, dEntry);
        FlutterEngineCache.getInstance().put("my_engine_id", engine);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    }

    @Override
    public void onDetachedFromActivity() {
    }

    @Override
    public void onMessage(@Nullable Object message, @NonNull BasicMessageChannel.Reply reply) {
        BasicMessageChannel overlayMessageChannel = new BasicMessageChannel(FlutterEngineCache.getInstance().get("my_engine_id")
                .getDartExecutor(), OverlayConstants.MESSENGER_TAG, JSONMessageCodec.INSTANCE);
        overlayMessageChannel.send(message, reply);
    }

    private boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return false;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FOR_OVERLAY_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                pendingResult.success(true);
            } else {
                pendingResult.success(false);
            }
            return true;
        }
        return false;
    }
}
package tech.bharatx.bharatx_flutter_simple

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import tech.bharatx.common.BharatXCommonUtilManager
import tech.bharatx.simple.BharatXTransactionManager
import tech.bharatx.simple.data_classes.PayeeBankDetails

/** BharatxFlutterSimplePlugin */
class BharatxFlutterSimplePlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
  private val signature = "flutter.bharatx.tech/simple"
  private lateinit var channel: MethodChannel
  private lateinit var applicationContext: Context
  private lateinit var binaryMessenger: BinaryMessenger
  private var activity: FragmentActivity? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    this.binaryMessenger = flutterPluginBinding.binaryMessenger
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, signature)
    channel.setMethodCallHandler(this)
    applicationContext = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "initialize" -> {
        BharatXTransactionManager.initialize(activity!!,
            call.argument<String>("partnerId")!!,
            call.argument<String>("partnerApiKey")!!)
        result.success(null)
      }
      "startTransaction" -> {
        val startTransactionChannel = MethodChannel(binaryMessenger, "${signature}/startTransaction")
        val payeeBankDetailsMap = call.argument<HashMap<String, String>>("payeeBankDetails")
        val payeeBankDetails = if (payeeBankDetailsMap == null) {
          null
        } else {
          PayeeBankDetails(payeeBankDetailsMap["beneficiaryName"]!!,
              payeeBankDetailsMap["accountNumber"]!!,
              payeeBankDetailsMap["ifscCode"]!!)
        }
        BharatXTransactionManager.startTransaction(activity!!, call.argument<String>("transactionId"),
            call.argument<String>("userId"), call.argument<String>("phoneNumber")!!,
            call.argument<Long>("amountInPaise")!!, payeeBankDetails, object : BharatXTransactionManager.TransactionListener() {
          override fun onSuccess() {
            startTransactionChannel.invokeMethod("onSuccess", null)
          }

          override fun onCancelled() {
            startTransactionChannel.invokeMethod("onCancelled", null)
          }

          override fun onFailure() {
            startTransactionChannel.invokeMethod("onFailure", null)
          }

        })
        result.success(null)
      }
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  private fun onActivityChange(activity: Activity) {
    this.activity = activity as FragmentActivity
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    onActivityChange(binding.activity)
  }

  override fun onDetachedFromActivity() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onActivityChange(binding.activity)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }
}

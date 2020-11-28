import 'dart:async';

import 'package:bharatx_flutter_securityhelpers/bharatx_flutter_securityhelpers.dart';
import 'package:flutter/services.dart';

class BharatXTransactionManager {
  static const String _signature = "flutter.bharatx.tech/simple";
  static const MethodChannel _channel = const MethodChannel(_signature);

  static Future<void> initialize(String partnerId, String partnerApiKey,
      [dynamic color]) async {
    await _channel.invokeMethod(
        'initialize', {"partnerId": partnerId, "partnerApiKey": partnerApiKey});
    if (color != null) {
      await BharatXSecurityHelpers.storeThemeColorPreference(color);
    }
  }

  static void startTransaction(String phoneNumber, int amountInPaise,
      {String transactionId,
      String userId,
      Map<String, String> payeeBankDetails,
      void onSuccess(),
      void onCancelled(),
      void onFailure()}) {
    _channel.invokeMethod('startTransaction', {
      "phoneNumber": phoneNumber,
      "amountInPaise": amountInPaise,
      "transactionId": transactionId,
      "userId": userId,
      "payeeBankDetails": payeeBankDetails
    });
    const MethodChannel registerTransactionIdChannel =
        const MethodChannel("$_signature/startTransaction");
    registerTransactionIdChannel.setMethodCallHandler((call) {
      switch (call.method) {
        case "onSuccess":
          {
            onSuccess?.call();
            break;
          }
        case "onCancelled":
          {
            onCancelled?.call();
            break;
          }
        case "onFailure":
          {
            onFailure?.call();
            break;
          }
      }
      return null;
    });
  }
}

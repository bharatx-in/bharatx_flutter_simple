import 'package:bharatx_flutter_simple/bharatx_flutter_simple.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  void confirmTransactionWithUser() async {
    try {
      await BharatXTransactionManager.initialize(
          "testSimplePartnerId", "testSimpleApiKey", Colors.deepOrange);
      BharatXTransactionManager.startTransaction("+91987654321", 1000,
          onSuccess: () {
        print("onSuccess");
      }, onCancelled: () {
        print("onCancelled");
      }, onFailure: () {
        print("onFailure");
      });
    } on PlatformException {
      print("Platform Exception");
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: TextButton(
            onPressed: confirmTransactionWithUser,
            child: Text("Click Here"),
          ),
        ),
      ),
    );
  }
}

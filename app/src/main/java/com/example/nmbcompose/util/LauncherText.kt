package com.example.nmbcompose.util

val launcherTexts = arrayListOf<String>(
    "人，是会思考的芦苇。",
    "这个肥肥又在说怪话",
    "ATM又一次延毕了"
)
public var launcherText = launcherTexts.random()

suspend fun refreshLauncherText() {
    launcherText = launcherTexts.random()
}
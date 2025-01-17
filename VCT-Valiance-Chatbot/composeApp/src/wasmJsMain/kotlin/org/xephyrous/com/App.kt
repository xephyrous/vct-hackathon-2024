package org.xephyrous.com

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import kotlinx.coroutines.*
import org.xephyrous.com.JSInterop.BedrockRuntime
import org.xephyrous.com.JSInterop.CookieHandler
import org.xephyrous.com.JSInterop.Firebase
import org.xephyrous.com.UI.*
import org.xephyrous.com.Utils.Global
import org.xephyrous.com.Utils.Global.initialized
import org.xephyrous.com.Utils.Global.initializing
import org.xephyrous.com.Utils.Global.sessionUUID
import org.xephyrous.com.Utils.updateText

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun App() {
    // Setup app connections and initialize resources
    if (!initialized && !initializing) {
        initializing = true

        GlobalScope.launch(Dispatchers.Default) {
            // Initialize Firebase connection
            Firebase.initializeFirebase().onFailure {
                this.cancel("Could not initialize database connection!")
                Alerts.displayUnmovingAlert("Initialization failure! Please refresh the application")
            }

            // Initialize session
            CookieHandler.getCookie("vctSessionUUID").onFailure {
                this.cancel("Could not initialize session!")
                Alerts.displayUnmovingAlert(
                    "Could not initialize session! Please enable cookies and refresh the application"
                )
            }.onSuccess {
                if (it == "") { // No existing session / cookie

                    Firebase.calculateSessionUUID().onSuccess { uuid -> sessionUUID = uuid }
                    Firebase.setSessionUUID(sessionUUID!!)

                    CookieHandler.addCookie("vctSessionUUID", sessionUUID!!)
                        .onFailure {
                            this.cancel("Could not initialize session!")
                            Alerts.displayUnmovingAlert(
                                "Could not initialize session! Please enable cookies and refresh the application"
                            )
                        }

                    Firebase.createUser()

                    // Get initial greeting
                    BedrockRuntime.InvokeModel("Hello!").onFailure {
                        this.cancel("Model failed to load response!")
                        Alerts.displayUnmovingAlert("Model Initialization Failed")
                    }.onSuccess { response ->
                        updateText(false, response)
                        Firebase.addMessage(response, "system")
                    }
                } else { // Existing session / cookie
                    CookieHandler.getCookie("vctSessionUUID")
                        .onSuccess { uuid -> sessionUUID = uuid }
                        .onFailure {
                            this.cancel("Could not initialize session!")
                            Alerts.displayUnmovingAlert(
                                "Could not initialize session! Please enable cookies and refresh the application"
                            )
                        }
                    Firebase.setSessionUUID(sessionUUID!!)

                    // Initialize messages
                    val messages = Firebase.getMessages().onSuccess { messages ->
                        val temp: ArrayList<ChatBox> = arrayListOf()

                        for (i in 0..<messages.first.size) {
                            if (i < messages.first.size) {
                                if (messages.first[i].isNotEmpty())
                                    temp.add(ChatBox(false, messages.first[i], true))
                            }

                            if (i < messages.second.size) {
                                if (messages.second[i].isNotEmpty())
                                    temp.add(ChatBox(true, messages.second[i], true))
                            }
                        }

                        Global.loadedMessages = temp
                    }.onFailure {
                        Alerts.displayUnmovingAlert(
                            "Failed to load session messages! Please refresh the application"
                        )
                    }
                }
            }
            initializing = false
            initialized = true
        }
    }

    // Main app structure
    MaterialTheme {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E1E)),
        ) {
            Global.viewSize = DpSize(maxWidth, maxHeight)

            UserChatField()
            TeamDisplay()
            Valiance()
            Settings()
            Alerts.createAlert()
        }
    }
}
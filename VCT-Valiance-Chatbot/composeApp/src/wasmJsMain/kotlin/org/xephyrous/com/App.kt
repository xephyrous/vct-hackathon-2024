package org.xephyrous.com

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import kotlinx.coroutines.*
import org.xephyrous.com.JSInterop.BedrockRuntime
import org.xephyrous.com.JSInterop.CookieHandler
import org.xephyrous.com.JSInterop.Firebase
import org.xephyrous.com.UI.TeamDisplay
import org.xephyrous.com.UI.TeamSelect
import org.xephyrous.com.Utils.Global
import org.xephyrous.com.UI.UserChatField
import org.xephyrous.com.UI.Valiance
import org.xephyrous.com.Utils.Global.initialized
import org.xephyrous.com.Utils.Global.sessionUUID
import org.xephyrous.com.Utils.awaitHandled
import org.xephyrous.com.Utils.handleNull
import org.xephyrous.com.Utils.ErrorType.*
import org.xephyrous.com.Utils.updateText

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun App() {
    // Setup app connections and initialize resources
    if (!initialized) {
        GlobalScope.launch(Dispatchers.Default) {
            Firebase.initializeFirebase().awaitHandled(DATABASE_INIT)
                ?: {
                    this.cancel("Could not initialize database connection!")
                    TODO("Initialization failure UI")
                }

            if (CookieHandler.getCookie("vctSessionUUID") == "") {
                sessionUUID = Firebase.calculateSessionUUID().awaitHandled(HASH).toString()
                Firebase.setSessionUUID(sessionUUID!!)

                CookieHandler.addCookie("vctSessionUUID", sessionUUID!!)
                    .handleNull(COOKIE_SET) {
                        this.cancel("Could not initialize session!")
                        TODO("Enable cookies and reload application UI")
                    }

                Firebase.createUser().awaitHandled(DATABASE_SET)
            } else {
                sessionUUID = CookieHandler.getCookie("vctSessionUUID")
                    .handleNull(COOKIE_SET) {
                        this.cancel("Could not initialize session!")
                        TODO("Enable cookies and reload application UI")
                    }
                Firebase.setSessionUUID(sessionUUID!!)
            }

            // Get initial greeting
            updateText(false, BedrockRuntime.InvokeModel("Hello!").awaitHandled(MODEL_RESPONSE).toString())

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

            Global.selectedTeam.name = "Sentinels"
            Global.selectedTeam.coach = "Kaplan"
            Global.selectedTeam.members = arrayOf("Tenz", "Zellsis", "JohnQT", "Zekken", "Sacy")
            Global.selectedTeam.igl = "JohnQT"
            Global.selectedTeam.agents = arrayOf("KAY/O", "Killjoy", "Viper", "Jett", "Sova")
            Global.selectedTeam.theme = arrayOf(4291890968, 4293620601, 4293283413, 4294967295, 4278190080)

            UserChatField()
            TeamSelect()
            TeamDisplay()
            Valiance()
        }
    }
}
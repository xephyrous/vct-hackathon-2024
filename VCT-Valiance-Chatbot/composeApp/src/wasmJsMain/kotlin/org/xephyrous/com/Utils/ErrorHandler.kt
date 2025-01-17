package org.xephyrous.com.Utils

import kotlinx.coroutines.await
import org.xephyrous.com.JSInterop.Firebase
import org.xephyrous.com.JSInterop.JSFirebase
import org.xephyrous.com.UI.Alerts
import vctvaliancechatbot.composeapp.generated.resources.Res
import kotlin.js.Promise

val _emptyLambda: () -> Unit = {}
val _emptyTypedLambda: () -> Any? = { null }

/**
 * Used alongside [awaitHandled] to serve accurate errors
 * @param reason The reason for the error, to be used in the UI alert
 */
enum class ErrorType(val reason: String) {
    DATABASE_INIT("Initializing Database Connection"),
    DATABASE_SET("Setting Database Member"),
    DATABASE_GET("Getting Database Member"),
    DATABASE_CHECK("Checking Database Member"),
    COOKIE_SET("Setting Browser Cookie"),
    COOKIE_GET("Getting Browser Cookie"),
    HASH("Generating Session Hash"),
    INTERNAL("Internally"),
    MODEL_RESPONSE("Handling AI Response")
}

/**
 * Handles error alerts for asynchronous functions, alerting them to the UI layer
 * @param type The [ErrorType] of the error if it is thrown, provides error details to the UI layer
 */
@Suppress("UNCHECKED_CAST")
suspend fun <T> Promise<JsAny>.awaitHandled(type: ErrorType, then: (JsAny) -> Unit = {}) : T? {
    return try {
        val waitVal = await<JsAny>()
        then(waitVal)
        waitVal as T
    } catch (e: Throwable) {
        JSFirebase.debug(e.toString())
        Alerts.displayAlert("Error ${type.reason}")
        null
    }
}

/**
 * Handles error alerts for asynchronous functions, that does not perform any casts, alerting them to the UI layer
 * This acts only on JsAny returning functions, in order to avoid type degradation
 * @param type The [ErrorType] of the error if it is thrown, provides error details to the UI layer
 */
suspend fun Promise<JsAny>.awaitHandledNoCast(type: ErrorType, then: (JsAny) -> Unit = {}) : JsAny? {
    return try {
        val waitVal = await<JsAny>()
        then(waitVal)
        waitVal
    } catch (e: Throwable) {
        JSFirebase.debug(e.toString())
        Alerts.displayAlert("Error ${type.reason}")
        null
    }
}

suspend fun Promise<*>.awaitHandledUnit(type: ErrorType) : Result<Unit> {
    return try {
        await<JsAny>()
        Result.success(Unit)
    } catch(e: Throwable) {
        Alerts.displayAlert("Error ${type.reason}")
        Result.failure(Exception(""))
    }
}

/**
 * Handles a null (failed) function value for non-async interop functions return JsAny?,
 * alerting then to the UI layer, along with an optional callback
 * @param type The [ErrorType] of the error if it is thrown, provides error details to the UI layer
 * @param callback An optional custom callback invoked when a null value is handled
 * @param suppressUI Whether to suppress the UI callback in favor of [callback] (callback must not be empty/undefined!)
 */
fun JsAny?.handleNull(
    type: ErrorType = ErrorType.INTERNAL,
    suppressUI: Boolean = false,
    callback: () -> Unit = {}
) {
    if (this == null) {
        if (!suppressUI && callback != _emptyLambda) {
            /* TODO("UI alert here using 'type' for details") */
        }

        callback()
    }
}

/**
 * Handles a null (failed) function value, alerting then to the UI layer,
 * along with an optional return handling callback
 * @param type The [ErrorType] of the error if it is thrown, provides error details to the UI layer
 * @param callback An optional custom callback invoked when a null value is handled
 * @param suppressUI Whether to suppress the UI callback in favor of [callback] (callback must not be empty/undefined!)
 * @return null
 */
fun <T> T?.handleNull(
    type: ErrorType = ErrorType.INTERNAL,
    suppressUI: Boolean = false,
    callback: () -> T? = { null }
): T? {
    if (this == null && callback != _emptyTypedLambda && callback != _emptyLambda) {
        // TODO("UI alert here using 'type' for details")
        return callback()
    }

    return this
}
package org.xephyrous.com.UI

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import vctvaliancechatbot.composeapp.generated.resources.*
import vctvaliancechatbot.composeapp.generated.resources.Res
import vctvaliancechatbot.composeapp.generated.resources.tungsten_bold
import vctvaliancechatbot.composeapp.generated.resources.valorant

/**
 * App major heading / splash screen font
 */
@Composable
fun ValorantFont() = FontFamily( Font(Res.font.valorant, FontWeight.Normal) )

/**
 * Main app font
 */
@Composable
fun FFMarkFont() = FontFamily(
    Font(Res.font.Mark_Bold, FontWeight.Bold),
    Font(Res.font.Mark_Black, FontWeight.Black),
    Font(Res.font.Mark_Extralight, FontWeight.ExtraLight),
    Font(Res.font.Mark_Light, FontWeight.Light),
    Font(Res.font.Mark_Medium, FontWeight.Medium),
    Font(Res.font.Mark_Regular, FontWeight.Normal),
    Font(Res.font.Mark_SmallCaps_Black, FontWeight.Black)
)


/**
 * Secondary app font
 */
@Composable
fun TungstenFont() = FontFamily( Font(Res.font.tungsten_bold, FontWeight.Bold) )

/**
 * Represents the application's Typography, set for each type of text
 */
val appTypography = Typography(
    body1 = TextStyle(fontWeight = FontWeight.Normal),
    body2 = TextStyle(fontWeight = FontWeight.Medium),
    button = TextStyle(fontWeight = FontWeight.SemiBold),
    caption = TextStyle(fontWeight = FontWeight.Light),
    h1 = TextStyle(fontWeight = FontWeight.Black),
    h2 = TextStyle(fontWeight = FontWeight.ExtraBold),
    h3 = TextStyle(fontWeight = FontWeight.Bold),
    h4 = TextStyle(fontWeight = FontWeight.SemiBold),
    h5 = TextStyle(fontWeight = FontWeight.Medium),
    h6 = TextStyle(fontWeight = FontWeight.Normal),
    overline = TextStyle(fontWeight = FontWeight.Light),
    subtitle1 = TextStyle(fontWeight = FontWeight.Normal),
    subtitle2 = TextStyle(fontWeight = FontWeight.Medium)
)


/**
 * A MaterialTheme wrapper for the main application,
 * applies the typography to all text
 *
 * @param content Content to apply the Typography to
 */
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        typography = appTypography,
        content = content
    )
}
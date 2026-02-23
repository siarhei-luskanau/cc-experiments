import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.bookreads.di.KoinApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() = ComposeViewport { KoinApp() }

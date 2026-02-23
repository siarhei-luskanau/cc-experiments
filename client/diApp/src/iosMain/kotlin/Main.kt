import androidx.compose.ui.window.ComposeUIViewController
import com.bookreads.di.KoinApp
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController =
    ComposeUIViewController {
        KoinApp()
    }

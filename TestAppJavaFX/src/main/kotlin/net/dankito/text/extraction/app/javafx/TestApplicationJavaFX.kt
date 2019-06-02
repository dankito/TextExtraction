package net.dankito.text.extraction.app.javafx

import javafx.application.Application
import net.dankito.text.extraction.app.javafx.window.main.MainWindow
import net.dankito.utils.javafx.ui.Utf8App


class TestApplicationJavaFX : Utf8App("Messages", MainWindow::class) {

}


fun main(args: Array<String>) {
    Application.launch(TestApplicationJavaFX::class.java, *args)
}
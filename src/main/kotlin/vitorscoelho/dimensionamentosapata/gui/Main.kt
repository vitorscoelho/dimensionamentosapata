package vitorscoelho.dimensionamentosapata.gui

import javafx.application.Application
import tornadofx.*
import vitorscoelho.dimensionamentosapata.gui.estilos.EstiloPrincipal
import vitorscoelho.dimensionamentosapata.gui.views.ViewInicial
import java.util.Locale


fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    Application.launch(MeuApp::class.java, *args)
}

class MeuApp : App(ViewInicial::class, EstiloPrincipal::class) {
    init {
        reloadStylesheetsOnFocus()
    }
}
package vitorscoelho.dimensionamentosapata.gui

import javafx.application.Application
import tornadofx.*
import vitorscoelho.dimensionamentosapata.gui.estilos.EstiloPrincipal
import vitorscoelho.dimensionamentosapata.gui.views.ViewInicial
import vitorscoelho.dimensionamentosapata.utils.inicializarUnidadesDeMedidaExtras
import java.util.*


fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    FX.locale= Locale("en","US")
//    ResourceBundle.getBundle("ViewInicial")
    inicializarUnidadesDeMedidaExtras()
    Application.launch(Aplicacao::class.java, *args)
}

class Aplicacao : App(ViewInicial::class, EstiloPrincipal::class) {
    init {
        reloadStylesheetsOnFocus()
        val versaoJava = System.getProperty("java.version")
        val versaoJavaFX = System.getProperty("javafx.version")
        println("Versão Java: $versaoJava // Versão JavaFX: $versaoJavaFX")
    }
}
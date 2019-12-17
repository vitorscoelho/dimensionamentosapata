package vitorscoelho.dimensionamentosapata.gui.controllers

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import tornadofx.Controller
import tornadofx.FX.Companion.messages
import vitorscoelho.dimensionamentosapata.gui.models.*
import vitorscoelho.dimensionamentosapata.gui.utils.PropertiesContext
import java.util.*

internal class ControllerInicial : Controller() {
    val processoIterativo = BeanProcessoIterativo()
    val esforcosDeformacoes = BeanEsforcosDeformacoes()
    val sapata = BeanSapataRetangular()
    val context = PropertiesContext()

    val textoResultadosProperty: StringProperty = SimpleStringProperty("")

    fun dimensionar() {
        context.commit()
    }
}
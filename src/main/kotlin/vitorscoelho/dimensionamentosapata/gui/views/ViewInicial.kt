package vitorscoelho.dimensionamentosapata.gui.views

import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units.METRE
import tornadofx.*
import vitorscoelho.dimensionamentosapata.gui.controllers.ControllerInicial
import vitorscoelho.dimensionamentosapata.gui.models.*
import vitorscoelho.dimensionamentosapata.gui.utils.TipoInput
import vitorscoelho.dimensionamentosapata.gui.utils.fieldNumber
import vitorscoelho.dimensionamentosapata.gui.utils.fieldQuantity

internal class ViewInicial : View() {
    private val controller: ControllerInicial by inject()
    val processoIterativo = BeanProcessoIterativo()
    val esforcosDeformacoes = BeanEsforcosDeformacoes()
    val sapata = SapataRetangularModel(BeanSapataRetangular())
//    private val processoIterativo: ProcessoIterativoModel
//        get() = controller.processoIterativo
//    private val esforcosDeformacoes: BeanEsforcosDeformacoes
//        get() = controller.esforcosDeformacoes
//    private val sapata: BeanSapataRetangular
//        get() = controller.sapata

    override val root = form {
        fieldset(text = messages["fieldsetProcessoIterativo"]) {
            //            fieldQuantity(property = processoIterativo.ecgInicialProperty)
//            fieldQuantity(property = processoIterativo.curvaturaXInicialProperty)
//            fieldQuantity(property = processoIterativo.curvaturaYInicialProperty)
//            fieldNumber(property = processoIterativo.qtdMaximaIteracoesProperty)
//            fieldQuantity(property = processoIterativo.toleranciaIteracaoNormalProperty)
//            fieldQuantity(property = processoIterativo.toleranciaIteracaoMomento)
        }
//        fieldset(text = messages["fieldsetEsforcosSolicitantes"]) {
//            fieldQuantity(property = esforcosDeformacoes.normalProperty)
//            fieldQuantity(property = esforcosDeformacoes.momentoXProperty)
//            fieldQuantity(property = esforcosDeformacoes.momentoYProperty)
//        }
        fieldset(text = messages["fieldsetDadosSapata"]) {
            //            fieldQuantity(property = esforcosDeformacoes.moduloReacaoSoloProperty)
//            fieldQuantity(property = sapata.lx)
            fieldQuantity(property = sapata.ly)
        }
        button("Mudar") {
            //            action { processoIterativo.ecgInicialProperty.value = Quantities.getQuantity(10.0, METRE) }

            enableWhen { processoIterativo.dirty }
        }
    }

    init {
        title = messages["tituloJanela"]
    }
}

//FUNÇÃO PARA TENTAR FAZER COM CSS DEPOIS
private fun configurarTextAreaResultados(textArea: TextArea) = with(textArea) {
    vboxConstraints { marginBottom = 20.0 }
    vgrow = Priority.ALWAYS
}

//FUNÇÃO PARA TENTAR FAZER COM CSS DEPOIS
private fun configurarVboxCanvas(vbox: VBox) = with(vbox) {
    fitToParentSize()
}
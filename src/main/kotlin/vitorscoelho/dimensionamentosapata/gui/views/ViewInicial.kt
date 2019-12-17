package vitorscoelho.dimensionamentosapata.gui.views

import javafx.geometry.Orientation
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*
import vitorscoelho.dimensionamentosapata.gui.controllers.ControllerInicial
import vitorscoelho.dimensionamentosapata.gui.estilos.EstiloPrincipal
import vitorscoelho.dimensionamentosapata.gui.models.*
import vitorscoelho.dimensionamentosapata.gui.utils.PropertiesContext
import vitorscoelho.dimensionamentosapata.gui.utils.textos
import vitorscoelho.dimensionamentosapata.gui.utils.fieldNumber
import vitorscoelho.dimensionamentosapata.gui.utils.fieldQuantity

internal class ViewInicial : View() {
    private val controller: ControllerInicial by inject()
    private val context: PropertiesContext
        get() = controller.context
    private val processoIterativo: BeanProcessoIterativo
        get() = controller.processoIterativo
    private val esforcosDeformacoes: BeanEsforcosDeformacoes
        get() = controller.esforcosDeformacoes
    private val sapata: BeanSapataRetangular
        get() = controller.sapata

    private val fieldsetProcessoIterativo = fieldset(text = textos["fieldsetProcessoIterativo"]) {
        hbox {
            labelPosition = Orientation.VERTICAL
            vbox {
                fieldQuantity(property = processoIterativo.ecgInicialProperty, context = context)
                fieldQuantity(property = processoIterativo.curvaturaXInicialProperty, context = context)
                fieldQuantity(property = processoIterativo.curvaturaYInicialProperty, context = context)
            }
            vbox {
                fieldNumber(property = processoIterativo.qtdMaximaIteracoesProperty, context = context)
                fieldQuantity(property = processoIterativo.toleranciaIteracaoNormalProperty, context = context)
                fieldQuantity(property = processoIterativo.toleranciaIteracaoMomentoProperty, context = context)
            }
        }
    }

    private val fieldsetEsforcos = fieldset(text = textos["fieldsetEsforcosSolicitantes"]) {
        labelPosition = Orientation.VERTICAL
        fieldQuantity(property = esforcosDeformacoes.normalProperty, context = context)
        fieldQuantity(property = esforcosDeformacoes.momentoXProperty, context = context)
        fieldQuantity(property = esforcosDeformacoes.momentoYProperty, context = context)
    }

    private val fieldsetSapata = fieldset(text = textos["fieldsetDadosSapata"]) {
        labelPosition = Orientation.VERTICAL
        fieldQuantity(property = esforcosDeformacoes.moduloReacaoSoloProperty, context = context)
        fieldQuantity(property = sapata.lxProperty, context = context)
        fieldQuantity(property = sapata.lyProperty, context = context)
    }

    private val formDados = form {
        add(fieldsetProcessoIterativo)
        hbox {
            add(fieldsetEsforcos)
            add(fieldsetSapata)
        }
    }
    override val root = hbox {
        setPrefSize(1300.0, 600.0)
        vbox {
            addClass(EstiloPrincipal.vboxDados)
            add(formDados)
            button(textos["botaoDimensionar"]) {
                action { controller.dimensionar() }
                enableWhen { controller.context.dirtyAndValid }
            }
            textarea(controller.textoResultadosProperty) {
                configurarTextAreaResultados(this)
                isEditable = false
            }
        }
    }

    init {
        title = textos["tituloJanela"]
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
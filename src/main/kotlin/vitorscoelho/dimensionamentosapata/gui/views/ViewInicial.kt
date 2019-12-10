package vitorscoelho.dimensionamentosapata.gui.views

import javafx.geometry.Orientation
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units.NEWTON
import tornadofx.*
import vitorscoelho.dimensionamentosapata.gui.controllers.ControllerInicial
import vitorscoelho.dimensionamentosapata.gui.estilos.EstiloPrincipal
import vitorscoelho.dimensionamentosapata.gui.models.Dados
import vitorscoelho.dimensionamentosapata.gui.models.DadosModel
import vitorscoelho.dimensionamentosapata.gui.models.NovosDados
import vitorscoelho.dimensionamentosapata.utils.SpringStiffnessPerUnitArea
import javax.measure.Quantity

internal class ViewInicial : View("Verificação de tensões em sapatas rígidas retangulares") {
    private val controller: ControllerInicial by inject()
    val dadosQuantity: NovosDados
        get() = controller.model
    private val contextoDeValidacao = ContextoDeValidacao()
    //TODO fazer um programa que pegue vários esforços e, através de intervalos de dimensões e critérios aplicados pelo usuário, encontre qual é a sapata mais econômica que atenda as condições

    private val fieldsetDadosProcessoIterativo = fieldset("Processo iterativo") {
        labelPosition = Orientation.VERTICAL
        hbox {
            vbox {
                fieldQuantity(property = dadosQuantity.ecgInicialProperty)
                fieldQuantity(property = dadosQuantity.curvaturaXInicialProperty)
                fieldQuantity(property = dadosQuantity.curvaturaYInicialProperty)
            }
            vbox {
                fieldQuantity(property = dadosQuantity.qtdMaximaIteracoesProperty, permitirNegativo = false)
                fieldQuantity(property = dadosQuantity.toleranciaNormalIteracaoProperty, permitirNegativo = false)
                fieldQuantity(property = dadosQuantity.toleranciaMomentoIteracaoProperty, permitirNegativo = false)
            }
        }
    }
    private val fieldsetDadosSapata = fieldset("Dados da sapata") {
        labelPosition = Orientation.VERTICAL
        fieldQuantity(property = dadosQuantity.lxProperty, permitirNegativo = false)
        fieldQuantity(property = dadosQuantity.lyProperty, permitirNegativo = false)
        fieldQuantity(property = dadosQuantity.moduloReacaoSoloProperty, permitirNegativo = false) { tf: TextField ->
            tf.enableWhen(dadosQuantity.utilizarModuloReacaoSoloProperty)
            checkbox(property = dadosQuantity.utilizarModuloReacaoSoloProperty) {
                this.selectedProperty().onChange {
                    dadosQuantity.moduloReacaoSoloProperty.magnitude = 1.0
                }
            }
        }
    }
    private val fieldsetDadosEsforcos = fieldset("Esforços solicitantes") {
        labelPosition = Orientation.VERTICAL
        fieldQuantity(property = dadosQuantity.normalProperty)
        fieldQuantity(property = dadosQuantity.momentoXProperty)
        fieldQuantity(property = dadosQuantity.momentoYProperty)
    }

    private val formDados = form {
        hbox {
            add(fieldsetDadosProcessoIterativo)
            add(fieldsetDadosSapata)
            add(fieldsetDadosEsforcos)
        }
    }

    override val root = hbox {
        setPrefSize(1300.0, 600.0)
        hbox {
            vbox {
                addClass(EstiloPrincipal.vboxDados)
                form {
                    add(fieldsetDadosProcessoIterativo)
                    hbox {
                        add(fieldsetDadosSapata)
                        add(fieldsetDadosEsforcos)
                    }
                }
                button("Dimensionar") {
                    action { controller.dimensionar() }
//                    enableWhen { dadosQuantity.dirty.and(contextoDeValidacao.invalidProperty.not()) }
                }
                textarea(controller.textoResultadosProperty) {
                    configurarTextAreaResultados(this)
                    isEditable = false
                }
            }
        }
        vbox {
            addClass(EstiloPrincipal.vboxCanvas)
            configurarVboxCanvas(this)
            add(controller.canvasSapata.node)
            label(controller.textoXMouseProperty)
            label(controller.textoYMouseProperty)
            label(controller.textoTensaoProperty)
            label(controller.textoDeformacaoProperty)
            label(controller.textoLegendaDesenhoProperty)
        }
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
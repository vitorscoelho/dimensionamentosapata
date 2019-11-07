package vitorscoelho.dimensionamentosapata.gui.views

import javafx.geometry.Orientation
import tornadofx.*
import vitorscoelho.dimensionamentosapata.gui.controllers.ControllerInicial
import vitorscoelho.dimensionamentosapata.gui.models.DadosModel

internal class ViewInicial : View() {
    private val controller: ControllerInicial by inject()
    val model: DadosModel
        get() = controller.model
    private val contextoDeValidacao = ContextoDeValidacao()
    //TODO fazer um programa que pegue vários esforços e, através de intervalos de dimensões e critérios aplicados pelo usuário, encontre qual é a sapata mais econômica que atenda as condições

    private val dados = form {
        fieldset("Critérios para processo iterativo") {
            labelPosition = Orientation.VERTICAL
            field("Δcg (cm)") {
                textfield {
                    numeroReal(
                        property = model.ecgInicial,
                        descricao = "Deformação no centro de gravidade da seção adotada na primeira iteração\r\n(positiva quando comprime o solo)",
                        contextoDeValidacao = contextoDeValidacao
                    )
                }
            }
            field("Φx (rad)") {
                textfield {
                    numeroReal(
                        property = model.curvaturaXInicial,
                        descricao = "Rotação em torno do eixo X na primeira iteração\r\n(positivo para o vetor apontando para a direita)",
                        contextoDeValidacao = contextoDeValidacao
                    )
                }
            }
            field("Φy (rad)") {
                textfield {
                    numeroReal(
                        property = model.curvaturaYInicial,
                        descricao = "Rotação em torno do eixo Y na primeira iteração\r\n(positivo para o vetor apontando para a cima)",
                        contextoDeValidacao = contextoDeValidacao
                    )
                }
            }
            field("Quantidade máxima de iterações") {
                textfield {
                    inteiroMaiorQueZero(
                        property = model.qtdMaximaIteracoes,
                        descricao = "Quantidade máxima permitida de iterações",
                        contextoDeValidacao = contextoDeValidacao
                    )
                }
            }
            field("Tolerância (kN ou kN.m)") {
                textfield {
                    realMaiorQueZero(
                        property = model.toleranciaIteracao,
                        descricao = "Diferença admissível entre o esforço solicitante e o resistente. Em kN para normal e kN.m para momento.",
                        contextoDeValidacao = contextoDeValidacao
                    )
                }
            }
        }
        fieldset("Dados da sapata") {
            labelPosition = Orientation.VERTICAL
            field("Lx (cm)") {
                val tf = textfield {
                    realMaiorQueZero(
                        property = model.lx,
                        descricao = "Comprimento da sapata em x",
                        contextoDeValidacao = contextoDeValidacao
                    )
                }
            }
            field("Ly (cm)") {
                textfield {
                    realMaiorQueZero(
                        property = model.ly,
                        descricao = "Comprimento da sapata em y",
                        contextoDeValidacao = contextoDeValidacao
                    )
                }
            }
            field("Kr (MPa/m)") {
                val tf = textfield {
                    realMaiorQueZero(
                        property = model.moduloReacaoSolo,
                        descricao = "Módulo de reação do solo",
                        contextoDeValidacao = contextoDeValidacao
                    )
                    enableWhen(model.utilizarModuloReacaoSolo)
                }
                checkbox(property = model.utilizarModuloReacaoSolo) {
                    this.selectedProperty().onChange { model.moduloReacaoSolo.value = 1.0 }
                }
            }
        }
        fieldset("Esforços solicitantes") {
            labelPosition = Orientation.VERTICAL
            field("N (kN)") {
                textfield {
                    numeroReal(
                        property = model.normal,
                        descricao = "Esforço normal solicitante (positivo quando compressão)",
                        contextoDeValidacao = contextoDeValidacao
                    )
                }
            }
            field("Mx (kN.m)") {
                textfield {
                    numeroReal(
                        property = model.momentoX,
                        descricao = "Momento fletor solicitante em torno do eixo X (positivo para o vetor apontando para a direita)",
                        contextoDeValidacao = contextoDeValidacao
                    )
                }
            }
            field("My (kN.m)") {
                textfield {
                    numeroReal(
                        property = model.momentoY,
                        descricao = "Momento fletor solicitante em torno do eixo Y (positivo para o vetor apontando para a cima)",
                        contextoDeValidacao = contextoDeValidacao
                    )
                }
            }
        }
    }

    override val root = hbox {
        setPrefSize(1300.0, 600.0)
        vbox {
            fitToParentHeight()
            minWidth = 380.0
            minHeight = 700.0
            add(dados)
            button("Dimensionar") {
                action { controller.dimensionar() }
                enableWhen { model.dirty.and(contextoDeValidacao.invalidProperty.not()) }
            }
            textarea(controller.textoResultadosProperty) {
                isEditable = false
            }
        }
        vbox {
            fitToParentSize()
            add(controller.canvasSapata.node)
            label(controller.textoXMouseProperty)
            label(controller.textoYMouseProperty)
            label(controller.textoTensaoProperty)
            label(controller.textoDeformacaoProperty)
            label(controller.textoLegendaDesenhoProperty)
        }
    }
}
package vitorscoelho.dimensionamentosapata.gui.views

import javafx.geometry.Orientation
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*
import vitorscoelho.dimensionamentosapata.gui.controllers.ControllerInicial
import vitorscoelho.dimensionamentosapata.gui.estilos.EstiloPrincipal
import vitorscoelho.dimensionamentosapata.gui.models.DadosModel

internal class ViewInicial : View("Verificação de tensões em sapatas rígidas retangulares") {
    private val controller: ControllerInicial by inject()
    val model: DadosModel
        get() = controller.model
    private val contextoDeValidacao = ContextoDeValidacao()
    //TODO fazer um programa que pegue vários esforços e, através de intervalos de dimensões e critérios aplicados pelo usuário, encontre qual é a sapata mais econômica que atenda as condições

    private val fieldsetDadosProcessoIterativo = fieldset("Processo iterativo") {
        labelPosition = Orientation.VERTICAL
        hbox {
            vbox {
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
            }
            vbox {
                field("Qtd. máxima de iterações") {
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
                            descricao = "Diferença admissível entre o esforço solicitante e o resistente\r\nEm kN para normal e kN.m para momento",
                            contextoDeValidacao = contextoDeValidacao
                        )
                    }
                }
            }
        }
    }
    private val fieldsetDadosSapata = fieldset("Dados da sapata") {
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
            textfield {
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
    private val fieldsetDadosEsforcos = fieldset("Esforços solicitantes") {
        labelPosition = Orientation.VERTICAL
        field("N (kN)") {
            textfield {
                numeroReal(
                    property = model.normal,
                    descricao = "Esforço normal solicitante\r\n(positivo quando compressão)",
                    contextoDeValidacao = contextoDeValidacao
                )
            }
        }
        field("Mx (kN.m)") {
            textfield {
                numeroReal(
                    property = model.momentoX,
                    descricao = "Momento fletor solicitante em torno do eixo X\r\n(positivo para o vetor apontando para a direita)",
                    contextoDeValidacao = contextoDeValidacao
                )
            }
        }
        field("My (kN.m)") {
            textfield {
                numeroReal(
                    property = model.momentoY,
                    descricao = "Momento fletor solicitante em torno do eixo Y\r\n(positivo para o vetor apontando para a cima)",
                    contextoDeValidacao = contextoDeValidacao
                )
            }
        }
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
                    enableWhen { model.dirty.and(contextoDeValidacao.invalidProperty.not()) }
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
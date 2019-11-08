package vitorscoelho.dimensionamentosapata.gui.estilos

import javafx.geometry.Pos
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import tornadofx.*

internal class EstiloPrincipal : Stylesheet() {
    private val fonteRegular = loadFont("/vitorscoelho/dimensionamentosapata/gui/fontes/Ubuntu-Regular.ttf", 15)!!
    private val fonteNegrito = loadFont("/vitorscoelho/dimensionamentosapata/gui/fontes/Ubuntu-Bold.ttf", 15)!!

    init {
        val fontSizePadrao = 13.px
        val fontSizeTooltip = fontSizePadrao
        root {
            font = fonteRegular
            fontSize = fontSizePadrao
        }
        tooltipDescricao {
            backgroundColor = multi(Color.GRAY)
            font = fonteRegular
            fontSize = fontSizeTooltip
        }
        tooltipErro {
            backgroundColor = multi(Color.RED)
            font = fonteRegular
            fontSize = fontSizeTooltip
        }
        val larguraFieldset = 180.px
        val larguraBotaoETextArea = larguraFieldset * 2.0
        val spacingVBox = 20.px
        vboxDados {
            font = fonteRegular
            fontSize = fontSizePadrao
            alignment = Pos.TOP_CENTER
            spacing = spacingVBox
            setMinMaxPrefWidth(Region.USE_COMPUTED_SIZE.px)
            setMinMaxPrefHeight(Region.USE_COMPUTED_SIZE.px)
            form {
                font = fonteRegular
                fontSize = fontSizePadrao
                legend {
                    font = fonteNegrito
                    fontSize = fontSizePadrao * 1.2
                }
                hbox {
                    spacing = 20.px
                }
                field {
                    setMinMaxPrefWidth(larguraFieldset)
                    textField {
                        prefWidth = larguraFieldset
                        maxWidth = larguraFieldset
                        alignment = Pos.CENTER_RIGHT
                    }
                }
            }
            button {
                setMinMaxPrefWidth(larguraBotaoETextArea)
                font = fonteNegrito
                fontSize = fontSizePadrao * 1.5
            }
            textArea {
                setMinMaxPrefWidth(larguraBotaoETextArea)
            }
        }
        vboxCanvas {
            font = fonteRegular
            fontSize = fontSizePadrao * 1.2
        }
    }

    private fun PropertyHolder.setMinMaxPrefWidth(valor: Dimension<Dimension.LinearUnits>) {
        prefWidth = valor; minWidth = valor; maxWidth = valor
    }

    private fun PropertyHolder.setMinMaxPrefHeight(valor: Dimension<Dimension.LinearUnits>) {
        prefHeight = valor; minHeight = valor; maxHeight = valor
    }

    companion object {
        private val gridpane by csselement("GridPane")
        private val hbox by csselement("HBox")
        val tooltipDescricao by cssclass()
        val tooltipErro by cssclass()
        val vboxDados by cssclass()
        val vboxCanvas by cssclass()
    }
}
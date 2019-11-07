package vitorscoelho.dimensionamentosapata.gui.estilos

import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.*

internal class EstiloPrincipal : Stylesheet() {
    private val fonteRegular = loadFont("/vitorscoelho/dimensionamentosapata/gui/fontes/Ubuntu-Regular.ttf", 15)!!
    private val fonteNegrito = loadFont("/vitorscoelho/dimensionamentosapata/gui/fontes/Ubuntu-Bold.ttf", 15)!!

    init {
        root {
            font = fonteRegular
            fontSize = 13.px
        }
        fundoBranco {
            backgroundColor = multi(Color.WHITE)
        }
        gridpane {
            hgap = 2.0.px
            vgap = 6.0.px
        }
        textField {
            alignment = Pos.CENTER_RIGHT
        }
        label
        form {
            font = fonteRegular
            fontSize = 10.px
        }
        tooltipDescricao {
            backgroundColor = multi(Color.DEEPSKYBLUE)
            fontSize = 12.px
        }
        tooltipErro {
            backgroundColor = multi(Color.RED)
            fontSize = 12.px
        }
    }

    companion object {
        val fundoBranco by cssclass()
        //        val gridpane by cssclass()
        private val gridpane by csselement("GridPane")
        val tooltipDescricao by cssclass()
        val tooltipErro by cssclass()
    }
}
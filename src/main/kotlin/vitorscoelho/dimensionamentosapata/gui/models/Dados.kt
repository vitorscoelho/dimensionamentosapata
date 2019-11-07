package vitorscoelho.dimensionamentosapata.gui.models

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.onChange
import tornadofx.setValue

class Dados {
    val ecgInicialProperty = SimpleDoubleProperty()
    var ecgInicial by ecgInicialProperty
    val curvaturaXInicialProperty = SimpleDoubleProperty()
    var curvaturaXInicial by curvaturaXInicialProperty
    val curvaturaYInicialProperty = SimpleDoubleProperty()
    var curvaturaYInicial by curvaturaYInicialProperty
    val qtdMaximaIteracoesProperty = SimpleIntegerProperty()
    var qtdMaximaIteracoes by qtdMaximaIteracoesProperty
    val toleranciaIteracaoProperty = SimpleDoubleProperty()
    var toleranciaIteracao by toleranciaIteracaoProperty

    val lxProperty = SimpleDoubleProperty()
    var lx by lxProperty
    val lyProperty = SimpleDoubleProperty()
    var ly by lyProperty
    val moduloReacaoSoloProperty = SimpleDoubleProperty()
    var moduloReacaoSolo by moduloReacaoSoloProperty
    val utilizarModuloReacaoSoloProperty = SimpleBooleanProperty()
    var utilizarModuloReacaoSolo by utilizarModuloReacaoSoloProperty

    val normalProperty = SimpleDoubleProperty()
    var normal by normalProperty
    val momentoXProperty = SimpleDoubleProperty()
    var momentoX by momentoXProperty
    val momentoYProperty = SimpleDoubleProperty()
    var momentoY by momentoYProperty

    init {
        ecgInicial = 1.0 / 100.0
        qtdMaximaIteracoes = 100
        toleranciaIteracao = 0.001

        lx = 100.0
        ly = 100.0
        moduloReacaoSolo = 1.0
        utilizarModuloReacaoSolo = false
    }
}

class DadosModel(dados: Dados) : ItemViewModel<Dados>(dados) {
    val ecgInicial = bind(Dados::ecgInicialProperty)
    val curvaturaXInicial = bind(Dados::curvaturaXInicialProperty)
    val curvaturaYInicial = bind(Dados::curvaturaYInicialProperty)
    val qtdMaximaIteracoes = bind(Dados::qtdMaximaIteracoesProperty)
    val toleranciaIteracao = bind(Dados::toleranciaIteracaoProperty)

    val lx = bind(Dados::lxProperty)
    val ly = bind(Dados::lyProperty)
    val moduloReacaoSolo = bind(Dados::moduloReacaoSoloProperty)
    val utilizarModuloReacaoSolo = bind(Dados::utilizarModuloReacaoSoloProperty)

    val normal = bind(Dados::normalProperty)
    val momentoX = bind(Dados::momentoXProperty)
    val momentoY = bind(Dados::momentoYProperty)
}
package vitorscoelho.dimensionamentosapata.gui.models

import tech.units.indriya.unit.Units.METRE
import tech.units.indriya.unit.Units.RADIAN
import tornadofx.*
import javax.measure.Quantity
import javax.measure.quantity.Angle
import javax.measure.quantity.Force
import javax.measure.quantity.Length
import vitorscoelho.dimensionamentosapata.gui.descricoes
import vitorscoelho.dimensionamentosapata.gui.utils.InputIntegerProperty
import vitorscoelho.dimensionamentosapata.gui.utils.InputObjectProperty
import vitorscoelho.dimensionamentosapata.gui.utils.TipoInput
import vitorscoelho.dimensionamentosapata.utils.*

class BeanProcessoIterativo : ViewModel() {
    val toleranciaIteracaoMomentoProperty = InputObjectProperty<Quantity<Moment>>()
    var toleranciaIteracaoMomento by toleranciaIteracaoMomentoProperty
    val toleranciaIteracaoNormalProperty = InputObjectProperty<Quantity<Force>>()
    var toleranciaIteracaoNormal by toleranciaIteracaoNormalProperty
    val qtdMaximaIteracoesProperty = InputIntegerProperty()
    var qtdMaximaIteracoes by qtdMaximaIteracoesProperty
    val curvaturaYInicialProperty = InputObjectProperty<Quantity<Angle>>()
    var curvaturaYInicial by curvaturaYInicialProperty
    val curvaturaXInicialProperty = InputObjectProperty<Quantity<Angle>>()
    var curvaturaXInicial by curvaturaXInicialProperty
    val ecgInicialProperty = InputObjectProperty<Quantity<Length>>()
    var ecgInicial by ecgInicialProperty

    init {
        ecgInicialProperty.apply {
            value = lengthOf(1.0 / 100.0, CENTIMETRO)
            setNomeDescricao("ecgInicial", descricoes)
        }
        curvaturaXInicialProperty.apply {
            value = angleOf(0.0, RADIAN)
            setNomeDescricao("curvaturaXInicial", descricoes)
        }
        curvaturaYInicialProperty.apply {
            value = angleOf(0.0, RADIAN)
            setNomeDescricao("curvaturaYInicial", descricoes)
        }
        qtdMaximaIteracoesProperty.apply {
            value = 100
            setNomeDescricao("qtdMaximaIteracoes", descricoes)
        }
        toleranciaIteracaoNormalProperty.apply {
            value = forceOf(0.001, QUILONEWTON)
            setNomeDescricao("toleranciaIteracaoNormal", descricoes)
            tipoInput = TipoInput.REAL_POSITIVO
        }
        toleranciaIteracaoMomentoProperty.apply {
            value = momentOf(0.001, QUILONEWTON.multiply(METRE).asType(Moment::class.java))
            setNomeDescricao("toleranciaIteracaoMomento", descricoes)
            tipoInput = TipoInput.REAL_POSITIVO
        }
    }
}

class ProcessoIterativoModel(initialValue: BeanProcessoIterativo) :
    ItemViewModel<BeanProcessoIterativo>(initialValue = initialValue) {
    val toleranciaIteracaoMomento = bind(BeanProcessoIterativo::toleranciaIteracaoMomentoProperty)
    val toleranciaIteracaoNormal = bind(BeanProcessoIterativo::toleranciaIteracaoNormalProperty)
    val qtdMaximaIteracoes = bind(BeanProcessoIterativo::qtdMaximaIteracoesProperty)
    val curvaturaYInicial = bind(BeanProcessoIterativo::curvaturaYInicialProperty)
    val curvaturaXInicial = bind(BeanProcessoIterativo::curvaturaXInicialProperty)
    val ecgInicial = bind(BeanProcessoIterativo::ecgInicialProperty)
}

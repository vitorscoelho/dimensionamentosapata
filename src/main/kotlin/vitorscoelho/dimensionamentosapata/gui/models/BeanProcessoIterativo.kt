package vitorscoelho.dimensionamentosapata.gui.models

import tech.units.indriya.unit.Units.METRE
import tech.units.indriya.unit.Units.RADIAN
import tornadofx.*
import javax.measure.Quantity
import javax.measure.quantity.Angle
import javax.measure.quantity.Force
import javax.measure.quantity.Length
import vitorscoelho.dimensionamentosapata.gui.utils.textos
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
            setNomeDescricao("ecgInicial", textos)
            tipoInput = TipoInput.REAL
        }
        curvaturaXInicialProperty.apply {
            value = angleOf(0.0, RADIAN)
            setNomeDescricao("curvaturaXInicial", textos)
            tipoInput = TipoInput.REAL
        }
        curvaturaYInicialProperty.apply {
            value = angleOf(0.0, RADIAN)
            setNomeDescricao("curvaturaYInicial", textos)
            tipoInput = TipoInput.REAL
        }
        qtdMaximaIteracoesProperty.apply {
            value = 100
            setNomeDescricao("qtdMaximaIteracoes", textos)
            tipoInput = TipoInput.INTEIRO_POSITIVO
        }
        toleranciaIteracaoNormalProperty.apply {
            value = forceOf(0.001, QUILONEWTON)
            setNomeDescricao("toleranciaIteracaoNormal", textos)
            tipoInput = TipoInput.REAL_POSITIVO
        }
        toleranciaIteracaoMomentoProperty.apply {
            value = momentOf(0.001, QUILONEWTON.multiply(METRE).asType(Moment::class.java))
            setNomeDescricao("toleranciaIteracaoMomento", textos)
            tipoInput = TipoInput.REAL_POSITIVO
        }
    }
}
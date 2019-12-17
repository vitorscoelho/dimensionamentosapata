package vitorscoelho.dimensionamentosapata.gui.models

import tech.units.indriya.unit.Units
import tech.units.indriya.unit.Units.METRE
import tornadofx.getValue
import tornadofx.setValue
import vitorscoelho.dimensionamentosapata.gui.utils.textos
import vitorscoelho.dimensionamentosapata.gui.utils.InputObjectProperty
import vitorscoelho.dimensionamentosapata.gui.utils.TipoInput
import vitorscoelho.dimensionamentosapata.utils.*
import javax.measure.Quantity
import javax.measure.quantity.Force

class BeanEsforcosDeformacoes {
    val momentoYProperty = InputObjectProperty<Quantity<Moment>>()
    var momentoY by momentoYProperty
    val momentoXProperty = InputObjectProperty<Quantity<Moment>>()
    var momentoX by momentoXProperty
    val normalProperty = InputObjectProperty<Quantity<Force>>()
    var normal by normalProperty
    val moduloReacaoSoloProperty = InputObjectProperty<Quantity<SpringStiffnessPerUnitArea>>()
    var moduloReacaoSolo by moduloReacaoSoloProperty

    init {
        normalProperty.apply {
            value = forceOf(0.0, QUILONEWTON)
            setNomeDescricao("normal", textos)
            tipoInput = TipoInput.REAL
        }
        momentoXProperty.apply {
            value = momentOf(0.0, QUILONEWTON.multiply(Units.METRE).asType(Moment::class.java))
            setNomeDescricao("momentoX", textos)
            tipoInput = TipoInput.REAL
        }
        momentoYProperty.apply {
            value = momentOf(0.0, QUILONEWTON.multiply(Units.METRE).asType(Moment::class.java))
            setNomeDescricao("momentoY", textos)
            tipoInput = TipoInput.REAL
        }
        moduloReacaoSoloProperty.apply {
            value = springStiffnessPerUnitArea(
                1.0, MEGAPASCAL.divide(METRE).asType(SpringStiffnessPerUnitArea::class.java)
            )
            setNomeDescricao("moduloReacaoSolo", textos)
            tipoInput = TipoInput.REAL_POSITIVO
        }
    }
}
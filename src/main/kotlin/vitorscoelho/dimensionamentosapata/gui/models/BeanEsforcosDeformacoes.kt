package vitorscoelho.dimensionamentosapata.gui.models

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.Property
import tech.units.indriya.unit.Units
import tech.units.indriya.unit.Units.METRE
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue
import vitorscoelho.dimensionamentosapata.gui.descricoes
import vitorscoelho.dimensionamentosapata.gui.utils.InputObjectProperty
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

    private val itemViewModel: EsforcosDeformacoesModel by lazy { EsforcosDeformacoesModel(initialValue = this) }
    val sujo: BooleanBinding
        get() = itemViewModel.dirty

    init {
        normalProperty.apply {
            value = forceOf(0.0, QUILONEWTON)
            setNomeDescricao("normal", descricoes)
        }
        momentoXProperty.apply {
            value = momentOf(0.0, QUILONEWTON.multiply(Units.METRE).asType(Moment::class.java))
            setNomeDescricao("momentoX", descricoes)
        }
        momentoYProperty.apply {
            value = momentOf(0.0, QUILONEWTON.multiply(Units.METRE).asType(Moment::class.java))
            setNomeDescricao("momentoY", descricoes)
        }
        moduloReacaoSoloProperty.apply {
            value = springStiffnessPerUnitArea(
                1.0, MEGAPASCAL.divide(METRE).asType(SpringStiffnessPerUnitArea::class.java)
            )
            setNomeDescricao("moduloReacaoSolo", descricoes)
        }
    }
}

class EsforcosDeformacoesModel(initialValue: BeanEsforcosDeformacoes) :
    ItemViewModel<BeanEsforcosDeformacoes>(initialValue = initialValue) {
    val momentoY: Property<Quantity<Moment>> = bind(BeanEsforcosDeformacoes::momentoYProperty)
    val momentoX = bind(BeanEsforcosDeformacoes::momentoXProperty)
    val normal = bind(BeanEsforcosDeformacoes::normalProperty)
    val moduloReacaoSolo = bind(BeanEsforcosDeformacoes::moduloReacaoSoloProperty)
}
package vitorscoelho.dimensionamentosapata.gui.models

import javafx.beans.property.Property
import tornadofx.BindingAwareSimpleObjectProperty
import tornadofx.ItemViewModel
import javax.measure.Quantity
import javax.measure.quantity.Length
import tornadofx.getValue
import tornadofx.setValue
import vitorscoelho.dimensionamentosapata.gui.descricoes
import vitorscoelho.dimensionamentosapata.gui.utils.InputObjectProperty
import vitorscoelho.dimensionamentosapata.gui.utils.TipoInput
import vitorscoelho.dimensionamentosapata.utils.CENTIMETRO
import vitorscoelho.dimensionamentosapata.utils.lengthOf

class BeanSapataRetangular {
    val lyProperty = InputObjectProperty<Quantity<Length>>()
    var ly by lyProperty
    val lxProperty = InputObjectProperty<Quantity<Length>>()
    var lx by lxProperty

    init {
        lxProperty.apply {
            value = lengthOf(0.0, CENTIMETRO)
            setNomeDescricao("sapataRetangular.lx", descricoes)
            tipoInput = TipoInput.REAL_POSITIVO
        }
        lyProperty.apply {
            value = lengthOf(0.0, CENTIMETRO)
            setNomeDescricao("sapataRetangular.ly", descricoes)
            tipoInput = TipoInput.REAL_POSITIVO
        }
    }
}

class SapataRetangularModel(initialValue: BeanSapataRetangular) :
    ItemViewModel<BeanSapataRetangular>(initialValue = BeanSapataRetangular()) {
    val ly: BindingAwareSimpleObjectProperty<Quantity<Length>> = bind(BeanSapataRetangular::lyProperty)
    val lx = bind(BeanSapataRetangular::lxProperty)
}
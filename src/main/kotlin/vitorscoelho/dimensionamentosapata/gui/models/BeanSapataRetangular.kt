package vitorscoelho.dimensionamentosapata.gui.models

import tornadofx.*
import javax.measure.Quantity
import javax.measure.quantity.Length
import vitorscoelho.dimensionamentosapata.gui.utils.textos
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
            setNomeDescricao("sapataRetangular.lx", textos)
            tipoInput = TipoInput.REAL_POSITIVO
        }
        lyProperty.apply {
            value = lengthOf(0.0, CENTIMETRO)
            setNomeDescricao("sapataRetangular.ly", textos)
            tipoInput = TipoInput.REAL_POSITIVO
        }
    }
}
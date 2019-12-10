package vitorscoelho.dimensionamentosapata.gui.models

import javafx.beans.property.*
import tech.units.indriya.quantity.Quantities
import tornadofx.onChange
import javax.measure.Quantity
import javax.measure.Unit
import tornadofx.getValue
import tornadofx.setValue

sealed class QuantityProperty<T : Quantity<T>, S : Number, U : Property<Number>>(
    val nome: String,
    val descricao: String,
    val unitProperty: ObjectProperty<Unit<T>>,
    val magnitudeProperty: U
) {
    abstract var magnitude: S
    var unit by unitProperty
    val unitTextProperty: ReadOnlyStringProperty = SimpleStringProperty(unit.toString()).apply {
        unitProperty.onChange {
            value = (it as Unit<*>).toString()
        }
    }

    val quantityObjectProperty: ReadOnlyObjectProperty<Quantity<T>> =
        SimpleObjectProperty(Quantities.getQuantity(0.0, unit) as Quantity<T>).apply {
            val atualizarQuantity = { magnitude: Number, unit: Unit<T> ->
                value = Quantities.getQuantity(magnitude, unit)
            }
            magnitudeProperty.onChange {
                atualizarQuantity(magnitude, unit)
            }
            unitProperty.onChange {
                atualizarQuantity(magnitude, unit)
            }
        }

    val quantity by quantityObjectProperty
}

class QuantityDoubleProperty<T : Quantity<T>>(
    nome: String = "",
    descricao: String = "",
    unitProperty: ObjectProperty<Unit<T>>
) : QuantityProperty<T, Double, DoubleProperty>(
    nome = nome,
    descricao = descricao,
    unitProperty = unitProperty,
    magnitudeProperty = SimpleDoubleProperty()
) {
    override var magnitude: Double by magnitudeProperty

    init {
        unitProperty.addListener { _, oldValue, newValue ->
            if (oldValue == null || newValue == null) return@addListener
            magnitude = Quantities.getQuantity(magnitude, oldValue).to(newValue).value.toDouble()
        }
    }
}

class QuantityIntProperty<T : Quantity<T>>(
    nome: String = "",
    descricao: String = "",
    unitProperty: ObjectProperty<Unit<T>>
) : QuantityProperty<T, Int, IntegerProperty>(
    nome = nome,
    descricao = descricao,
    unitProperty = unitProperty,
    magnitudeProperty = SimpleIntegerProperty(0)
) {
    override var magnitude: Int by magnitudeProperty

    init {
        unitProperty.addListener { _, oldValue, newValue ->
            if (oldValue == null || newValue == null) return@addListener
            magnitude = Quantities.getQuantity(magnitude, oldValue).to(newValue).value.toInt()
        }
    }
}
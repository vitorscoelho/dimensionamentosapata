package vitorscoelho.dimensionamentosapata.gui.utils

import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.TextField
import tornadofx.ValidationContext
import tornadofx.onChange

class PropertiesContext {
    private val validationTornadoFX = ValidationContext()
    private val properties = mutableListOf<ObservableValue<*>>()
    val valid = validationTornadoFX.valid
    val invalid = validationTornadoFX.valid.not()
    val dirty: BooleanProperty = SimpleBooleanProperty(false)
    val dirtyAndValid = dirty.and(validationTornadoFX.valid)

    fun add(property: ObservableValue<*>) {
        property.onChange { dirty.value = true }
        properties += property
    }

    fun commit() {
        dirty.value = false
    }

    companion object {
        fun addValidator(
            context: PropertiesContext,
            textField: TextField,
            predicate: () -> Boolean
        ): BooleanExpression {
            val validator = context.validationTornadoFX.addValidator(
                node = textField,
                property = textField.textProperty()
            ) {
                if (predicate.invoke()) null else error()
            }
            return validator.valid
        }
    }
}
package vitorscoelho.dimensionamentosapata.gui.utils

import javafx.beans.property.Property
import javafx.scene.control.TextFormatter
import javafx.util.StringConverter
import tech.units.indriya.quantity.Quantities
import tornadofx.isDouble
import tornadofx.isInt
import javax.measure.Quantity

enum class TipoInput {
    INTEIRO {
        override fun filterInput(formatter: TextFormatter.Change): Boolean {
            return formatter.controlNewText.isInt() || formatter.controlNewText == "-"
        }

        override val converter = stringConverterInt
        override fun <T : Quantity<T>> converter(property: Property<Quantity<T>>): StringConverter<Quantity<T>> =
            stringConverterQuantity(property = property, converterNumber = stringConverterInt)
    },
    INTEIRO_POSITIVO {
        override fun filterInput(formatter: TextFormatter.Change): Boolean {
            return formatter.controlNewText.isInt() && !formatter.controlNewText.contains("-")
        }

        override val converter = stringConverterInt
        override fun <T : Quantity<T>> converter(property: Property<Quantity<T>>): StringConverter<Quantity<T>> =
            stringConverterQuantity(property = property, converterNumber = stringConverterInt)
    },
    REAL {
        override fun filterInput(formatter: TextFormatter.Change): Boolean {
            return (formatter.controlNewText.isDouble() && !formatter.controlNewText.contains(
                other = "d",
                ignoreCase = true
            )) || formatter.controlNewText == "-"
        }

        override val converter = stringConverterDouble
        override fun <T : Quantity<T>> converter(property: Property<Quantity<T>>): StringConverter<Quantity<T>> =
            stringConverterQuantity(property = property, converterNumber = stringConverterDouble)
    },
    REAL_POSITIVO {
        override fun filterInput(formatter: TextFormatter.Change): Boolean {
            return formatter.controlNewText.isDouble() &&
                    !formatter.controlNewText.contains("-") &&
                    !formatter.controlNewText.contains(other = "d", ignoreCase = true)
        }

        override val converter = stringConverterDouble
        override fun <T : Quantity<T>> converter(property: Property<Quantity<T>>): StringConverter<Quantity<T>> =
            stringConverterQuantity(property = property, converterNumber = stringConverterDouble)
    };

    abstract fun filterInput(formatter: TextFormatter.Change): Boolean
    abstract val converter: StringConverter<Number>
    abstract fun <T : Quantity<T>> converter(property: Property<Quantity<T>>): StringConverter<Quantity<T>>
}

private fun <T : Quantity<T>> stringConverterQuantity(
    property: Property<Quantity<T>>,
    converterNumber: StringConverter<Number>
) = object : StringConverter<Quantity<T>>() {
    override fun toString(valor: Quantity<T>?) = valor?.value.toString() ?: ""

    override fun fromString(string: String?): Quantity<T> {
        val magnitude = converterNumber.fromString(string)
        return Quantities.getQuantity(magnitude, property.value.unit)
    }
}

private val stringConverterDouble: StringConverter<Number> = object : StringConverter<Number>() {
    override fun toString(valor: Number?): String? {
        if (valor == null) return null
        return valor.toString()
    }

    override fun fromString(valor: String?): Double {
        if (valor.isNullOrBlank() || valor == "-") return 0.0
        return valor.toDouble()
    }
}

private val stringConverterInt: StringConverter<Number> = object : StringConverter<Number>() {
    override fun toString(valor: Number?): String? {
        if (valor == null) return null
        return valor.toString()
    }

    override fun fromString(valor: String?): Int {
        if (valor.isNullOrBlank() || valor == "-") return 0
        return valor.toInt()
    }
}
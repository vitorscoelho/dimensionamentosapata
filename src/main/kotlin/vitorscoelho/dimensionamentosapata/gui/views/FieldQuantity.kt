package vitorscoelho.dimensionamentosapata.gui.views

import javafx.beans.binding.Bindings
import javafx.event.EventTarget
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.control.Tooltip
import javafx.util.Duration
import javafx.util.StringConverter
import tornadofx.*
import vitorscoelho.dimensionamentosapata.gui.models.QuantityDoubleProperty
import vitorscoelho.dimensionamentosapata.gui.models.QuantityIntProperty
import vitorscoelho.dimensionamentosapata.gui.models.QuantityProperty
import java.lang.IllegalArgumentException
import javax.measure.Quantity

internal val DELAY_TOOLTIP_FIELD_QUANTITY = Duration(200.0)

fun <T : Quantity<T>> EventTarget.fieldQuantity(
    property: QuantityProperty<T, *, *>,
    permitirNegativo: Boolean = true,
    op: Field.(tf: TextField) -> kotlin.Unit = { }
): Field {
    return this.field {
        textProperty.bind(
            Bindings.createStringBinding(
                {
                    val unidade: String = property.unitTextProperty.value
                    var retorno: String = property.nome
                    if (!unidade.isBlank() && unidade != "one") retorno += " ($unidade)"
                    return@createStringBinding retorno
                },
                arrayOf(property.unitTextProperty)
            )
        )
        val tf = textfield {
            val (converter, tipoInput) = when (property) {
                is QuantityDoubleProperty<*> -> Pair(
                    stringConverterDouble,
                    if (permitirNegativo) TipoInput.REAL else TipoInput.REAL_POSITIVO
                )
                is QuantityIntProperty<*> -> Pair(
                    stringConverterInt,
                    if (permitirNegativo) TipoInput.INTEIRO else TipoInput.INTEIRO_POSITIVO
                )
            }
            bind(property = property.magnitudeProperty, converter = converter)
            filterInput { tipoInput.filterInput(it) }
            tooltip = Tooltip(property.descricao)
            tooltip.showDelay = DELAY_TOOLTIP_FIELD_QUANTITY
        }
        op(this, tf)
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

private enum class TipoInput {
    INTEIRO {
        override fun filterInput(formatter: TextFormatter.Change): Boolean {
            return formatter.controlNewText.isInt() || formatter.controlNewText == "-"
        }
    },
    INTEIRO_POSITIVO {
        override fun filterInput(formatter: TextFormatter.Change): Boolean {
            return formatter.controlNewText.isInt() && !formatter.controlNewText.contains("-")
        }
    },
    REAL {
        override fun filterInput(formatter: TextFormatter.Change): Boolean {
            return (formatter.controlNewText.isDouble() && !formatter.controlNewText.contains(
                other = "d",
                ignoreCase = true
            )) || formatter.controlNewText == "-"
        }
    },
    REAL_POSITIVO {
        override fun filterInput(formatter: TextFormatter.Change): Boolean {
            return formatter.controlNewText.isDouble() &&
                    !formatter.controlNewText.contains("-") &&
                    !formatter.controlNewText.contains(other = "d", ignoreCase = true)
        }
    };

    abstract fun filterInput(formatter: TextFormatter.Change): Boolean
}
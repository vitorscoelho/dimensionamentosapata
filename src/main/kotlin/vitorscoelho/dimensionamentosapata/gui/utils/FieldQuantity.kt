package vitorscoelho.dimensionamentosapata.gui.utils

import javafx.beans.binding.BooleanExpression
import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.util.Duration
import javax.measure.Quantity
import tornadofx.*
import vitorscoelho.dimensionamentosapata.gui.estilos.EstiloPrincipal

fun EventTarget.fieldNumber(
    property: InputIntegerProperty,
    context: PropertiesContext? = null,
    op: Field .(tf: TextField) -> kotlin.Unit = {}
): Field {
    return fieldNumber(
        property = property,
        tipoInput = property.tipoInput,
        nome = property.nome,
        descricao = property.descricao,
        context = context,
        op = op
    )
}

fun EventTarget.fieldNumber(
    property: InputDoubleProperty,
    context: PropertiesContext? = null,
    op: Field .(tf: TextField) -> kotlin.Unit = {}
): Field {
    return fieldNumber(
        property = property,
        tipoInput = property.tipoInput,
        nome = property.nome,
        descricao = property.descricao,
        context = context,
        op = op
    )
}

fun EventTarget.fieldNumber(
    property: Property<Number>,
    nome: String, descricao: String,
    tipoInput: TipoInput,
    context: PropertiesContext?,
    op: Field .(tf: TextField) -> kotlin.Unit = {}
): Field {
    return this.field {
        text = nome
        val tf = textfield {
            filterInput { tipoInput.filterInput(it) }
            textProperty().bindBidirectional(property, tipoInput.converter)
            if (descricao.isNotBlank()) adicionarTooltip(descricao)
            if (context != null) {
                val valid = addContext(context = context, tipoInput = tipoInput, textField = this)
                valid.onChange { valido ->
                    adicionarTooltip(valido = valido, descricao = descricao, msgErro = tipoInput.mensagemDeErro)
                }
            }
        }
        op(this, tf)
    }
}

fun <T : Quantity<T>> EventTarget.fieldQuantity(
    property: InputObjectProperty<Quantity<T>>,
    context: PropertiesContext? = null,
    op: Field.(tf: TextField) -> kotlin.Unit = {}
): Field {
    return fieldQuantity(
        property = property,
        tipoInput = property.tipoInput,
        nome = property.nome,
        descricao = property.descricao,
        context = context,
        op = op
    )
}

fun <T : Quantity<T>> EventTarget.fieldQuantity(
    property: Property<Quantity<T>>,
    tipoInput: TipoInput,
    nome: String, descricao: String,
    context: PropertiesContext?,
    op: Field.(tf: TextField) -> kotlin.Unit = {}
): Field {
    return this.field {
        text = tituloField(nome = nome, qtd = property.value)
        val tf = textfield {
            filterInput { tipoInput.filterInput(it) }
            textProperty().bindBidirectional(property, tipoInput.converter(property))
            if (descricao.isNotBlank()) adicionarTooltip(descricao)
            if (context != null) {
                val valid = addContext(context = context, tipoInput = tipoInput, textField = this)
                valid.onChange { valido ->
                    adicionarTooltip(valido = valido, descricao = descricao, msgErro = tipoInput.mensagemDeErro)
                }
            }
        }
        property.addListener { _, _, newValue -> text = tituloField(nome = nome, qtd = newValue) }
        op(this, tf)
    }
}

private fun <T : Quantity<T>> tituloField(nome: String, qtd: Quantity<T>): String {
    var unidade = qtd.unit.toString()
    unidade = if (!unidade.isBlank() && unidade != "one") " ($unidade)" else ""
    return nome + unidade
}

private fun addContext(context: PropertiesContext, tipoInput: TipoInput, textField: TextField): BooleanExpression {
    val validator = PropertiesContext.addValidator(
        context = context,
        textField = textField,
        predicate = { tipoInput.valid(textField.text) }
    )
    context.add(property = textField.textProperty())
    return validator
}

private fun TextField.adicionarTooltip(descricao: String) {
    val tooltip = Tooltip(descricao)
    tooltip.showDelay = Duration(TOOLTIP_DELAY)
    setTooltip(tooltip)
    tooltip.addClass(EstiloPrincipal.tooltipDescricao)
}

private fun TextField.adicionarTooltip(valido: Boolean, descricao: String, msgErro: String) {
    //Configurando o Tooltip
    //Tive que criar um novo tooltip ao invés de só mudar o estilo (quando a msgErro deveria ser mostrada, porque a classe do css não estava alternando. BUG?
    val tooltip = Tooltip(descricao)
    tooltip.showDelay = Duration(TOOLTIP_DELAY)
    setTooltip(tooltip)
    tooltip.text = if (valido) descricao else msgErro
    if (valido) {
        tooltip.text = descricao
        tooltip.addClass(EstiloPrincipal.tooltipDescricao)
    } else {
        tooltip.text = msgErro
        tooltip.addClass(EstiloPrincipal.tooltipErro)
    }
}

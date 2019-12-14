package vitorscoelho.dimensionamentosapata.gui.utils

import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.util.Duration
import javax.measure.Quantity
import tornadofx.*

fun EventTarget.fieldNumber(
    property: InputIntegerProperty,
    op: Field .(tf: TextField) -> kotlin.Unit = {}
): Field {
    return fieldNumber(
        property = property,
        tipoInput = property.tipoInput,
        nome = property.nome,
        descricao = property.descricao,
        op = op
    )
}

fun EventTarget.fieldNumber(
    property: InputDoubleProperty,
    op: Field .(tf: TextField) -> kotlin.Unit = {}
): Field {
    return fieldNumber(
        property = property,
        tipoInput = property.tipoInput,
        nome = property.nome,
        descricao = property.descricao,
        op = op
    )
}

fun EventTarget.fieldNumber(
    property: Property<Number>,
    nome: String, descricao: String,
    tipoInput: TipoInput = TipoInput.REAL,
    op: Field .(tf: TextField) -> kotlin.Unit = {}
): Field {
    return this.field {
        text = nome
        val tf = textfield {
            filterInput { tipoInput.filterInput(it) }
            bind(property = property, converter = tipoInput.converter)
            textProperty().bindBidirectional(property, tipoInput.converter)
            if (descricao.isNotBlank()) adicionarTooltip(descricao)
        }
        op(this, tf)
    }
}

fun <T : Quantity<T>> EventTarget.fieldQuantity(
    property: InputObjectProperty<Quantity<T>>,
    op: Field.(tf: TextField) -> kotlin.Unit = {}
): Field {
    return fieldQuantity(
        property = property,
        tipoInput = property.tipoInput,
        nome = property.nome,
        descricao = property.descricao,
        op = op
    )
}

fun <T : Quantity<T>> EventTarget.fieldQuantity(
    property: Property<Quantity<T>>,
    tipoInput: TipoInput = TipoInput.REAL,
    nome: String, descricao: String,
    op: Field.(tf: TextField) -> kotlin.Unit = {}
): Field {
    return this.field {
        text = tituloField(nome = nome, qtd = property.value)
        val tf = textfield {
            filterInput { tipoInput.filterInput(it) }
            textProperty().bindBidirectional(property, tipoInput.converter(property))
            if (descricao.isNotBlank()) adicionarTooltip(descricao)
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


private fun TextField.adicionarTooltip(descricao: String) {
    //Configurando o Tooltip
    //Tive que criar um novo tooltip ao invés de só mudar o estilo (quando a msgErro deveria ser mostrada, porque a classe do css não estava alternando. BUG?
    val tooltip = Tooltip(descricao)
    tooltip.showDelay = Duration(200.0)
    setTooltip(tooltip)
    tooltip.text = descricao
//    tooltip.text = if (valido) descricao else msgErro
//    if (valido) {
//        tooltip.text = descricao
//        tooltip.addClass(EstiloPrincipal.tooltipDescricao)
//    } else {
//        tooltip.text = msgErro
//        tooltip.addClass(EstiloPrincipal.tooltipErro)
//    }
}
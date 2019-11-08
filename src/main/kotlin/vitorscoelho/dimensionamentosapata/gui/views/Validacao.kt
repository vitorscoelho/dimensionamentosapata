package vitorscoelho.dimensionamentosapata.gui.views

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.Property
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.control.Tooltip
import javafx.util.Duration
import javafx.util.StringConverter
import tornadofx.*
import vitorscoelho.dimensionamentosapata.gui.estilos.EstiloPrincipal
import java.text.DecimalFormat

internal class ContextoDeValidacao {
    private val validationTornadoFX = ValidationContext()
    val invalidProperty = validationTornadoFX.valid.not()

    fun addValidator(
        textField: TextField,
        predicate: () -> Boolean
    ): ValidationContext.Validator<String> {
        return validationTornadoFX.addValidator(
            node = textField,
            property = textField.textProperty()
        ) {
            if (predicate.invoke()) null else error()
        }
    }
}

internal fun TextField.numeroInteiro(
    property: Property<Number>,
    descricao: String,
    contextoDeValidacao: ContextoDeValidacao
) {
    configuracoesIniciais(
        property = property,
        descricao = descricao,
        msgErro = "Deve ser um número inteiro",
        contextoDeValidacao = contextoDeValidacao,
        filterInputDiscriminator = { it.controlNewText.isInt() },
        predicate = { text.isInt() }
    )
}

internal fun TextField.inteiroMaiorQueZero(
    property: Property<Number>,
    descricao: String,
    contextoDeValidacao: ContextoDeValidacao
) {
    configuracoesIniciais(
        property = property,
        descricao = descricao,
        msgErro = "Deve ser um inteiro maior que zero",
        contextoDeValidacao = contextoDeValidacao,
        filterInputDiscriminator = { it.controlNewText.isInt() && !it.controlNewText.contains("-") },
        predicate = { (text.isInt()) && (text.toInt() > 0) }
    )
}

internal fun TextField.numeroReal(
    property: Property<Number>,
    descricao: String,
    contextoDeValidacao: ContextoDeValidacao
) {
    configuracoesIniciais(
        property = property,
        descricao = descricao,
        msgErro = "Deve ser um número real",
        contextoDeValidacao = contextoDeValidacao,
        filterInputDiscriminator = {
            it.controlNewText.isDouble() && !it.controlNewText.contains(other = "d", ignoreCase = true)
        },
        predicate = { text.isDouble() }
    )
}

internal fun TextField.realMaiorQueZero(
    property: Property<Number>,
    descricao: String,
    contextoDeValidacao: ContextoDeValidacao
) {
    configuracoesIniciais(
        property = property,
        descricao = descricao,
        msgErro = "Deve ser um número real maior que zero",
        contextoDeValidacao = contextoDeValidacao,
        filterInputDiscriminator = {
            it.controlNewText.isDouble() &&
                    !it.controlNewText.contains("-") &&
                    !it.controlNewText.contains(other = "d", ignoreCase = true)
        },
        predicate = { (text.isDouble()) && (text.toDouble() > 0.0) }
    )
}

private fun TextField.configuracoesIniciais(
    property: Property<Number>,
    descricao: String,
    msgErro: String,
    contextoDeValidacao: ContextoDeValidacao,
    filterInputDiscriminator: (TextFormatter.Change) -> Boolean,
    predicate: () -> Boolean
) {
    //Configurando o filtro que impede o uso de algumas teclas do teclado
    filterInput { filterInputDiscriminator.invoke(it) }
    //Configurando o bind para que não tenha com problemas com formatação do texto no TextField, como, por exemplo, vírgula sendo usada para separação de milhar
    bind(property = property, converter = stringConverterTextField)
    //Adicionando o validator no ValidatorContext. Teve que fazer com o validator sem mensagem de erro porque o procedimento padrão
    //do TornadoFX faz com que o tooltip com a descrição fique desconfigurado
    val msgErroComAvisoValorInvalido = "Valor inválido!\r\n$msgErro"
    adicionarTooltip(valido = true, descricao = descricao, msgErro = msgErroComAvisoValorInvalido)
    val validator = contextoDeValidacao.addValidator(this) { predicate.invoke() }
    validator.valid.onChange { valido ->
        adicionarTooltip(valido = valido, descricao = descricao, msgErro = msgErroComAvisoValorInvalido)
    }
}

private val stringConverterTextField: StringConverter<Number> = object : StringConverter<Number>() {
    val dc = DecimalFormat("0.0")
    override fun toString(valor: Number?): String? {
        if (valor == null) return null
//        return dc.format(valor)
        return valor.toString()
    }

    override fun fromString(valor: String?): Double? {
        if (valor == null) return 0.0
        return valor.toDouble()
    }
}

private fun TextField.adicionarTooltip(valido: Boolean, descricao: String, msgErro: String) {
    //Configurando o Tooltip
    //Tive que criar um novo tooltip ao invés de só mudar o estilo (quando a msgErro deveria ser mostrada, porque a classe do css não estava alternando. BUG?
    val tooltip = Tooltip(descricao)
    alterarDelayTime(tooltip)
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

/**
 * Gambiarra para alterar o delay de surgimento da Tooltip, já que o JavaFX não fornece maneira mais fácil de fazer isso
 * Retirada em https://stackoverflow.com/questions/26854301/how-to-control-the-javafx-tooltips-delay
 */
private fun alterarDelayTime(tooltip: Tooltip) {
    try {
        val fieldBehavior = tooltip.javaClass.getDeclaredField("BEHAVIOR")
        fieldBehavior.isAccessible = true
        val objBehavior = fieldBehavior.get(tooltip)

        val fieldTimer = objBehavior.javaClass.getDeclaredField("activationTimer")
        fieldTimer.isAccessible = true
        val objTimer = fieldTimer.get(objBehavior) as Timeline

        objTimer.keyFrames.clear()
        objTimer.keyFrames.add(KeyFrame(Duration(100.0)))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
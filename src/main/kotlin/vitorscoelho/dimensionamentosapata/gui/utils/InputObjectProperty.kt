package vitorscoelho.dimensionamentosapata.gui.utils

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.get
import java.util.*

interface ComDadosAdicionaisInput {
    var nome: String
    var descricao: String
    var tipoInput: TipoInput
    fun setNomeDescricao(key: String, rb: ResourceBundle)
}

private class ComDadosAdicionaisInputImplementacao : ComDadosAdicionaisInput {
    override var nome: String = ""
    override var descricao: String = ""
    override var tipoInput: TipoInput = TipoInput.REAL

    override fun setNomeDescricao(key: String, rb: ResourceBundle) {
        nome = rb["$key.nome"]
        descricao = rb["$key.descricao"]
    }
}

class InputObjectProperty<T> : SimpleObjectProperty<T>(),
    ComDadosAdicionaisInput by ComDadosAdicionaisInputImplementacao()

class InputIntegerProperty : SimpleIntegerProperty(), ComDadosAdicionaisInput by ComDadosAdicionaisInputImplementacao()
class InputDoubleProperty : SimpleDoubleProperty(), ComDadosAdicionaisInput by ComDadosAdicionaisInputImplementacao()

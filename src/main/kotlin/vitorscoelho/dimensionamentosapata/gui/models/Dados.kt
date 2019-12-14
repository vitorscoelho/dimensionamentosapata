package vitorscoelho.dimensionamentosapata.gui.models

import javafx.beans.property.*
import tech.units.indriya.AbstractUnit.ONE
import tech.units.indriya.quantity.Quantities.getQuantity
import tech.units.indriya.unit.Units.*
import tornadofx.*
import vitorscoelho.dimensionamentosapata.utils.*
import javax.measure.MetricPrefix.*
import javax.measure.MetricPrefix
import javax.measure.Quantity
import javax.measure.Unit
import javax.measure.quantity.*

//class DadosFinais : ViewModel() {
//    val ecgInicialProperty = SimpleObjectProperty<Quantity<Length>>()
//    var ecgInicial by ecgInicialProperty
//
//    val curvaturaXInicialProperty = SimpleObjectProperty<Quantity<Angle>>()
//    var curvaturaXInicial by curvaturaXInicialProperty
//
//    val curvaturaYInicialProperty = SimpleObjectProperty<Quantity<Angle>>()
//    var curvaturaYInicial by curvaturaYInicialProperty
//
//    val qtdMaximaIteracoesProperty = SimpleIntegerProperty()
//    var qtdMaximaIteracoes by qtdMaximaIteracoesProperty
//
//    val toleranciaIteracaoNormalProperty = SimpleObjectProperty<Quantity<Force>>()
//    var toleranciaIteracaoNormal by toleranciaIteracaoNormalProperty
//
//    val toleranciaIteracaoMomentoProperty = SimpleObjectProperty<Quantity<Moment>>()
//    var toleranciaIteracaoMomento by toleranciaIteracaoMomentoProperty
//
//    val lxProperty = SimpleObjectProperty<Quantity<Length>>()
//    var lx by lxProperty
//
//    val lyProperty = SimpleObjectProperty<Quantity<Length>>()
//    var ly by lyProperty
//
//    val moduloReacaoSoloProperty = SimpleObjectProperty<Quantity<SpringStiffnessPerUnitArea>>()
//    var moduloReacaoSolo by moduloReacaoSoloProperty
//
//    val utilizarModuloReacaoSoloProperty = SimpleBooleanProperty()
//    var utilizarModuloReacaoSolo by utilizarModuloReacaoSoloProperty
//
//    val normalProperty = SimpleObjectProperty<Quantity<Force>>()
//    var normal by normalProperty
//
//    val momentoXProperty = SimpleObjectProperty<Quantity<Moment>>()
//    var momentoX by momentoXProperty
//
//    val momentoYProperty = SimpleObjectProperty<Quantity<Moment>>()
//    var momentoY by momentoYProperty
//
//    init {
//        ecgInicial = getQuantity(0.01, CENTI(METRE))
//    }
//}
//
//class NovosDados : ViewModel() {
//    val unitDeformacaoTranslacaoProperty: ObjectProperty<Unit<Length>> = SimpleObjectProperty(CENTIMETRO)
//    var unitDeformacaoTranslacao by unitDeformacaoTranslacaoProperty
//    val unitDeformacaoRotacaoProperty: ObjectProperty<Unit<Angle>> = SimpleObjectProperty(RADIAN)
//    var unitDeformacaoRotacao by unitDeformacaoRotacaoProperty
//    val unitAdimensionalProperty: ObjectProperty<Unit<Dimensionless>> = SimpleObjectProperty(ONE)
//    val unitForcaProperty: ObjectProperty<Unit<Force>> = SimpleObjectProperty(QUILONEWTON)
//    var unitForca by unitForcaProperty
//    val unitMomentoProperty: ObjectProperty<Unit<Moment>> =
//        SimpleObjectProperty(MetricPrefix.KILO(NEWTON).multiply(METRE).asType(Moment::class.java))
//    var unitMomento by unitMomentoProperty
//    val unitDimensoesFundacaoProperty: ObjectProperty<Unit<Length>> = SimpleObjectProperty(CENTIMETRO)
//    var unitDimensoesFundacao by unitDimensoesFundacaoProperty
//    val unitModuloReacaoProperty: ObjectProperty<Unit<SpringStiffnessPerUnitArea>> =
//        SimpleObjectProperty(MetricPrefix.MEGA(PASCAL).divide(METRE).asType(SpringStiffnessPerUnitArea::class.java))
//    val unitPressaoProperty: ObjectProperty<Unit<Pressure>> = SimpleObjectProperty(MetricPrefix.KILO(PASCAL))
//    var unitPressao by unitPressaoProperty
//    val unitAreaSapataProperty: ObjectProperty<Unit<Area>> = SimpleObjectProperty(
//        CENTIMETRO.pow(2).asType(Area::class.java)
//    )
//    var unitAreaSapata by unitAreaSapataProperty
//    val unitModuloFlexaoSapataProperty: ObjectProperty<Unit<Volume>> = SimpleObjectProperty(
//        CENTIMETRO.pow(3).asType(Volume::class.java)
//    )
//    var unitModuloFlexaoSapata by unitModuloFlexaoSapataProperty
//
//    val ecgInicialProperty = QuantityDoubleProperty(
//        nome = "${DELTA_MAIUSCULO}cg",
//        descricao = "Deformação no centro de gravidade da seção adotada na primeira iteração\r\n(positiva quando comprime o solo)",
//        unitProperty = unitDeformacaoTranslacaoProperty
//    )
//    val curvaturaXInicialProperty = QuantityDoubleProperty(
//        nome = "${PHI_MAIUSCULO}x",
//        descricao = "Rotação em torno do eixo X na primeira iteração\r\n(positivo para o vetor apontando para a direita)",
//        unitProperty = unitDeformacaoRotacaoProperty
//    )
//    val curvaturaYInicialProperty = QuantityDoubleProperty(
//        nome = "${PHI_MAIUSCULO}y",
//        descricao = "Rotação em torno do eixo Y na primeira iteração\r\n(positivo para o vetor apontando para a cima)",
//        unitProperty = unitDeformacaoRotacaoProperty
//    )
//    val qtdMaximaIteracoesProperty = QuantityIntProperty(
//        nome = "Qtd. máxima de iterações",
//        descricao = "Quantidade máxima permitida de iterações",
//        unitProperty = unitAdimensionalProperty
//    )
//    val toleranciaNormalIteracaoProperty = QuantityDoubleProperty(
//        nome = "Tolerância normal",
//        descricao = "Diferença admissível entre o normal solicitante e o resistente",
//        unitProperty = unitForcaProperty
//    )
//    val toleranciaMomentoIteracaoProperty = QuantityDoubleProperty(
//        nome = "Tolerância momento",
//        descricao = "Diferença admissível entre o momento solicitante e o resistente",
//        unitProperty = unitMomentoProperty
//    )
//
//    val lxProperty = QuantityDoubleProperty(
//        nome = "Lx",
//        descricao = "Comprimento da sapata em x",
//        unitProperty = unitDimensoesFundacaoProperty
//    )
//    val lyProperty = QuantityDoubleProperty(
//        nome = "Ly",
//        descricao = "Comprimento da sapata em y",
//        unitProperty = unitDimensoesFundacaoProperty
//    )
//    val moduloReacaoSoloProperty = QuantityDoubleProperty(
//        nome = "Kr",
//        descricao = "Módulo de reação do solo",
//        unitProperty = unitModuloReacaoProperty
//    )
//    val utilizarModuloReacaoSoloProperty = SimpleBooleanProperty()
//    var utilizarModuloReacaoSolo by utilizarModuloReacaoSoloProperty
//
//    val normalProperty = QuantityDoubleProperty(
//        nome = "N",
//        descricao = "Esforço normal solicitante\r\n(positivo quando compressão)",
//        unitProperty = unitForcaProperty
//    )
//    val momentoXProperty = QuantityDoubleProperty(
//        nome = "Mx",
//        descricao = "Momento fletor solicitante em torno do eixo X\r\n(positivo para o vetor apontando para a direita)",
//        unitProperty = unitMomentoProperty
//    )
//    val momentoYProperty = QuantityDoubleProperty(
//        nome = "My",
//        descricao = "Momento fletor solicitante em torno do eixo Y\r\n(positivo para o vetor apontando para a cima)",
//        unitProperty = unitMomentoProperty
//    )
//
//    init {
//        ecgInicialProperty.magnitude = 1.0 / 100.0
//        qtdMaximaIteracoesProperty.magnitude = 100
//        toleranciaNormalIteracaoProperty.magnitude = 0.001
//        toleranciaMomentoIteracaoProperty.magnitude = 0.001
//
//        lxProperty.magnitude = 100.0
//        lyProperty.magnitude = 100.0
//        moduloReacaoSoloProperty.magnitude = 1.0
//        utilizarModuloReacaoSolo = false
//    }
//}
//
//class Dados {
//    val ecgInicialProperty = SimpleDoubleProperty()
//    var ecgInicial by ecgInicialProperty
//    val curvaturaXInicialProperty = SimpleDoubleProperty()
//    var curvaturaXInicial by curvaturaXInicialProperty
//    val curvaturaYInicialProperty = SimpleDoubleProperty()
//    var curvaturaYInicial by curvaturaYInicialProperty
//    val qtdMaximaIteracoesProperty = SimpleIntegerProperty()
//    var qtdMaximaIteracoes by qtdMaximaIteracoesProperty
//    val toleranciaIteracaoProperty = SimpleDoubleProperty()
//    var toleranciaIteracao by toleranciaIteracaoProperty
//
//    val lxProperty = SimpleDoubleProperty()
//    var lx by lxProperty
//    val lyProperty = SimpleDoubleProperty()
//    var ly by lyProperty
//    val moduloReacaoSoloProperty = SimpleDoubleProperty()
//    var moduloReacaoSolo by moduloReacaoSoloProperty
//    val utilizarModuloReacaoSoloProperty = SimpleBooleanProperty()
//    var utilizarModuloReacaoSolo by utilizarModuloReacaoSoloProperty
//
//    val normalProperty = SimpleDoubleProperty()
//    var normal by normalProperty
//    val momentoXProperty = SimpleDoubleProperty()
//    var momentoX by momentoXProperty
//    val momentoYProperty = SimpleDoubleProperty()
//    var momentoY by momentoYProperty
//
//    init {
//        ecgInicial = 1.0 / 100.0
//        qtdMaximaIteracoes = 100
//        toleranciaIteracao = 0.001
//
//        lx = 100.0
//        ly = 100.0
//        moduloReacaoSolo = 1.0
//        utilizarModuloReacaoSolo = false
//    }
//}
//
//class DadosModel(dados: Dados) : ItemViewModel<Dados>(dados) {
//    val ecgInicial = bind(Dados::ecgInicialProperty)
//    val curvaturaXInicial = bind(Dados::curvaturaXInicialProperty)
//    val curvaturaYInicial = bind(Dados::curvaturaYInicialProperty)
//    val qtdMaximaIteracoes = bind(Dados::qtdMaximaIteracoesProperty)
//    val toleranciaIteracao = bind(Dados::toleranciaIteracaoProperty)
//
//    val lx = bind(Dados::lxProperty)
//    val ly = bind(Dados::lyProperty)
//    val moduloReacaoSolo = bind(Dados::moduloReacaoSoloProperty)
//    val utilizarModuloReacaoSolo = bind(Dados::utilizarModuloReacaoSoloProperty)
//
//    val normal = bind(Dados::normalProperty)
//    val momentoX = bind(Dados::momentoXProperty)
//    val momentoY = bind(Dados::momentoYProperty)
//}
package vitorscoelho.dimensionamentosapata.gui.controllers

import javafx.beans.property.SimpleStringProperty
import tech.units.indriya.AbstractUnit
import tech.units.indriya.format.SimpleUnitFormat
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.ProductUnit
import tech.units.indriya.unit.Units.PERCENT
import tornadofx.*
import vitorscoelho.dimensionamentosapata.estadio2.*
import vitorscoelho.dimensionamentosapata.gui.models.NovosDados
import vitorscoelho.dimensionamentosapata.gui.views.*
import vitorscoelho.dimensionamentosapata.gui.views.CanvasSapata
import vitorscoelho.dimensionamentosapata.sapata.SapataRetangular
import tech.units.indriya.unit.Units.RADIAN
import vitorscoelho.dimensionamentosapata.sapata.ResultadosFlexaoSapata
import vitorscoelho.dimensionamentosapata.utils.*
import javax.measure.Quantity
import javax.measure.Unit
import javax.measure.quantity.Length
import kotlin.math.pow
import kotlin.math.sqrt

internal class ControllerInicial : Controller() {
    //    val model: DadosModel = DadosModel(Dados())
    val model: NovosDados = NovosDados()
    val canvasSapata = CanvasSapata(this)
    val textoResultadosProperty = SimpleStringProperty()
    var textoResultados by textoResultadosProperty

    val textoXMouseProperty = SimpleStringProperty()
    val textoYMouseProperty = SimpleStringProperty()
    val textoTensaoProperty = SimpleStringProperty()
    val textoDeformacaoProperty = SimpleStringProperty()
    val textoLegendaDesenhoProperty = SimpleStringProperty()

    init {
        model.dirty.onChange { alterado -> if (alterado) limparResultados() }
    }

    fun dimensionar() {
        model.commit()
        val lx = model.lxProperty.quantity.doublePadrao()
        val ly = model.lyProperty.quantity.doublePadrao()
        val moduloReacaoSolo = model.moduloReacaoSoloProperty.quantity.doublePadrao()

        val ecgInicial = model.ecgInicialProperty.quantity.doublePadrao()
        val curvaturaXInicial = model.curvaturaXInicialProperty.quantity.doublePadrao()
        val curvaturaYInicial = model.curvaturaYInicialProperty.quantity.doublePadrao()

        val qtdMaximaIteracoes = model.qtdMaximaIteracoesProperty.magnitude
        val deltaNAdm = model.toleranciaNormalIteracaoProperty.quantity.doublePadrao()
        val deltaMAdm = model.toleranciaMomentoIteracaoProperty.quantity.doublePadrao()

        val normal = model.normalProperty.quantity.doublePadrao()
        val momentoX = model.momentoXProperty.quantity.doublePadrao()
        val momentoY = model.momentoYProperty.quantity.doublePadrao()

        val sapata = SapataRetangular(
            lx = lx, ly = ly, moduloReacaoSolo = moduloReacaoSolo
        )
        val criteriosProcessoIterativo = CriteriosProcessoIterativo(
            deformadaInicial = Deformada.criar(
                ponto = Ponto.ZERO,
                deformacaoPonto = ecgInicial,
                curvaturaX = curvaturaXInicial,
                curvaturaY = curvaturaYInicial
            ),
            qtdMaximaIteracoes = qtdMaximaIteracoes,
            deltaNAdm = deltaNAdm,
            deltaMxAdm = deltaMAdm,
            deltaMyAdm = deltaMAdm
        )
        val esforco = Esforco(
            normal = normal,
            momentoX = momentoX,
            momentoY = momentoY
        )

        println(sapata)
        println(criteriosProcessoIterativo)
        println(esforco)

        try {
            val resultados = sapata.resultadosFlexao(
                esforco = esforco, criteriosProcessoIterativo = criteriosProcessoIterativo
            )
            textoResultados = textoResultados(resultados, model)
            canvasSapata.resultados = resultados
        } catch (e: ConvergenciaNaoAlcancadaException) {
            limparResultados()
            error(title = "Erro", header = "Impossível dimensionar!", content = e.message)
        }
    }

    fun limparResultados() {
        canvasSapata.resultados = null
        textoResultados = ""
    }
}

fun textoResultados(resultados: ResultadosFlexaoSapata, m: NovosDados): String {
    val deformacaoCG = transformLengthOfPadrao(resultados.deformacao(x = 0.0, y = 0.0), m.unitDeformacaoTranslacao)
    val curvaturaX = transformAngleOfPadrao(resultados.deformada.curvaturaX, m.unitDeformacaoRotacao)
    val curvaturaY = transformAngleOfPadrao(resultados.deformada.curvaturaY, m.unitDeformacaoRotacao)
    val curvaturaResultante = transformAngleOfPadrao(
        sqrt(resultados.deformada.curvaturaX.pow(2) + resultados.deformada.curvaturaY.pow(2)),
        m.unitDeformacaoRotacao
    )
    val tensaoMinima = transformPressureOfPadrao(resultados.tensaoMinima, m.unitPressao)
    val tensaoMaxima = transformPressureOfPadrao(resultados.tensaoMaxima, m.unitPressao)
    val areaComprimida = transformAreaOfPadrao(resultados.areaSecaoComprimida, m.unitAreaSapata)
    val proporcaoAreaComprimida = Quantities.getQuantity(
        resultados.areaSecaoComprimida / resultados.sapata.area, AbstractUnit.ONE
    ).to(PERCENT)
    val areaSapata = transformAreaOfPadrao(resultados.sapata.area, m.unitAreaSapata)
    val moduloFlexaoXSapata = transformVolumeOfPadrao(resultados.sapata.moduloFlexaoX, m.unitModuloFlexaoSapata)
    val moduloFlexaoYSapata = transformVolumeOfPadrao(resultados.sapata.moduloFlexaoY, m.unitModuloFlexaoSapata)
    val normal = transformForceOfPadrao(resultados.esforcoSolicitante.normal, m.unitForca)
    val momentoX = transformMomentOfPadrao(resultados.esforcoSolicitante.momentoX, m.unitMomento)
    val momentoY = transformMomentOfPadrao(resultados.esforcoSolicitante.momentoY, m.unitMomento)
    val ex = momentoX.divide(normal).asType(Length::class.java).to(m.unitDimensoesFundacao)
    val ey = momentoY.divide(normal).asType(Length::class.java).to(m.unitDimensoesFundacao)

    return with(StringBuilder()) {
        appendln("- Resultados da análise")
        if (m.utilizarModuloReacaoSolo) {
            appendln("   ${DELTA_MAIUSCULO}cg= ${deformacaoCG.toString(dc2Casas)}")
            appendln("   ${PHI_MAIUSCULO}x= ${curvaturaX.toString(dcCientifica3Casas)}")
            appendln("   ${PHI_MAIUSCULO}y= ${curvaturaY.toString(dcCientifica3Casas)}")
            appendln("   ${PHI_MAIUSCULO}= ${curvaturaResultante.toString(dcCientifica3Casas)}")
        }
        appendln("   ${SIGMA_MINUSCULO}Mín= ${tensaoMinima.toString(dc2Casas)}")
        appendln("   ${SIGMA_MINUSCULO}Máx= ${tensaoMaxima.toString(dc2Casas)}")
        appendln("   Área comprimida= ${areaComprimida.toString(dc2Casas)}")
        appendln("   Área comprimida= ${proporcaoAreaComprimida.toString(dc1Casa)}")
        appendln("\r\n- Geometria da Sapata")
        appendln("   Área= ${areaSapata.toString(dc2Casas)}")
        appendln("   Wx= ${moduloFlexaoXSapata.toString(dc2Casas)}")
        appendln("   Wy= ${moduloFlexaoYSapata.toString(dc2Casas)}")
        appendln("\r\n- Esforços")
        appendln("   N= ${normal.toString(dc2Casas)}")
        appendln("   Mx= ${momentoX.toString(dc2Casas)}")
        appendln("   My= ${momentoY.toString(dc2Casas)}")
        appendln("   ex= ${ex.toString(dc2Casas)}")
        appendln("   ey= ${ey.toString(dc2Casas)}")
    }.toString()
}
package vitorscoelho.dimensionamentosapata.gui.controllers

import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import vitorscoelho.dimensionamentosapata.estadio2.*
import vitorscoelho.dimensionamentosapata.gui.models.Dados
import vitorscoelho.dimensionamentosapata.gui.models.DadosModel
import vitorscoelho.dimensionamentosapata.gui.views.*
import vitorscoelho.dimensionamentosapata.gui.views.CanvasSapata
import vitorscoelho.dimensionamentosapata.sapata.SapataRetangular
import kotlin.math.sqrt

internal class ControllerInicial : Controller() {
    val model: DadosModel = DadosModel(Dados())
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
        with(model.item) {
            model.commit()
            val multiplicadorModuloReacao = 1 / 1000.0
            val multiplicadorMomento = 100.0
            val multiplicadorTensao = 10_000.0
            val sapata = SapataRetangular(
                lx = lx, ly = ly, moduloReacaoSolo = moduloReacaoSolo * multiplicadorModuloReacao
            )
            val criteriosProcessoIterativo = CriteriosProcessoIterativo(
                deformadaInicial = Deformada.criar(
                    ponto = Ponto.ZERO,
                    deformacaoPonto = ecgInicial,
                    curvaturaX = curvaturaXInicial,
                    curvaturaY = curvaturaYInicial
                ),
                qtdMaximaIteracoes = qtdMaximaIteracoes,
                deltaNAdm = toleranciaIteracao,
                deltaMxAdm = toleranciaIteracao * multiplicadorMomento,
                deltaMyAdm = toleranciaIteracao * multiplicadorMomento
            )
            val esforco = Esforco(
                normal = normal,
                momentoX = momentoX * multiplicadorMomento,
                momentoY = momentoY * multiplicadorMomento
            )
            println(sapata)
            println(criteriosProcessoIterativo)
            println(esforco)
            try {
                val resultados = sapata.resultadosFlexao(
                    esforco = esforco, criteriosProcessoIterativo = criteriosProcessoIterativo
                )
                val deformacaoCG = resultados.deformacao(x = 0.0, y = 0.0)
                val curvaturaX = resultados.deformada.curvaturaX
                val curvaturaY = resultados.deformada.curvaturaY
                val curvaturaResultante = sqrt(curvaturaX * curvaturaX + curvaturaY * curvaturaY)
                val tensaoMinima = resultados.tensaoMinima / multiplicadorTensao
                val tensaoMaxima = resultados.tensaoMaxima / multiplicadorTensao
                val areaSapata = sapata.area
                val moduloFlexaoXSapata = sapata.moduloFlexaoX
                val moduloFlexaoYSapata = sapata.moduloFlexaoY
                val areaComprimida = resultados.areaSecaoComprimida
                val porcentagemAreaComprimida = 100.0 * areaComprimida / areaSapata
                val normal = esforco.normal
                val momentoX = esforco.momentoX / multiplicadorMomento
                val momentoY = esforco.momentoY / multiplicadorMomento
                textoResultados = run {
                    val sb = StringBuilder()
                    sb.append("- Resultados da análise\r\n")
                    if (utilizarModuloReacaoSolo) {
                        sb.append("   Δcg= ${dc2Casas.format(deformacaoCG)} cm\r\n")
                        sb.append("   Φx= ${dcCientifica3Casas.format(curvaturaX)} rad\r\n")
                        sb.append("   Φy= ${dcCientifica3Casas.format(curvaturaY)} rad\r\n")
                        sb.append("   Φ= ${dcCientifica3Casas.format(curvaturaResultante)} rad\r\n")
                    }
                    sb.append("   σMín= ${dc2Casas.format(tensaoMinima)} kPa\r\n")
                    sb.append("   σMáx= ${dc2Casas.format(tensaoMaxima)} kPa\r\n")
                    sb.append("   Área comprimida= ${dc2Casas.format(areaComprimida)} cm²\r\n")
                    sb.append("   Área comprimida= ${dc1Casa.format(porcentagemAreaComprimida)} %\r\n")
                    sb.append("\r\n- Geometria da Sapata\r\n")
                    sb.append("   Área= ${dc2Casas.format(areaSapata)} cm²\r\n")
                    sb.append("   Wx= ${dc2Casas.format(moduloFlexaoXSapata)} cm³\r\n")
                    sb.append("   Wy= ${dc2Casas.format(moduloFlexaoYSapata)} cm³\r\n")
                    sb.append("\r\n- Esforços\r\n")
                    sb.append("   N= ${dc2Casas.format(normal)} kN\r\n")
                    sb.append("   Mx= ${dc2Casas.format(momentoX)} kN.m\r\n")
                    sb.append("   My= ${dc2Casas.format(momentoY)} kN.m\r\n")
                    sb.append("   ex= ${dc2Casas.format(100.0 * momentoY / normal)} cm\r\n")
                    sb.append("   ey= ${dc2Casas.format(100.0 * momentoX / normal)} cm")
                    sb.toString()
                }
                canvasSapata.resultados = resultados
            } catch (e: ConvergenciaNaoAlcancadaException) {
                limparResultados()
                error(title = "Erro", header = "Impossível dimensionar!", content = e.message)
            }
        }
    }

    fun limparResultados() {
        canvasSapata.resultados = null
        textoResultados = ""
    }
}
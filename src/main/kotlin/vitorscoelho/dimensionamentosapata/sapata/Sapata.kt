package vitorscoelho.dimensionamentosapata.sapata

import vitorscoelho.dimensionamentosapata.estadio2.*
import kotlin.math.max
import kotlin.math.pow

class ResultadosFlexaoSapata internal constructor(private val resultados: ResultadosFlexaoCompostaEstadio2) {
    private val secaoComprimida: SecaoConcreto by lazy {
        SecaoConcreto(
            vertices = resultados.flexaoCompostaEstadio2.verticesSecaoComprimida(deformada = resultados.deformadaFinal),
            moduloDeformacao = 1.0 //Tanto faz, pois não é utilizado
        )
    }
    val verticesSapata: List<Ponto>
        get() = resultados.flexaoCompostaEstadio2.secaoCA.secaoConcreto.vertices

    val verticesSecaoComprimida: List<Ponto>
        get() = secaoComprimida.vertices

    val areaSecaoComprimida: Double
        get() = secaoComprimida.area

    val deformada: Deformada
        get() = resultados.deformadaFinal

    val esforcoSolicitante: Esforco
        get() = resultados.esforcoSolicitante

    val tensaoMaxima: Double by lazy {
        val valor=verticesSapata.map { tensaoSolo(it.x, it.y) }.max()!!
        println(valor)
        valor
    }
    val tensaoMinima: Double by lazy { verticesSapata.map { tensaoSolo(it.x, it.y) }.min()!! }

    fun tensaoSolo(x: Double, y: Double) = resultados.tensaoConcreto(x, y)
    fun deformacao(x: Double, y: Double) = resultados.deformacao(x, y)

}

abstract class Sapata(vertices: List<Ponto>, val moduloDeformacaoSolo: Double) {
    private val secaoCA: SecaoCA = run {
        val secaoConcreto = SecaoConcreto(vertices = vertices, moduloDeformacao = moduloDeformacaoSolo)
        val secaoAco = SecaoAco(barras = emptyList(), moduloDeformacao = 1.0)
        SecaoCA(secaoConcreto = secaoConcreto, secaoAco = secaoAco)
    }

    val vertices: List<Ponto>
        get() = secaoCA.secaoConcreto.vertices
    val area: Double
        get() = secaoCA.secaoConcreto.area
    abstract val moduloFlexaoX: Double
    abstract val moduloFlexaoY: Double

    fun resultadosFlexao(
        esforco: Esforco,
        criteriosProcessoIterativo: CriteriosProcessoIterativo
    ): ResultadosFlexaoSapata {
        val flexaoCompostaEstadio2 = FlexaoCompostaEstadio2(
            secaoCA = secaoCA,
            pontoEsforcoNormal = secaoCA.secaoConcreto.cg,
            desconsiderarConcretoNasArmaduras = true,
            criteriosProcessoIterativo = criteriosProcessoIterativo
        )
        val resultadosFlexaoCompostaEstadio2 = flexaoCompostaEstadio2.resultados(esforco = esforco)
        return ResultadosFlexaoSapata(resultadosFlexaoCompostaEstadio2)
    }
}

class SapataRetangular(val lx: Double, val ly: Double, moduloReacaoSolo: Double) :
    Sapata(
        vertices = listOf(
            Ponto(x = -0.5 * lx, y = -0.5 * ly),
            Ponto(x = 0.5 * lx, y = -0.5 * ly),
            Ponto(x = 0.5 * lx, y = 0.5 * ly),
            Ponto(x = -0.5 * lx, y = 0.5 * ly)
        ),
        moduloDeformacaoSolo = moduloReacaoSolo
    ) {
    val pontoEsquerdoInferior: Ponto
        get() = vertices[0]
    val pontoEsquerdoSuperior: Ponto
        get() = vertices[3]
    val pontoDireitoInferior: Ponto
        get() = vertices[1]
    val pontoDireitoSuperior: Ponto
        get() = vertices[2]
    override val moduloFlexaoX: Double = lx * ly * ly / 6.0
    override val moduloFlexaoY: Double = ly * lx * lx / 6.0

    override fun toString() = "{lx: $lx, ly:$ly, moduloSolo: $moduloDeformacaoSolo}"
}
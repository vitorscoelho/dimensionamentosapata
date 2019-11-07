package vitorscoelho.dimensionamentosapata.sapata

import org.junit.Assert.*
import org.junit.Test
import vitorscoelho.dimensionamentosapata.estadio2.CriteriosProcessoIterativo
import vitorscoelho.dimensionamentosapata.estadio2.Deformada
import vitorscoelho.dimensionamentosapata.estadio2.Esforco
import vitorscoelho.dimensionamentosapata.estadio2.Ponto

class SapataRetangularTest {
    private val tolerancia = 0.0000001
    @Test
    fun testeSapataSemTracao() {
        val sapata = SapataRetangular(lx = 150.0, ly = 200.0, moduloReacaoSolo = 1.0)
        val criterios = CriteriosProcessoIterativo(
            deformadaInicial = Deformada.criar(
                ponto = Ponto.ZERO,
                deformacaoPonto = 1.0 / 1000.0,
                curvaturaX = 0.0,
                curvaturaY = 0.0
            ),
            qtdMaximaIteracoes = 500,
            deltaNAdm = 0.00001,
            deltaMxAdm = 0.00001,
            deltaMyAdm = 0.00001
        )
        val esforco = Esforco(normal = 300.0, momentoX = 4000.0, momentoY = 2700.0)
        val resultados = sapata.resultadosFlexao(esforco = esforco, criteriosProcessoIterativo = criterios)
        assertEquals(0.0024, resultados.tensaoMinima, tolerancia)
        assertEquals(0.0176, resultados.tensaoMaxima, tolerancia)
    }
}
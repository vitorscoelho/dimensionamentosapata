package vitorscoelho.dimensionamentosapata.estadio2

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.sqrt

private val DELTA = 0.00000001

class FlexaoCompostaEstadio2Test {
    @Test
    fun exemplo1DissertacaoFadiga() {
        val p1 = Ponto(x = 40.0, y = 0.0)
        val p2 = Ponto(x = 60.0, y = 0.0)
        val p3 = Ponto(x = 60.0, y = 100.0)
        val p4 = Ponto(x = 100.0, y = 100.0)
        val p5 = Ponto(x = 100.0, y = 120.0)
        val p6 = Ponto(x = 0.0, y = 120.0)
        val p7 = Ponto(x = 0.0, y = 100.0)
        val p8 = Ponto(x = 40.0, y = 100.0)
        val secaoConcreto = SecaoConcreto(
            vertices = listOf(p1, p2, p3, p4, p5, p6, p7, p8),
            moduloDeformacao = 21_000.0 / 10.0
        )

        val areaAcoTotal = 0.005 * secaoConcreto.area
        val areaRelativaTotal = 2.0 + 0.5 + 0.5 + 1.0 + 2.0 + 0.5 + 0.5 + 1.0
        val cArea = areaAcoTotal / areaRelativaTotal
        val b1 = Barra.pelaArea(x = 45.0, y = 5.0, area = cArea * 2.0)
        val b2 = Barra.pelaArea(x = 5.0, y = 105.0, area = cArea * 0.5)
        val b3 = Barra.pelaArea(x = 5.0, y = 115.0, area = cArea * 0.5)
        val b4 = Barra.pelaArea(x = 45.0, y = 115.0, area = cArea * 1.0)
        val b5 = Barra.pelaArea(x = 55.0, y = 5.0, area = cArea * 2.0)
        val b6 = Barra.pelaArea(x = 95.0, y = 105.0, area = cArea * 0.5)
        val b7 = Barra.pelaArea(x = 95.0, y = 115.0, area = cArea * 0.5)
        val b8 = Barra.pelaArea(x = 55.0, y = 115.0, area = cArea * 1.0)
        val secaoAco = SecaoAco(
            barras = listOf(b1, b2, b3, b4, b5, b6, b7, b8),
            moduloDeformacao = 21_000.0
        )

        val secaoCA = SecaoCA(secaoConcreto = secaoConcreto, secaoAco = secaoAco)

        val momentoX = -58330.0 + (-1100.0) * (60.0 - 80.0)
        val solicitacao1 = Esforco(normal = -1100.0, momentoX = momentoX, momentoY = -16670.0)
        val solicitacao2 = Esforco(normal = -1100.0, momentoX = momentoX, momentoY = 16670.0)

        val tolerancia = 0.00001
        val criterios = CriteriosProcessoIterativo(
            deformadaInicial = Deformada.criar(
                ponto = secaoConcreto.cg,
                deformacaoPonto = -1.0 / 1000.0,
                curvaturaX = 0.0,
                curvaturaY = 0.0
            ),
            qtdMaximaIteracoes = 500,
            deltaNAdm = 0.00001,
            deltaMxAdm = tolerancia,
            deltaMyAdm = tolerancia
        )

        assertEquals(4000.0, secaoConcreto.area, DELTA)
        assertEquals(50.0, secaoConcreto.cg.x, DELTA)
        assertEquals(80.0, secaoConcreto.cg.y, DELTA)
        assertEquals(5333333.33333333, secaoConcreto.momentoInerciaX, DELTA)
        assertEquals(1733333.33333333, secaoConcreto.momentoInerciaY, DELTA)

        run {
            //Considerando o concreto nos lugares onde tem barra de aço
            val flexaoComposta = FlexaoCompostaEstadio2(
                secaoCA = secaoCA,
                pontoEsforcoNormal = secaoConcreto.cg,
                desconsiderarConcretoNasArmaduras = false,
                criteriosProcessoIterativo = criterios
            )

            val resultados1 = flexaoComposta.resultados(esforco = solicitacao1)
            val resultados2 = flexaoComposta.resultados(esforco = solicitacao2)

            assertEquals(0.0, resultados1.tensaoConcreto(p1), DELTA)
            assertEquals(0.0, resultados1.tensaoConcreto(p2), DELTA)
            assertEquals(-0.472370216, resultados1.tensaoConcreto(p3), DELTA)
            assertEquals(-0.844635734, resultados1.tensaoConcreto(p4), DELTA)
            assertEquals(-1.043779558, resultados1.tensaoConcreto(p5), DELTA)
            assertEquals(-0.113115762, resultados1.tensaoConcreto(p6), DELTA)
            assertEquals(0.0, resultados1.tensaoConcreto(p7), DELTA)
            assertEquals(-0.286237457, resultados1.tensaoConcreto(p8), DELTA)

            assertEquals(6.131625162, resultados1.tensaoAco(b1), DELTA)
            assertEquals(-0.102910841, resultados1.tensaoAco(b2), DELTA)
            assertEquals(-1.098629959, resultados1.tensaoAco(b3), DELTA)
            assertEquals(-4.821285143, resultados1.tensaoAco(b4), DELTA)
            assertEquals(5.200961366, resultados1.tensaoAco(b5), DELTA)
            assertEquals(-8.478885003, resultados1.tensaoAco(b6), DELTA)
            assertEquals(-9.474604122, resultados1.tensaoAco(b7), DELTA)
            assertEquals(-5.751948939, resultados1.tensaoAco(b8), DELTA)

            assertEquals(0.0, resultados2.tensaoConcreto(p1), DELTA)
            assertEquals(0.0, resultados2.tensaoConcreto(p2), DELTA)
            assertEquals(-0.286237457, resultados2.tensaoConcreto(p3), DELTA)
            assertEquals(0.0, resultados2.tensaoConcreto(p4), DELTA)
            assertEquals(-0.113115762, resultados2.tensaoConcreto(p5), DELTA)
            assertEquals(-1.043779558, resultados2.tensaoConcreto(p6), DELTA)
            assertEquals(-0.844635734, resultados2.tensaoConcreto(p7), DELTA)
            assertEquals(-0.472370216, resultados2.tensaoConcreto(p8), DELTA)

            assertEquals(5.200961356, resultados2.tensaoAco(b1), DELTA)
            assertEquals(-8.478885004, resultados2.tensaoAco(b2), DELTA)
            assertEquals(-9.474604122, resultados2.tensaoAco(b3), DELTA)
            assertEquals(-5.751948938, resultados2.tensaoAco(b4), DELTA)
            assertEquals(6.131625152, resultados2.tensaoAco(b5), DELTA)
            assertEquals(-0.102910842, resultados2.tensaoAco(b6), DELTA)
            assertEquals(-1.098629959, resultados2.tensaoAco(b7), DELTA)
            assertEquals(-4.821285143, resultados2.tensaoAco(b8), DELTA)
        }

        run {
            //Desconsiderando o concreto nos lugares onde tem barra de aço
            val flexaoComposta = FlexaoCompostaEstadio2(
                secaoCA = secaoCA,
                pontoEsforcoNormal = secaoConcreto.cg,
                desconsiderarConcretoNasArmaduras = true,
                criteriosProcessoIterativo = criterios
            )

            val resultados1 = flexaoComposta.resultados(esforco = solicitacao1)
            val resultados2 = flexaoComposta.resultados(esforco = solicitacao2)

            assertEquals(0.0, resultados1.tensaoConcreto(p1), DELTA)
            assertEquals(0.0, resultados1.tensaoConcreto(p2), DELTA)
            assertEquals(-0.474646124, resultados1.tensaoConcreto(p3), DELTA)
            assertEquals(-0.849075539, resultados1.tensaoConcreto(p4), DELTA)
            assertEquals(-1.049127697, resultados1.tensaoConcreto(p5), DELTA)
            assertEquals(-0.113054159, resultados1.tensaoConcreto(p6), DELTA)
            assertEquals(0.0, resultados1.tensaoConcreto(p7), DELTA)
            assertEquals(-0.287431416, resultados1.tensaoConcreto(p8), DELTA)

            assertEquals(6.160126582, resultados1.tensaoAco(b1), DELTA)
            assertEquals(-0.098187175, resultados1.tensaoAco(b2), DELTA)
            assertEquals(-1.098447966, resultados1.tensaoAco(b3), DELTA)
            assertEquals(-4.842742118, resultados1.tensaoAco(b4), DELTA)
            assertEquals(5.224053044, resultados1.tensaoAco(b5), DELTA)
            assertEquals(-8.522849018, resultados1.tensaoAco(b6), DELTA)
            assertEquals(-9.523109809, resultados1.tensaoAco(b7), DELTA)
            assertEquals(-5.778815656, resultados1.tensaoAco(b8), DELTA)

            assertEquals(0.0, resultados2.tensaoConcreto(p1), DELTA)
            assertEquals(0.0, resultados2.tensaoConcreto(p2), DELTA)
            assertEquals(-0.287431416, resultados2.tensaoConcreto(p3), DELTA)
            assertEquals(0.0, resultados2.tensaoConcreto(p4), DELTA)
            assertEquals(-0.113054159, resultados2.tensaoConcreto(p5), DELTA)
            assertEquals(-1.049127697, resultados2.tensaoConcreto(p6), DELTA)
            assertEquals(-0.849075539, resultados2.tensaoConcreto(p7), DELTA)
            assertEquals(-0.474646124, resultados2.tensaoConcreto(p8), DELTA)

            assertEquals(5.224053034, resultados2.tensaoAco(b1), DELTA)
            assertEquals(-8.522849018, resultados2.tensaoAco(b2), DELTA)
            assertEquals(-9.523109808, resultados2.tensaoAco(b3), DELTA)
            assertEquals(-5.778815656, resultados2.tensaoAco(b4), DELTA)
            assertEquals(6.160126572, resultados2.tensaoAco(b5), DELTA)
            assertEquals(-0.098187176, resultados2.tensaoAco(b6), DELTA)
            assertEquals(-1.098447966, resultados2.tensaoAco(b7), DELTA)
            assertEquals(-4.842742118, resultados2.tensaoAco(b8), DELTA)


        }
    }
}
package vitorscoelho.dimensionamentosapata.estadio2

import org.apache.commons.math3.linear.SingularMatrixException
import java.lang.Exception
import kotlin.math.pow

data class CriteriosProcessoIterativo(
    val deformadaInicial: Deformada,
    val qtdMaximaIteracoes: Int,
    val deltaNAdm: Double,
    val deltaMxAdm: Double,
    val deltaMyAdm: Double
)

class ConvergenciaNaoAlcancadaException(mensagem: String) : Exception(mensagem)

/**
 * @property pontoEsforcoNormal ponto onde será aplicado o esforço normal
 * @property desconsiderarConcretoNasArmaduras 'true' se o que se deseja é desconsiderar o concreto onde houver barras. Marcar como 'true' seria a forma "mais exata" de se fazer.
 */
class FlexaoCompostaEstadio2(
    val secaoCA: SecaoCA,
    val pontoEsforcoNormal: Ponto,
    val desconsiderarConcretoNasArmaduras: Boolean = true,
    val criteriosProcessoIterativo: CriteriosProcessoIterativo
) {
    private val matrizGeometricaAcoConsiderandoConcretoNasArmaduras: Matriz by lazy { matrizGeometricaAcoConsiderandoConcretoNasArmaduras() }

    fun resultados(esforco: Esforco) = ResultadosFlexaoCompostaEstadio2(
        flexaoCompostaEstadio2 = this, deformadas = deformadas(esforco = esforco), esforcoSolicitante = esforco
    )

    /**
     * Retorna uma lista com as deformadas de cada uma das iterações adotadas até que fosse encontrado o equilíbrio
     */
    private fun deformadas(esforco: Esforco): List<Deformada> {
        /**
         * Esforco considerando a origem (0, 0) como referência, ao invés do [pontoEsforcoNormal].
         * Inverte o sinal do esforço normal para que fique igual ao da dissertação.
         * Transforma o eixo y também para que o sistema de coordenadas fique igual ao da dissertação.
         */
        val normal = -esforco.normal
        val momentoX = esforco.momentoX + normal * pontoEsforcoNormal.y
        val momentoY = -esforco.momentoY + normal * pontoEsforcoNormal.x

        val vetorEsforcoSolicitante = Matriz.vetorColuna(normal, momentoX, momentoY)
        var qtdIteracoes = 0
        var deformada = criteriosProcessoIterativo.deformadaInicial
        var vetorDeformacao = deformada.toVetorColuna()
        val deformadas = mutableListOf<Deformada>()
        while (true) {
            qtdIteracoes++
            deformadas += deformada
            val matrizGeometrica = matrizGeometrica(deformada)
            val vetorEsforcoResistente = matrizGeometrica * vetorDeformacao
            val vetorDelta = vetorEsforcoResistente - vetorEsforcoSolicitante
            if (converge(vetorDelta)) return deformadas
            if (qtdIteracoes > criteriosProcessoIterativo.qtdMaximaIteracoes) throw ConvergenciaNaoAlcancadaException(
                "A quantidade máxima de iterações (${criteriosProcessoIterativo.qtdMaximaIteracoes}) foi atingida sem que os valores convergissem."
            )
            try {
                vetorDeformacao = matrizGeometrica.inversa() * vetorEsforcoSolicitante
            } catch (e: SingularMatrixException) {
                throw ConvergenciaNaoAlcancadaException(
                    "Não foi possível encontrar o equilíbrio na análise."
                )
            }
            deformada = deformada(vetorDeformacao)
        }
    }

    /**
     * Retorna um vetor coluna com os seguintes elemento:
     * 0 - Deformação no [pontoEsforcoNormal]
     * 1 - Curvatura em torno do eixo X
     * 2 - Curvatura em torno do eixo Y
     */
    private fun Deformada.toVetorColuna(): Matriz = Matriz.vetorColuna(
        -deformacao(posicao = Ponto.ZERO), curvaturaX, -curvaturaY
    )

    private fun deformada(vetorColuna: Matriz): Deformada = Deformada.criar(
        ponto = Ponto.ZERO, deformacaoPonto = -vetorColuna.get(0, 0),
        curvaturaX = vetorColuna.get(1, 0), curvaturaY = -vetorColuna.get(2, 0)
    )

    private fun converge(vetorDelta: Matriz): Boolean {
        val deltaN = vetorDelta.get(0, 0)
        val deltaMx = vetorDelta.get(1, 0)
        val deltaMy = vetorDelta.get(2, 0)
        val deltaNAdm = criteriosProcessoIterativo.deltaNAdm
        val deltaMxAdm = criteriosProcessoIterativo.deltaMxAdm
        val deltaMyAdm = criteriosProcessoIterativo.deltaMyAdm
        val delta = (deltaN / deltaNAdm).pow(2) + (deltaMx / deltaMxAdm).pow(2) + (deltaMy / deltaMyAdm).pow(2)
        return (delta < 1.0)
    }

    private fun matrizGeometrica(deformada: Deformada): Matriz {
        val matrizConcreto = matrizGeometricaConcreto(deformada)
        val matrizAco = if (desconsiderarConcretoNasArmaduras) {
            matrizGeometricaAcoDesconsiderandoConcretoNasArmaduras(deformada = deformada)
        } else {
            matrizGeometricaAcoConsiderandoConcretoNasArmaduras
        }
        return matrizConcreto + matrizAco
    }

    private fun arestasSecaoComprimida(deformada: Deformada): List<Aresta> {
        val pontosSecaoComprimida = verticesSecaoComprimida(deformada = deformada)
        if (pontosSecaoComprimida.size < 3) return emptyList()
        return arestas(pontosSecaoComprimida)
    }

    fun verticesSecaoComprimida(deformada: Deformada): List<Ponto> {
        val pontosSecaoComprimida = mutableListOf<Ponto>()
        secaoCA.secaoConcreto.arestas.forEach { aresta ->
            val deformacaoPontoInicial = deformada.deformacao(aresta.pontoInicial)
            if (deformacaoPontoInicial >= 0.0) {
                pontosSecaoComprimida += aresta.pontoInicial
            }

            val deformacaoPontoFinal = deformada.deformacao(aresta.pontoFinal)
            if (deformacaoPontoInicial * deformacaoPontoFinal < 0) {
                val xi = aresta.pontoInicial.x
                val yi = aresta.pontoInicial.y
                val xj = aresta.pontoFinal.x
                val yj = aresta.pontoFinal.y
                val ei = deformacaoPontoInicial
                val ej = deformacaoPontoFinal
                val xLinhaNeutra = (xi * ej - xj * ei) / (ej - ei)
                val yLinhaNeutra = (yi * ej - yj * ei) / (ej - ei)
                pontosSecaoComprimida += Ponto(xLinhaNeutra, yLinhaNeutra)
            }
        }
        return pontosSecaoComprimida
    }

    private fun matrizGeometricaConcreto(deformada: Deformada): Matriz {
        val arestas = arestasSecaoComprimida(deformada)
        val area = arestas.sumByDouble { it.area() }
        val momentoEstaticoX = arestas.sumByDouble { it.momentoEstaticoX() }
        val momentoEstaticoY = arestas.sumByDouble { it.momentoEstaticoY() }
        val momentoInerciaX = arestas.sumByDouble { it.momentoInerciaX() }
        val momentoInerciaY = arestas.sumByDouble { it.momentoInerciaY() }
        val produtoDeInercia = arestas.sumByDouble { it.produtoDeInercia() }

        val matriz = Matriz.initBuilder()
            .linha(area, momentoEstaticoX, momentoEstaticoY)
            .linha(momentoEstaticoX, momentoInerciaX, produtoDeInercia)
            .linha(momentoEstaticoY, produtoDeInercia, momentoInerciaY)
            .build()
        return secaoCA.secaoConcreto.moduloDeformacao * matriz
    }

    private fun matrizGeometricaAcoConsiderandoConcretoNasArmaduras(): Matriz {
        return matrizGeometricaAco(
            barras = secaoCA.secaoAco.barras, moduloDeformacao = secaoCA.secaoAco.moduloDeformacao
        )
    }

    private fun matrizGeometricaAcoDesconsiderandoConcretoNasArmaduras(deformada: Deformada): Matriz {
        val (barrasComprimidas, barrasTracionadas) = this.secaoCA.secaoAco.barras.partition { barra ->
            deformada.deformacao(barra.cg) > 0.0
        }
        val moduloDeformacaoBarrasComprimidas =
            secaoCA.secaoAco.moduloDeformacao - secaoCA.secaoConcreto.moduloDeformacao
        val matrizGeometricaBarrasComprimidas = matrizGeometricaAco(
            barras = barrasComprimidas,
            moduloDeformacao = moduloDeformacaoBarrasComprimidas
        )
        val moduloDeformacaoBarrasTracionadas = secaoCA.secaoAco.moduloDeformacao
        val matrizGeometricaTracionadas = matrizGeometricaAco(
            barras = barrasTracionadas,
            moduloDeformacao = moduloDeformacaoBarrasTracionadas
        )
        return matrizGeometricaBarrasComprimidas + matrizGeometricaTracionadas
    }

    private fun matrizGeometricaAco(barras: List<Barra>, moduloDeformacao: Double): Matriz {
        val area = barras.sumByDouble { it.area }
        val momentoEstaticoX = barras.sumByDouble { it.area * it.cg.y }
        val momentoEstaticoY = barras.sumByDouble { it.area * it.cg.x }
        val momentoInerciaX = barras.sumByDouble { it.area * it.cg.y * it.cg.y }
        val momentoInerciaY = barras.sumByDouble { it.area * it.cg.x * it.cg.x }
        val produtoDeInercia = barras.sumByDouble { it.area * it.cg.x * it.cg.y }
        val matriz = Matriz.initBuilder()
            .linha(area, momentoEstaticoX, momentoEstaticoY)
            .linha(momentoEstaticoX, momentoInerciaX, produtoDeInercia)
            .linha(momentoEstaticoY, produtoDeInercia, momentoInerciaY)
            .build()
        return moduloDeformacao * matriz
    }
}

class ResultadosFlexaoCompostaEstadio2(
    val flexaoCompostaEstadio2: FlexaoCompostaEstadio2,
    val esforcoSolicitante: Esforco,
    val deformadas: List<Deformada>
) {
    val deformadaFinal: Deformada
        get() = deformadas.last()
    val qtdIteracoesRealizadas: Int
        get() = deformadas.size
    val moduloDeformacaoConcreto = flexaoCompostaEstadio2.secaoCA.secaoConcreto.moduloDeformacao
    val moduloDeformacaoAco = flexaoCompostaEstadio2.secaoCA.secaoAco.moduloDeformacao

    fun deformacao(x: Double, y: Double) = deformadaFinal.deformacao(x = x, y = y)

    fun deformacao(ponto: Ponto) = deformacao(x = ponto.x, y = ponto.y)

    fun tensaoConcreto(x: Double, y: Double): Double {
        val deformacao = deformacao(x = x, y = y)
        if (deformacao <= 0.0) return 0.0
        return moduloDeformacaoConcreto * deformacao
    }

    fun tensaoConcreto(ponto: Ponto) = tensaoConcreto(x = ponto.x, y = ponto.y)

    fun tensaoAco(x: Double, y: Double) = moduloDeformacaoAco * deformacao(x = x, y = y)
    fun tensaoAco(ponto: Ponto) = tensaoAco(x = ponto.x, y = ponto.y)
    fun tensaoAco(barra: Barra) = tensaoAco(ponto = barra.cg)
}
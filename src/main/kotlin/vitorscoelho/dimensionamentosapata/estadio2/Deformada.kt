package vitorscoelho.dimensionamentosapata.estadio2

/**
 * Representa uma regra que é capaz de determinar a deformação em qualquer ponto do plano cartesiano
 * @property deformacaoZero deformação específica na origem do plano cartesiano (convencionada positiva se for um encurtamento)
 * @property curvaturaX é a curvatura da barra em torno do eixo baricêntrico X (convencionada positiva quando o vetor aponta para a "direita")
 * @property curvaturaY é a curvatura da barra em torno do eixo baricêntrico Y (convencionada positiva quando o vetor aponta para "cima")
 */
class Deformada private constructor(
    private val deformacaoZero: Double, val curvaturaX: Double, val curvaturaY: Double
) {
    /**
     * Retorna a deformação específica em um determinado ponto. Positiva para encurtamento e negativa para alongamento.
     * @param posicao a posição do ponto que se deseja saber a deformação específica
     */
    fun deformacao(posicao: Ponto): Double = deformacao(x = posicao.x, y = posicao.y)

    /**
     * Retorna a deformação específica em um determinado ponto. Positiva para encurtamento e negativa para alongamento.
     * @param x a abscissa do ponto que se deseja saber a deformação específica
     * @param y a ordenada do ponto que se deseja saber a deformação específica
     */
    fun deformacao(x: Double, y: Double): Double = deformacaoZero - curvaturaX * y + curvaturaY * x

    override fun toString(): String = "{e0: $deformacaoZero, curvaturaX: $curvaturaX, curvaturaY: $curvaturaY}"

    companion object {
        /**
         * Cria uma instância de [Deformada] a partir da deformação de um ponto qualquer e das componentes da curvatura
         * @param ponto um ponto qualquer com a deformação conhecida
         * @param deformacaoPonto deformação específica no [ponto]. Positivo para encurtamento e negativo para alongamento
         * @param curvaturaX curvatura da seção em torno do eixo baricêntrico X
         * @param curvaturaY curvatura da seção em torno do eixo baricêntrico Y
         */
        fun criar(ponto: Ponto, deformacaoPonto: Double, curvaturaX: Double, curvaturaY: Double): Deformada {
            val deformacaoZero = deformacaoPonto + curvaturaX * ponto.y - curvaturaY * ponto.x
            return Deformada(deformacaoZero = deformacaoZero, curvaturaX = curvaturaX, curvaturaY = curvaturaY)
        }
    }
}
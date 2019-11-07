package vitorscoelho.dimensionamentosapata.estadio2

/**
 * Representa uma aresta de uma seção transversal
 * @property pontoInicial ponto inicial da aresta
 * @property pontoFinal ponto final da aresta
 */
class Aresta(val pontoInicial: Ponto, val pontoFinal: Ponto) {
    private val xm = (pontoFinal.x + pontoInicial.x) / 2.0
    private val ym = (pontoFinal.y + pontoInicial.y) / 2.0
    private val dx = (pontoFinal.x - pontoInicial.x) / 2.0
    private val dy = (pontoFinal.y - pontoInicial.y) / 2.0

    fun area() = 2.0 * xm * dy
    fun momentoEstaticoX() = 2.0 * (xm * ym + dx * dy / 3.0) * dy
    fun momentoEstaticoY() = (xm * xm + dx * dx / 3.0) * dy
    fun produtoDeInercia() = (xm * xm * ym + (ym * dx + 2.0 * xm * dy) * dx / 3.0) * dy
    fun momentoInerciaX() = 2.0 * (xm * ym * ym + (xm * dy + 2.0 * ym * dx) * dy / 3.0) * dy
    fun momentoInerciaY() = 2.0 * xm * (xm * xm + dx * dx) * dy / 3.0

    override fun toString() = "Aresta: $pontoInicial -> $pontoFinal"
}

/**
 * @return uma lista de arestas formadas pelos pontos informados em [vertices]
 */
fun arestas(vertices: List<Ponto>): List<Aresta> {
    require(vertices.size > 1) { "Deveria haver pelo menos dois elementos na lista de vertices" }
    return (vertices + vertices.first()).zipWithNext { atual, proximo ->
        Aresta(
            pontoInicial = atual,
            pontoFinal = proximo
        )
    }
}
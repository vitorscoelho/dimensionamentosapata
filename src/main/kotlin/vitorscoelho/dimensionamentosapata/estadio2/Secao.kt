package vitorscoelho.dimensionamentosapata.estadio2

private fun cg(arestas: List<Aresta>): Ponto {
    val area = arestas.sumByDouble { it.area() }
    val momentoEstaticoX = arestas.sumByDouble { it.momentoEstaticoX() }
    val momentoEstaticoY = arestas.sumByDouble { it.momentoEstaticoY() }
    val xcg = momentoEstaticoY / area
    val ycg = momentoEstaticoX / area
    return Ponto(x = xcg, y = ycg)
}

/**
 * Representa uma seção bruta de concreto. Esta seção é poligonal e formada pelos [vertices] informados.
 * A ordem dos vértices na lista é importante e deve determinada por um caminhamento à esquerda
 * (área da seção sempre à esquerda de quem percorre o contorno da seção no sentido do caminhamento escolhido).
 */
class SecaoConcreto(val vertices: List<Ponto>, val moduloDeformacao: Double) {
    val cg: Ponto = cg(arestas(vertices))

    val arestas = arestas(vertices)
    val area: Double by lazy { arestas.sumByDouble { it.area() } }
    val momentoInerciaX: Double by lazy { arestas.sumByDouble { it.momentoInerciaX() } - area * cg.y * cg.y }
    val momentoInerciaY: Double by lazy { arestas.sumByDouble { it.momentoInerciaY() } - area * cg.x * cg.x }
}

class SecaoAco(val barras: List<Barra>, val moduloDeformacao: Double)

class SecaoCA(val secaoConcreto: SecaoConcreto, val secaoAco: SecaoAco)
package vitorscoelho.dimensionamentosapata.estadio2

import kotlin.math.PI
import kotlin.math.sqrt

fun area(diametro: Double) = PI * diametro * diametro / 4.0
fun diametro(area: Double) = sqrt(4.0 * area / PI)

/**
 * Representa uma barra de aço.
 * @property cg coordenadas do centro de gravidade da barra
 * @property area área da barra
 */
class Barra private constructor(val cg: Ponto, val area: Double) {
    init {
        require(area >= 0.0) { "|area| não pode ser menor que 0" }
    }

    companion object {
        fun pelaArea(cg: Ponto, area: Double) = Barra(cg, area)
        fun pelaArea(x: Double, y: Double, area: Double) = Barra(Ponto(x, y), area)
        fun peloDiametro(cg: Ponto, diametro: Double) = Barra(cg, area(diametro))
        fun peloDiametro(x: Double, y: Double, diametro: Double) = Barra(Ponto(x, y), area(diametro))
    }
}
package vitorscoelho.dimensionamentosapata.estadio2

data class Ponto(val x: Double, val y: Double) {
    companion object {
        val ZERO: Ponto by lazy { Ponto(0.0, 0.0) }
    }
}
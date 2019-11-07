package vitorscoelho.dimensionamentosapata.estadio2

data class Esforco(val normal: Double, val momentoX: Double, val momentoY: Double) {
    fun toVetorColuna(): Matriz = Matriz.vetorColuna(normal, momentoX, momentoY)
}
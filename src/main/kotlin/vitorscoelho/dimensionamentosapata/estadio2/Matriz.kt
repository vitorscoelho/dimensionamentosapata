package vitorscoelho.dimensionamentosapata.estadio2

import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix

operator fun Double.times(matriz: Matriz): Matriz = matriz * this

class Matriz private constructor(private val matrizApache: RealMatrix) {
    constructor(array: Array<DoubleArray>) : this(MatrixUtils.createRealMatrix(array))

    operator fun plus(matriz: Matriz): Matriz = Matriz(matrizApache.add(matriz.matrizApache))
    operator fun minus(matriz: Matriz): Matriz = Matriz(matrizApache.subtract(matriz.matrizApache))
    operator fun times(matriz: Matriz): Matriz = Matriz(matrizApache.multiply(matriz.matrizApache))
    operator fun times(valor: Double): Matriz = Matriz(matrizApache.scalarMultiply(valor))
    fun inversa() = Matriz(MatrixUtils.inverse(matrizApache))

    fun get(linha: Int, coluna: Int): Double = matrizApache.getEntry(linha, coluna)

    override fun toString(): String {
        val qtdLinhas = matrizApache.rowDimension
        val qtdColunas = matrizApache.columnDimension
        val sb = StringBuilder()
        sb.append("{")
        (0 until qtdLinhas).forEach { linha ->
            sb.append("{")
            (0 until qtdColunas).forEach { coluna ->
                sb.append(get(linha, coluna))
                if (coluna < qtdColunas - 1) sb.append(", ")
            }
            sb.append("}")
            if (linha < qtdLinhas - 1) sb.append(", ")
        }
        sb.append("}")
        return sb.toString()
    }

    companion object {
        fun initBuilder(): BuilderLinhas = BuilderLinhas()

        /**Constrói um vetor com uma linha*/
        fun vetorLinha(vararg valores: Double): Matriz = Matriz(arrayOf(valores))

        /**Constrói um vetor com uma coluna*/
        fun vetorColuna(vararg valores: Double): Matriz = Matriz(valores.map { doubleArrayOf(it) }.toTypedArray())
    }
}

class BuilderLinhas {
    private val listaLinhas = mutableListOf<DoubleArray>()
    private val qtdLinhas: Int
        get() = listaLinhas.size
    private val qtdColunas: Int
        get() = listaLinhas.first().size

    fun linha(vararg valores: Double): BuilderLinhas {
        listaLinhas += valores
        require(valores.isNotEmpty()) {
            "A linha ${qtdLinhas - 1} não possui nenhum valor informado"
        }
        require(valores.size == qtdColunas) {
            "A quantidade de colunas da linha ${qtdLinhas - 1} está diferente da informada na linha 0"
        }
        return this
    }

    fun build(): Matriz = Matriz(listaLinhas.toTypedArray())
}
package vitorscoelho.dimensionamentosapata.estadio2

/**
 * Representa os esforços que ocasionam tensões normais em uma seção transversal
 * @param normal esforço normal, em kN. Positivo para compressão, negativo para tração
 * @param momentoX momento fletor, em kN.cm, em torno do eixo X. Positivo quando o vetor aponta para a "direita".
 * @param momentoY momento fletor, em kN.cm, em torno do eixo Y. Positivo quando o vetor aponta para a "cima".
 */
data class Esforco(val normal: Double, val momentoX: Double, val momentoY: Double)
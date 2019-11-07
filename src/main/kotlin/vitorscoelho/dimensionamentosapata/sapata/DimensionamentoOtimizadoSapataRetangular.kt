package vitorscoelho.dimensionamentosapata.sapata

import vitorscoelho.dimensionamentosapata.estadio2.CriteriosProcessoIterativo
import vitorscoelho.dimensionamentosapata.estadio2.Esforco
import kotlin.math.absoluteValue

/**
 * Os valores mínimos e máximos das dimensões da sapata devem ser múltiplos de 5, pois a variação de valores ocorrerá de 5 em 5.
 * @param criteriosPossiveis uma lista com todos os critérios possíveis. Caso o primeiro critério não consiga convergir, as iterações são reiniciadas a partir do segundo elemento da lista, e assim segue, sucessivamente.
 */
//class DimensionamentoOtimizadoSapataRetangular(
//    val lxMinimo: Int, val lxMaximo: Int,
//    val lyMinimo: Int, val lyMaximo: Int,
//    val moduloReacaoSolo: Double, val tensaoAdmissivel: Double,
//    val porcentagemAreaComprimidaAdmissivel: Double,
//    val criteriosPossiveis: List<CriteriosProcessoIterativo>,
//    val esforcos: List<Esforco>,
//    val volume: (lx: Double, ly: Double) -> Double
//) {
//    init {
//        require(lxMinimo > 0) { "|lxMinimo| deve ser maior que 0" }
//        require(lyMinimo > 0) { "|lyMinimo| deve ser maior que 0" }
//        require(lxMaximo > lxMinimo) { "|lxMaximo| deve ser maior que |lxMinimo|" }
//        require(lyMaximo > lyMaximo) { "|lyMaximo| deve ser maior que |lyMinimo|" }
//        require(lxMinimo % 5 != 0) { "|lxMinimo| deve ser múltiplo de 5" }
//        require(lxMaximo % 5 != 0) { "|lxMaximo| deve ser múltiplo de 5" }
//        require(lyMinimo % 5 != 0) { "|lyMinimo| deve ser múltiplo de 5" }
//        require(lyMaximo % 5 != 0) { "|lyMaximo| deve ser múltiplo de 5" }
//    }
//
//    /**
//     * Retorna um [Map] com todas as sapatas que atendem aos critérios e os resultados do dimensionamento
//     */
//    fun resultado(considerarPesoSapata: Boolean): Map<SapataRetangular, ResultadosFlexaoSapata> {
//        val hashmap = hashMapOf<SapataRetangular, ResultadosFlexaoSapata>()
//        (lxMinimo..lxMaximo step 5).map { it.toDouble() }.forEach { lx ->
//            (lyMinimo..lyMaximo step 5).map { it.toDouble() }.forEach { ly ->
//                val sapata = SapataRetangular(
//                    lx = lx, ly = ly, moduloReacaoSolo = moduloReacaoSolo
//                )
//                val pesoSapata = if (considerarPesoSapata) volume.invoke(lx, ly) * 25.0 else 0.0
//                esforcos.forEach { esforcoSemPesoSapata ->
//                    val esforco = Esforco(
//                        normal = esforcoSemPesoSapata.normal - pesoSapata,
//                        momentoX = esforcoSemPesoSapata.momentoX,
//                        momentoY = esforcoSemPesoSapata.momentoY
//                    )
//                    val resultadosFlexaoSapata = sapata.resultadosFlexao(
//                        esforco=esforco,criteriosProcessoIterativo =
//                    )
//                }
//            }
//        }
//    }
//
//    private fun passa(sapata: SapataRetangular)
//
//    private fun passa(sapata: SapataRetangular, resultados: ResultadosFlexaoSapata): Boolean =
//        (resultados.tensaoMaxima.absoluteValue <= tensaoAdmissivel) &&
//                (resultados.areaSecaoComprimida / sapata.area <= porcentagemAreaComprimidaAdmissivel / 100.0)
//}
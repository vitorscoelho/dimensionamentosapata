package vitorscoelho.dimensionamentosapata.gui.models

//import tech.units.indriya.ComparableQuantity
//import tech.units.indriya.quantity.Quantities
//import tech.units.indriya.unit.Units.NEWTON
//import vitorscoelho.dimensionamentosapata.utils.QUILONEWTON
//import java.lang.IllegalArgumentException
//import javax.measure.Quantity
//import javax.measure.Unit
//import javax.measure.quantity.Force
//
//class Intervalo<T : Quantity<T>> {
//    private val comparadores = mutableListOf<Comparador<T>>()
//    fun igualA(quantity: ComparableQuantity<T>): Intervalo<T> {
//        comparadores += IgualA(quantity)
//        return this
//    }
////    fun maiorQue()
////    fun maiorOuIgualA()
////    fun menorQue()
////    fun menorOuIgualA()
//
//    fun check(quantity: ComparableQuantity<T>) {
//        comparadores.forEach { it.check(quantity) }
//    }
//}
//
//interface Comparador<T : Quantity<T>> {
//    val quantity: ComparableQuantity<T>
//    fun valido(quantityAtual: ComparableQuantity<T>): Boolean
//    fun mensagemErro(): String
//    fun check(quantityAtual: ComparableQuantity<T>) {
//        quantityAtual.to(quantity.unit)
//        if (!valido(quantityAtual)) throw IllegalArgumentException(mensagemErro())
//    }
//}
//
//class IgualA<T : Quantity<T>>(override val quantity: ComparableQuantity<T>) : Comparador<T> {
//    override fun valido(quantityAtual: ComparableQuantity<T>): Boolean = (quantity.isEquivalentTo(quantityAtual))
//    override fun mensagemErro(): String = "Deveria ser igual a $quantity"
//}

//abstract class IntervaloQuantity<T : Quantity<T>, S : Number>(
//    val minimo: S, val maximo: S, val unit: Unit<T>
//) {
//    abstract val inclusiveMinimo: Boolean
//    abstract val inclusiveMaximo: Boolean
//
//    private val minimoQuantity = Quantities.getQuantity(minimo, unit)
//    private val maximoQuantity = Quantities.getQuantity(maximo, unit)
//
//    fun noIntervalo(magnitude: Number, unit: Unit<T>): Boolean {
////        if (inclusiveMinimo ||)
//    }
//
//    fun noIntervalo(quantity: ComparableQuantity<T>): Boolean {
//        val compareMinimo = quantity.compareTo(minimoQuantity)
//        val compareMaximo = quantity.compareTo(maximoQuantity)
//        if (inclusiveMinimo && compareMinimo == 0) return true
//        if (inclusiveMaximo && compareMaximo == 0) return true
//
//    }
//
//    //TODO estudar maneira de fazer um intervalo sem limite pra mínimo ou sem limite pra maximo
//    private fun maiorQueOMinimo(quantity: ComparableQuantity<T>): Boolean {
//
//    }
//
//    private fun maiorOuIgualAoMinimo(quantity: ComparableQuantity<T>): Boolean {
//        if (semLimiteMinimo) return true
//        val compareMinimo = quantity.compareTo(minimoQuantity)
//        return (compareMinimo >= 0)
//    }
//}
//
//class IntervaloQuantityDouble<T : Quantity<T>>(
//    minimo: Double,
//    override val inclusiveMinimo: Boolean,
//    maximo: Double,
//    override val inclusiveMaximo: Boolean,
//    unit: Unit<T>
//) : IntervaloQuantity<T, Double>(minimo = minimo, maximo = maximo, unit = unit)
//
///*
//double distanceInMeters = 50.0;
//    UnitConverter metreToKilometre = METRE.getConverterTo(MetricPrefix.KILO(METRE));
//    double distanceInKilometers = metreToKilometre.convert(distanceInMeters );
//    assertEquals(0.05, distanceInKilometers, 0.00f);
// */
//
//class IntervaloQuantityInt<T : Quantity<T>>(
//    minimo: Int,
//    override val inclusiveMinimo: Boolean,
//    maximo: Int,
//    override val inclusiveMaximo: Boolean,
//    unit: Unit<T>
//) : IntervaloQuantity<T, Int>(minimo = minimo, maximo = maximo, unit = unit)

//fun main() {
//    val newton = Quantities.getQuantity(20.0 * 1000.0, NEWTON)
//    val quilonewton = Quantities.getQuantity(209.0, QUILONEWTON)
////    println(newton qeq quilonewton)
////    println(newton.compareTo(quilonewton))
//    val intervalo = Intervalo<Force>().igualA(newton)
//    intervalo.check(quilonewton)
//}
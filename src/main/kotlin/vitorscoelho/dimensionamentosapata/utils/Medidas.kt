package vitorscoelho.dimensionamentosapata.utils

import tech.units.indriya.AbstractUnit
import tech.units.indriya.format.SimpleUnitFormat
import tech.units.indriya.quantity.Quantities.getQuantity
import tech.units.indriya.unit.ProductUnit
import tech.units.indriya.unit.Units
import tech.units.indriya.unit.Units.*
import java.text.DecimalFormat
import javax.measure.MetricPrefix
import javax.measure.Quantity
import javax.measure.Unit
import javax.measure.quantity.*

interface Moment : Quantity<Moment>
interface SpringStiffness : Quantity<SpringStiffness>
interface SpringStiffnessPerUnitLength : Quantity<SpringStiffnessPerUnitLength>
interface SpringStiffnessPerUnitArea : Quantity<SpringStiffnessPerUnitArea>
interface ForcePerUnitLength : Quantity<ForcePerUnitLength>

internal fun inicializarUnidadesDeMedidaExtras() {
    adicionarViaReflection(ProductUnit<Moment>(NEWTON.multiply(METRE)), Moment::class.java)
    adicionarViaReflection(ProductUnit<SpringStiffness>(NEWTON.divide(METRE)), SpringStiffness::class.java)
    adicionarViaReflection(
        ProductUnit<SpringStiffnessPerUnitLength>(NEWTON.divide(METRE).divide(METRE)),
        SpringStiffnessPerUnitLength::class.java
    )
    adicionarViaReflection(
        ProductUnit<SpringStiffnessPerUnitArea>(NEWTON.divide(SQUARE_METRE).divide(METRE)),
        SpringStiffnessPerUnitArea::class.java
    )
    adicionarViaReflection(ProductUnit<ForcePerUnitLength>(NEWTON.divide(METRE)), ForcePerUnitLength::class.java)

    corrigirNomesDasUnidadesAdicionais()
}

private fun <U : AbstractUnit<*>> adicionarViaReflection(unit: U, type: Class<out Quantity<*>>) {
    val instanciaUnits = Units.getInstance()
    val metodoAddUnits = Units::class.java.getDeclaredMethod("addUnit", AbstractUnit::class.java, Class::class.java)
    metodoAddUnits.isAccessible = true
    metodoAddUnits.invoke(instanciaUnits, unit, type)
}

private fun corrigirNomesDasUnidadesAdicionais() {
    SimpleUnitFormat.getInstance().label(QUILOGRAMA_FORCA, "kgf")
    SimpleUnitFormat.getInstance().label(TONELADA_FORCA, "tf")
}

val QUILONEWTON: Unit<Force> = MetricPrefix.KILO(NEWTON)
val MEGANEWTON: Unit<Force> = MetricPrefix.MEGA(NEWTON)
val QUILOGRAMA_FORCA: Unit<Force> = QUILONEWTON.divide(100)
val TONELADA_FORCA: Unit<Force> = QUILOGRAMA_FORCA.multiply(1000)

val MILIMETRO: Unit<Length> = MetricPrefix.MILLI(METRE)
val CENTIMETRO: Unit<Length> = MetricPrefix.CENTI(METRE)

val MEGAPASCAL: Unit<Pressure> = MetricPrefix.MEGA(PASCAL)

val QUILONEWTON_POR_CENTIMETRO_QUADRADO: Unit<Pressure> =
    QUILONEWTON.divide(MetricPrefix.CENTI(METRE).pow(2)).asType(Pressure::class.java)

//fun getTextoUnidade(unidade: Unit<*>): String {
//    return unidade.toString()
//        .replace("kN*100", "kgf")
//        .replace("kN/10", "tf")
//        .replace("one", "")
//}

private val UNIT_PADRAO_LENGTH: Unit<Length> = CENTIMETRO
private val UNIT_PADRAO_FORCE: Unit<Force> = QUILONEWTON
private val UNIT_PADRAO_MOMENT: Unit<Moment> = QUILONEWTON.multiply(CENTIMETRO).asType(Moment::class.java)
private val UNIT_PADRAO_PRESSURE: Unit<Pressure> = QUILONEWTON_POR_CENTIMETRO_QUADRADO
private val UNIT_PADRAO_SPRING_STIFFNESS_PER_UNIT_AREA: Unit<SpringStiffnessPerUnitArea> =
    QUILONEWTON_POR_CENTIMETRO_QUADRADO.divide(CENTIMETRO).asType(SpringStiffnessPerUnitArea::class.java)
private val UNIT_PADRAO_ANGLE: Unit<Angle> = RADIAN
private val UNIT_PADRAO_AREA: Unit<Area> = CENTIMETRO.pow(2).asType(Area::class.java)
private val UNIT_PADRAO_VOLUME: Unit<Volume> = CENTIMETRO.pow(3).asType(Volume::class.java)

private val map = mapOf(
    Length::class.java to UNIT_PADRAO_LENGTH,
    Force::class.java to UNIT_PADRAO_FORCE,
    Moment::class.java to UNIT_PADRAO_MOMENT
)

@JvmName("converterPadraoLength")
fun Quantity<Length>.converterPadrao(): Quantity<Length> = this.to(UNIT_PADRAO_LENGTH)

@JvmName("converterPadraoForce")
fun Quantity<Force>.converterPadrao(): Quantity<Force> = this.to(UNIT_PADRAO_FORCE)

@JvmName("converterPadraoMoment")
fun Quantity<Moment>.converterPadrao(): Quantity<Moment> = this.to(UNIT_PADRAO_MOMENT)

@JvmName("converterPadraoPressure")
fun Quantity<Pressure>.converterPadrao(): Quantity<Pressure> = this.to(UNIT_PADRAO_PRESSURE)

@JvmName("converterPadraoSpringStiffnessPerUnitArea")
fun Quantity<SpringStiffnessPerUnitArea>.converterPadrao(): Quantity<SpringStiffnessPerUnitArea> =
    this.to(UNIT_PADRAO_SPRING_STIFFNESS_PER_UNIT_AREA)

@JvmName("converterPadraoAngle")
fun Quantity<Angle>.converterPadrao(): Quantity<Angle> = this.to(UNIT_PADRAO_ANGLE)

fun lengthOf(value: Number, unit: Unit<Length>): Quantity<Length> = getQuantity(value, unit)
fun forceOf(value: Number, unit: Unit<Force>): Quantity<Force> = getQuantity(value, unit)
fun momentOf(value: Number, unit: Unit<Moment>): Quantity<Moment> = getQuantity(value, unit)
fun pressureOf(value: Number, unit: Unit<Pressure>): Quantity<Pressure> = getQuantity(value, unit)
fun angleOf(value: Number, unit: Unit<Angle>): Quantity<Angle> = getQuantity(value, unit)
fun areaOf(value: Number, unit: Unit<Area>): Quantity<Area> = getQuantity(value, unit)
fun volumeOf(value: Number, unit: Unit<Volume>): Quantity<Volume> = getQuantity(value, unit)
fun springStiffnessPerUnitArea(
    value: Number,
    unit: Unit<SpringStiffnessPerUnitArea>
): Quantity<SpringStiffnessPerUnitArea> = getQuantity(value, unit)

fun lengthOfPadrao(value: Number): Quantity<Length> = lengthOf(value, UNIT_PADRAO_LENGTH)
fun pressureOfPadrao(value: Number): Quantity<Pressure> = pressureOf(value, UNIT_PADRAO_PRESSURE)
fun angleOfPadrao(value: Number): Quantity<Angle> = angleOf(value, UNIT_PADRAO_ANGLE)
fun areaOfPadrao(value: Number): Quantity<Area> = areaOf(value, UNIT_PADRAO_AREA)
fun volumeOfPadrao(value: Number): Quantity<Volume> = volumeOf(value, UNIT_PADRAO_VOLUME)
fun forceOfPadrao(value: Number): Quantity<Force> = forceOf(value, UNIT_PADRAO_FORCE)
fun momentOfPadrao(value: Number): Quantity<Moment> = momentOf(value, UNIT_PADRAO_MOMENT)

fun transformLengthOfPadrao(value: Number, newUnit: Unit<Length>) = lengthOfPadrao(value).to(newUnit)
fun transformPressureOfPadrao(value: Number, newUnit: Unit<Pressure>) = pressureOfPadrao(value).to(newUnit)
fun transformAngleOfPadrao(value: Number, newUnit: Unit<Angle>) = angleOfPadrao(value).to(newUnit)
fun transformAreaOfPadrao(value: Number, newUnit: Unit<Area>) = areaOfPadrao(value).to(newUnit)
fun transformVolumeOfPadrao(value: Number, newUnit: Unit<Volume>) = volumeOfPadrao(value).to(newUnit)
fun transformForceOfPadrao(value: Number, newUnit: Unit<Force>) = forceOfPadrao(value).to(newUnit)
fun transformMomentOfPadrao(value: Number, newUnit: Unit<Moment>) = momentOfPadrao(value).to(newUnit)

@JvmName("doublePadraoLength")
fun Quantity<Length>.doublePadrao(): Double = valorPadrao().toDouble()

@JvmName("doublePadraoSpringStiffnessPerUnitArea")
fun Quantity<SpringStiffnessPerUnitArea>.doublePadrao(): Double = valorPadrao().toDouble()

@JvmName("doublePadraoAngle")
fun Quantity<Angle>.doublePadrao(): Double = valorPadrao().toDouble()

@JvmName("doublePadraoForce")
fun Quantity<Force>.doublePadrao(): Double = valorPadrao().toDouble()

@JvmName("doublePadraoMoment")
fun Quantity<Moment>.doublePadrao(): Double = valorPadrao().toDouble()

@JvmName("doublePadraoPressure")
fun Quantity<Pressure>.doublePadrao(): Double = valorPadrao().toDouble()

@JvmName("valorPadraoLength")
fun Quantity<Length>.valorPadrao(): Number = this.converterPadrao().value

@JvmName("valorPadraoForce")
fun Quantity<Force>.valorPadrao(): Number = this.converterPadrao().value

@JvmName("valorPadraoMoment")
fun Quantity<Moment>.valorPadrao(): Number = this.converterPadrao().value

@JvmName("valorPadraoPressure")
fun Quantity<Pressure>.valorPadrao(): Number = this.converterPadrao().value

@JvmName("valorPadraoAngle")
fun Quantity<Angle>.valorPadrao(): Number = this.converterPadrao().value

@JvmName("valorPadraoSpringStiffnessPerUnitArea")
fun Quantity<SpringStiffnessPerUnitArea>.valorPadrao(): Number = this.converterPadrao().value

fun Quantity<*>.toString(dc: DecimalFormat): String {
    val valorFormatado = dc.format(this.getValue())
    return "$valorFormatado ${getUnit()}"
}

@JvmName("toStringLength")
fun Quantity<Length>.toString(dc: DecimalFormat, unit: Unit<Length>): String = this.to(unit).toString(dc)

@JvmName("toStringPressure")
fun Quantity<Pressure>.toString(dc: DecimalFormat, unit: Unit<Pressure>): String = this.to(unit).toString(dc)

@JvmName("toStringAngle")
fun Quantity<Angle>.toString(dc: DecimalFormat, unit: Unit<Angle>): String = this.to(unit).toString(dc)

//fun Quantity<*>.toString(dc: DecimalFormat, unit: Unit<*>): String {
//    val quantityConvertido = this.to(unit)
//}

fun main() {
    inicializarUnidadesDeMedidaExtras()
    println(pressureOf(10.0, MetricPrefix.MEGA(PASCAL)).to(QUILONEWTON_POR_CENTIMETRO_QUADRADO))
//    println(forceOf(1000.0, QUILOGRAMA_FORCA).to(TONELADA_FORCA))
//    val emQuilonewtonPorCentimetro = pressureOf(1000.0, MetricPrefix.KILO(PASCAL))
//    println(emQuilonewtonPorCentimetro)
//    println(emQuilonewtonPorCentimetro.to(QUILONEWTON_POR_CENTIMETRO_QUADRADO))
//    println(emQuilonewtonPorCentimetro.doublePadrao())
////    val kgf = AlternateUnit<Force>((METRE.multiply(KILOGRAM).divide(SECOND.pow(2))), "kgf")//NEWTON.multiply(100).alternate("kgf")
//    val kgf = TransformedUnit<Force>("kgf", NEWTON, NEWTON, MultiplyConverter.of(10))
//    SimpleUnitFormat.getInstance().label(MetricPrefix.DEKA(NEWTON), "kgf")
//    println("Converter")
//    println(kgf.symbol)
//    println(forceOf(100.0, MetricPrefix.DEKA(NEWTON)))
//    println(getQuantity(10.0, HOUR))
//    println("Tempo")
//    val minuto = TransformedUnit<Time>("minuto", SECOND, SECOND, MultiplyConverter.ofRational(60, 1))
//    println(getQuantity(12.0, minuto))
//    //new TransformedUnit<>("min", SECOND, SECOND, MultiplyConverter.ofRational(60, 1))
//    //addUnit(new AlternateUnit<Force>(METRE.multiply(KILOGRAM).divide(SECOND.pow(2)), "N"), Force.class)
//    /*
//    Unit<Pressure> PASCAL = NEWTON.divide(METRE.pow(2))
//      .alternate("Pa").asType(Pressure.class);
//    assertTrue(SimpleUnitFormat.getInstance().parse("Pa")
//      .equals(PASCAL));
//     */
//    println("NOVO")
//    val novoKgf: Unit<Force> = QUILONEWTON.multiply(100)
//    SimpleUnitFormat.getInstance().label(novoKgf, "kgf")
//    println(getQuantity(100.0, novoKgf))
}
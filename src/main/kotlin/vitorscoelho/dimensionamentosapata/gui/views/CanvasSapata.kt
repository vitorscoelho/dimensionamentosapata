package vitorscoelho.dimensionamentosapata.gui.views

import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import vitorscoelho.dimensionamentosapata.sapata.ResultadosFlexaoSapata
import vitorscoelho.gyncanvas.core.dxf.Color
import vitorscoelho.gyncanvas.core.dxf.FXDrawingArea
import vitorscoelho.gyncanvas.core.dxf.PanDragged
import vitorscoelho.gyncanvas.core.dxf.ZoomScroll
import vitorscoelho.gyncanvas.core.dxf.tables.Layer
import tornadofx.*
import vitorscoelho.dimensionamentosapata.estadio2.Ponto
import vitorscoelho.dimensionamentosapata.gui.controllers.ControllerInicial
import vitorscoelho.gyncanvas.core.dxf.blocks.Block
import vitorscoelho.gyncanvas.core.dxf.entities.*
import vitorscoelho.gyncanvas.core.dxf.tables.DimStyle
import vitorscoelho.gyncanvas.core.dxf.tables.TextStyle
import vitorscoelho.gyncanvas.math.Vector2D
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

internal class CanvasSapata(val controller: ControllerInicial) {
    //    val resultadosFlexaoSapata: ResultadosFlexaoSapata
    val drawingArea = FXDrawingArea()
    val node: Node
        get() = drawingArea.node
    val resultadosProperty = SimpleObjectProperty<ResultadosFlexaoSapata>(null)
    var resultados: ResultadosFlexaoSapata? by resultadosProperty

    init {
        PanDragged(mouseButton = MouseButton.MIDDLE, cursorPan = Cursor.MOVE, drawingArea = drawingArea).enable()
        ZoomScroll(zoomFactor = 1.2, drawingArea = drawingArea).enable()
        adicionarCapturaDeCoordenadas(drawingArea.node)
        resultadosProperty.onChange {
            limparDesenho()
            if (it != null) redesenharSapata()
        }
    }

    fun limparDesenho() {
        drawingArea.removeAllEntities()
        drawingArea.draw()
        controller.textoXMouseProperty.value = ""
        controller.textoYMouseProperty.value = ""
        controller.textoTensaoProperty.value = ""
        controller.textoDeformacaoProperty.value = ""
        controller.textoLegendaDesenhoProperty.value = ""
    }

    private val verticesSapata: List<Ponto>
        get() = resultados!!.verticesSapata
    private val verticesComprimidos: List<Ponto>
        get() = resultados!!.verticesSecaoComprimida

    fun redesenharSapata() {
        desenharContornoSapata()
        desenharContornoComprimido()
        desenharCotas()
        enquadrarDesenho()
        drawingArea.draw()
    }

    val xMinimo: Double
        get() = resultados!!.verticesSapata.minBy { it.x }!!.x
    val xMaximo: Double
        get() = resultados!!.verticesSapata.maxBy { it.x }!!.x
    val yMinimo: Double
        get() = resultados!!.verticesSapata.minBy { it.y }!!.y
    val yMaximo: Double
        get() = resultados!!.verticesSapata.maxBy { it.y }!!.y

    private fun desenharContornoSapata() {
        val contornoSapata = LwPolyline.initBuilder(layer = layerContorno)
            .startPoint(verticesSapata[0].x, verticesSapata[0].y)
            .lineTo(verticesSapata[1].x, verticesSapata[1].y)
            .lineTo(verticesSapata[2].x, verticesSapata[2].y)
            .lineTo(verticesSapata[3].x, verticesSapata[3].y)
            .closeAndBuild()
        drawingArea.addEntity(contornoSapata)
    }

    private fun desenharContornoComprimido() {
        if (verticesComprimidos.size > 2) {
            val contornoComprimidoBuilder = LwPolyline.initBuilder(layer = layerContornoComprimido)
                .startPoint(verticesComprimidos[0].x, verticesComprimidos[0].y)
            (1..verticesComprimidos.lastIndex).forEach { indice ->
                contornoComprimidoBuilder.lineTo(verticesComprimidos[indice].x, verticesComprimidos[indice].y)
            }
            val contornoComprimido = contornoComprimidoBuilder.closeAndBuild()
            val hachura = Hatch.fromLwPolyline(layer = layerContornoComprimido, lwPolyline = contornoComprimido)
            drawingArea.addEntity(hachura)
        }
    }

    private val tolerancia = 0.001
    private fun toleravel(valor1: Double, valor2: Double): Boolean = (valor1 - valor2).absoluteValue <= tolerancia
    private fun List<Ponto>.toVector() = map { Vector2D(it.x, it.y) }
    private fun cotaHorizontal(xPoint1: Vector2D, xPoint2: Vector2D, yDimLine: Double) = RotatedDimension.horizontal(
        layer = layerCota, dimStyle = dimStyle, xPoint1 = xPoint1, xPoint2 = xPoint2, yDimensionLine = yDimLine
    )

    private fun cotaVertical(xPoint1: Vector2D, xPoint2: Vector2D, xDimLine: Double) = RotatedDimension.vertical(
        layer = layerCota, dimStyle = dimStyle, xPoint1 = xPoint1, xPoint2 = xPoint2, xDimensionLine = xDimLine
    )

    private fun List<Vector2D>.cotasHorizontais(yDimLine: Double): List<RotatedDimension> =
        zipWithNext { atual, proximo ->
            RotatedDimension.horizontal(
                layer = layerCota, dimStyle = dimStyle, xPoint1 = atual, xPoint2 = proximo, yDimensionLine = yDimLine
            )
        }.filter { it.measurement >= tolerancia }

    private fun List<Vector2D>.cotasVerticais(xDimLine: Double): List<RotatedDimension> =
        zipWithNext { atual, proximo ->
            RotatedDimension.vertical(
                layer = layerCota, dimStyle = dimStyle, xPoint1 = atual, xPoint2 = proximo, xDimensionLine = xDimLine
            )
        }.filter { it.measurement >= tolerancia }

    private val distCota = 15.0

    private fun desenharCotas() {
        val todosVertices = resultados!!.verticesSapata + resultados!!.verticesSecaoComprimida
        val verticesCotasSuperiores = todosVertices.filter { toleravel(it.y, yMaximo) }.sortedBy { it.x }.toVector()
        val verticesCotasInferiores = todosVertices.filter { toleravel(it.y, yMinimo) }.sortedBy { it.x }.toVector()
        val verticesCotasEsquerda = todosVertices.filter { toleravel(it.x, xMinimo) }.sortedBy { it.y }.toVector()
        val verticesCotasDireita = todosVertices.filter { toleravel(it.x, xMaximo) }.sortedBy { it.y }.toVector()
        val cotasSuperiores = verticesCotasSuperiores.cotasHorizontais(yDimLine = yMaximo + distCota)
        val cotasInferiores = verticesCotasInferiores.cotasHorizontais(yDimLine = yMinimo - distCota)
        val cotasEsquerda = verticesCotasEsquerda.cotasVerticais(xDimLine = xMinimo - distCota)
        val cotasDireita = verticesCotasDireita.cotasVerticais(xDimLine = xMaximo + distCota)
        drawingArea.addEntities(cotasEsquerda + cotasSuperiores)
        if (cotasSuperiores.size > 1) {
            val xPoint1 = Vector2D(xMinimo, yMaximo)
            val xPoint2 = Vector2D(xMaximo, yMaximo)
            val cota = cotaHorizontal(xPoint1 = xPoint1, xPoint2 = xPoint2, yDimLine = yMaximo + 2.0 * distCota)
            drawingArea.addEntity(cota)
        }
        if (cotasEsquerda.size > 1) {
            val xPoint1 = Vector2D(xMinimo, yMinimo)
            val xPoint2 = Vector2D(xMinimo, yMaximo)
            val cota = cotaVertical(xPoint1 = xPoint1, xPoint2 = xPoint2, xDimLine = xMinimo - 2.0 * distCota)
            drawingArea.addEntity(cota)
        }
        if (cotasInferiores.size > 1) drawingArea.addEntities(cotasInferiores)
        if (cotasDireita.size > 1) drawingArea.addEntities(cotasDireita)
    }

    private fun enquadrarDesenho() {
        val espacoContorno = 50.0
        drawingArea.camera.zoomWindow(
            x1 = xMinimo - espacoContorno,
            y1 = yMinimo - espacoContorno,
            x2 = xMaximo + espacoContorno,
            y2 = yMaximo + espacoContorno
        )
    }

    private fun criarCursor(x: Double, y: Double, zoom: Double) = Circle(
        layer = layerCursor, centerPoint = Vector2D(x, y), diameter = 10.0 / zoom
    )

    private var cursor = criarCursor(x = 0.0, y = 0.0, zoom = 1.0)

    private fun adicionarCapturaDeCoordenadas(node: Node) {
        val eventHandler: EventHandler<MouseEvent> by lazy {
            EventHandler<MouseEvent> { event ->
                drawingArea.removeEntity(cursor)
                val coordenadas = drawingArea.camera.worldCoordinates(xCamera = event.x, yCamera = event.y)
                if (resultados != null) {
                    val x = min(xMaximo, max(xMinimo, coordenadas.x))
                    val y = min(yMaximo, max(yMinimo, coordenadas.y))
                    val tensao = resultados!!.tensaoSolo(x = x, y = y)
                    val deformacao = resultados!!.deformacao(x = x, y = y)
                    controller.textoXMouseProperty.value = "X= ${dc1Casa.format(x)} cm"
                    controller.textoYMouseProperty.value = "Y= ${dc1Casa.format(y)} cm"
                    controller.textoTensaoProperty.value = "Tensão= ${dc2Casas.format(tensao * 10_000)} kPa"
                    if (controller.model.utilizarModuloReacaoSolo.value) {
                        controller.textoDeformacaoProperty.value = "Deformação= ${dc2Casas.format(deformacao)} cm"
                    } else {
                        controller.textoDeformacaoProperty.value = ""
                    }
                    controller.textoLegendaDesenhoProperty.value =
                        "Contorno da sapata em vermelho. Área comprimida em amarelo."
                    cursor = criarCursor(x = x, y = y, zoom = drawingArea.camera.zoom)
                    drawingArea.addEntity(cursor)
                    drawingArea.draw()
                }
            }
        }
        node.addEventHandler(MouseEvent.MOUSE_MOVED, eventHandler)
    }
}

private val layerEsforco = Layer(name = "Esforço", color = Color.INDEX_6)
private val layerCursor = Layer(name = "Cursor", color = Color.INDEX_3)
private val layerContorno = Layer(name = "Contorno", color = Color.INDEX_1)
private val layerContornoComprimido = Layer(name = "Contorno comprimido", color = Color.INDEX_2)
private val layerCota = Layer(name = "Cota", color = Color.INDEX_142)
private val blocoCota: Block = run {
    val circulo1 = Circle(layer = layerCota, centerPoint = Vector2D.ZERO, diameter = 0.5)
    val circulo2 = Circle(layer = layerCota, centerPoint = Vector2D.ZERO, diameter = 1.0)
    val linha =
        Line(layer = layerCota, startPoint = Vector2D(x = -1.0, y = 0.0), endPoint = Vector2D(x = 0.0, y = 0.0))
    return@run Block(name = "_Origin2", entities = listOf(circulo1, circulo2, linha))
}
private val textStyle = TextStyle(
    name = "Standard",
    fontName = "Ubuntu Light",
    fontFileName = "Ubuntu Light"
)
private val dimStyle = DimStyle(
    name = "1_100 TQS",
    dimensionLinesColor = Color.BY_BLOCK,
    dimensionLinesSuppressDimLine1 = false,
    dimensionLinesSuppressDimLine2 = false,
    extensionLinesColor = Color.BY_BLOCK,
    extensionLinesSuppressExtLine1 = false,
    extensionLinesSuppressExtLine2 = false,
    extensionLinesExtendBeyondDimLines = 0.0,
    extensionLinesOffsetFromOrigin = 0.06,
    firstArrowHead = blocoCota,
    secondArrowHead = blocoCota,
    leaderArrowHead = Block.NONE,
    arrowSize = 0.05,
    textStyle = textStyle,
    textColor = Color.BY_LAYER,
    textHeight = 0.12,
    textOffsetFromDimLine = 0.015,
    overallScale = 75.0,
    linearDimensionPrecision = 4,
    decimalSeparator = ',',
    unitRound = 0.1,
    prefix = "",
    suffix = "",
    scaleFactor = 1.0,
    linearDimensionSuppressLeadingZeros = true,
    linearDimensionSuppressTrailingZeros = true,
    angularDimensionPrecision = 0
)
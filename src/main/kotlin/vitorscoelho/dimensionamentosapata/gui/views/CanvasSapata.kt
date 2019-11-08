package vitorscoelho.dimensionamentosapata.gui.views

import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
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
import vitorscoelho.gyncanvas.core.dxf.transformation.MutableTransformationMatrix
import vitorscoelho.gyncanvas.math.Vector2D
import kotlin.math.PI
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
        desenharVetoresDeEsforcos()
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

    private fun criarCursor(x: Double, y: Double, zoom: Double): Entity {
        val diametro = 10.0 / zoom
        return circunferenciaPreenchida(layer = layerCursor, centro = Vector2D(x = x, y = y), diametro = diametro)
    }

    private fun desenharVetoresDeEsforcos() {
        with(resultados!!.esforcoSolicitante) {
            desenharVetorNormal(normal =normal)
            desenharVetorMomentoX(momentoX = momentoX)
            desenharVetorMomentoY(momentoX = momentoX,momentoY = momentoY)
        }
    }

    private val toleranciaEsforcoNulo = 0.009
    private fun desenharVetorNormal(normal: Double) {
        if (normal.absoluteValue <= toleranciaEsforcoNulo) return
        val diametroRepresentacao = 10.0
        val circulo = Circle(layer = layerEsforco, centerPoint = Vector2D.ZERO, diameter = diametroRepresentacao)
        drawingArea.addEntity(circulo)
        if (normal > 0.0) {
            val dimensao = diametroRepresentacao / 2.0
            val meiaDimensao = dimensao / 2.0
            val linha1 = Line(
                layer = layerEsforco,
                startPoint = Vector2D(x = -meiaDimensao, y = -meiaDimensao),
                endPoint = Vector2D(x = meiaDimensao, y = meiaDimensao)
            )
            val linha2 = Line(
                layer = layerEsforco,
                startPoint = Vector2D(x = -meiaDimensao, y = meiaDimensao),
                endPoint = Vector2D(x = meiaDimensao, y = -meiaDimensao)
            )
            drawingArea.addEntities(listOf(linha1, linha2))
        } else {
            val diametro = diametroRepresentacao / 3.0
            val representacaoInterna = circunferenciaPreenchida(
                layer = layerEsforco, centro = Vector2D.ZERO, diametro = diametro
            )
            drawingArea.addEntity(representacaoInterna)
        }
        desenharValorEsforco(
            valor = normal,
            unidade = "kN",
            posicao = Vector2D(x = 0.8 * diametroRepresentacao, y = 0.25 * diametroRepresentacao),
            justify = AttachmentPoint.BOTTOM_LEFT,
            rotacao = PI / 4.0
        )
    }

    private fun desenharVetorMomentoX(momentoX: Double) {
        if (momentoX.absoluteValue <= toleranciaEsforcoNulo) return
        val rotacao = if (momentoX > 0.0) 0.0 else PI
        desenharSeta(rotacao = rotacao)
        val justify = if (momentoX > 0.0) AttachmentPoint.TOP_LEFT else AttachmentPoint.TOP_RIGHT
        desenharValorEsforco(
            valor = momentoX.absoluteValue / 100.0,
            unidade = "kN.m",
            posicao = Vector2D(x = 0.0, y = -5.0),
            justify = justify,
            rotacao = 0.0
        )
    }

    private fun desenharVetorMomentoY(momentoX: Double, momentoY: Double) {
        if (momentoY.absoluteValue <= toleranciaEsforcoNulo) return
        val rotacao = if (momentoY > 0.0) PI / 2.0 else -PI / 2.0
        desenharSeta(rotacao = rotacao)
        val justify = if (momentoY > 0.0){
            AttachmentPoint.BOTTOM_LEFT
        } else if (momentoY < 0.0 && momentoX < 0.0){
            AttachmentPoint.TOP_RIGHT
        }else{
            AttachmentPoint.BOTTOM_RIGHT
        }
        val posicaoTexto = if (momentoY < 0.0 && momentoX < 0.0) -1.0 else 1.0
        desenharValorEsforco(
            valor = momentoY.absoluteValue / 100.0,
            unidade = "kN.m",
            posicao = Vector2D(x = -5.0 * posicaoTexto, y = 0.0),
            justify = justify,
            rotacao = PI / 2.0
        )
    }

    private fun desenharSeta(rotacao: Double) {
        val comprimento = 40.0
        val largura = 5.0
        val meiaLargura = largura / 2.0
        val comprimentoPonta = 5.0
        val distanciaPontaDupla = 5.0
        val pontaSeta = Vector2D(x = comprimento, y = 0.0)
        val superiorPonta1 = Vector2D(x = comprimento - comprimentoPonta, y = meiaLargura)
        val inferiorPonta1 = Vector2D(x = comprimento - comprimentoPonta, y = -meiaLargura)
        val linhaSeta = Line(layer = layerEsforco, startPoint = Vector2D.ZERO, endPoint = pontaSeta)
        val ponta1 = LwPolyline.initBuilder(layerEsforco)
            .startPoint(inferiorPonta1).lineTo(pontaSeta).lineTo(superiorPonta1).build()
        val ponta2 = ponta1.transform(
            MutableTransformationMatrix().translate(xOffset = -distanciaPontaDupla, yOffset = 0.0)
        )
        val transformacao = MutableTransformationMatrix().rotate(angle = rotacao)
        val elementos = listOf(linhaSeta, ponta1, ponta2).map { it.transform(transformacao) }
        drawingArea.addEntities(elementos)
    }

    private fun desenharValorEsforco(
        valor: Double,
        unidade: String,
        posicao: Vector2D,
        justify: AttachmentPoint,
        rotacao: Double
    ) {
        val conteudo = "${dc2CasasSuprimindoZero.format(valor.absoluteValue)}$unidade"
        val tamanho = 8.0
        val mtext = MText(
            layer = layerEsforco,
            style = textStyle,
            size = tamanho,
            position = posicao,
            justify = justify,
            content = conteudo,
            rotation = rotacao
        )
        drawingArea.addEntity(mtext)
    }

    private var cursor = criarCursor(x = 0.0, y = 0.0, zoom = 1.0)
    private fun desenharCursorECapturarCoordenadas(xTela: Double, yTela: Double) {
        drawingArea.removeEntity(cursor)
        val coordenadas = drawingArea.camera.worldCoordinates(xCamera = xTela, yCamera = yTela)
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
                "OBS.: Contorno da sapata em vermelho. Área comprimida em amarelo."
            cursor = criarCursor(x = x, y = y, zoom = drawingArea.camera.zoom)
            drawingArea.addEntity(cursor)
            drawingArea.draw()
        }
    }

    private fun adicionarCapturaDeCoordenadas(node: Node) {
        val eventHandlerMouseMoved: EventHandler<MouseEvent> by lazy {
            EventHandler<MouseEvent> { event -> desenharCursorECapturarCoordenadas(xTela = event.x, yTela = event.y) }
        }
        val eventHandlerScroll: EventHandler<ScrollEvent> by lazy {
            EventHandler<ScrollEvent> { event -> desenharCursorECapturarCoordenadas(xTela = event.x, yTela = event.y) }
        }
        node.addEventHandler(MouseEvent.MOUSE_MOVED, eventHandlerMouseMoved)
        node.addEventHandler(ScrollEvent.SCROLL, eventHandlerScroll)
    }
}

private fun circunferenciaPreenchida(layer: Layer, centro: Vector2D, diametro: Double): Entity {
    val raio = diametro / 2.0
    val x = centro.x
    val y = centro.y
    val polyline = LwPolyline.initBuilder(layer = layerCursor)
        .startPoint(x = x - raio, y = y)
        .arcTo(
            xTangent1 = x - raio, yTangent1 = y - raio,
            xTangent2 = x, yTangent2 = y - raio,
            radius = raio
        )
        .arcTo(
            xTangent1 = x + raio, yTangent1 = y - raio,
            xTangent2 = x + raio, yTangent2 = y,
            radius = raio
        )
        .arcTo(
            xTangent1 = x + raio, yTangent1 = y + raio,
            xTangent2 = x, yTangent2 = y + raio,
            radius = raio
        )
        .arcTo(
            xTangent1 = x - raio, yTangent1 = y + raio,
            xTangent2 = x - raio, yTangent2 = y,
            radius = raio
        )
        .closeAndBuild()
    return Hatch.fromLwPolyline(layer = layerEsforco, lwPolyline = polyline)
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
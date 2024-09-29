import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.SwingConstants
import kotlin.math.*


fun main() {
    val frame = JFrame()
    val pane = frame.contentPane
    pane.layout = BorderLayout()

    val headingSlider = JSlider(0, 360, 180)
    pane.add(headingSlider, BorderLayout.SOUTH)

    val pitchSlider = JSlider(SwingConstants.VERTICAL, -90, 90, 0)
    pane.add(pitchSlider, BorderLayout.EAST)

    val renderPanel = object : JPanel() {
        override fun paintComponent(graphics: Graphics) {
            //General Setup
            val graphics2d: Graphics2D = graphics as Graphics2D
            graphics2d.color = Color.BLACK
            graphics2d.fillRect(0, 0, width, height)
            graphics2d.color = Color.WHITE

            //Matrices
            val heading = Math.toRadians(headingSlider.value.toDouble())
            val headingTransform = Matrix3D(doubleArrayOf(
                cos(heading),  0.0, sin(heading),
                0.0,           1.0, 0.0,
                -sin(heading), 0.0, cos(heading)
            ))
            val pitch = Math.toRadians(pitchSlider.value.toDouble())
            val pitchTransform = Matrix3D(doubleArrayOf(
                1.0, 0.0,         0.0,
                0.0, cos(pitch),  sin(pitch),
                0.0, -sin(pitch), cos(pitch)
            ))
            val transform = headingTransform.multiply(pitchTransform)

            //General Rendering
            val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val zBuffer = DoubleArray(img.width * img.height) { Double.NEGATIVE_INFINITY }

            //Render Tetrahedon
            for (triangle in tetrahedon()) {
                renderTriangle(img, zBuffer, transform, triangle)
            }

            //Finish up
            graphics2d.drawImage(img, 0, 0, null)

        }
    }

    // Add functionality to Sliders
    headingSlider.addChangeListener { renderPanel.repaint() }
    pitchSlider.addChangeListener { renderPanel.repaint() }

    pane.add(renderPanel, BorderLayout.CENTER)

    frame.setSize(400, 400)
    frame.isVisible = true
}

private fun renderTriangle(img: BufferedImage, zBuffer: DoubleArray, transform: Matrix3D, triangle: Triangle) {
    val v1 = transform.transform(triangle.v1)
    val v2 = transform.transform(triangle.v2)
    val v3 = transform.transform(triangle.v3)

    val norm = normalVector(Triangle(v1, v2, v3))
    val angleCos = abs(sin(norm.z))

    // Translate to screen coordinates
    v1.x += img.width  / 2
    v1.y += img.height / 2
    v2.x += img.width  / 2
    v2.y += img.height / 2
    v3.x += img.width  / 2
    v3.y += img.height / 2

    val minX = max(0.0,              ceil( min(v1.x, min(v2.x, v3.x)))).toInt()
    val maxX = min(img.width - 1.0,  floor(max(v1.x, max(v2.x, v3.x)))).toInt()
    val minY = max(0.0,              ceil( min(v1.y, min(v2.y, v3.y)))).toInt()
    val maxY = min(img.height - 1.0, floor(max(v1.y, max(v2.y, v3.y)))).toInt()

    val triangleArea = (v1.y - v3.y) * (v2.x - v3.x) +
                       (v2.y - v3.y) * (v3.x - v1.x)

    // Coloring
    for (y in minY..maxY) {
        for (x in minX..maxX) {
            val b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea
            val b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea
            val b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea

            val depth: Double = b1 * v1.z + b2 * v2.z + b3 * v3.z
            val zIndex: Int = y * img.width + x

            if (b1 in 0.0..1.0 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                if (zBuffer[zIndex] < depth) {
                    img.setRGB(x, y, getShade(triangle.color, angleCos).rgb)
                    zBuffer[zIndex] = depth
                }
            }
        }
    }
}

fun normalVector(triangle: Triangle): Vertex {
    val ab = Vertex(
        triangle.v2.x - triangle.v1.x,
        triangle.v2.y - triangle.v1.y,
        triangle.v2.z - triangle.v1.z
    )
    val ac = Vertex(
        triangle.v3.x - triangle.v1.x,
        triangle.v3.y - triangle.v1.y,
        triangle.v3.z - triangle.v1.z
    )

    val norm = Vertex(
        ab.y * ac.z - ab.z * ac.y,
        ab.z * ac.x - ab.x * ac.z,
        ab.x * ac.y - ab.y * ac.x
    )
    val normalLength = sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z)

    norm.x /= normalLength
    norm.y /= normalLength
    norm.z /= normalLength

    return norm
}


fun getShade(color: Color, shade: Double): Color {
    val redLinear =   color.red.toDouble().pow(2.4)   * shade
    val greenLinear = color.green.toDouble().pow(2.4) * shade
    val blueLinear =  color.blue.toDouble().pow(2.4)  * shade

    val red = redLinear.pow(1/2.4).toInt()
    val green = greenLinear.pow(1/2.4).toInt()
    val blue = blueLinear.pow(1/2.4).toInt()

    return Color(red, green, blue)
}
import java.awt.Color
import kotlin.math.sqrt

class Vertex(
    var x: Double,
    var y: Double,
    var z: Double
)

class Triangle(
    var v1: Vertex,
    var v2: Vertex,
    var v3: Vertex,
    var color: Color = Color.WHITE
)

fun tetrahedon(): ArrayList<Triangle>{
    val tris = ArrayList<Triangle>()
    tris.add(Triangle(
        Vertex( 100.0,  100.0,  100.0),
        Vertex(-100.0, -100.0,  100.0),
        Vertex(-100.0,  100.0, -100.0),
        Color.GREEN
    ))

    tris.add(Triangle(
        Vertex( 100.0,  100.0,  100.0),
        Vertex(-100.0, -100.0,  100.0),
        Vertex( 100.0, -100.0, -100.0),
        Color.CYAN
    ))

    tris.add(Triangle(
        Vertex(-100.0,  100.0, -100.0),
        Vertex( 100.0, -100.0, -100.0),
        Vertex( 100.0,  100.0,  100.0),
        Color.WHITE
    ))

    tris.add(Triangle(
        Vertex(-100.0,  100.0, -100.0),
        Vertex( 100.0, -100.0, -100.0),
        Vertex(-100.0, -100.0,  100.0),
        Color.PINK
    ))

    return tris
}

fun ballon(): ArrayList<Triangle>{
    val result = ArrayList<Triangle>()

    for (t in tetrahedon()) {
        val m1 = Vertex((t.v1.x + t.v2.x) / 2, (t.v1.y + t.v2.y) / 2, (t.v1.z + t.v2.z) / 2)
        val m2 = Vertex((t.v2.x + t.v3.x) / 2, (t.v2.y + t.v3.y) / 2, (t.v2.z + t.v3.z) / 2)
        val m3 = Vertex((t.v1.x + t.v3.x) / 2, (t.v1.y + t.v3.y) / 2, (t.v1.z + t.v3.z) / 2)

        result.add(Triangle(t.v1, m1, m3, t.color))
        result.add(Triangle(t.v2, m1, m2, t.color))
        result.add(Triangle(t.v3, m2, m3, t.color))
        result.add(Triangle(m1, m2, m3, t.color))
    }
    for (t in result) {
        for (v in arrayOf(t.v1, t.v2, t.v3)) {
            val l = sqrt(v.x * v.x + v.y * v.y + v.z * v.z) / sqrt(30000.0)
            v.x /= l
            v.y /= l
            v.z /= l
        }
    }
    return result
}
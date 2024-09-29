import java.awt.Color
import kotlin.math.*


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

val origin = Vertex(0.0, 0.0, 0.0)

fun createCube(origin: Vertex, size: Double, color: Color = Color.WHITE): List<Triangle> {
    val (x, y, z) = listOf(origin.x, origin.y, origin.z)
    val d = size / 2

    // Define vertices relative to the origin
    val vertices = listOf(
        Vertex(x - d, y - d, z - d), // 0
        Vertex(x + d, y - d, z - d), // 1
        Vertex(x + d, y + d, z - d), // 2
        Vertex(x - d, y + d, z - d), // 3
        Vertex(x - d, y - d, z + d), // 4
        Vertex(x + d, y - d, z + d), // 5
        Vertex(x + d, y + d, z + d), // 6
        Vertex(x - d, y + d, z + d)  // 7
    )

    // Define cube faces using vertex indices (2 triangles per face)
    val faces = listOf(
        listOf(0, 1, 2, 3),  // Front
        listOf(4, 5, 6, 7),  // Back
        listOf(1, 5, 6, 2),  // Left
        listOf(0, 4, 7, 3),  // Right
        listOf(4, 5, 1, 0),  // Bottom
        listOf(7, 6, 2, 3)   // Top
    )

    // Generate triangles for each face
    val triangles = mutableListOf<Triangle>()
    faces.forEach { (i1, i2, i3, i4) ->
        triangles.add(Triangle(vertices[i1], vertices[i2], vertices[i3], color))
        triangles.add(Triangle(vertices[i1], vertices[i4], vertices[i3], color))
    }

    return triangles
}

fun createSphere(center: Vertex, radius: Double, segments: Int, rings: Int, color: Color = Color.WHITE): List<Triangle> {
    val triangles = mutableListOf<Triangle>()

    val vertices = mutableListOf<Vertex>()

    // Generate vertices for the sphere
    for (i in 0..rings) {
        val theta = PI * i / rings // from 0 to PI (latitude)
        val sinTheta = sin(theta)
        val cosTheta = cos(theta)

        for (j in 0..segments) {
            val phi = 2 * PI * j / segments // from 0 to 2PI (longitude)
            val sinPhi = sin(phi)
            val cosPhi = cos(phi)

            val x = center.x + radius * sinTheta * cosPhi
            val y = center.y + radius * sinTheta * sinPhi
            val z = center.z + radius * cosTheta

            vertices.add(Vertex(x, y, z))
        }
    }

    // Generate triangles using the vertices
    for (i in 0..<rings) {
        for (j in 0..<segments) {
            val first = i * (segments + 1) + j
            val second = first + segments + 1

            // Two triangles per quad
            triangles.add(Triangle(vertices[first], vertices[second], vertices[first + 1], color))
            triangles.add(Triangle(vertices[second], vertices[second + 1], vertices[first + 1], color))
        }
    }

    return triangles
}
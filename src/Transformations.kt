class Matrix3D(
    private val values: DoubleArray
) {
    fun multiply(multiplicant: Matrix3D): Matrix3D {

        val result = DoubleArray(9)
        for (row in 0..2) {
            for (col in 0..2) {
                for (i in 0..2) {
                    result[row * 3 + col] +=
                        values[row * 3 + i] * multiplicant.values[i * 3 + col]
                }
            }
        }
        return Matrix3D(result)
    }

    fun transform(input: Vertex): Vertex {
        return Vertex(
            input.x * values[0] + input.y * values[3] + input.z * values[6],
            input.x * values[1] + input.y * values[4] + input.z * values[7],
            input.x * values[2] + input.y * values[5] + input.z * values[8]
        )
    }
}
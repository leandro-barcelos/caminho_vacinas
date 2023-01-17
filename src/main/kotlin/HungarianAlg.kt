import kotlin.system.exitProcess

/**
 * @author <a href="https://github.com/aalmi">...</a> | march 2014
 * @version 1.0
 */

class HungarianAlg (private val matrix: Array<DoubleArray>) {

    init {
        if (matrix.size != matrix[0].size) {
            try {
                throw IllegalAccessError("Matrix is not square!")
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                exitProcess(1)
            }
        }
    }

    private val squareInRow: IntArray = IntArray(matrix.size)
    private val squareInCol: IntArray  = IntArray(matrix[0].size)
    private val rowIsCovered: IntArray = IntArray(matrix.size)
    private val colIsCovered: IntArray = IntArray(matrix[0].size)
    private val staredZeroesInRow: IntArray = IntArray(matrix.size)


    init {
        // Posição dos 0s cobertos
        squareInRow.fill(-1)
        squareInCol.fill(-1)

        // Posição dos 0 estrelados
        staredZeroesInRow
        staredZeroesInRow.fill(-1)
    }

    fun hungarianAlg(): Array<IntArray> {
        step1()
        step2()
        step3()

        while (!allColumnsAreCovered()) {
            var mainZero = step4()
            while (mainZero[0] < 0 && mainZero[1] < 0) {
                step7()
                mainZero = step4()
            }

            if (squareInRow[mainZero[0]] == -1) {
                step6(mainZero)
                step3()
            } else {
                rowIsCovered[mainZero[0]] = 1
                colIsCovered[squareInRow[mainZero[0]]] =0
                step7()
            }
        }

        val optimalAssignment = Array(matrix.size) {IntArray(matrix.size)}
        for (i in squareInCol.indices)
            optimalAssignment[i] = intArrayOf(i, squareInCol[i])

        return optimalAssignment
    }

    /**
     * Check if all columns are covered. If that's the case then the
         * optimal solution is found
     *
     * @return true or false
     */
    private fun allColumnsAreCovered(): Boolean {
        for (i in colIsCovered)
            if (i == 0)
                return false

        return true
    }

    /**
     * Step 1:
     * Reduce the matrix so that in each row and column at least one zero exists:
     * 1. subtract each row minima from each element of the row
     * 2. subtract each column minima from each element of the column
     */
    private fun step1() {
        for (i in matrix.indices) {
            var currentRowMin = Double.MAX_VALUE
            for (j in matrix[0].indices) {
                if (matrix[i][j] < currentRowMin)
                    currentRowMin = matrix[i][j]
            }

            for (k in matrix[0].indices)
                matrix[i][k] -= currentRowMin
        }

        for (i in matrix[0].indices) {
            var currentColMin = Double.MAX_VALUE
            for (n in matrix) {
                if (n[i] < currentColMin)
                    currentColMin = n[i]
            }

            for (k in matrix.indices)
                matrix[k][i] -= currentColMin
        }
    }





    /**
     * Step 2:
     * mark each 0 with a "square", if there are no other marked zeroes in the same row or column
     */
    private fun step2() {
        val rowHasSquare = IntArray(matrix.size)
        val colHasSquare = IntArray(matrix[0].size)

        for (i in matrix.indices) {
            for (j in matrix.indices) {
                if (matrix[i][j] == 0.0 && rowHasSquare[i] == 0 && colHasSquare[j] == 0) {
                    rowHasSquare[i] = 1
                    colHasSquare[j] = 1
                    squareInRow[i] = j
                    squareInCol[j] = i
                }
            }
        }
    }

    /**
     * Step 3:
     * Cover all columns which are marked with a "square"
     */
    private fun step3() {
        for (i in squareInCol.indices)
            colIsCovered[i] = if (squareInCol[i] != -1) 1 else 0
    }

    /**
     * Step 7:
     * 1. Find the smallest uncovered value in the matrix.
     * 2. Subtract it from all uncovered values
     * 3. Add it to all twice-covered values
     */
    private fun step7() {
        var minUncoveredValue = Double.MAX_VALUE

        for (i in matrix.indices) {
            if (rowIsCovered[i] == 1) continue
            for (j in matrix[0].indices)
                if (colIsCovered[j] == 0 && matrix[i][j] < minUncoveredValue)
                    minUncoveredValue = matrix[i][j]
        }

        if (minUncoveredValue > 0) {
            for (i in matrix.indices) {
                for (j in matrix[0].indices) {
                    if (rowIsCovered[i] == 1 && colIsCovered[j] == 1)
                        matrix[i][j] += minUncoveredValue
                    else if (rowIsCovered[i] == 0 && colIsCovered[j] == 0)
                        matrix[i][j] -= minUncoveredValue
                }
            }
        }
    }

    /**
     * Step 4:
     * Find zero value Z_0 and mark it as "0*".
     *
     * @return position of Z_0 in the matrix
     */
    private fun step4(): IntArray {
        for (i in matrix.indices) {
            if (rowIsCovered[i] == 0) {
                for (j in matrix[i].indices) {
                    if (matrix[i][j] == 0.0 && colIsCovered[j] == 0) {
                        staredZeroesInRow[i] = j
                        return intArrayOf(i, j)
                    }
                }
            }
        }

        return intArrayOf(-1, -1)
    }

    /**
     * Step 6:
     * Create a chain K of alternating "squares" and "0*"
     *
     * @param mainZero => Z_0 of Step 4
     */
    private fun step6(mainZero: IntArray) {
        var i: Int
        var j = mainZero[1]

        val k = LinkedHashSet<IntArray>()
        k.add(mainZero)

        var found: Boolean

        do {
            found = if (squareInCol[j] != -1) {
                k.add(intArrayOf(squareInCol[j], j))
                true
            } else {
                false
            }

            if (!found) break

            i = squareInCol[j]
            j = staredZeroesInRow[i]

            if (j != -1)
                k.add(intArrayOf(i, j))
            else
                found = false
        } while (found)

        for (zero in k) {
            if (squareInCol[zero[1]] == zero[0]) {
                squareInCol[zero[1]] = -1
                squareInRow[zero[0]] = -1
            }

            if (staredZeroesInRow[zero[0]] == zero[1]) {
                squareInCol[zero[1]] = zero[0]
                squareInRow[zero[0]] = zero[1]
            }
        }

        staredZeroesInRow.fill(-1)
        rowIsCovered.fill(0)
        colIsCovered.fill(0)
    }
}
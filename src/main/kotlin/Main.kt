data class DistanceAB<T>(var a: T, var b: T, var distance: Double)

fun main() {
    val distanceMatrix = DistTableReader("src/main/resources/distancias.csv").getDistList()

    val srsUberlandia = SRSGraph("SRS Uberlandia", distanceMatrix)

    srsUberlandia.printEulerianPath()
}





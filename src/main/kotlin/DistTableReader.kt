import com.opencsv.CSVReader
import java.io.FileReader

class DistTableReader (private val filepath: String) {
    fun getDistList() : List<DistanceAB<String>> {
        val cr = CSVReader(FileReader(filepath))
        val table= cr.readAll()

        return table.drop(1).flatMap { row ->
            (1 until row.size).map {j ->
                val distString = row[j]
                if (distString.isNotEmpty()) {
                    DistanceAB<String>(table[0][j], row[0], distString.toDouble())
                } else {
                    null
                }
            }
        }.filterNotNull()
    }
}
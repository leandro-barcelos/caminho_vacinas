import org.graphstream.algorithm.Dijkstra
import org.graphstream.graph.Edge
import org.graphstream.graph.Graph
import org.graphstream.graph.Node
import org.graphstream.graph.implementations.SingleGraph

class SRSGraph (idSRS: String, distanceABList: List<DistanceAB<String>>) {
    private val graph: Graph

    // Inicializa grafo
    init {
        graph = SingleGraph(idSRS)
        graph.setAttribute("ui.stylesheet", "url('src/main/resources/graphStyle.css')")

        for (rota in distanceABList) {

            if (!graph.any { n -> n.id == rota.a })
                graph.addNode<Node>(rota.a)?.apply { addAttribute("ui.label", rota.a) }
            if (!graph.any { n -> n.id == rota.b })
                graph.addNode<Node>(rota.b)?.apply { addAttribute("ui.label", rota.b) }

            val id = rota.a + "<->" + rota.b
            val edge = graph.addEdge<Edge>(id, rota.a, rota.b)

            edge.apply {
                setAttribute("distance", rota.distance)
                setAttribute("ui.label", "${rota.distance / 1000}km")
            }
        }
    }


    private fun addNewEdge(dist: DistanceAB<Node>, directional: Boolean) {
        var id: String

        if (!directional) {
            id = dist.a.id + "<->" + dist.b.id
            val edge = graph.addEdge<Edge>(id, dist.a, dist.b)
            edge.setAttribute("distance", dist.distance)
            edge.setAttribute("ui.label", "${dist.distance / 1000} km")
        } else {
            id = dist.a.id + "->" + dist.b.id
            var edge = graph.addEdge<Edge>(id, dist.a, dist.b, true)
            edge.setAttribute("distance", dist.distance)
            edge.setAttribute("ui.label", "${dist.distance / 1000} km")

            id = dist.b.id + "->" + dist.a.id
            edge = graph.addEdge(id, dist.b, dist.a, true)
            edge.setAttribute("distance", dist.distance)
            edge.setAttribute("ui.label", "${dist.distance / 1000} km")
        }
    }

    fun printEulerianPath() {

        // PASSO 1 - verificar se o grafo G é euleriano; caso positivo, ir para passo 6

        val isEulerian: Boolean = graph.all {it.degree % 2 == 0}

        if (!isEulerian) {
            // PASSO 2 - determinar o conjunto I de vértices de grau ı́mpar em G

            val oddNodes = graph.filter { it.degree % 2 != 0 }

            // PASSO 3 - estabelecer as distâncias dij para i, j ∈ I, i < j

            val distOddNodes: Array<Array<DistanceAB<Node>>>  = Array(oddNodes.size) {
                Array(oddNodes.size) {
                        i -> DistanceAB(graph.getNode(i), graph.getNode(i), Double.POSITIVE_INFINITY)
                }
            }

            val d = Dijkstra(Dijkstra.Element.EDGE, null, "distance")
            d.init(graph)

            for (i in oddNodes.indices) {
                val n = oddNodes[i]

                d.setSource(n)
                d.compute()

                for (j in oddNodes.indices) {
                    val n2 = oddNodes[j]

                    distOddNodes[i][j] = if (n == n2)
                        DistanceAB(n, n, Double.POSITIVE_INFINITY)
                    else
                        DistanceAB(n, n2, d.getPathLength(n2))
                }
            }

            // PASSO 4 - seja D(I) = [dij ] a matriz obtida, fazer dii = ∞ e aplicar a
            // D(I) o Algoritmo Húngaro;

            val distMatrix = distOddNodes.map { row -> row.map {it.distance}.toDoubleArray()}.toTypedArray()

            val outHungarianAlg = HungarianAlg(distMatrix).hungarianAlg()

            // PASSO 5 - para cada alocação (k, l) feita pelo algoritmo húngaro, acrescentar
            //ao grafo a aresta (k, l) de valor dkl ;

            for (i in 0 until outHungarianAlg.size / 2) {
                val dist = distOddNodes[outHungarianAlg[i][0]][outHungarianAlg[i][1]]
                val id = dist.a.id + "<->" + dist.b.id

                if (graph.getEdge<Edge>(id) == null) {
                    addNewEdge(dist, false)
                } else {
                    graph.removeEdge<Edge>(id)
                    addNewEdge(dist, true)
                }
            }
        }


        // PASSO 6 - aplicar um algoritmo de busca de percurso euleriano.

        HierholzerAlg().findEulerianPath(graph, "uberlandia")

//        graph.display()
    }
}


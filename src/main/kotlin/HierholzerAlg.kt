import org.graphstream.graph.Edge
import org.graphstream.graph.Graph
import org.graphstream.graph.Node
import java.util.ArrayDeque
import java.util.HashMap
import java.util.Stack

class HierholzerAlg {
     fun findEulerianPath(graph: Graph, startId: String) {
        val edges = HashMap<Int, Int>()

        graph.forEachIndexed { i, node -> edges[i] = node.degree}

        val currPath = Stack<Int>()
        val circuit = ArrayList<Int>()

         val firstVert = graph.getNode<Node>(startId).index

        currPath.push(firstVert)
        var currV = firstVert

        while (currPath.isNotEmpty()) {
            if (edges[currV]!! != 0) {

                currPath.push(currV)

                val nextV = graph.getNode<Node>(currV)
                    .getEdge<Edge>(graph.getNode<Node>(currV).degree - 1)
                    .getOpposite<Node>(graph.getNode(currV))
                    .index


                edges[currV] = edges[currV]!! - 1
                edges[nextV] = edges[nextV]!! - 1
                graph.removeEdge<Edge>(graph.getNode<Node>(currV).getEdge(graph.getNode<Node>(currV).degree - 1))

                currV = nextV
            } else {
                circuit.add(currV)
                currV = currPath.pop()
            }
        }

        for (i in (circuit.size - 1) downTo 0) {
            print(graph.getNode<Node?>(circuit[i]).id)

            if (i != 0)
                print(" -> ")
        }
    }
}
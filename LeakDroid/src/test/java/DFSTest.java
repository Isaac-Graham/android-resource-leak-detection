import org.apache.log4j.Logger;
import org.junit.Test;
import soot.Unit;
import soot.toolkits.graph.UnitGraph;

import java.util.*;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/1 17:26
 */
public class DFSTest {
    private static final Logger logger = Logger.getLogger(DFSTest.class);

    static class Node {
        int name;
        List<Node> next;

        Node(int name) {
            this.name = name;
            this.next = new ArrayList<>();
        }

        void addNeighbor(Node neighbor) {
            this.next.add(neighbor);
        }

        public String toString() {
            return name + "";
        }
    }

    public static int[][] edges = new int[][]{
            {0, 1}, {0, 2},
            {1, 3}, {1, 4},
            {2, 5}, {2, 6},
            {3, 7},
            {4, 5},
            {5, 6},
            {6, 8},
            {7, 4}
    };

    private static Node[] buildGraph() {
        Node[] nodes = new Node[9];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(i);
        }

        for (int i = 0; i < edges.length; i++) {
            int from = edges[i][0];
            int to = edges[i][1];
            nodes[from].addNeighbor(nodes[to]);
        }
        return nodes;
    }

    @Test
    public void test01() {
        Node[] nodes = buildGraph();
        DFS(nodes[0], nodes[4]);
    }

    private static boolean DFS(Node start, Node end) {
        Stack<Node> mainStack = new Stack<>();
        Stack<Stack<Node>> assistStack = new Stack<>();
        List<Integer> stackEle = new ArrayList<>();
        mainStack.add(start);
        assistStack.add(new Stack<>());
        stackEle.add(start.name);
        start.next.forEach(neighbor -> {
            assistStack.peek().add(neighbor);
        });


        while (!mainStack.empty()) {
            if (mainStack.peek().name == end.name) {
                StringBuilder res = new StringBuilder();
                for (Integer integer : stackEle) {
                    res.append(integer).append(" -> ");
                }
                logger.info(res.toString());
            }
            if (mainStack.peek().name == 7) {
                mainStack.pop();
                stackEle.remove(stackEle.size() - 1);
                assistStack.pop();
            }
            Stack<Node> assistStackTop = assistStack.peek();
            if (!assistStackTop.empty()) {
                Node nextNode = assistStackTop.pop();
                mainStack.push(nextNode);
                stackEle.add(nextNode.name);
                assistStack.add(new Stack<>());
                nextNode.next.forEach(neighbor -> {
                    assistStack.peek().add(neighbor);
                });
            } else {
                mainStack.pop();
                stackEle.remove(stackEle.size() - 1);
                assistStack.pop();
            }
        }
        return true;
    }
}

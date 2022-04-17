package com.example.pacman_hra.game.path;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Dijkstra {

    private int endX, endY;
    private final int maxX, maxY;

    private final ArrayList<ArrayList<Node>> graph;
    private final PriorityQueue<Node> defaultUnvisitedNodes = new PriorityQueue<>();
    private PriorityQueue<Node> unVisitedNodes = new PriorityQueue<>();

    public Dijkstra(ArrayList<ArrayList<Integer>> map)
    {
        graph = new ArrayList<>();

        // predpripravime uzly
        for (int y = 0; y < map.size(); ++y)
        {
            graph.add(new ArrayList<>());

            for (int x = 0; x < map.get(y).size(); ++x)
            {
                Node node = new Node(x, y);
                node.isCrossable = (map.get(y).get(x) != 0);
                graph.get(y).add(node);
            }
        }

        maxX = map.get(0).size();
        maxY = map.size();

        // vytvorime hrany z uzlov
        for (int y = 0; y < graph.size(); ++y)
        {
            for (int x = 0; x < graph.get(y).size(); ++x)
            {
                if (map.get(y).get(x) == 0)
                    continue;

                if (!defaultUnvisitedNodes.contains(getNode(x, y)))
                    defaultUnvisitedNodes.add(getNode(x, y));

                Node[] sourceAdjacent = getNode(x, y).adjacentNodes;

                int directions = 0;

                if (isValid(x, y + 1))
                    sourceAdjacent[directions++] = (getNode(x, y + 1));
                if (isValid(x, y - 1))
                    sourceAdjacent[directions++] = (getNode(x, y - 1));
                if (isValid(x + 1, y))
                    sourceAdjacent[directions++] = (getNode(x + 1, y));
                if (isValid(x - 1, y))
                    sourceAdjacent[directions] = (getNode(x - 1, y));
            }
        }
    }

    private void resetNodes()
    {
        for (Node node : defaultUnvisitedNodes)
        {
            node.total_cost = 9999;
            node.previousPath = null;
        }

        unVisitedNodes = new PriorityQueue<>();
    }

    public void run(int pos_x, int pos_y, int target_x, int target_y)
    {
        // priprava
        endX = target_x; endY = target_y;

        resetNodes();

        Node startingNode = graph.get(pos_y).get(pos_x);
        Node targetNode = graph.get(endY).get(endX);
        Node currentNode; // nenavstiveny vrchol s najlacnejsou hranou

        startingNode.total_cost = 0;

        unVisitedNodes.add(startingNode);

        // algoritmus
        while ((currentNode = unVisitedNodes.poll()) != null)
        {
            if (currentNode == targetNode)
            {
                break; // koncovy bod dosiahnuty
            }
            for (int i = 0; i < currentNode.adjacentNodes.length; ++i)
            {
                if (currentNode.adjacentNodes[i] != null)
                {
                    int adjacentCost = currentNode.total_cost + currentNode.adjacentNodes[i].cost + currentNode.adjacentNodes[i].getHeuristic(endX, endY);

                    if (adjacentCost < currentNode.adjacentNodes[i].total_cost)
                    {
                        currentNode.adjacentNodes[i].total_cost = adjacentCost;
                        currentNode.adjacentNodes[i].previousPath = currentNode;

                        unVisitedNodes.add(currentNode.adjacentNodes[i]);
                    }
                }
            }
        }
    }

    public ArrayList<Node> getOutput()
    {
        ArrayList<Node> output = new ArrayList<>();
        Node targetNode = getNode(endX, endY);

        while (targetNode != null)
        {
            output.add(targetNode);
            targetNode = targetNode.previousPath;
        }

        return output;
    }

    private boolean isValid(int x, int y)
    {
        return !(y < 0 || y >= maxY || x < 0 || x >= maxX || !getNode(x, y).isCrossable);
    }

    private Node getNode(int x, int y)
    {
        return graph.get(y).get(x);
    }
}

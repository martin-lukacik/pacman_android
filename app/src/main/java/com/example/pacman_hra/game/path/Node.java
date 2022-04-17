package com.example.pacman_hra.game.path;

public class Node implements Comparable<Node> {

    public int x;
    public int y;
    public boolean isCrossable;

    public int total_cost;
    public int cost = 1;

    public Node[] adjacentNodes = new Node[4];

    public Node previousPath = null;

    public Node(int x, int y)
    {
        this.x = x;
        this.y = y;

        total_cost = 9999;
    }

    public int getHeuristic(int target_x, int target_y)
    {
        return (int)Math.ceil(Math.sqrt(Math.pow(target_x - x, 2) + Math.pow(target_y - y, 2)));
    }

    // pouzite v priority queue
    @Override
    public int compareTo(Node o)
    {
        return (total_cost - o.total_cost);
    }
}

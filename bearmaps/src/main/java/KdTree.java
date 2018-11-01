import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class KdTree {
    GraphDB graph;
    KDNode root;
    KDNode nmin;
    double ndist;

    KdTree(GraphDB g) {
        this.graph = g;
        ArrayList<GraphDB.Vertex> ls = new ArrayList<>(graph.allVertexes.values());
        root = constructHelper(ls, true);
    }

    public KDNode constructHelper(List<GraphDB.Vertex> ls, boolean comparex) {
        if (ls.size() == 0) {
            return null;
        }
        sort(ls, comparex);

        List<GraphDB.Vertex> left = ls.subList(0, ls.size() / 2);
        List<GraphDB.Vertex> right = ls.subList((ls.size()) / 2 + 1, ls.size());
        int mid = (ls.size()) / 2;

        KDNode mv = new KDNode(ls.get(mid),
                constructHelper(left, !comparex),
                constructHelper(right, !comparex));
        return mv;
    }

    private List<GraphDB.Vertex> sort(List<GraphDB.Vertex> vs, boolean comparex) {
        if (comparex) {
            Collections.sort(vs, (GraphDB.Vertex v1, GraphDB.Vertex v2)
                -> Double.compare(GraphDB.projectToX(v1.getLon(), v1.getLat()),
                    GraphDB.projectToX(v2.getLon(), v2.getLat())));
            return vs;
        }
        Collections.sort(vs, (GraphDB.Vertex v1, GraphDB.Vertex v2)
            -> Double.compare(GraphDB.projectToY(v1.getLon(), v1.getLat()),
                GraphDB.projectToY(v2.getLon(), v2.getLat())));
        return vs;

    }


    public KDNode nearestNeighbor(double lon, double lat) {
        if (root == null) {
            return null;
        }

        double xu = GraphDB.projectToX(lon, lat);
        double yu = GraphDB.projectToY(lon, lat);

        nmin = root;
        ndist = nmin.disTo(xu, yu);
        nearestPoint(root, xu, yu, true);

        return nmin;
    }



    private void nearestPoint(KDNode curr, double xu,
                              double yu, boolean comparex) {
        double pivot;
        double tocompare;

        if (curr == null) {
            return;
        }
        double dist = curr.disTo(xu, yu);

        if (dist < ndist) {
            nmin = curr;
            ndist = dist;
        }

        if (comparex) {
            pivot = GraphDB.projectToX(curr.node.getLon(), curr.node.getLat());
            tocompare = xu;
        } else {
            pivot = GraphDB.projectToY(curr.node.getLon(), curr.node.getLat());
            tocompare = yu;
        }
        if (tocompare < pivot) {
            nearestPoint(curr.left, xu, yu, !comparex);

            if (pivot - tocompare <= ndist) {
                nearestPoint(curr.right, xu, yu, !comparex);
            }
        } else {
            nearestPoint(curr.right, xu, yu, !comparex);
            if (tocompare - pivot <= ndist) {
                nearestPoint(curr.left, xu, yu, !comparex);
            }
        }


    }

    class KDNode {
        GraphDB.Vertex node;
        KDNode left;
        KDNode right;
        int depthN;

        KDNode() {
            this.node = null;
            this.left = null;
            this.right = null;
            this.depthN = 0;

        }

        KDNode(GraphDB.Vertex node, KDNode left, KDNode right) {
            this.node = node;
            this.left = left;
            this.right = right;


        }

        private double disTo(double xu, double yu) {
            double x = GraphDB.projectToX(this.node.getLon(), this.node.getLat());
            double y = GraphDB.projectToY(this.node.getLon(), this.node.getLat());

            return Math.sqrt(Math.pow(x - xu, 2) + Math.pow(y - yu, 2));
        }
    }
}

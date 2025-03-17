package structure;

import config.ParameterTest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

/**
 * @author: hehe
 * @create: 2024-06-05 20:32
 * @Description:
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class QuadtreeNode implements Serializable {
    private float xMin, yMin, xMax, yMax;
//    private List<Point> points;
    private boolean divided;
    private QuadtreeNode[] children;
    private Map<Integer, Integer> datasetCounts;
    private int layer = 0;


    @Override
    public String toString() {
        return "QuadtreeNode{" +
                "xMin=" + xMin +
                ", yMin=" + yMin +
                ", xMax=" + xMax +
                ", yMax=" + yMax +
                ", divided=" + divided +
                ", children=" + Arrays.toString(children) +
                ", datasetCounts=" + datasetCounts +
                ", layer=" + layer +
                '}';
    }

//    public QuadtreeNode(double xMin, double yMin, double xMax, double yMax, int layer) {
//        this.xMin = xMin;
//        this.yMin = yMin;
//        this.xMax = xMax;
//        this.yMax = yMax;
////        this.points = new ArrayList<>();
//        this.divided = false;
//        this.datasetCounts = new HashMap<>();
//        this.layer = layer;
//    }

    public QuadtreeNode(float xMin, float yMin, float xMax, float yMax, int layer) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
//        this.points = new ArrayList<>();
        this.divided = false;
        this.datasetCounts = new HashMap<>();
        this.layer = layer;
    }

    private void subdivide() {
        float xMid = (xMin + xMax) / 2;
        float yMid = (yMin + yMax) / 2;

        children = new QuadtreeNode[4];
        children[0] = new QuadtreeNode(xMin, yMin, xMid, yMid, layer + 1); // SW
        children[1] = new QuadtreeNode(xMid, yMin, xMax, yMid, layer + 1); // SE
        children[2] = new QuadtreeNode(xMin, yMid, xMid, yMax, layer + 1); // NW
        children[3] = new QuadtreeNode(xMid, yMid, xMax, yMax, layer + 1); // NE

        divided = true;
    }

    public void insert(Point point) {
        if (layer <= ParameterTest.quadTreeMaxLayer) {
            if (contains(point)) {
                datasetCounts.put(point.getDatasetId(), datasetCounts.getOrDefault(point.getDatasetId(), 0)+1);
                if (layer+1 < ParameterTest.quadTreeMaxLayer&&!divided) {
                    subdivide();
                }
                if(children!=null){
                    for (QuadtreeNode child : children) {
                        child.insert(point);
                    }
                }
            }
        }
    }


    private boolean contains(Point point) {
        return (point.x >= xMin && point.x <= xMax && point.y >= yMin && point.y <= yMax);
    }

    public void query(double xMin, double yMin, double xMax, double yMax, List<QuadtreeNode> found) {
        if(isContainedByRange(xMin, yMin, xMax, yMax)||(this.layer+1== ParameterTest.quadTreeMaxLayer)&&intersects(xMin, yMin, xMax, yMax)){
            found.add(this);
        }else {
            if (divided) {
                for (QuadtreeNode child : children) {
                    child.query(xMin, yMin, xMax, yMax, found);
                }
            }
        }
    }

    private boolean intersects(double xMin, double yMin, double xMax, double yMax) {
        return !(xMax < this.xMin || xMin > this.xMax || yMax < this.yMin || yMin > this.yMax);
    }

    public Map<Integer, Integer> getDatasetCounts() {
        return datasetCounts;
    }

    private boolean isContainedByRange(double xMin, double yMin, double xMax, double yMax) {
        return this.xMin >= xMin && this.yMin >= yMin && this.xMax <= xMax && this.yMax <= yMax;
    }

    /**
     * 查询指定范围内数据集点数最多的数据集
     *
     * @param xMin
     * @param yMin
     * @param xMax
     * @param yMax
     * @return
     */
    public Map<Integer, Integer> queryMostPoints(double xMin, double yMin, double xMax, double yMax) {
        Map<Integer, Integer> counts = new HashMap<>();
        List<QuadtreeNode> nodes = new ArrayList<>();
        query(xMin, yMin, xMax, yMax, nodes);

        for (QuadtreeNode node : nodes) {
            Map<Integer, Integer> nodeCounts = node.getDatasetCounts();
            for (Map.Entry<Integer, Integer> entry : nodeCounts.entrySet()) {
                counts.put(entry.getKey(), counts.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }

        return counts;
    }


    /**
     * 判断点是否在多边形内
     * @param point 检测点
     * @param pts   多边形的顶点
     * @return      点在多边形内返回true,否则返回false
     */
//    public static boolean IsPtInPoly(Point2D.Double point, List<Point2D.Double> pts){
//
//        int N = pts.size();
//        boolean boundOrVertex = true; //如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
//        int intersectCount = 0;//cross points count of x
//        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
//        Point2D.Double p1, p2;//neighbour bound vertices
//        Point2D.Double p = point; //当前点
//
//        p1 = pts.get(0);//left vertex
//        for(int i = 1; i <= N; ++i){//check all rays
//            if(p.equals(p1)){
//                return boundOrVertex;//p is an vertex
//            }
//
//            p2 = pts.get(i % N);//right vertex
//            if(p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)){//ray is outside of our interests
//                p1 = p2;
//                continue;//next ray left point
//            }
//
//            if(p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)){//ray is crossing over by the algorithm (common part of)
//                if(p.y <= Math.max(p1.y, p2.y)){//x is before of ray
//                    if(p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)){//overlies on a horizontal ray
//                        return boundOrVertex;
//                    }
//
//                    if(p1.y == p2.y){//ray is vertical
//                        if(p1.y == p.y){//overlies on a vertical ray
//                            return boundOrVertex;
//                        }else{//before ray
//                            ++intersectCount;
//                        }
//                    }else{//cross point on the left side
//                        double xinters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;//cross point of y
//                        if(Math.abs(p.y - xinters) < precision){//overlies on a ray
//                            return boundOrVertex;
//                        }
//                        if(p.y < xinters){//before ray
//                            ++intersectCount;
//                        }
//                    }
//                }
//            }else{//special case when ray is crossing through the vertex
//                if(p.x == p2.x && p.y <= p2.y){//p crossing over p2
//                    Point2D.Double p3 = pts.get((i+1) % N); //next vertex
//                    if(p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)){//p.x lies between p1.x & p3.x
//                        ++intersectCount;
//                    }else{
//                        intersectCount += 2;
//                    }
//                }
//            }
//            p1 = p2;//next ray left point
//        }
//
//        if(intersectCount % 2 == 0){//偶数在多边形外
//            return false;
//        } else { //奇数在多边形内
//            return true;
//        }
//
//    }

}

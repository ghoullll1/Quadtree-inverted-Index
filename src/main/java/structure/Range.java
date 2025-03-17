package structure;

import com.github.davidmoten.rtree.geometry.Geometries;
import utils.Time;

import java.util.Set;

/**
 * @author: hehe
 * @create: 2024-06-07 12:44
 * @Description:
 */
public class Range{
    private Rectangle MBR;// 最小边界矩形

    /**
     * @param lon1 经度下限
     * @param lat1 纬度下限
     * @param lon2 经度上限
     * @param lat2 纬度上限
     */
    public Range(double lon1, double lat1, double lon2, double lat2) {
//        MBR = Geometries.rectangleGeographic(lon1, lat1, lon2, lat2);
    }

    /**
     * 计算指定数据集中在查询范围内的空间数据点个数
     *
     * @param dataset 指定数据集的空间数据点集合
     * @return int 查询范围内的空间数据点个数
     */
    public int intersects(Set<Point> dataset) {
        // 遍历数据集中所有空间数据点，如果空间数据点在空间范围中，则计数加一
        int sizeOfIntersection = 0;
        for (Point point : dataset) {
//            sizeOfIntersection = MBR.intersects(point) ? sizeOfIntersection + 1 : sizeOfIntersection;
        }
        return sizeOfIntersection;
    }

    public Rectangle getMBR() {
        return MBR;
    }

    public void setMBR(Rectangle MBR) {
        this.MBR = MBR;
    }
}

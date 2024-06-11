package query;

import com.github.davidmoten.rtree.geometry.Point;
import structure.Range;
import utils.Time;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: hehe
 * @create: 2024-06-07 12:42
 * @Description:
 */
public class BasicSearch {
    /**
     * 遍历数据集后返回排序结果
     *
     * @param range     查询范围
     * @param warehouse 数据集仓库
     * @param record    用于实验参数和结果记录
     * @param topk      结果最大数量
     * @return {@link List}<{@link Integer}> 交集数据集排序结果
     */
//    public static List querySimple(Range range, Warehouse warehouse, Record record, int topk) {
//        Time time = new Time();// 创建 Time 对象用于记录评分用时
////        time.start();// 开始记录评分用时
//        time.startSum();
//
////        Map<Integer, Integer> mapOfSizeOfIntersection = Score.simple(warehouse.getPoints_datasets(), range);// 遍历数据点，计算数据集编号和数据集在查询范围内的数据点个数
//
//        ResultList resultList = new ResultList(topk);
//        for (int start = 0; start < warehouse.getNum_datasets(); start += 10000) {
//            int end = start + 10000;
//            end = Math.min(end, warehouse.getNum_datasets());
//            time.start();
//            Map<Integer, Set<Point>> datasets = warehouse.getPointsIO(start, end);
//            time.end();
//            for(Map.Entry<Integer, Set<Point>> entry:datasets.entrySet()){
//                int id_dataset = entry.getKey();
//                Set<Point> dataset = entry.getValue();
//                resultList.add(id_dataset,Score.simple(dataset,range));
//            }
//        }
//
//        List<Integer> result = resultList.get();
//
//        record.add("线性方案IO时间/毫秒", time.getSumOfTimeOfSpend());
//
//        return result;
//    }

}

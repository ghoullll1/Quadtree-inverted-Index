import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import config.ParameterTest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.lucene.util.RamUsageEstimator;
import query.BasicSearch;
import query.QuadTreeSearch;
import structure.Quadtree;
import structure.QueryRange;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author: hehe
 * @create: 2024-06-07 13:10
 * @Description:
 */
public class QueryTest {
    public static void main(String[] args) {
        basicSearch(10,30,"1000");

//        generateQueryRangeWithTargetArea(100,2000,"2000");
//        generateQueryRangeWithScope(100,"20000-40000range");

//        ArrayList<QueryRange> ranges = readQueryRange(100);
//        testQuery10WithQueryTime(6);

//        Quadtree quadtree = new Quadtree(ParameterTest.xMin, ParameterTest.yMin, ParameterTest.xMax, ParameterTest.yMax);
//        QuadTreeSearch quadTreeSearch = new QuadTreeSearch(quadtree);
//        quadTreeSearch.createQuadtreeIndex();
//        getSizeOfIndex(quadTreeSearch.getQuadtree().getRoot());
//        quadTreeSearch.saveQuadtreeToIndexFile();
//        quadTreeSearch.readQuadtreeToIndexFile(10);
//        quadTreeSearch.query(-20, -20, 10, 10, 10);

//        quadTreeSearch.saveQuadtreeToFile();
//        quadTreeGridQueryWithQueryTime(10,"1000");
//        quadTreeGridQueryWithAccuracy(100,"1000");
//        List<Point2D.Double> pts = PolygonRange.generateRandomPolygon(5);
//        Point2D.Double point = new Point2D.Double(119.245584,39.068196);
//        long start = System.nanoTime();
//        for (int i = 0; i < 10; i++) {
//            boolean b = QuadtreeNode.IsPtInPoly(point, pts);
//            System.out.println(b);
//        }
//        long end = System.nanoTime();
//        System.out.println((end-start)/10000000.0);
    }

    /**
     * @Description: 基础查询方案, 查询范围为n个, k个查询结果, 并将正确结果存入文件
     * @param n : 查询范围个数 0~100
     * @param k : 数据集仓库的选择
     * @param name : 查询范围的大小和文件名
     * @return void
     */

    private static void basicSearch(int n,int k,String name) {
        BasicSearch basicSearch = new BasicSearch(1);
        HashMap<Integer, QueryRange> ranges = readQueryRange(name);
        HashMap<Integer, List<Integer>> correctMap = new HashMap<>();
        HashMap<Integer, Double> res = new HashMap<>();
        for (int i = 0; i < n; i++) {
            long start = System.nanoTime();
            List<Integer> topkList = basicSearch.query(ranges.get(i), k);
            long end = System.nanoTime();
            res.put(i, (end - start) / 1000000.0);
            correctMap.put(i, topkList);
        }
        List<Double> values = new ArrayList<>(res.values());

        // 找到最大值和最小值
        double maxValue = Collections.max(values);
        double minValue = Collections.min(values);

        // 移除最大值和最小值
        values.remove(maxValue);
        values.remove(minValue);

        // 计算剩余值的平均值
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }

        double average = sum / values.size();
        System.out.println("Average: " + average);
//        String jsonString = JSON.toJSONString(correctMap);
//        try (FileWriter file = new FileWriter(ParameterTest.correctResultPath + name + ".json")) {
//            file.write(jsonString);
//            System.out.println("Serialized data is saved in " + ParameterTest.correctResultPath + name + ".json");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private static void quadTreeGridQueryWithQueryTime(int n,String name){
        Quadtree quadtree = new Quadtree(ParameterTest.xMin, ParameterTest.yMin, ParameterTest.xMax, ParameterTest.yMax);
        QuadTreeSearch quadTreeSearch = new QuadTreeSearch(quadtree);
        ArrayList<HashMap<Integer, Double>> resList = new ArrayList<>();
        HashMap<Integer, QueryRange> ranges = readQueryRange(name);
        for (int i = 0; i < n; i++) {
            HashMap<Integer, Double> res = new HashMap<>();
            QueryRange queryRange = ranges.get(i);
            double x1 = queryRange.getX1();
            double x2 = queryRange.getX2();
            double y1 = queryRange.getY1();
            double y2 = queryRange.getY2();
            System.out.println(i + ":{" + "x1:" + x1 + " y1:" + y1 + " x2:" + x2 + " y2:" + y2 + "}");
            for (int j = 7; j < 13; j++) {
                quadTreeSearch.readQuadtreeToIndexFile(j);
                ParameterTest.quadTreeMaxLayer = j;
                long start = System.nanoTime();
                List<Integer> query = quadTreeSearch.query(x1, y1, x2, y2, 110);
                long end = System.nanoTime();
                res.put(j, (end - start) / 1000000.0);
                System.out.println("queryRers"+query);
            }
            resList.add(res);
        }
        HashMap<Integer, Double> avgQueryTime = avgQueryTime(resList);
        saveQueryTimeRes(avgQueryTime);
    }

    private static void quadTreeGridQueryWithAccuracy(int n,String name){
        Quadtree quadtree = new Quadtree(ParameterTest.xMin, ParameterTest.yMin, ParameterTest.xMax, ParameterTest.yMax);
        QuadTreeSearch quadTreeSearch = new QuadTreeSearch(quadtree);
        ArrayList<HashMap<Integer, Double>> resList = new ArrayList<>();
        HashMap<Integer, QueryRange> ranges = readQueryRange(name);
        HashMap<Integer, List<Integer>> correctRes = readCorrectResult("num30000-1000-top30");
        for (int i = 0; i < n; i++) {
            HashMap<Integer, Double> res = new HashMap<>();
            QueryRange queryRange = ranges.get(i);
            double x1 = queryRange.getX1();
            double x2 = queryRange.getX2();
            double y1 = queryRange.getY1();
            double y2 = queryRange.getY2();
            System.out.println(i + ":{" + "x1:" + x1 + " y1:" + y1 + " x2:" + x2 + " y2:" + y2 + "}");
            for (int j = 8; j < 13; j++) {
                ParameterTest.quadTreeMaxLayer = j;
                quadTreeSearch.readQuadtreeToIndexFile(j);
                List<Integer> queryRes = quadTreeSearch.query(x1, y1, x2, y2, 30);
                System.out.println("queryRes"+queryRes);
                double accuracy = getAccuracy(queryRes, correctRes.get(i));
                System.out.println("accuracy"+accuracy);
                res.put(j, accuracy);
            }
            resList.add(res);
        }
        HashMap<Integer, Double> avgAccuracy = avgAccuracy(resList);
        saveAccuracyRes(avgAccuracy);
    }

    private static void generateQueryRangeWithTargetArea(int num,double area,String name) {
//        ArrayList<QueryRange> rangeList  = new ArrayList<>();
        HashMap<Integer, QueryRange> rangeMap = new HashMap<>();
        for (int i = 0; i < num; i++) {
            System.out.println(i);
            rangeMap.put(i, new QueryRange(area));
        }
        saveQueryRange(rangeMap,name);
    }

    private static void generateQueryRangeWithScope(int num ,String name) {
//        ArrayList<QueryRange> rangeList  = new ArrayList<>();
        HashMap<Integer, QueryRange> rangeMap = new HashMap<>();
        for (int i = 0; i < num; i++) {
            rangeMap.put(i, new QueryRange(20000,40000));
        }
        saveQueryRange(rangeMap,name);
    }

    private static void saveQueryRange(HashMap<Integer, QueryRange> rangeMap,String name) {
        String jsonString = JSON.toJSONString(rangeMap);
        try (FileWriter file = new FileWriter(ParameterTest.queryRangeFilePath + name + ".json")) {
            file.write(jsonString);
            System.out.println("Serialized data is saved in " + ParameterTest.queryRangeFilePath + name + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<Integer, QueryRange> readQueryRange(String name) {
        HashMap<Integer, QueryRange> rangeMap = new HashMap<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(ParameterTest.queryRangeFilePath + name + ".json")));
            rangeMap = JSON.parseObject(content, new TypeReference<HashMap<Integer, QueryRange>>() {});
            System.out.println("Deserialized data from " + ParameterTest.queryRangeFilePath + name + ".json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rangeMap;
    }

    private static double getAccuracy(List<Integer> queryRes,List<Integer> correctList) {
        if (correctList.isEmpty()&&queryRes.isEmpty()){
            return 1.0;
        }else if(correctList.isEmpty()&&!queryRes.isEmpty()){
            return 0.0;
        }else{
            long count = queryRes.stream().filter(correctList::contains).count();
            return count * 1.0 / correctList.size();
        }
//        return count * 1.0 / queryRes.size();
    }

    private static HashMap<Integer, Double> avgQueryTime(ArrayList<HashMap<Integer, Double>> resList) {
        HashMap<Integer, ArrayList<Double>> allValues = new HashMap<>();

        // 收集每个key的所有值
        for (HashMap<Integer, Double> map : resList) {
            map.forEach((k, v) -> {
                allValues.computeIfAbsent(k, x -> new ArrayList<>()).add(v);
            });
        }

        HashMap<Integer, Double> avgMap = new HashMap<>();

        // 计算去除最大值和最小值后的平均值
        allValues.forEach((k, vList) -> {
            if (vList.size() > 2) { // 如果元素多于2个才去除最大值和最小值
                Collections.sort(vList);
                vList.remove(vList.size() - 1); // 去除最大值
                vList.remove(0); // 去除最小值
            }
            double sum = 0.0;
            for (double v : vList) {
                sum += v;
            }
            avgMap.put(k, sum / vList.size());
        });

        return avgMap;
    }

    private static HashMap<Integer, Double> avgAccuracy(ArrayList<HashMap<Integer, Double>> resList) {
        HashMap<Integer, ArrayList<Double>> allValues = new HashMap<>();

        // 收集每个key的所有值
        for (HashMap<Integer, Double> map : resList) {
            map.forEach((k, v) -> {
                allValues.computeIfAbsent(k, x -> new ArrayList<>()).add(v);
            });
        }

        HashMap<Integer, Double> avgMap = new HashMap<>();

        // 计算去除最大值和最小值后的平均值
        allValues.forEach((k, vList) -> {
            if (vList.size() > 2) { // 如果元素多于2个才去除最大值和最小值
                Collections.sort(vList);
//                vList.remove(vList.size() - 1); // 去除最大值
//                vList.remove(0); // 去除最小值
            }
            double sum = 0.0;
            for (double v : vList) {
                sum += v;
            }
            avgMap.put(k, sum / vList.size());
        });

        return avgMap;
    }

    private static void saveQueryTimeRes(Map<Integer, Double> data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = LocalDate.now().format(formatter);
        String basePath = ParameterTest.resultQueryTimePath + date;
        String filePath = basePath + ".csv";
        // 检查当天文件是否存在,存在就添加一个-数字后缀
        int count = 1;
        while (new File(filePath).exists()) {
            filePath = basePath + "-" + count + ".csv";
            count++;
        }

        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Layer", "Time(ms)"))) {

            for (Map.Entry<Integer, Double> entry : data.entrySet()) {
                csvPrinter.printRecord(entry.getKey(), entry.getValue());
            }

            System.out.println("CSV 文件已保存到: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveAccuracyRes(Map<Integer, Double> data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = LocalDate.now().format(formatter);
        String basePath = ParameterTest.resultAccuracyPath + date;
        String filePath = basePath + ".csv";
        // 检查当天文件是否存在,存在就添加一个-数字后缀
        int count = 1;
        while (new File(filePath).exists()) {
            filePath = basePath + "-" + count + ".csv";
            count++;
        }

        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Layer", "Accuracy"))) {

            for (Map.Entry<Integer, Double> entry : data.entrySet()) {
                csvPrinter.printRecord(entry.getKey(), entry.getValue());
            }

            System.out.println("CSV 文件已保存到: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static HashMap<Integer, List<Integer>> readCorrectResult(String name){
        HashMap<Integer, List<Integer>> correctMap = new HashMap<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(ParameterTest.correctResultPath + name + ".json")));
            correctMap = JSON.parseObject(content, new TypeReference<HashMap<Integer, List<Integer>>>() {});
            System.out.println("Deserialized data from " + ParameterTest.correctResultPath + name + ".json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return correctMap;
    }

    private static void getSizeOfIndex(Object obj){
        String s = RamUsageEstimator.humanSizeOf(obj);
        System.out.println("index size:"+s);
    }
}

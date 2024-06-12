import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import config.ParameterTest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
//        generateQueryRange(100);

//        ArrayList<QueryRange> ranges = readQueryRange(100);
//        testQuery10WithQueryTime(6);

//        Quadtree quadtree = new Quadtree(Parameter.xMin, Parameter.yMin, Parameter.xMax, Parameter.yMax);
//        QuadTreeSearch quadTreeSearch = new QuadTreeSearch(quadtree);
//        quadTreeSearch.createQuadtreeIndex();
//        quadTreeSearch.saveQuadtreeToIndexFile();
//        quadTreeSearch.readQuadtreeToIndexFile(10);
//        quadTreeSearch.query(-20, -20, 10, 10, 10);

//        quadTreeSearch.saveQuadtreeToFile();
        testQuery100WithQueryTime();
//        testQuery10WithAccuracy();
    }

    private static void testQuery10WithQueryTime(int layer) {
        Quadtree quadtree = new Quadtree(ParameterTest.xMin, ParameterTest.yMin, ParameterTest.xMax, ParameterTest.yMax);
        QuadTreeSearch quadTreeSearch = new QuadTreeSearch(quadtree);
        ArrayList<HashMap<Integer, Double>> resList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<Integer, Double> res = new HashMap<>();
//                quadTreeSearch.readQuadTreeFromFile(j);
            quadTreeSearch.readQuadtreeToIndexFile(layer);
            ParameterTest.quadTreeMaxLayer = layer;
            long start = System.nanoTime();
            List<Integer> queryRes = quadTreeSearch.query(-20, -20, 10, 10, 10);
            long end = System.nanoTime();
            res.put(layer, (end - start) / 1000000.0);
            resList.add(res);
        }
        HashMap<Integer, Double> avgQueryTime = avgQueryTime(resList);
        saveQueryTimeRes(avgQueryTime);
    }

    /**
     * @param
     * @return void
     * @Description:进行10次查询，每次查询的范围是(-10,-10,5,5)
     */

    private static void testQuery100WithQueryTime() {
        Quadtree quadtree = new Quadtree(ParameterTest.xMin, ParameterTest.yMin, ParameterTest.xMax, ParameterTest.yMax);
        QuadTreeSearch quadTreeSearch = new QuadTreeSearch(quadtree);
        ArrayList<HashMap<Integer, Double>> resList = new ArrayList<>();
        HashMap<Integer, QueryRange> ranges = readQueryRange(100);
        for (int i = 0; i < 100; i++) {
            HashMap<Integer, Double> res = new HashMap<>();
            QueryRange queryRange = ranges.get(i);
            double x1 = queryRange.getX1();
            double x2 = queryRange.getX2();
            double y1 = queryRange.getY1();
            double y2 = queryRange.getY2();
            System.out.println(i + ":{" + "x1:" + x1 + " y1:" + y1 + " x2:" + x2 + " y2:" + y2 + "}");
            for (int j = 5; j < 13; j++) {
//                quadTreeSearch.readQuadTreeFromFile(j);
                quadTreeSearch.readQuadtreeToIndexFile(j);
                ParameterTest.quadTreeMaxLayer = j;
                long start = System.nanoTime();
                quadTreeSearch.query(x1, y1, x2, y2, 100);
                long end = System.nanoTime();
                res.put(j, (end - start) / 1000000.0);
            }
            resList.add(res);
        }
        HashMap<Integer, Double> avgQueryTime = avgQueryTime(resList);
        saveQueryTimeRes(avgQueryTime);
    }

    private static void testQuery100WithAccuracy() {
        Quadtree quadtree = new Quadtree(ParameterTest.xMin, ParameterTest.yMin, ParameterTest.xMax, ParameterTest.yMax);
        QuadTreeSearch quadTreeSearch = new QuadTreeSearch(quadtree);
        ArrayList<HashMap<Integer, Double>> resList = new ArrayList<>();
        HashMap<Integer, QueryRange> ranges = readQueryRange(100);
        for (int i = 0; i < 100; i++) {
            HashMap<Integer, Double> res = new HashMap<>();
            QueryRange queryRange = ranges.get(i);
            double x1 = queryRange.getX1();
            double x2 = queryRange.getX2();
            double y1 = queryRange.getY1();
            double y2 = queryRange.getY2();
            System.out.println(i + ":{" + "x1:" + x1 + " y1:" + y1 + " x2:" + x2 + " y2:" + y2 + "}");
            for (int j = 5; j < 15; j++) {
                ParameterTest.quadTreeMaxLayer = j;
                quadTreeSearch.readQuadTreeFromFile(j);
                List<Integer> queryRes = quadTreeSearch.query(x1, y1, x2, y2, 100);
                double accuracy = getAccuracy(queryRes, i);
                res.put(j, accuracy);
            }
            resList.add(res);
        }
        HashMap<Integer, Double> avgAccuracy = avgAccuracy(resList);
        saveAccuracyRes(avgAccuracy);
    }

    private static void generateQueryRange(int num) {
//        ArrayList<QueryRange> rangeList  = new ArrayList<>();
        HashMap<Integer, QueryRange> rangeMap = new HashMap<>();
        for (int i = 0; i < num; i++) {
            rangeMap.put(i, new QueryRange());
        }
        saveQueryRange(rangeMap);
    }

    private static void saveQueryRange(HashMap<Integer, QueryRange> rangeMap) {
        String jsonString = JSON.toJSONString(rangeMap);
        try (FileWriter file = new FileWriter(ParameterTest.queryRangeFilePath + rangeMap.size() + ".json")) {
            file.write(jsonString);
            System.out.println("Serialized data is saved in " + ParameterTest.queryRangeFilePath + rangeMap.size() + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<Integer, QueryRange> readQueryRange(int num) {
        HashMap<Integer, QueryRange> rangeMap = new HashMap<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(ParameterTest.queryRangeFilePath + num + ".json")));
            rangeMap = JSON.parseObject(content, new TypeReference<HashMap<Integer, QueryRange>>() {});
            System.out.println("Deserialized data from " + ParameterTest.queryRangeFilePath + num + ".json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rangeMap;
    }

    private static double getAccuracy(List<Integer> queryRes, int k) {
        int[] arr = {997, 70, 204, 218, 219, 224, 429, 676, 706, 774};
        List<Integer> list = new ArrayList<>();
        for (int i : arr) {
            list.add(i);
        }
        long count = queryRes.stream().filter(list::contains).count();
        return count * 1.0 / queryRes.size();
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
}

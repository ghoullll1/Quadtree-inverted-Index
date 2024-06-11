import config.Parameter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import query.QuadTreeSearch;
import structure.Quadtree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        testQuery10WithQueryTime();
//        testQuery10WithAccuracy();
    }
    /**
     * @Description:进行10次查询，每次查询的范围是(-10,-10,5,5)
     * @param
     * @return void
     */

    private static void testQuery10WithQueryTime(){
        Quadtree quadtree = new Quadtree(Parameter.xMin, Parameter.yMin, Parameter.xMax, Parameter.yMax);
        QuadTreeSearch quadTreeSearch = new QuadTreeSearch(quadtree);
        ArrayList<HashMap<Integer, Double>> resList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<Integer, Double> res = new HashMap<>();
            for (int j = 5; j < 15; j++) {
                quadTreeSearch.readQuadTreeFromFile(j);
                Parameter.quadTreeMaxLayer= j;
                long start = System.nanoTime();
                List<Integer> queryRes = quadTreeSearch.query(-20, -20, 10, 10, 10);
                long end = System.nanoTime();
                res.put(j, (end - start) / 1000000.0);
            }
            resList.add(res);
        }
        HashMap<Integer, Double> avgQueryTime = avgQueryTime(resList);
        saveQueryTimeRes(avgQueryTime);
    }

    private static void testQuery10WithAccuracy(){
        Quadtree quadtree = new Quadtree(Parameter.xMin, Parameter.yMin, Parameter.xMax, Parameter.yMax);
        QuadTreeSearch quadTreeSearch = new QuadTreeSearch(quadtree);
        ArrayList<HashMap<Integer, Double>> resList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<Integer, Double> res = new HashMap<>();
            for (int j = 5; j < 15; j++) {
                Parameter.quadTreeMaxLayer= j;
                quadTreeSearch.readQuadTreeFromFile(j);
                List<Integer> queryRes = quadTreeSearch.query(-20, -20, 10, 10, 10);
                double accuracy = getAccuracy(queryRes);
                res.put(j, accuracy);
            }
            resList.add(res);
        }
        HashMap<Integer, Double> avgAccuracy = avgAccuracy(resList);
        saveAccuracyRes(avgAccuracy);
    }
    
    private static double getAccuracy(List<Integer> queryRes){
        int[] arr={997, 70, 204, 218, 219, 224, 429, 676, 706, 774};
        List<Integer> list = new ArrayList<>();
        for (int i : arr) {
            list.add(i);
        }
        long count = queryRes.stream().filter(list::contains).count();
        return count*1.0/queryRes.size();
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

    private static void saveQueryTimeRes(Map<Integer, Double> data){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = LocalDate.now().format(formatter);
        String basePath = Parameter.resultQueryTimePath + date;
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

    private static void saveAccuracyRes(Map<Integer, Double> data){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = LocalDate.now().format(formatter);
        String basePath = Parameter.resultAccuracyPath + date;
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

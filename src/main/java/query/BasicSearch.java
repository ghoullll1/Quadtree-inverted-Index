package query;

import config.ParameterTest;
import structure.Point;
import structure.Quadtree;
import structure.QueryRange;
import structure.Range;
import utils.NumericNameComparator;
import utils.Time;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: hehe
 * @create: 2024-06-07 12:42
 * @Description:
 */
public class BasicSearch {
    private Map<Integer, List<Point>> datasets;

    public BasicSearch(int k) {
        datasets=new HashMap<>();
        readDataSets(k);
    }

    public void readDataSets(int k) {
        File folder = new File(ParameterTest.datasetsPath.get(k));
        if (folder.isDirectory() && folder.exists()) {
            File[] files = folder.listFiles();
            Arrays.sort(files, Comparator.comparing(File::getName, new NumericNameComparator()));
            int num = Math.min(files.length, ParameterTest.datasetNum);
            for (int i = 0; i < num; i++) {
                System.out.println("数据集读取进度:"+i * 1.0 / num * 1.0 * 100 + "%");
                // 获取数据集中所有点的坐标
                List<Point> dataset = readCSV(files[i].getAbsolutePath(), Integer.parseInt(files[i].getName().split("\\.")[0]));
                // 将数据集存储,key为数据集的id,value为对应数据集的点集
                datasets.put(Integer.parseInt(files[i].getName().split("\\.")[0]), dataset);
            }
        }
    }

    private List<Point> readCSV(String filePath, int datasetId) {
        List<Point> list = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                String[] parts = line.split(",");
                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);
                Point point = new Point(latitude, longitude, datasetId);
                list.add(point);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * @Description:查询range范围内点数量最多的topk个数据集
     * @param range
     * @param topk
     * @return java.util.List<java.lang.Integer>
     */

    public List<Integer> query(QueryRange range, int topk) {
        HashMap<Integer, Integer> resMap = new HashMap<>();
        datasets.forEach((id, dataset) -> {
            dataset.forEach(point -> {
                if (contain(range, point)) {
                    resMap.put(id, resMap.getOrDefault(id, 0) + 1);
                }
            });
        });
        return resMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(topk)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * @Description:判断point是否在range中
     * @param range
     * @param point
     * @return boolean
     */

    private boolean contain(QueryRange range, Point point) {
        return range.getX1() <= point.getX() && point.getX() <= range.getX2() && range.getY1() <= point.getY() && point.getY() <= range.getY2();
    }

}

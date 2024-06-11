package query;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import config.Parameter;
import structure.Point;
import structure.Quadtree;
import utils.Time;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.alibaba.fastjson2.JSONWriter.Feature.LargeObject;

/**
 * @author: hehe
 * @create: 2024-06-07 12:58
 * @Description:
 */
public class QuadTreeSearch {
    private Quadtree quadtree;

    public QuadTreeSearch(Quadtree quadtree) {
        this.quadtree = quadtree;
    }

    public List<Integer> query(double queryXMin, double queryYMin, double queryXMax, double queryYMax,int topk) {
        List<Integer> topkList = quadtree.queryTopKDataset(queryXMin, queryYMin, queryXMax, queryYMax, topk);
        return topkList;
    }

    public void saveQuadtreeToFile() {
        JSON.config(LargeObject, true);
        String index = JSONObject.toJSONString(quadtree);
        try (FileWriter file = new FileWriter(Parameter.quadTreeIndexFilePath+Parameter.quadTreeMaxLayer+".json")) {
            file.write(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Quadtree readQuadTreeFromFile(int maxLayer){
        File file = new File(Parameter.quadTreeIndexFilePath+maxLayer+".json");
        try (FileReader reader = new FileReader(file)) {
            // 从文件中读取 JSON 字符串
            StringBuilder jsonBuilder = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {
                jsonBuilder.append((char) ch);
            }
            String jsonString = jsonBuilder.toString();

            // 将 JSON 字符串反序列化为 Java 对象
            quadtree = JSON.parseObject(jsonString, Quadtree.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return quadtree;
    }

    public void createQuadtreeIndex() {
        for (int i = 0; i < Parameter.datasetNum; i++) {
            String filePath = Parameter.datasetPath + i + ".csv";
            // 将数据集插入到QuadTree中
            readCSVAndInsertPoints(filePath, i);
        }
    }

    private void readCSVAndInsertPoints(String filePath, int datasetId) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                String[] parts = line.split(",");
                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);
                Point point = new Point(latitude, longitude, datasetId);
                quadtree.insert(point);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

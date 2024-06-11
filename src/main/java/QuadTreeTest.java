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
 * @create: 2024-06-05 20:43
 * @Description:
 */
public class QuadTreeTest {
    public static void main(String[] args) throws IOException {
        // 初始化QuadTree
        Quadtree quadtree = new Quadtree(Parameter.xMin, Parameter.yMin, Parameter.xMax, Parameter.yMax);

        // 读取所有数据集
//        for (int i = 0; i < 1000; i++) {
//            String filePath = "D:\\Desktop\\experiments\\datasets\\Datasets\\" + i + ".csv";
//            // 将数据集插入到QuadTree中
//            readCSVAndInsertPoints(filePath, i, quadtree);
//        }
//        saveQuadtreeToFile(quadtree, "D:\\Desktop\\experiments\\index\\quadtree_index.json");
        quadtree=readQuadTreeFromFile("D:\\Desktop\\experiments\\index\\quadtree_index.json", quadtree);
        // 将建好的QuadTree索引保存到文件

        double queryXMin = -10, queryYMin = -10, queryXMax = 5, queryYMax = 5;

        Time time = new Time();
        time.start();
        List<Integer> topkList = quadtree.queryTopKDataset(queryXMin, queryYMin, queryXMax, queryYMax, 10);
        time.end();
        System.out.println("queryTime:" + time.getTime());
        System.out.println("Topk Dataset:" + topkList);
    }

    /**
     * @param filePath
     * @param datasetId
     * @param quadtree
     * @throws IOException
     * @Description:读取CSV文件并插入到QuadTree中
     */
    private static void readCSVAndInsertPoints(String filePath, int datasetId, Quadtree quadtree) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        Time time = new Time();
        time.start();
        for (String line : lines) {
            String[] parts = line.split(",");
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);
            Point point = new Point(latitude, longitude, datasetId);
            quadtree.insert(point);
        }
        time.end();
        System.out.println("Insert Time Avg:" + time.getNanoTime() / lines.size());
    }

    /**
     * @param quadtree
     * @param filePath
     * @return void
     * @Description:保存建好的索引到文件
     */
    private static void saveQuadtreeToFile(Quadtree quadtree, String filePath) {
        JSON.config(LargeObject, true);
        String index = JSONObject.toJSONString(quadtree);
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Quadtree readQuadTreeFromFile(String filePath,Quadtree quadtree){
        File file = new File(filePath);
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
}

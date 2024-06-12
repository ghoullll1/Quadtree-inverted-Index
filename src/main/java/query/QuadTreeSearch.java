package query;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import config.ParameterTest;
import structure.Point;
import structure.Quadtree;

import java.io.*;
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

    public List<Integer> query(double queryXMin, double queryYMin, double queryXMax, double queryYMax, int topk) {
        List<Integer> topkList = quadtree.queryTopKDataset(queryXMin, queryYMin, queryXMax, queryYMax, topk);
        return topkList;
    }

    public void saveQuadtreeToFile() {
        JSON.config(LargeObject, true);
        String index = JSONObject.toJSONString(quadtree);
        try (FileWriter file = new FileWriter(ParameterTest.quadTreeIndexFilePath + ParameterTest.quadTreeMaxLayer + ".json")) {
            file.write(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveQuadtreeToIndexFile() {
        try (FileOutputStream fileOut = new FileOutputStream("D:\\Desktop\\experiments\\index\\" + ParameterTest.quadTreeMaxLayer + ".index");
             BufferedOutputStream bufferOut = new BufferedOutputStream(fileOut);
             ObjectOutputStream out = new ObjectOutputStream(bufferOut)) {
            out.writeObject(quadtree);
            System.out.println("Serialized data is saved in " + "D:\\Desktop\\experiments\\index\\" + ParameterTest.quadTreeMaxLayer + ".index");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void readQuadtreeToIndexFile(int maxLayer) {
        try (FileInputStream fileIn = new FileInputStream(ParameterTest.quadTreeIndexFilePath + maxLayer+ ".index");
             BufferedInputStream bufferIn = new BufferedInputStream(fileIn);
             ObjectInputStream in = new ObjectInputStream(bufferIn)) {
            quadtree = (Quadtree) in.readObject();
            System.out.println("Read Quadtree " +  maxLayer + " Index");
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Person class not found");
            c.printStackTrace();
        }
    }

    public Quadtree readQuadTreeFromFile(int maxLayer) {
        File file = new File(ParameterTest.quadTreeIndexFilePath + maxLayer + ".json");
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
        File folder = new File(ParameterTest.datasetsPath.get(1));
        if (folder.isDirectory() && folder.exists()) {
            File[] files = folder.listFiles();
            int num = Math.min(files.length, ParameterTest.datasetNum);
            for (int i = 0; i < num; i++) {
                System.out.println(i * 1.0 / num * 1.0 * 100 + "%");
                // 将数据集插入到QuadTree中
                readCSVAndInsertPoints(files[i].getAbsolutePath(), Integer.parseInt(files[i].getName().split("\\.")[0]));
            }
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

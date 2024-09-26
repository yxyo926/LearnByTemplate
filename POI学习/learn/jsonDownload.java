import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpsRequestWithBinaryStream {
    public static void main(String[] args) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("https://your-url-to-json-file");
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = entity.getContent();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[16384];
                    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    byte[] byteArray = buffer.toByteArray();
                    ObjectMapper objectMapper = new ObjectMapper();
                    // 假设 JSON 数据对应一个名为 YourJsonObject 的类
                    YourJsonObject yourObject = objectMapper.readValue(byteArray, YourJsonObject.class);
                    // 现在 yourObject 包含了解析后的 JSON 数据，可以进行进一步操作
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

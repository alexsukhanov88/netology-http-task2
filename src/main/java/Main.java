import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;

public class Main {

    public static void getNasaResponse() {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                            .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                            .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                            .build())
                    .build();
            HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=gwcd8zTni0Gm9mQsMgHgh1y6m5wIjPb3pgZSYh6c");
            CloseableHttpResponse response = httpClient.execute(request);

            ObjectMapper mapper = new ObjectMapper();
            NasaResponse nasaResponse = mapper.readValue(response.getEntity().getContent(), NasaResponse.class);

            System.out.println(nasaResponse.toString());

            HttpGet requestUrl = new HttpGet(nasaResponse.getUrl());
            CloseableHttpResponse responseUrl = httpClient.execute(requestUrl);

            URL resultUrl= new URL(nasaResponse.getUrl());
            String resultFileName = FilenameUtils.getName(resultUrl.getPath());

            File result = new File(resultFileName);

            try (FileOutputStream outputStream = new FileOutputStream(result);
                 ByteArrayInputStream bis = new ByteArrayInputStream(responseUrl.getEntity().getContent().readAllBytes())) {
                    byte[] buffer = new byte[bis.available()];
                    bis.read(buffer);
                    outputStream.write(buffer);
                }
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        getNasaResponse();
    }
}

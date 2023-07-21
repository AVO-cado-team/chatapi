package sk.avo.chatapi.infrastructure.rest.filestorage;

import okhttp3.*;
import okio.Okio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sk.avo.chatapi.domain.model.filestorage.FileNotFoundException;
import sk.avo.chatapi.domain.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
  private final String simpleFileStorageAPIUrl;
  private final OkHttpClient httpClient;
  private final ObjectMapper objectMapper;

  public FileStorageServiceImpl(
          @Value("${simple-file-storage-api.url}")
          String simpleFileStorageAPIUrl
  ) {
    this.simpleFileStorageAPIUrl = simpleFileStorageAPIUrl;
    this.httpClient = new OkHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public UUID storeFile(File file) {
    RequestBody requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("application/octet-stream")))
            .build();

    String STORAGE_FILE_PATH = "/upload";
    Request request = new Request.Builder()
            .url(simpleFileStorageAPIUrl + STORAGE_FILE_PATH)
            .post(requestBody)
            .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (response.isSuccessful()) {
        // Parse the response JSON and extract the UUID
        assert response.body() != null;
        String responseBody = response.body().string();
        String fileId = objectMapper.readTree(responseBody).get("id").asText();
        return UUID.fromString(fileId);
      } else {
        throw new IOException("Failed to store file: " + response.code() + " - " + response.message());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public File getFile(UUID fileId) throws FileNotFoundException {
    String GET_FILE_PATH = "/download/{fileId}";
    String getFileUrl = simpleFileStorageAPIUrl + GET_FILE_PATH.replace("{fileId}", fileId.toString());
    Request request = new Request.Builder()
            .url(getFileUrl)
            .get()
            .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (response.isSuccessful()) {
        // File retrieved successfully, return the file
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
          File tempFile = File.createTempFile("file_", "");
          responseBody.source().readAll(Okio.sink(tempFile));
          return tempFile;
        }
      } else {
        throw new FileNotFoundException("Failed to retrieve file: " + response.code() + " - " + response.message());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return null;
  }
}
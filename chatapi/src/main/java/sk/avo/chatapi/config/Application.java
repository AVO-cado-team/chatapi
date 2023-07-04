package sk.avo.chatapi.config;

import lombok.Getter;
import java.util.Properties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Application {
  @Getter private String version;
  @Getter private String buildTime;
  @Getter private String gitCommitSha;
  @Getter private String gitBranch;

  private static final String PROPERTIES_FILE = "build-info.properties";
  private static final String VERSION_FIELD_NAME = "version";
  private static final String BUILD_TIME_FIELD_NAME = "build.time";
  private static final String GIT_COMMIT_SHA_FIELD_NAME = "git.commit.sha";
  private static final String GIT_BRANCH_FIELD_NAME = "git.branch";

  public Application() {
    Properties properties = new Properties();
    try {
      properties.load(getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE));
      version = properties.getProperty(VERSION_FIELD_NAME);
      buildTime = properties.getProperty(BUILD_TIME_FIELD_NAME);
      gitCommitSha = properties.getProperty(GIT_COMMIT_SHA_FIELD_NAME);
      gitBranch = properties.getProperty(GIT_BRANCH_FIELD_NAME);
    } catch (Exception e) {
      version = "unknown";
      buildTime = "unknown";
      gitCommitSha = "unknown";
      gitBranch = "unknown";
    }
  }
}

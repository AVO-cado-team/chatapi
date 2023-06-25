package sk.avo.chatapi.config;

import lombok.Getter;
import java.util.Properties;

public class Application {
    @Getter
    private String version;
    @Getter
    private String buildTime;
    @Getter
    private String gitCommitSha;

    public Application() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));
            version = properties.getProperty("version");
            buildTime = properties.getProperty("build.time");
            gitCommitSha = properties.getProperty("git.commit.sha");
        } catch (Exception e) {
            version = "unknown";
            buildTime = "unknown";
            gitCommitSha = "unknown";
        }
    }
}

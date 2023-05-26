package br.com.yuri.restwithspringboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageConfig {

    private String upload_dir;

    public String getUploadDir() {
        return upload_dir;
    }

    public void setUploadDir(String upload_dir) {
        this.upload_dir = upload_dir;
    }
}

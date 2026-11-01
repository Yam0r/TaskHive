package my.app.files.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DropboxConfig {

    @Bean
    public DbxClientV2 dropboxClient() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String appName = dotenv.get("MY_APP_NAME", "TaskHive");
        String accessToken = dotenv.get("MY_DROPBOX_ACCESS_TOKEN", "");
        DbxRequestConfig config = DbxRequestConfig.newBuilder(appName).build();
        return new DbxClientV2(config, accessToken);
    }
}

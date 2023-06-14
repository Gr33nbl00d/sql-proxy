package jdbc;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

import com.google.gson.Gson;

@Getter
@Setter
@Service
public class Connector {

  @Autowired
  private DatabaseLookup databaseLookup;
  private DataSource dataSource;

  @PostConstruct
  public void postConstruct() {
    final Path path = Paths.get("dbconfig.json");
    if (path.toFile().exists()) {
      try {
        final String jsondata = Files.readString(path, StandardCharsets.UTF_8);
        Gson gson = new Gson();
        final DatabaseConnectionConfig databaseConnectionConfig = gson.fromJson(jsondata, DatabaseConnectionConfig.class);
        connect(databaseConnectionConfig);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void connect(DatabaseConnectionConfig conConfig) throws SQLException, IOException {
    dataSource = init(conConfig);
    dataSource.getConnection();
    Gson gson = new Gson();
    final String json = gson.toJson(conConfig);
    Files.writeString(Paths.get("dbconfig.json"), json, StandardCharsets.UTF_8);
  }

  private DataSource init(DatabaseConnectionConfig conn) {
    DataSourceBuilder builder = getBuilder(conn);
    builder.username(conn.getUsername());
    builder.password(conn.getPassword());
    return builder.build();
  }

  private DataSourceBuilder getBuilder(DatabaseConnectionConfig conn) {
    DataSourceBuilder builder = DataSourceBuilder.create();
    String type = conn.getType().toLowerCase();
    Map<String, String> dbSettings = databaseLookup.get(type);
    System.out.println(dbSettings);
    if (dbSettings != null) {
      builder.driverClassName(dbSettings.get("driver"));
      String urlTemplate = dbSettings.get("url");
      String url = MessageFormat.format(urlTemplate, conn.getHost(), conn.getPort(), conn.getDatabase());
      if (conn.getPort() == "") {
        url = url.replace(conn.getHost() + ':', conn.getHost());
      }
      url = url.replace("{3}", conn.getUsername());
      url = url.replace("{4}", conn.getPassword());
      System.out.println(url);
      builder.url(url);
    } else {
      throw new RuntimeException("Database type  " + type + " not supported");
    }
    return builder;
  }
}

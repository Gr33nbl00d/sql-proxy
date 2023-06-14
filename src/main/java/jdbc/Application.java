package jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class Application {

  @Autowired
  private Connector connector;

  @RequestMapping(value = "/", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public TestDataSourceResponse test(@RequestBody DatabaseConnectionConfig conConfig) throws SQLException, IOException {
    if (conConfig.getHost().contains(":")) {
      String[] hostPort = conConfig.getHost().split(":");
      conConfig.setHost(hostPort[0]);
      conConfig.setPort(hostPort[1]);
    }
    if (conConfig.getPort() == null) {
      conConfig.setPort("");
    }
    return connect(conConfig);
  }

  @RequestMapping("/query")
  @ResponseStatus(HttpStatus.OK)
  public List query(@RequestParam String sql) {
    System.out.println(sql);
    JdbcTemplate template = new JdbcTemplate(connector.getDataSource());
    final List<Map<String, Object>> data = template.queryForList(sql);
    return data;
    //return new QueryResponse(data);
  }

  @RequestMapping(value = "/connect", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public TestDataSourceResponse connect(@RequestBody DatabaseConnectionConfig conn) throws SQLException, IOException {
    connector.connect(conn);
    return new TestDataSourceResponse("Connected");
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}

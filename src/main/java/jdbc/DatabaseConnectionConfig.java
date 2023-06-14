package jdbc;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
public class DatabaseConnectionConfig {
    private String username;
    private String password;
    private String host;
    private String port;
    private String database;
    private String type;
    private Map<String, String> params;
}

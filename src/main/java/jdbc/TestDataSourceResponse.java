package jdbc;

import lombok.Data;

@Data
public class TestDataSourceResponse {
  public TestDataSourceResponse(String data) {
    this.data = data;
  }

  String data;

  public String getData() {
    return data;
  }
}

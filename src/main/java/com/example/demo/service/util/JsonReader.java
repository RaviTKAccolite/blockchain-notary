package com.example.demo.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

public class JsonReader {
  public JsonReader() {
  }

  public <T> T jsonToObject(String fileName, Class<T> actualClass) {
    InputStream in = this.getClass().getResourceAsStream(fileName);
    if (in != null) {
      ObjectMapper object = new ObjectMapper();

      try {
        return object.readValue(in, actualClass);
      } catch (Exception var6) {
        var6.printStackTrace();
      }
    }

    return null;
  }

}

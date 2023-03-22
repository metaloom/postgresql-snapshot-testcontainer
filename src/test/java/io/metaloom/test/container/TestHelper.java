package io.metaloom.test.container;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.testcontainers.containers.PostgreSQLContainer;

public final class TestHelper {

  private TestHelper() {
  }

  private static final String CREATE_TABLE = """
    CREATE TABLE users
    (id INT PRIMARY KEY, name TEXT)
    """;

  private static final String INSERT_USER = "INSERT INTO users (id, name) VALUES (?, ?)";

  private static final String SELECT_USERS = "SELECT id, name from users";

  private static final String DELETE_USERS = "DELETE FROM users";

  public static final int USER_COUNT = 50_000;

  public static void setupTable(PostgreSQLContainer db) throws SQLException {
    try (Connection connection = DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword())) {
      Statement statement = connection.createStatement();
      statement.execute(CREATE_TABLE);
    }
  }

  public static void insertUsers(PostgreSQLContainer db, int id, String name) throws SQLException {
    try (Connection connection = DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword())) {
      for (int i = 1; i <= USER_COUNT; i++) {
        PreparedStatement statement = connection.prepareStatement(INSERT_USER);
        statement.setInt(1, id + i);
        statement.setString(2, name + "_" + i);
        assertEquals(1, statement.executeUpdate());
      }
    }
  }

  public static void deleteUsers(PostgreSQLContainer db) throws SQLException {
    try (Connection connection = DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword())) {
      PreparedStatement statement = connection.prepareStatement(DELETE_USERS);
      statement.executeUpdate();
    }

  }

  public static List<User> selectUsers(PostgreSQLContainer db) throws SQLException {
    try (Connection connection = DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword())) {
      Statement statement = connection.createStatement();
      ResultSet res = statement.executeQuery(SELECT_USERS);
      List<User> users = new ArrayList<>();
      while (res.next()) {
        users.add(new User(res.getInt("id"), res.getString("name")));
      }
      return users;
    }
  }

  public static void assertDatabase(PostgreSQLContainer db) throws SQLException {
    assertEquals("There should be one user", USER_COUNT, selectUsers(db).size());
    deleteUsers(db);
    assertEquals("There should be no user", 0, selectUsers(db).size());
  }

}

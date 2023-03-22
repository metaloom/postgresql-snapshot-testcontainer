package io.metaloom.test.container;

import static io.metaloom.test.container.TestHelper.deleteUsers;
import static io.metaloom.test.container.TestHelper.insertUsers;
import static io.metaloom.test.container.TestHelper.setupTable;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class NoSnapshotTest implements DatabaseTestcases {

  @Container
  public static PostgreSQLContainer db = new PostgreSQLContainer();

  @BeforeAll
  public static void setupTables() throws SQLException {
    System.out.println("Setup DB");
    setupTable(db);
  }

  @BeforeEach
  public void setupSnapshot() throws Exception {
    deleteUsers(db);
    insertUsers(db, 42, "johannes");
  }

  @Override
  public PostgreSQLContainer db() {
    return db;
  }

}

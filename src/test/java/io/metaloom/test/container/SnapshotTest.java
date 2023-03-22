package io.metaloom.test.container;

import static io.metaloom.test.container.TestHelper.insertUsers;
import static io.metaloom.test.container.TestHelper.setupTable;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class SnapshotTest implements DatabaseTestcases {

  @Container
  public static PostgreSQLSnapshotContainer db = new PostgreSQLSnapshotContainer(128, 128);

  @BeforeAll
  public static void setupSnapshot() throws SQLException, UnsupportedOperationException, IOException, InterruptedException {
    db.startPostgresql();
    setupTable(db);
    insertUsers(db, 42, "johannes");
    db.snapshot();
  }

  @BeforeEach
  public void restoreSnapshot() throws Exception {
    db.restore();
  }

  @Override
  public PostgreSQLContainer db() {
    return db;
  }
}

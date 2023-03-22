package io.metaloom.test.container;

import static io.metaloom.test.container.TestHelper.insertUsers;
import static io.metaloom.test.container.TestHelper.setupTable;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class SnapshotFsTest implements DatabaseTestcases {

  @Container
  public static PostgreSQLSnapshotContainer db = new PostgreSQLSnapshotContainer(128, 128).withFileSystemSnapshotLocation("snapshots", "TEST");

  @BeforeAll
  public static void setupSnapshot() throws Exception {
    if (!db.hasFSSnapshot()) { 
      db.startPostgresql();
      setupTable(db);
      insertUsers(db, 42, "johannes");
      db.fsSnapshot();
    }
  }

  @BeforeEach
  public void restoreSnapshot() throws Exception {
    db.fsRestore();
  }

  @Override
  public PostgreSQLContainer db() {
    return db;
  }
}

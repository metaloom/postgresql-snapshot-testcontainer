package io.metaloom.test.container;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import static io.metaloom.test.container.TestHelper.assertDatabase;

public interface DatabaseTestcases {

  public PostgreSQLContainer db();

  @Test
  default void testSnapshot() throws SQLException {
    assertDatabase(db());
  }

  @Test
  default void testSnapshot2() throws SQLException {
    assertDatabase(db());
  }

  @Test
  default void testSnapshot3() throws SQLException {
    assertDatabase(db());
  }

  @Test
  default void testSnapshot4() throws SQLException {
    assertDatabase(db());
  }

  @Test
  default void testSnapshot5() throws SQLException {
    assertDatabase(db());
  }

  @Test
  default void testSnapshot6() throws SQLException {
    assertDatabase(db());
  }
}

package io.metaloom.test.container;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgreSQLSnapshotContainer extends PostgreSQLContainer<PostgreSQLSnapshotContainer> {

  public static final String DEFAULT_IMAGE = "postgres:13.2";
  public static final String SNAPSHOT_IMAGE = "postgres-snapshot:13.2";
  private String snapshotName;
  private String snapshotLocation;

  public PostgreSQLSnapshotContainer(int liveTmpFsSizeInMB, int snapshotTmpFsSizeInMB) {
    super(DockerImageName.parse(SNAPSHOT_IMAGE).asCompatibleSubstituteFor("postgres"));
    withDatabaseName("postgres");
    withUsername("sa");
    withPassword("sa");
    withEnv("PGDATA", "/live/pgdata");
    withTmpFs(tmpFs(liveTmpFsSizeInMB, snapshotTmpFsSizeInMB));
  }

  private Map<String, String> tmpFs(int liveSizeMB, int snapshotSizeMB) {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("/snapshot", "rw,size=" + snapshotSizeMB + "m");
    mapping.put("/live", "rw,size=" + liveSizeMB + "m");
    return mapping;
  }

  public int getPort() {
    return getFirstMappedPort();
  }

  public void startPostgresql() throws UnsupportedOperationException, IOException, InterruptedException {
    execInContainer("nohup", "bash", "-c", "docker-entrypoint.sh postgres -c fsync=off &", "disown");
  }

  public void stopPostgresql() throws UnsupportedOperationException, IOException, InterruptedException {
    execInContainer("killall", "postgres");
  }

  public void snapshot() throws UnsupportedOperationException, IOException, InterruptedException {
    stopPostgresql();
    execInContainer("sync");
    execInContainer("rsync", "-rav", "/live/pgdata", "/snapshot");
    startPostgresql();
  }

  public void fsSnapshot() throws UnsupportedOperationException, IOException, InterruptedException {
    stopPostgresql();
    execInContainer("sync");
    execInContainer("rsync", "-rav", "/live/pgdata", "/fs-snapshot");
    startPostgresql();
  }

  public void fsRestore() throws UnsupportedOperationException, IOException, InterruptedException {
    stopPostgresql();
    execInContainer("rm", "-rf", "/live/pgdata");
    execInContainer("rsync", "-rav", "/fs-snapshot/pgdata", "/live");
    execInContainer("sync");
    startPostgresql();
  }

  public void restore() throws UnsupportedOperationException, IOException, InterruptedException {
    stopPostgresql();
    execInContainer("rm", "-rf", "/live/pgdata");
    execInContainer("rsync", "-rav", "/snapshot/pgdata", "/live");
    execInContainer("sync");
    startPostgresql();
  }

  public PostgreSQLSnapshotContainer withFileSystemSnapshotLocation(String snapshotLocation, String snapshotName) {
    this.snapshotLocation = snapshotLocation;
    this.snapshotName = snapshotName;
    File hostPath = new File(snapshotLocation, snapshotName);
    if (!hostPath.exists()) {
      assertTrue("Snapshot directory could not be created on the host " + hostPath.getAbsolutePath(), hostPath.mkdirs());
    }
    withFileSystemBind(hostPath.getAbsolutePath(), "/fs-snapshot");
    return this;
  }

  public boolean hasFSSnapshot() {
    File snapshotDir = new File(snapshotLocation, snapshotName);
    return snapshotDir.exists() && snapshotDir.list().length != 0;
  }

}

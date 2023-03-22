# PostgreSQL Testcontainer with Snapshot Support

This project contains a test implementation for a Testcontainer which uses TmpFs and snapshots to speedup testing.


# Typical performance issues with test databases

Depending on your test setup you may run into performance problems. Typical problems are:

## Problem 1
> **Creation of tables take long for each test**

Potential solution:
* Only run the table setup once per testclass via `@BeforeAll` and prune the tables for each testcase.
* Prepare postgres database before running your test classes and provide either a snapshot or a allocated database which the test can consume (throwaway database). This process requires a daemon which keeps track of the databases and purges no longer needed ones.

## Problem 2:
> **Creation of test fixture (rows) take long for each test**

Potential solution:
* Setup the database once and create a snapshot which can be restored by restarting the database.
* Prepare a database before running your test cases. Each testcase can consume the allocated database. (throwaay database). This process requires a daemon which keeps track of the databases and purges no longer needed ones.


## Problem 3:
> **Transaction commit is slow**

Potential solutions:
* Disable fsync (already the disabled for postgres testcontainer)
* Use libeatmydata to really disable fsync
* Use tmpfs


# Implementation

This implementation currently focuses on problem 2. A snapshot mechanism may be used to store the contents of the database in an additional tmpfs. During the restore process the tmpfs contents gets copied to the *live* location of the database. A restart of the database will thus restore the original state.

Typical usecase:

```java
  @Container
  public static PostgreSQLSnapshotContainer db = new PostgreSQLSnapshotContainer(128, 128);

  @BeforeAll
  public static void setupSnapshot() throws Exception {
    db.startPostgresql();
    setupTable(db);
    insertUsers(db, 42, "johannes");
    db.snapshot();
  }

  @BeforeEach
  public void restoreSnapshot() throws Exception {
    db.restore();
  }
```

Depending on the testsetup this may improve the test performance.

Results of a dummy test:
* 16s using snapshots  (`SnapshotTest`)
* 26s without snapshots (`NoSnapshotTest`)

NOTE: The testcontainer requires a custom container image. This image can be build via the `image/Dockerfile`.
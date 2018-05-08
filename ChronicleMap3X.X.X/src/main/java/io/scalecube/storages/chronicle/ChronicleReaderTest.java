package io.scalecube.storages.chronicle;

import static io.scalecube.storages.common.Constants.N;

import io.scalecube.storages.common.Order;
import io.scalecube.storages.common.Storage;
import io.scalecube.storages.common.StorageReaderTest;

import com.codahale.metrics.MetricRegistry;

public class ChronicleReaderTest {

  private static final int nThreads = Runtime.getRuntime().availableProcessors();

  public static void main(String[] args) throws Exception {
    MetricRegistry registry = new MetricRegistry();
    Storage<String, Order> storage = new ChronicleMapStorage(N);
    try {
      new StorageReaderTest(nThreads, N, registry, storage).test();
    } finally {
      storage.close();
    }
  }
}

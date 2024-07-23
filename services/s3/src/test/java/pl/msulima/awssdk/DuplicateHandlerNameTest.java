package pl.msulima.awssdk;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.SdkEventLoopGroup;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;


public class DuplicateHandlerNameTest {
    private static final String S3_BUCKET = "put.your.bucket.name.here";
    private static final String FILE_KEY = "test.txt";

    private static final int REQUEST_SIZE = 0;
    private static final int REQUESTS = 3;

    @Test
    void test() {
        try (S3AsyncClient client = createClient()) {
            CompletableFuture<?>[] futures =
                IntStream.range(0, REQUESTS).mapToObj(i -> putObject(client)).toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(futures).join();
        }
    }

    private static S3AsyncClient createClient() {
        return S3AsyncClient.builder()
                            .httpClient(NettyNioAsyncHttpClient.builder()
                                                               .maxConcurrency(1)
                                                               .eventLoopGroupBuilder(SdkEventLoopGroup.builder().numberOfThreads(1))
                                                               .build())
                            .region(Region.EU_NORTH_1)
                            .build();
    }

    private static CompletableFuture<?> putObject(S3AsyncClient client) {
        return client.putObject(r -> r.bucket(S3_BUCKET).key(FILE_KEY), AsyncRequestBody.fromBytes(new byte[REQUEST_SIZE]));
    }
}

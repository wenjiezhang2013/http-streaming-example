package jz.demo.springmvcstreaming;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class Client {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		try {
			// Start the client
			httpclient.start();

			// Execute request
			final HttpGet request1 = new HttpGet("http://localhost:8080/sse");
			Future<HttpResponse> future = httpclient.execute(request1, null);
			// and wait until a response is received
			HttpResponse response1 = future.get();
			String responseAsString = EntityUtils.toString(response1.getEntity());
			System.err.println(request1.getRequestLine() + "->" + responseAsString);
			

			// In real world one most likely would also want to stream
			// request and response body content
			final HttpGet request = new HttpGet("http://localhost:8080/sse");
			final CountDownLatch latch = new CountDownLatch(1);
			HttpAsyncRequestProducer producer = HttpAsyncMethods.create(request);
			AsyncCharConsumer<HttpResponse> consumer = new AsyncCharConsumer<HttpResponse>() {

				HttpResponse response;

				@Override
				protected void onResponseReceived(final HttpResponse response) {
					System.err.print("sse -> " + response.toString());
					this.response = response;
				}

				@Override
				protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
					System.err.print("sse -> " + buf.toString());
				}

				@Override
				protected void releaseResources() {
				}

				@Override
				protected HttpResponse buildResult(final HttpContext context) {

					System.err.println("sse -> " + request.getRequestLine() + " -> " + response.getEntity());

					return this.response;
				}

			};
			httpclient.execute(producer, consumer, new FutureCallback<HttpResponse>() {

				public void completed(final HttpResponse response3) {
					latch.countDown();
					System.err.println("sse -> " + request.getRequestLine() + " -> " + response3.getStatusLine());
				}

				public void failed(final Exception ex) {
					latch.countDown();
					System.out.println(request.getRequestLine() + "->" + ex);
				}

				public void cancelled() {
					latch.countDown();
					System.out.println(request.getRequestLine() + " cancelled");
				}

			});
			latch.await();
		} finally {
			httpclient.close();
		}
	}

}

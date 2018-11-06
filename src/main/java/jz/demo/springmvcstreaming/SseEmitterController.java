package jz.demo.springmvcstreaming;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class SseEmitterController {
	private ExecutorService nonBlockingService = Executors.newCachedThreadPool();

	@GetMapping("/sse")
	public SseEmitter handleSse() {
		SseEmitter emitter = new SseEmitter();

		nonBlockingService.execute(() -> {
			try {

				MyIterator iterator = new MyIterator();

				while (iterator.hasNext()) {

					emitter.send("{\"date\": \""
							+ new Date()
							+ "\"}", MediaType.APPLICATION_JSON_UTF8);
				}

				emitter.complete();
			} catch (Exception ex) {
				emitter.completeWithError(ex);
			}
		});
		return emitter;
	}
}
package jz.demo.springmvcstreaming;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Controller
public class ResponseBodyEmitterController {

	private ExecutorService executor = Executors.newCachedThreadPool();

	@GetMapping("/http_streaming")
	public ResponseEntity<ResponseBodyEmitter> handleRbe() {
		ResponseBodyEmitter emitter = new ResponseBodyEmitter();
		executor.execute(() -> {
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
		return new ResponseEntity<ResponseBodyEmitter>(emitter, HttpStatus.OK);
	}
}
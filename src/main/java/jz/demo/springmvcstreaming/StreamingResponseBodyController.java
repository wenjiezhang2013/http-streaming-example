package jz.demo.springmvcstreaming;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Controller
public class StreamingResponseBodyController {

	@GetMapping("/streaming_body")
	public ResponseEntity<StreamingResponseBody> handleRbe() {
		StreamingResponseBody stream = out -> {
			MyIterator iterator = new MyIterator();

			while (iterator.hasNext()) {
				String msg = "{\"date\": \""
						+ new Date()
						+ "\"}";
				out.write(msg.getBytes());
			}
		};
		return new ResponseEntity<StreamingResponseBody>(stream, HttpStatus.OK);
	}
}
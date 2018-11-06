package jz.demo.springmvcstreaming;

import java.util.Date;
import java.util.Iterator;

public class MyIterator implements Iterator<String> {
	
	int max = 10;

	@Override
	public boolean hasNext() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		max--;
		if(max > 0)
			return true;
		else
			return false;
	}

	@Override
	public String next() {
		return new Date().toString();
	}
}
package cyfluxviz;

import static org.junit.Assert.*;

import org.junit.Test;

public class FluxDirectionTest {

	@Test
	public void testToInt() {
		assertEquals(1, FluxDirection.FORWARD.toInt());
		assertEquals(-1, FluxDirection.REVERSE.toInt());
	}

}

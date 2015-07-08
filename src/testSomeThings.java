import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;


public class testSomeThings {

	@Test
	public void testColorCompare(){
		
		Color color1 = new Color(200,200,200);
		Color color2 = new Color(210,210,210);
		Color color3 = new Color(210,210,0);
		
		assertTrue(ColorExtended.colorsAreSimilarRGB(color1,color2));
		
		assertEquals(false,ColorExtended.colorsAreSimilarRGB(color1,color3));
	}
	
	@Test
	public void testColorCompareHSB(){
		assertEquals(true,ColorExtended.colorsAreSimilarHSB(Color.blue, Color.blue.darker()));
	}
}

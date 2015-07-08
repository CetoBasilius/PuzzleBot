import java.awt.Color;


public abstract class ColorExtended {


	private static final int TOLERANCE_RED = 16;
	private static final int TOLERANCE_GREEN = 36;
	private static final int TOLERANCE_BLUE = 36;
	
	
	private static final int TOLERANCE_HUE = 8;
	private static final int TOLERANCE_SATURATION = 136;
	private static final int TOLERANCE_BRIGHTNESS = 136;
	
	

	public static boolean colorsAreSimilarRGB(Color colorA,Color colorB){
		int colorAint = colorA.getRGB();
		int red1 = ((colorAint & 0x00ff0000) >> 16);
		int green1 = ((colorAint & 0x0000ff00) >> 8);
		int blue1 = ((colorAint & 0x000000ff) >> 0);
		
		
		int colorBint = colorB.getRGB();
		int red2 = ((colorBint & 0x00ff0000) >> 16);
		int green2 = ((colorBint & 0x0000ff00) >> 8);
		int blue2 = ((colorBint & 0x000000ff) >> 0);
		
		if(((red1+TOLERANCE_RED)>red2) && ((red1-TOLERANCE_RED)<red2)){
			if(((green1+TOLERANCE_GREEN)>green2) && ((green1-TOLERANCE_GREEN)<green2)){
				if(((blue1+TOLERANCE_BLUE)>blue2) && ((blue1-TOLERANCE_BLUE)<blue2)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	public static boolean colorsAreSimilarHSB(Color colorA,Color colorB){
	
		int colorAint = colorA.getRGB(); 
		
		int red1 = ((colorAint & 0x00ff0000) >> 16);
		int green1 = ((colorAint & 0x0000ff00) >> 8);
		int blue1 = ((colorAint & 0x000000ff) >> 0);
		
		float hsbValuesA[] = new float[3];
		Color.RGBtoHSB(red1,green1,blue1, hsbValuesA);
		hsbValuesA[0]*=240;
		hsbValuesA[1]*=240;
		hsbValuesA[2]*=240;
		
		
		int colorBint = colorB.getRGB();
		int red2 = ((colorBint & 0x00ff0000) >> 16);
		int green2 = ((colorBint & 0x0000ff00) >> 8);
		int blue2 = ((colorBint & 0x000000ff) >> 0);
		
		float hsbValuesB[] = new float[3];
		Color.RGBtoHSB(red2,green2,blue2, hsbValuesB);
		hsbValuesB[0]*=240;
		hsbValuesB[1]*=240;
		hsbValuesB[2]*=240;
		

		if(((hsbValuesA[0]+TOLERANCE_HUE)>hsbValuesB[0]) && ((hsbValuesA[0]-TOLERANCE_HUE)<hsbValuesB[0])){
			if(((hsbValuesA[1]+TOLERANCE_SATURATION)>hsbValuesB[1]) && ((hsbValuesA[1]-TOLERANCE_SATURATION)<hsbValuesB[1])){
				if(((hsbValuesA[2]+TOLERANCE_BRIGHTNESS)>hsbValuesB[2]) && ((hsbValuesA[2]-TOLERANCE_BRIGHTNESS)<hsbValuesB[2])){
					return true;
				}
			}
		}

		
		return false;
	}
	
	
	
	
	
	
	public static boolean colorsAreSimilarRGBHardCoded(Color colorA,Color colorB){
		
		int colorAint = colorA.getRGB();
		int red1 = ((colorAint & 0x00ff0000) >> 16);
		int green1 = ((colorAint & 0x0000ff00) >> 8);
		int blue1 = ((colorAint & 0x000000ff) >> 0);
		
		
		int colorBint = colorB.getRGB();
		int red2 = ((colorBint & 0x00ff0000) >> 16);
		int green2 = ((colorBint & 0x0000ff00) >> 8);
		int blue2 = ((colorBint & 0x000000ff) >> 0);
		
		if(((red1+TOLERANCE_RED)>red2) && ((red1-TOLERANCE_RED)<red2)){
			if(((green1+TOLERANCE_GREEN)>green2) && ((green1-TOLERANCE_GREEN)<green2)){
				if(((blue1+TOLERANCE_BLUE)>blue2) && ((blue1-TOLERANCE_BLUE)<blue2)){
					return true;
				}
			}
		}
		
		//Yellow with coin
		if(colorA.equals(new Color(199,170,45))){
			if(colorB.equals(new Color(246,216,0))){
				System.out.println("found yellow with coin");
				return true;
			}
		}
		if(colorB.equals(new Color(199,170,45))){
			if(colorA.equals(new Color(246,216,0))){
				System.out.println("found yellow with coin");
				return true;
			}
		}
		

		//Yellow with coin 	199,170,45		---		32,151,115
		//Yellow Normal 	246,216,0		---		35,240,216
		//Yellow x3			176,173,0		---		39,240,83
		
		//Purple x2			160,19,162		--		199,190,85
		//Purple x3 		160,19,162		---		199,190,85
		//Purple Normal 	244,9,244		---		200,224,119
		
		//Red Shiny			255,13,33		---		237,240,126
		//Red Nromal		254,0,22		---		237,240,120
		
		//Blue x2			0,108,208		---		139,240,98
		//Blue Normal		0,127,253		---		140,240,119
		
		//Orange x2			228,90,0		---		16,240,107
		//Orange Normal		254,114,11		---		17,238,125
		
		//White x2			128,128,128		---		160,0,120
		//White fire		243,243,243		---		40,10,228
		//White Normal		242,242,242		---		160,0,228
		//White shiny		255,255,255		---		160,0,240
		
		return false;
	}
	
	
	public static boolean colorsAreSimilarHSBHardCoded(Color colorA,Color colorB){
		
		int colorAint = colorA.getRGB(); 
		
		int red1 = ((colorAint & 0x00ff0000) >> 16);
		int green1 = ((colorAint & 0x0000ff00) >> 8);
		int blue1 = ((colorAint & 0x000000ff) >> 0);
		
		float hsbValuesA[] = new float[3];
		Color.RGBtoHSB(red1,green1,blue1, hsbValuesA);
		hsbValuesA[0]*=240;
		hsbValuesA[1]*=240;
		hsbValuesA[2]*=240;
		
		
		int colorBint = colorB.getRGB();
		int red2 = ((colorBint & 0x00ff0000) >> 16);
		int green2 = ((colorBint & 0x0000ff00) >> 8);
		int blue2 = ((colorBint & 0x000000ff) >> 0);
		
		float hsbValuesB[] = new float[3];
		Color.RGBtoHSB(red2,green2,blue2, hsbValuesB);
		hsbValuesB[0]*=240;
		hsbValuesB[1]*=240;
		hsbValuesB[2]*=240;
		

		if(((hsbValuesA[0]+TOLERANCE_HUE)>hsbValuesB[0]) && ((hsbValuesA[0]-TOLERANCE_HUE)<hsbValuesB[0])){
			if(((hsbValuesA[1]+TOLERANCE_SATURATION)>hsbValuesB[1]) && ((hsbValuesA[1]-TOLERANCE_SATURATION)<hsbValuesB[1])){
				if(((hsbValuesA[2]+TOLERANCE_BRIGHTNESS)>hsbValuesB[2]) && ((hsbValuesA[2]-TOLERANCE_BRIGHTNESS)<hsbValuesB[2])){
					return true;
				}
			}
		}
		
		//Yellow with coin
		if(colorA.equals(new Color(199,170,45))){
			if(colorB.equals(new Color(246,216,0))){
				System.out.println("found yellow with coin");
				return true;
			}
		}

		//Yellow with coin
		if(colorB.equals(new Color(199,170,45))){
			System.out.println("found yellow with coin");
			if(colorA.equals(new Color(246,216,0))){
				return true;
			}
		}


		return false;
	}
}

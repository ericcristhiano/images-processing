package core;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageProcessing {
	public static Image grayScale(Image imagem, int red, int green, int blue, int x1, int y1, int x2, int y2) {
		try {
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			
			PixelReader pixelReader = imagem.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					Color originalColor = pixelReader.getColor(x, y);
					double average = calcAverage(originalColor.getRed(), red, originalColor.getGreen(), green, originalColor.getBlue(), blue);
					Color newColor = new Color(average, average, average, originalColor.getOpacity());
					
					if (x1 > 0 && y1 > 0 && x2 > 0 && y2 > 0) {
						if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
							pixelWriter.setColor(x, y, newColor);
						} else {
							pixelWriter.setColor(x, y, originalColor);							
						}
					}
					
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image limiarizacao(Image imagem, int red, int green, int blue, double limiar) {
		try {
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			
			PixelReader pixelReader = imagem.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int i = 0; i < imageWidth; i++) {
				for(int j = 0; j < imageHeight; j++) {
					Color originalColor = pixelReader.getColor(i, j);
					double average = calcAverage(originalColor.getRed(), red, originalColor.getGreen(), green, originalColor.getBlue(), blue);
					
					int color = 0;
							
					if (average >= limiar) {
						color = 1;
					}
					
					Color newColor = new Color(color, color, color, originalColor.getOpacity());						
					pixelWriter.setColor(i, j, newColor);
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image negative(Image imagem) {
		try {
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			
			PixelReader pixelReader = imagem.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int i = 0; i < imageWidth; i++) {
				for(int j = 0; j < imageHeight; j++) {
					Color originalColor = pixelReader.getColor(i, j);
					Color newColor = new Color(1 - originalColor.getRed(),  1 - originalColor.getGreen(), 1 - originalColor.getBlue(), originalColor.getOpacity());						
					pixelWriter.setColor(i, j, newColor);
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image challenge01(Image imagem) {
		try {
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			
			PixelReader pixelReader = imagem.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int x = 0; x < imageWidth; x++) {
				int region = getRegion(imageWidth, x, 4);
				
				for(int y = 0; y < imageHeight; y++) {
					Color originalColor = pixelReader.getColor(x, y);
					Color newColor;
					double average;
					
					switch (region) {
						case 1:
							newColor = new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), originalColor.getOpacity());						
							pixelWriter.setColor(x, y, newColor);
							break;
						case 2:
							average = (originalColor.getRed() + originalColor.getGreen() +  originalColor.getBlue()) / 3;
							newColor = new Color(average, average, average, originalColor.getOpacity());						
							pixelWriter.setColor(x, y, newColor);
							break;
						case 3:
							average = (originalColor.getRed() + originalColor.getGreen() +  originalColor.getBlue()) / 3;
							int color = 0;
							
							if (average >= 0.5) {
								color = 1;
							}
							
							newColor = new Color(color, color, color, originalColor.getOpacity());						
							pixelWriter.setColor(x, y, newColor);
							break;
						case 4:
							newColor = new Color(1 - originalColor.getRed(),  1 - originalColor.getGreen(), 1 - originalColor.getBlue(), originalColor.getOpacity());						
							pixelWriter.setColor(x, y, newColor);
							break;
						default:
							break;
					}
				}
			}
			
			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image noise(Image image, NeighborType neighborsType) {
		try {
			int imageWidth = (int)image.getWidth();
			int imageHeight = (int)image.getHeight();

			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					Pixel pixel = new Pixel(x, y, image);
					Color newColor = pixel.getMedianColor(neighborsType);					
					pixelWriter.setColor(x, y, newColor);
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image subtract(Image image1, Image image2) {
		try {
			int minWidth = (int)Math.min(image1.getWidth(), image2.getWidth());
			int minHeight = (int)Math.min(image1.getHeight(), image2.getHeight());
			
			PixelReader pixelReader1 = image1.getPixelReader();
			PixelReader pixelReader2 = image2.getPixelReader();
			WritableImage writableImage = new WritableImage(minWidth, minHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int i = 0; i < minWidth; i++) {
				for(int j = 0; j < minHeight; j++) {
					Color originalColor1 = pixelReader1.getColor(i, j);
					Color originalColor2 = pixelReader2.getColor(i, j);
					
					double rColor = originalColor1.getRed() - originalColor2.getRed();
					double gColor = originalColor1.getGreen() - originalColor2.getGreen();
					double bColor = originalColor1.getBlue() - originalColor2.getBlue();
					
					rColor = rColor < 0 ? 0 : rColor;
					gColor = gColor < 0 ? 0 : gColor;
					bColor = bColor < 0 ? 0 : bColor;
					
					Color newColor = new Color(rColor, gColor, bColor, 1);
					pixelWriter.setColor(i, j, newColor);
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image add(Image image1, Image image2, double opacity) {
		try {
			int minWidth = (int)Math.min(image1.getWidth(), image2.getWidth());
			int minHeight = (int)Math.min(image1.getHeight(), image2.getHeight());
			
			PixelReader pixelReader1 = image1.getPixelReader();
			PixelReader pixelReader2 = image2.getPixelReader();
			WritableImage writableImage = new WritableImage(minWidth, minHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int i = 0; i < minWidth; i++) {
				for(int j = 0; j < minHeight; j++) {
					Color originalColor1 = pixelReader1.getColor(i, j);
					Color originalColor2 = pixelReader2.getColor(i, j);
					
					double rColor = originalColor1.getRed() * opacity + originalColor2.getRed() * (1 - opacity);
					double gColor = originalColor1.getGreen() * opacity + originalColor2.getGreen() * (1 - opacity);
					double bColor = originalColor1.getBlue() * opacity + originalColor2.getBlue() * (1 - opacity);
					
					Color newColor = new Color(rColor, gColor, bColor, 1);
					pixelWriter.setColor(i, j, newColor);
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image markImage(Image image, int x1, int y1, int x2, int y2) {
		try {
			int imageWidth = (int)image.getWidth();
			int imageHeight = (int)image.getHeight();
			
			PixelReader pixelReader = image.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					Color originalColor = pixelReader.getColor(x, y);
					pixelWriter.setColor(x, y, originalColor);
				}
			}
			
			for (int i = x1; i < x2; i += 10) {
				Color newColor = new Color(1, 0, 0, 1);
				
				for (int j = 0; j < 5; j++) {
					pixelWriter.setColor(i + j, y1, newColor);
					pixelWriter.setColor(i + j, y2, newColor);
				}
			}
			
			for (int i = y1; i < y2; i += 10) {
				Color newColor = new Color(1, 0, 0, 1);
				
				for (int j = 0; j < 5; j++) {
					pixelWriter.setColor(x1, i + j, newColor);
					pixelWriter.setColor(x2, i + j, newColor);					
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static double calcAverage(double number1, int weight1, double number2, int weight2, double number3, int weight3) {
		return (number1 * weight1 + number2 * weight2 + number3 * weight3) / (weight1 + weight2 + weight3);
	}
	
	private static int getRegion(int width, int x, int numberOfRegions) {
		int portion = width / numberOfRegions;
		
		for (int i = 1; i <= numberOfRegions; i++) {
			if (x <= i * portion) {
				return i;
			}
		}
		
		return 1;
	}
}

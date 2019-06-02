package core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
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
			
			for (int i = x1; i < x2; i++) {
				Color newColor = new Color(0, 0, 0, 1);
				pixelWriter.setColor(i, y1, newColor);
				pixelWriter.setColor(i, y2, newColor);
			}
			
			for (int i = y1; i < y2; i++) {
				Color newColor = new Color(0, 0, 0, 1);
				pixelWriter.setColor(x1, i, newColor);
				pixelWriter.setColor(x2, i, newColor);
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static int[] calcHistogram(Image image, CanalType canal) {
		int[] quantityTotal = new int[256];
		
		try {
			int imageWidth = (int)image.getWidth();
			int imageHeight = (int)image.getHeight();
			
			PixelReader pixelReader = image.getPixelReader();
		
			for (int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					Color originalColor = pixelReader.getColor(x, y);
					
					switch (canal) {
						case BLUE:
							quantityTotal[(int) (originalColor.getBlue() * 255)]++;
							break;
						case RED:
							quantityTotal[(int) (originalColor.getRed() * 255)]++;
							break;
						case GREEN:
							quantityTotal[(int) (originalColor.getGreen() * 255)]++;						
							break;
						case UNIQUE:
							quantityTotal[(int) (originalColor.getBlue() * 255)]++;
							quantityTotal[(int) (originalColor.getRed() * 255)]++;
							quantityTotal[(int) (originalColor.getGreen() * 255)]++;
							break;
					}
				}
			}

			return quantityTotal;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static int[] calcAccumulatedHistogram(int[] histogram) {
		int[] accumulatedHistogram = new int[256];
		int accumulated = 0;
		
		for (int i = 0; i < histogram.length; i++) {
			accumulated += histogram[i];
			accumulatedHistogram[i] = accumulated;
		}
		
		return accumulatedHistogram;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void plotChart(Image image, BarChart<String, Number> chart) {
		int[] histogram = calcHistogram(image, CanalType.BLUE);
		
		XYChart.Series series = new XYChart.Series();
		
		for (int i = 0; i < histogram.length; i++) {
			series.getData().add(new XYChart.Data(i+"", histogram[i]));
		}

		chart.getData().addAll(series);
	}
	
	public static Image equalizeHistogram(Image imagem, boolean trueColors) {
		try {
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			
			PixelReader pixelReader = imagem.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			int[] quantityTotalRed = calcHistogram(imagem, CanalType.RED);
			int[] quantityTotalAccumulatedRed = calcAccumulatedHistogram(quantityTotalRed);
			int countRedValid = getCountValidColors(quantityTotalRed);
			
			int[] quantityTotalGreen = calcHistogram(imagem, CanalType.GREEN);
			int[] quantityTotalAccumulatedGreen = calcAccumulatedHistogram(quantityTotalGreen);
			int countGreenValid = getCountValidColors(quantityTotalGreen);
			
			int[] quantityTotalBlue = calcHistogram(imagem, CanalType.BLUE);
			int[] quantityTotalAccumulatedBlue = calcAccumulatedHistogram(quantityTotalBlue);
			int countBlueValid = getCountValidColors(quantityTotalBlue);
			
			int maxColor = getMaxValue(quantityTotalGreen);
			int minColor = getMinValue(quantityTotalGreen);
			
			for (int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					Color originalColor = pixelReader.getColor(x, y);
					
					double newRed = calcEqualizedColor(trueColors ? countRedValid : 255, imageWidth * imageHeight, quantityTotalAccumulatedRed[(int)(originalColor.getRed() * 255)], trueColors ? minColor : 0, maxColor);
					double newGreen = calcEqualizedColor(trueColors ? countGreenValid : 255, imageWidth * imageHeight, quantityTotalAccumulatedGreen[(int)(originalColor.getGreen() * 255)], trueColors ? minColor : 0, maxColor);
					double newBlue = calcEqualizedColor(trueColors ? countBlueValid : 255, imageWidth * imageHeight, quantityTotalAccumulatedBlue[(int)(originalColor.getBlue() * 255)], trueColors ? minColor : 0, maxColor);
					
					Color newColor = new Color(newRed, newGreen, newBlue, originalColor.getOpacity());		
					pixelWriter.setColor(x, y, newColor);
					
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image simulation1(Image imagem, int quantity, Color color) {
		try {
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			
			PixelReader pixelReader = imagem.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int i = 0; i < imageWidth; i++) {
				for(int j = 0; j < imageHeight; j++) {
					Color originalColor = pixelReader.getColor(i, j);
					Color newColor = new Color(originalColor.getRed(),  originalColor.getGreen(), originalColor.getBlue(), originalColor.getOpacity());
					
					if (i % quantity == 0) {
						newColor = color;
					}
					
					pixelWriter.setColor(i, j, newColor);
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image simulation2(Image imagem) {
		try {
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			
			PixelReader pixelReader = imagem.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int i = 0; i < imageWidth; i++) {
				for(int j = 0; j < imageHeight; j++) {
					Color originalColor = pixelReader.getColor(i, j);
					if (j < imageHeight / 2) {
						pixelWriter.setColor(i, j, originalColor);						
					} else {
						int newPosition = ((imageHeight / 2) + (imageHeight - j));
						pixelWriter.setColor(i, newPosition - 1, originalColor);
					}
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static HashSet<String> simulation3(Image imagem, int x1, int y1, int x2, int y2) {
		try {
			HashSet<String> colors = new HashSet<>();
			
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			
			PixelReader pixelReader = imagem.getPixelReader();
			
			for (int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					
					if (x1 > 0 && y1 > 0 && x2 > 0 && y2 > 0) {
						if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
							Color originalColor = pixelReader.getColor(x, y);
							
							if (originalColor.getRed() > originalColor.getBlue() && originalColor.getRed() > originalColor.getGreen()) {
								colors.add("Vermelho");
							}
							
							if (originalColor.getBlue() > originalColor.getRed() && originalColor.getBlue() > originalColor.getGreen()) {
								colors.add("Azul");
							}
							
							if (originalColor.getGreen() > originalColor.getRed() && originalColor.getGreen() > originalColor.getBlue()) {
								colors.add("Verde");
							}
						}
					}
				}
			}

			return colors;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image increase(Image imagem) {
		try {
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			
			PixelReader pixelReader = imagem.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth * 2, imageHeight * 2);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					Color originalColor = pixelReader.getColor(x, y);
					Color newColor = new Color(originalColor.getRed(),  originalColor.getGreen(), originalColor.getBlue(), originalColor.getOpacity());
					int newX = x * 2;
					int newY = y * 2;

					pixelWriter.setColor(newX, newY, newColor);
					pixelWriter.setColor(newX, newY + 1, newColor);
					pixelWriter.setColor(newX + 1, newY, newColor);
					pixelWriter.setColor(newX + 1, newY + 1, newColor);
					
					if (y > 0) {
						pixelWriter.setColor(newX + 1, newY - 1, newColor);
						pixelWriter.setColor(newX, newY - 1, newColor);
					}
					
					if (x > 0) {
						pixelWriter.setColor(newX - 1, newY + 1, newColor);
						pixelWriter.setColor(newX - 1, newY, newColor);
					}
					
					if (x > 0 && y > 0) {
						pixelWriter.setColor(newX - 1, newY - 1, newColor);						
					}					
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image decrease(Image imagem) {
		try {
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			
			PixelReader pixelReader = imagem.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth / 2, imageHeight / 2);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int i = 0; i < imageWidth / 2; i++) {
				for(int j = 0; j < imageHeight / 2; j++) {
					Color originalColor = pixelReader.getColor(i * 2, j * 2);
					Color newColor = new Color(originalColor.getRed(),  originalColor.getGreen(), originalColor.getBlue(), originalColor.getOpacity());						
					pixelWriter.setColor(i, j, newColor);
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image rotate90Degrees(Image imagem, int x1, int y1, int x2, int y2) {
		try {
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			
			PixelReader pixelReader = imagem.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					Color originalColor = pixelReader.getColor(x, y);
					
					if (x1 > 0 && y1 > 0 && x2 > 0 && y2 > 0) {
						int selectedHeight = y2 - y1;
						int selectedWidth = x2 - x1;

						int newY = (y1 + selectedHeight) - (y - y1);
						int newX = (x1 + selectedWidth) - (x - x1);
						
						if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
							pixelWriter.setColor(newX, newY, originalColor);
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
	
	public static Image rotate90(Image img1) {

		try {
			int w1 = (int)img1.getWidth();
			int h1 = (int)img1.getHeight();

			PixelReader pr1 = img1.getPixelReader();
			
			WritableImage wi = new WritableImage(h1,w1);
			PixelWriter pw = wi.getPixelWriter();
			
			int m = w1 - 1;
			for (int i = 0; i < w1; i++) {
				int n = h1;
				for (int j = 0; j < h1; j++) {
					Color prevColor = pr1.getColor(i, j);

					double color1 = (prevColor.getRed());
					double color2 = (prevColor.getGreen());
					double color3 = (prevColor.getBlue());
					
					Color newColor = new Color(color1, color2, color3, prevColor.getOpacity());
					
					pw.setColor(h1 - n, m, newColor);
					n--;
				}
				m--;
			}
			return wi;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image evaluation1Question1(Image imagem, int columns) {
		try {
			int imageWidth = (int)imagem.getWidth();
			int imageHeight = (int)imagem.getHeight();
			int columnWidth = imageWidth / columns;
			boolean grayScale = true;
			
			PixelReader pixelReader = imagem.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();

			for (int x = 0; x < imageWidth; x++) {
				if (x > 0 && x % columnWidth == 0) {
					grayScale = !grayScale;
				}
				
				for(int y = 0; y < imageHeight; y++) {
					Color originalColor = pixelReader.getColor(x, y);
					
					if (grayScale) {
						double average = calcAverage(originalColor.getRed(), 1, originalColor.getGreen(), 1, originalColor.getBlue(), 1);
						Color newColor = new Color(average, average, average, originalColor.getOpacity());
						pixelWriter.setColor(x, y, newColor);						
					} else {
						pixelWriter.setColor(x, y, originalColor);						
					}
				}
			}

			return writableImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image evaluation1Question2(Image image, int x1, int y1, int x2, int y2) {
		try {
			int imageWidth = (int)image.getWidth();
			int imageHeight = (int)image.getHeight();
			
			PixelReader pixelReader = image.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					Color originalColor = pixelReader.getColor(x, y);
					
					if (x1 > 0 && y1 > 0 && x2 > 0 && y2 > 0) {
						int selectedHeight = y2 - y1;
						int selectedWidth = x2 - x1;

						int newY = (y1 + selectedHeight) - (y - y1);
						int newX = (x1 + selectedWidth) - (x - x1);
						
						if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
							pixelWriter.setColor(newX, newY, originalColor);
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
	
	public static boolean evaluation1Question3(Image image) {
		try {
			int imageWidth = (int)image.getWidth();
			int imageHeight = (int)image.getHeight();
			
			PixelReader pixelReader = image.getPixelReader();
			
			for (int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					Color originalColor = pixelReader.getColor(x, y);

					if (originalColor.getRed() == 0 && originalColor.getGreen() == 0 && originalColor.getBlue() == 0) {
						Color rightDiagonalColor = pixelReader.getColor(x + 1, y + 1);
						
						if (rightDiagonalColor.getRed() == 0 && rightDiagonalColor.getGreen() == 0 && rightDiagonalColor.getBlue() == 0) {
							return true;
						} else {
							return false;
						}
					}
				}
			}

			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static Image faceDetection(Image originalImage, Image image1, Image image2) {
		File fileOriginalImage = new File("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
		File fileImage1 = new File("C:\\Users\\Eric\\Pictures\\img\\src\\image1.png");
		File fileImage2 = new File("C:\\Users\\Eric\\Pictures\\img\\src\\image2.png");
		
		BufferedImage bufferedOriginalImg = null;
		BufferedImage bufferedImg1 = null;
		BufferedImage bufferedImg2 = null;
		
		if (originalImage != null) {
			bufferedOriginalImg = SwingFXUtils.fromFXImage(originalImage, null);			
		}
		
		if (image1 != null) {
			bufferedImg1 = SwingFXUtils.fromFXImage(image1, null);			
		}
		
		if (image2 != null) {
			bufferedImg2 = SwingFXUtils.fromFXImage(image2, null);			
		}

		try {
			if (bufferedOriginalImg != null) {
				ImageIO.write(bufferedOriginalImg, "PNG", fileOriginalImage);				
			}
			
			if (bufferedImg1 != null ) {
				ImageIO.write(bufferedImg1, "PNG", fileImage1);
			}
			
			if (bufferedImg2 != null) {
				ImageIO.write(bufferedImg2, "PNG", fileImage2);				
			}

			CascadeClassifier faceDetector = new CascadeClassifier("resources/haarcascades/haarcascade_frontalface_alt.xml");
			Mat image = Imgcodecs.imread("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
			MatOfRect faceDetections = new MatOfRect();
			faceDetector.detectMultiScale(image, faceDetections);
			System.out.println("Detectado: " + faceDetections.toArray().length);
			
			for (Rect rect : faceDetections.toArray()) {
				Imgproc.rectangle(
						image,
						new Point(rect.x, rect.y),
						new Point(rect.x + rect.width, rect.y + rect.height),
						new Scalar(0, 255, 0), 3
				);
			}
			
			Imgcodecs.imwrite("C:\\Users\\Eric\\Pictures\\img\\src\\result.png", image);
			
			return new Image(new File("C:\\Users\\Eric\\Pictures\\img\\src\\result.png").toURI().toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image dilation(Image originalImage, Image image1, Image image2) {
		File fileOriginalImage = new File("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
		
		BufferedImage bufferedOriginalImg = null;
		
		if (originalImage != null) {
			bufferedOriginalImg = SwingFXUtils.fromFXImage(originalImage, null);			
		}

		try {
			if (bufferedOriginalImg != null) {
				ImageIO.write(bufferedOriginalImg, "PNG", fileOriginalImage);				
			}

			Mat imageCv = Imgcodecs.imread("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
			Mat destination = new Mat(imageCv.rows(), imageCv.cols(), imageCv.type());
			
			int dilation_size = 20;
			
			Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilation_size + 1, 2 * dilation_size + 1));
			Imgproc.dilate(imageCv, destination, element1);
			Imgcodecs.imwrite("C:\\Users\\Eric\\Pictures\\img\\src\\result.png", destination);
			
			return new Image(new File("C:\\Users\\Eric\\Pictures\\img\\src\\result.png").toURI().toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image erosion(Image originalImage, Image image1, Image image2) {
		File fileOriginalImage = new File("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
		
		BufferedImage bufferedOriginalImg = null;
		
		if (originalImage != null) {
			bufferedOriginalImg = SwingFXUtils.fromFXImage(originalImage, null);			
		}
		
		try {
			if (bufferedOriginalImg != null) {
				ImageIO.write(bufferedOriginalImg, "PNG", fileOriginalImage);				
			}
			
			Mat imageCv = Imgcodecs.imread("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
			Mat destination = new Mat(imageCv.rows(), imageCv.cols(), imageCv.type());
			
			int erosonSize = 20;
			Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * erosonSize + 1, 2 * erosonSize + 1));
			
			Imgproc.erode(imageCv, destination, element);
			Imgcodecs.imwrite("C:\\Users\\Eric\\Pictures\\img\\src\\result.png", destination);
			
			return new Image(new File("C:\\Users\\Eric\\Pictures\\img\\src\\result.png").toURI().toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image canny(Image originalImage, Image image1, Image image2) {
		File fileOriginalImage = new File("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
		BufferedImage bufferedOriginalImg = null;
		
		if (originalImage != null) {
			bufferedOriginalImg = SwingFXUtils.fromFXImage(originalImage, null);			
		}
		
		try {
			if (bufferedOriginalImg != null) {
				ImageIO.write(bufferedOriginalImg, "PNG", fileOriginalImage);				
			}
			
			Mat imageCv = Imgcodecs.imread("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
			Mat destination = new Mat(imageCv.rows(), imageCv.cols(), imageCv.type());
			Imgproc.cvtColor(imageCv, destination, Imgproc.COLOR_BGR2GRAY);
			Imgproc.blur(destination, destination, new Size(3, 3));
			
			int threshold = 3;
			int ratio = 50;
			int kernelSize = 3;
			Imgproc.Canny(destination, destination, 10, 100);
//			Imgproc.Canny(destination, destination, threshold, threshold * ratio, kernelSize, false);
			
			Imgcodecs.imwrite("C:\\Users\\Eric\\Pictures\\img\\src\\result.png", destination);
			
			return new Image(new File("C:\\Users\\Eric\\Pictures\\img\\src\\result.png").toURI().toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image sobel(Image originalImage, Image image1, Image image2) {
		File fileOriginalImage = new File("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
		BufferedImage bufferedOriginalImg = null;
		
		if (originalImage != null) {
			bufferedOriginalImg = SwingFXUtils.fromFXImage(originalImage, null);			
		}
		
		try {
			if (bufferedOriginalImg != null) {
				ImageIO.write(bufferedOriginalImg, "PNG", fileOriginalImage);				
			}
			
			Mat imageCv = Imgcodecs.imread("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
			Mat destination = new Mat(imageCv.rows(), imageCv.cols(), imageCv.type());
			Imgproc.cvtColor(imageCv, destination, Imgproc.COLOR_BGR2GRAY);
			Imgproc.blur(destination, destination, new Size(3, 3));
			
			Imgproc.Sobel(destination, destination, -2, 0, 2);
			
			Imgcodecs.imwrite("C:\\Users\\Eric\\Pictures\\img\\src\\result.png", destination);
			
			return new Image(new File("C:\\Users\\Eric\\Pictures\\img\\src\\result.png").toURI().toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image laplace(Image originalImage, Image image1, Image image2) {
		File fileOriginalImage = new File("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
		BufferedImage bufferedOriginalImg = null;
		
		if (originalImage != null) {
			bufferedOriginalImg = SwingFXUtils.fromFXImage(originalImage, null);			
		}
		
		try {
			if (bufferedOriginalImg != null) {
				ImageIO.write(bufferedOriginalImg, "PNG", fileOriginalImage);				
			}
			
			Mat src = Imgcodecs.imread("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
			
			// Declare the variables we are going to use
	        Mat src_gray = new Mat(), dst = new Mat();
	        int kernel_size = 3;
	        int scale = 1;
	        int delta = 0;
	        int ddepth = CvType.CV_16S;
	        
	        // Reduce noise by blurring with a Gaussian filter ( kernel size = 3 )
	        Imgproc.GaussianBlur( src, src, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT );
	        // Convert the image to grayscale
	        Imgproc.cvtColor( src, src_gray, Imgproc.COLOR_RGB2GRAY );
	        Mat abs_dst = new Mat();
	        Imgproc.Laplacian( src_gray, dst, ddepth, kernel_size, scale, delta, Core.BORDER_DEFAULT );
	        // converting back to CV_8U
	        Core.convertScaleAbs( dst, abs_dst );
			
			Imgcodecs.imwrite("C:\\Users\\Eric\\Pictures\\img\\src\\result.png", dst);
			
			return new Image(new File("C:\\Users\\Eric\\Pictures\\img\\src\\result.png").toURI().toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image test(Image originalImage, Image image1, Image image2) {
		File fileOriginalImage = new File("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
		BufferedImage bufferedOriginalImg = null;
		
		if (originalImage != null) {
			bufferedOriginalImg = SwingFXUtils.fromFXImage(originalImage, null);			
		}
		
		try {
			if (bufferedOriginalImg != null) {
				ImageIO.write(bufferedOriginalImg, "PNG", fileOriginalImage);				
			}
			
			Mat imageCv = Imgcodecs.imread("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
			Mat destination = new Mat(imageCv.rows(), imageCv.cols(), imageCv.type());
			Imgproc.cvtColor(imageCv, destination, Imgproc.COLOR_BGR2GRAY); // grayscale
			Imgproc.threshold(imageCv, destination, 0, 255, Imgproc.THRESH_OTSU); // threshold
			
			
//			Imgproc.blur(destination, destination, new Size(3, 3));
			
//			Imgproc.Sobel(destination, destination, -2, 0, 2);
			
			Imgcodecs.imwrite("C:\\Users\\Eric\\Pictures\\img\\src\\result.png", destination);
			
			return new Image(new File("C:\\Users\\Eric\\Pictures\\img\\src\\result.png").toURI().toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image prewitt(Image originalImage, Image image1, Image image2) {
		File fileOriginalImage = new File("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
		BufferedImage bufferedOriginalImg = null;
		
		if (originalImage != null) {
			bufferedOriginalImg = SwingFXUtils.fromFXImage(originalImage, null);			
		}
		
		try {
			if (bufferedOriginalImg != null) {
				ImageIO.write(bufferedOriginalImg, "PNG", fileOriginalImage);				
			}

            int kernelSize = 9;
            Mat kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F) {
                {
                    put(0,0,-1);
                    put(0,1,0);
                    put(0,2,1);

                    put(1,0-1);
                    put(1,1,0);
                    put(1,2,1);

                    put(2,0,-1);
                    put(2,1,0);
                    put(2,2,1);
                }
            };

			
			Mat imageCv = Imgcodecs.imread("C:\\Users\\Eric\\Pictures\\img\\src\\originalImage.png");
			Mat destination = new Mat(imageCv.rows(), imageCv.cols(), imageCv.type());
			
			Imgproc.filter2D(imageCv, destination, -1, kernel);
			Imgcodecs.imwrite("C:\\Users\\Eric\\Pictures\\img\\src\\result.png", destination);
			
			return new Image(new File("C:\\Users\\Eric\\Pictures\\img\\src\\result.png").toURI().toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image segmentation(Image image) {
		try {
			int imageWidth = (int)image.getWidth();
			int imageHeight = (int)image.getHeight();

			PixelReader pixelReader = image.getPixelReader();
			WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			
			for (int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					Color originalColor = pixelReader.getColor(x, y);
					Color newColor;
					
					Pixel pixel = new Pixel(x, y, image);
					
					for (Pixel neighbor: pixel.getAllNeighbors()) {
						if (neighbor == null) {
							continue;							
						}
						
						double neighborRed = pixelReader.getColor(neighbor.getX(), neighbor.getY()).getRed();
						double neighborGreen = pixelReader.getColor(neighbor.getX(), neighbor.getY()).getGreen();
						double neighborBlue = pixelReader.getColor(neighbor.getX(), neighbor.getY()).getBlue();
						
						double differenceRed = Math.abs(originalColor.getRed() - neighborRed);
						double differenceGreen = Math.abs(originalColor.getGreen() - neighborGreen);
						double differenceBlue = Math.abs(originalColor.getBlue() - neighborBlue);
						
						pixelWriter.setColor(x, y, originalColor);
						if (differenceRed < 0.03 && differenceGreen < 0.03 && differenceBlue < 0.03) {
							pixelWriter.setColor(x, y, new Color(1, 0, 0, 1));
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
	
	private static int getCountValidColors(int[] quantityTotal) {
		int counter = 0;
		
		for (int i = 0; i < quantityTotal.length; i++) {
			if (quantityTotal[i] > 0) {
				counter++;
			}
		}

		return counter;
	}
	
	private static int getMaxValue(int[] quantityTotal) {
		int max = 0;
		
		for (int i = 0; i < quantityTotal.length; i++) {
			if (quantityTotal[i] != 0) {
				max = i;
			}
		}

		return max;
	}
	
	private static int getMinValue(int[] quantityTotal) {
		int min = 255;
		
		for (int i = quantityTotal.length - 1; i > 0; i--) {
			if (quantityTotal[i] != 0) {
				min = i;
			}
		}

		return min;
	}
	
	private static double calcEqualizedColor(int totalColors, int totalPixels, int accumulated, int minColor, int maxColor) {
		return ((((totalColors - 1.0) / totalPixels) * accumulated) + minColor) / 255;
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

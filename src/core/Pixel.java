package core;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class Pixel {
	private int x;
	private int y;
	private int imageWidth;
	private int imageHeight;
	private Image image;
	private PixelReader pixelReader;
	private Pixel[] xNeighbors = new Pixel[4];
	private Pixel[] crossNeighbors = new Pixel[4];
	private Pixel[] allNeighbors = new Pixel[8];
	
	public Pixel(int x, int y, Image image) {
		this.x = x;
		this.y = y;
		this.imageWidth = (int)image.getWidth();
		this.imageHeight = (int)image.getHeight();
		this.image = image;
		this.pixelReader = image.getPixelReader();
	}
	
	public Pixel[] getXNeighbors() {
		this.xNeighbors[0] = this.getTopLeftNeighbor();
		this.xNeighbors[1] = this.getTopRightNeighbor();
		this.xNeighbors[3] = this.getBottomRightNeighbor();
		this.xNeighbors[2] = this.getBottomLeftNeighbor();
		
		return this.xNeighbors;
	}
	
	public Pixel[] getCrossNeighbors() {
		this.crossNeighbors[0] = this.getTopNeighbor();
		this.crossNeighbors[1] = this.getRightNeighbor();
		this.crossNeighbors[2] = this.getBottomNeighbor();
		this.crossNeighbors[3] = this.getLeftNeighbor();
		
		return this.crossNeighbors;
	}
	
	public Pixel[] getAllNeighbors() {
		this.allNeighbors[0] = this.getTopNeighbor();
		this.allNeighbors[1] = this.getTopRightNeighbor();
		this.allNeighbors[2] = this.getRightNeighbor();
		this.allNeighbors[3] = this.getBottomRightNeighbor();
		this.allNeighbors[4] = this.getBottomNeighbor();
		this.allNeighbors[5] = this.getBottomLeftNeighbor();
		this.allNeighbors[6] = this.getLeftNeighbor();
		this.allNeighbors[7] = this.getTopLeftNeighbor();
		
		return this.allNeighbors;
	}
	
	@Override
	public String toString() {
		return "Pixel [x=" + x + ", y=" + y + "]";
	}
	
	public Color getMedianColor(NeighborType neighborsType) {
		ArrayList<Color> colors = new ArrayList<Color>(); 
		colors.add(pixelReader.getColor(x, y));
		
		switch (neighborsType) {
			case ALL_NEIGHBORS:
				for (Pixel pixel : getAllNeighbors()) {
					if (pixel != null) {
						colors.add(pixelReader.getColor(pixel.x, pixel.y));
					}
				}
				break;
			case X_NEIGHBORS:
				for (Pixel pixel : getXNeighbors()) {
					if (pixel != null) {
						colors.add(pixelReader.getColor(pixel.x, pixel.y));
					}
				}
				break;
			case CROSS_NEIGHBORS:
				for (Pixel pixel : getCrossNeighbors()) {
					if (pixel != null) {
						colors.add(pixelReader.getColor(pixel.x, pixel.y));
					}
				}
				break;
			default:
				break;
		}
		
		double colorsR[] = new double[colors.size()];
		double colorsG[] = new double[colors.size()];
		double colorsB[] = new double[colors.size()];
		
		for (int i = 0; i < colors.size(); i++) {
			colorsR[i] = colors.get(i).getRed();
			colorsG[i] = colors.get(i).getGreen();
			colorsB[i] = colors.get(i).getBlue();
		}
		
		Arrays.sort(colorsR);
		Arrays.sort(colorsG);
		Arrays.sort(colorsB);
		
		int medianPosition = Math.round(colors.size() / 2);
		
		return new Color(colorsR[medianPosition], colorsG[medianPosition], colorsB[medianPosition], pixelReader.getColor(x, y).getOpacity());
	}

	private Pixel getTopNeighbor() {
		if (!this.isTopBorder()) {
			return new Pixel(x, y - 1, image);
		}
		
		return null;
	}
	
	private Pixel getTopRightNeighbor() {
		if (!this.isTopBorder() && !this.isRightBorder()) {
			return new Pixel(x + 1, y - 1, image);
		}
		
		return null;
	}
	
	private Pixel getRightNeighbor() {
		if (!this.isRightBorder()) {
			return new Pixel(x + 1, y, image);
		}

		return null;
	}
	
	private Pixel getBottomRightNeighbor() {
		if (!this.isBottomBorder() && !this.isRightBorder()) {
			return new Pixel(x + 1, y + 1, image);
		}

		return null;
	}
	
	private Pixel getBottomNeighbor() {
		if (!this.isBottomBorder()) {
			return new Pixel(x, y + 1, image);
		}

		return null;
	}
	
	private Pixel getBottomLeftNeighbor() {
		if (!this.isBottomBorder() && !this.isLeftBorder()) {
			return new Pixel(x - 1, y + 1, image);
		}

		return null;
	}
	
	private Pixel getLeftNeighbor() {
		if (!this.isLeftBorder()) {
			return new Pixel(x - 1, y, image);
		}

		return null;
	}
	
	private Pixel getTopLeftNeighbor() {
		if (!this.isTopBorder() && !this.isLeftBorder()) {
			return new Pixel(x - 1, y - 1, image);
		}

		return null;
	}

	private boolean isLeftBorder() {
		return this.x == 0;
	}
	
	private boolean isRightBorder() {
		return this.x == this.imageWidth - 1;
	}
	
	private boolean isTopBorder() {
		return this.y == 0;
	}
	
	private boolean isBottomBorder() {
		return this.y == this.imageHeight - 1;
	}
}

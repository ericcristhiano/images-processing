package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.opencv.imgcodecs.Imgcodecs;

import core.ImageProcessing;
import core.NeighborType;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {
	@FXML
	private ImageView imageView1;

	@FXML
	private ImageView imageView2;
	
	@FXML
	private ImageView resultImageView;
	
	private Image originalImage;
	private Image image1;
	private Image image2;
	private Image resultImage;
	
	private File file1;
	private File file2;
	
	@FXML
	private Label labelR;
	
	@FXML
	private Label labelG;
	
	@FXML
	private Label labelB;
	
	@FXML
	private Pane colorPane;
	
	@FXML
	private TextField grayscaleRed;
	
	@FXML
	private TextField grayscaleGreen;
	
	@FXML
	private TextField grayscaleBlue;
	
	@FXML
	private Slider sliderLimiarizacao;
	
	@FXML
	private RadioButton xNeighbors;
	
	@FXML
	private RadioButton crossNeighbors;
	
	@FXML
	private RadioButton allNeighbors;
	
	@FXML
	private Slider sliderOpacityImage;
	
	@FXML
	private TextField simulation1Quantity;
	
	@FXML
	private ColorPicker simulation1Color;
	
	@FXML
	private TextField quantityColumns;

	private int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
	
	@FXML
	public void grayScaleAverage() {
		resultImage = ImageProcessing.grayScale(originalImage, Integer.parseInt(grayscaleRed.getText()), Integer.parseInt(grayscaleGreen.getText()), Integer.parseInt(grayscaleBlue.getText()), x1, y1, x2, y2);
		updateResultImageView();
	}
	
	@FXML
	public void limiarizacao() {
		resultImage = ImageProcessing.limiarizacao(image1, Integer.parseInt(grayscaleRed.getText()), Integer.parseInt(grayscaleGreen.getText()), Integer.parseInt(grayscaleBlue.getText()), sliderLimiarizacao.getValue());
		updateResultImageView();
	}
	
	@FXML
	public void negative() {
		resultImage = ImageProcessing.negative(image1);
		updateResultImageView();
	}
	
	@FXML
	public void noise() {
		NeighborType neighborType = null;
		
		if (allNeighbors.isSelected()) {
			neighborType = NeighborType.ALL_NEIGHBORS;
		} else if (xNeighbors.isSelected()) {
			neighborType = NeighborType.X_NEIGHBORS;
		} else if (crossNeighbors.isSelected()) {
			neighborType = NeighborType.CROSS_NEIGHBORS;
		}
		
		resultImage = ImageProcessing.noise(image1, neighborType);
		
		updateResultImageView();
	}
	
	@FXML
	public void challenge01() {
		resultImage = ImageProcessing.challenge01(image1);
		updateResultImageView();
	}
	
	@FXML
	public void rasterImage(MouseEvent mouseEvent) {
		ImageView imageView = (ImageView)mouseEvent.getTarget();

		if (imageView.getImage() != null) {
			checkColor(imageView.getImage(), (int) mouseEvent.getX(), (int) mouseEvent.getY());
		}
	}
	
	@FXML
	public void pressImage(MouseEvent mouseEvent) {
		ImageView imageView = (ImageView)mouseEvent.getTarget();

		if (imageView.getImage() != null) {
			this.x1 = (int) mouseEvent.getX();
			this.y1 = (int) mouseEvent.getY();
		}
	}
	
	@FXML
	public void releaseImage(MouseEvent mouseEvent) {
		ImageView imageView = (ImageView)mouseEvent.getTarget();

		if (imageView.getImage() != null) {
			this.x2 = (int) mouseEvent.getX();
			this.y2 = (int) mouseEvent.getY();
		}

		image1 = originalImage;
		
		int x1 = Math.min(this.x1, this.x2);
		int x2 = Math.max(this.x1, this.x2);
		int y1 = Math.min(this.y1, this.y2);
		int y2 = Math.max(this.y1, this.y2);
		
		image1 = ImageProcessing.markImage(image1, x1, y1, x2, y2);
		updateImage1();
	}
	
	@FXML
	public void saveImage() {
		if (resultImage != null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagem", "*.png"));
			fileChooser.setInitialDirectory(new File("C:\\Users\\Eric\\Pictures\\img"));
			File file = fileChooser.showSaveDialog(null);
			
			if (file != null) {
				BufferedImage bufferedImg = SwingFXUtils.fromFXImage(resultImage, null);
				
				try {
					ImageIO.write(bufferedImg, "PNG", file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			showAlert("Salvar imagem", "Não é possível salvar a imagem.", "Não há nenhuma imagem modificada.", AlertType.ERROR);
		}
	}
	
	@FXML
	public void subtract() {
		resultImage = ImageProcessing.subtract(image1, image2);
		updateResultImageView();
	}
	
	@FXML
	public void add() {
		resultImage = ImageProcessing.add(image1, image2, sliderOpacityImage.getValue());
		updateResultImageView();
	}

	@FXML
	public void applyArticle() {
		Image removeNoise = ImageProcessing.noise(originalImage, NeighborType.ALL_NEIGHBORS);
		Image gaussianImage = ImageProcessing.gaussian(removeNoise, removeNoise, image2);
		Image grayscaleImage = ImageProcessing.grayscaleCv(gaussianImage, gaussianImage, image2);
		Image cannyImage = ImageProcessing.canny(grayscaleImage, grayscaleImage, image2);
		Image dilatedImage = ImageProcessing.dilation(cannyImage, cannyImage, image2);
		resultImage = ImageProcessing.findAndDrawContours(dilatedImage, dilatedImage, image2);
		resultImage = ImageProcessing.findAndDrawContours(resultImage, resultImage, image2);

		this.saveImage("C:\\Users\\Eric\\Pictures\\img\\src\\processo\\1 - Original.png", originalImage);
	    this.saveImage("C:\\Users\\Eric\\Pictures\\img\\src\\processo\\2 - Ruído.png", removeNoise);
	    this.saveImage("C:\\Users\\Eric\\Pictures\\img\\src\\processo\\3 - Gaussiano.png", gaussianImage);
	    this.saveImage("C:\\Users\\Eric\\Pictures\\img\\src\\processo\\4 - Escala de Cinza.png", grayscaleImage);
	    this.saveImage("C:\\Users\\Eric\\Pictures\\img\\src\\processo\\5 - Canny.png", cannyImage);
	    this.saveImage("C:\\Users\\Eric\\Pictures\\img\\src\\processo\\6 - Dilatação.png", dilatedImage);
	    this.saveImage("C:\\Users\\Eric\\Pictures\\img\\src\\processo\\7 - Find And Draw Countours.png", resultImage);
	    
	    resultImage = ImageProcessing.add(originalImage, resultImage, 1);
	    
	    this.saveImage("C:\\Users\\Eric\\Pictures\\img\\src\\processo\\8 - Final.png", resultImage);

//		resultImage = dilatedImage;
//		resultImage = ImageProcessing.grayscaleCv(originalImage, image1, image2);
		updateResultImageView();
	}
	
	private void saveImage(String path, Image image) {
		File outputFile = new File(path);
	    BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

	    try {
	      ImageIO.write(bImage, "png", outputFile);
	    } catch (IOException e) {
	      throw new RuntimeException(e);
	    }
	}

	@FXML
	public void openImageSelector1() {
		file1 = selectImage();
		
		if (file1 != null) {
			image1 = new Image(file1.toURI().toString());
			originalImage = new Image(file1.toURI().toString());
			imageView1.setImage(image1);
			imageView1.setFitWidth(image1.getWidth());
			imageView1.setFitHeight(image1.getHeight());
		}
	}
	
	@FXML
	public void openImageSelector2() {
		file2 = selectImage();
		
		if (file2 != null) {
			image2 = new Image(file2.toURI().toString());
			imageView2.setImage(image2);
			imageView2.setFitWidth(image2.getWidth());
			imageView2.setFitHeight(image2.getHeight());
		}
	}
	
	@FXML
	public void openHistogramModal(ActionEvent actionEvent) {
		try {
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Histogram.fxml"));
			Parent root = loader.load();
			stage.setScene(new Scene(root));
			stage.setTitle("Histogram");
			stage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
			stage.show();
			
			HistogramController histogramController = (HistogramController)loader.getController();
			
			if (image1 != null) {
				ImageProcessing.plotChart(image1, histogramController.chart1);
			}
			
			if (image2 != null) {
				ImageProcessing.plotChart(image2, histogramController.chart2);
			}
			
			if (resultImage != null) {
				ImageProcessing.plotChart(resultImage, histogramController.chart3);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void equalizeHistogram() {
		resultImage = ImageProcessing.equalizeHistogram(image1, false);
		updateResultImageView();
	}
	
	@FXML
	public void equalizeHistogramWithColors() {
		resultImage = ImageProcessing.equalizeHistogram(image1, true);
		updateResultImageView();
	}
	
	@FXML
	public void simulation1() {
		int quantity = Integer.parseInt(simulation1Quantity.getText());
		resultImage = ImageProcessing.simulation1(originalImage, quantity, simulation1Color.getValue());
		updateResultImageView();
	}
	
	@FXML
	public void question2() {
		resultImage = ImageProcessing.simulation2(originalImage);
		updateResultImageView();
	}
	
	@FXML
	public void question3() {
		HashSet<String> colors = ImageProcessing.simulation3(originalImage, x1, y1, x2, y2);
		String colorsString = "";
		
		for (String color: colors) {
			colorsString += color + "\n";
		}
		
		showAlert("Cores encontradas", "Cores na seleção", colorsString, AlertType.INFORMATION);
	}
	
	@FXML
	public void increaseSize() {
		resultImage = ImageProcessing.increase(originalImage);
		updateResultImageView();
	}
	
	@FXML
	public void decreaseSize() {
		resultImage = ImageProcessing.decrease(originalImage);
		updateResultImageView();
	}
	
	@FXML
	public void rotate90Degrees() {
		resultImage = ImageProcessing.rotate90Degrees(originalImage, x1, y1, x2, y2);
		originalImage = resultImage;
		updateResultImageView();
	}
	
	@FXML
	public void evaluation1Question1() {
		resultImage = ImageProcessing.evaluation1Question1(image1, Integer.parseInt(quantityColumns.getText()));
		updateResultImageView();
	}
	
	@FXML
	public void evaluation1Question2() {
		resultImage = ImageProcessing.evaluation1Question2(image1, x1, y1, x2, y2);
		updateResultImageView();
	}
	
	@FXML
	public void evaluation1Question3() {
		boolean filledSquare = ImageProcessing.evaluation1Question3(image1);
		
		showAlert("Característica do quadrado", null, "O quadrado é " + (filledSquare ? "preenchido!" : "vazio!"), AlertType.INFORMATION);
	}
	
	@FXML
	public void segmentation() {
		resultImage = ImageProcessing.segmentation(originalImage);
		updateResultImageView();
	}
	
	@FXML
	public void faceDetection() {
		resultImage = ImageProcessing.faceDetection(originalImage, image1, image2);
		
		updateResultImageView();
	}
	
	@FXML
	public void dilation() {
		resultImage = ImageProcessing.dilation(originalImage, image1, image2);
		
		updateResultImageView();
	}
	
	@FXML
	public void erosion() {
		resultImage = ImageProcessing.erosion(originalImage, image1, image2);
		
		updateResultImageView();
	}
	
	@FXML
	public void canny() {
		resultImage = ImageProcessing.canny(originalImage, image1, image2);
		
		updateResultImageView();
	}
	
	@FXML
	public void laplace() {
		resultImage = ImageProcessing.laplace(originalImage, image1, image2);
		
		updateResultImageView();
	}
	
	@FXML
	public void sobel() {
		resultImage = ImageProcessing.sobel(originalImage, image1, image2);
		
		updateResultImageView();
	}
	
	@FXML
	public void prewitt() {
		resultImage = ImageProcessing.prewitt(originalImage, image1, image2);
		
		updateResultImageView();
	}
	
	@FXML
	public void test() {
		resultImage = ImageProcessing.test(originalImage, image1, image2);
		
		updateResultImageView();
	}
	
	@FXML
	public void gaussian() {
		resultImage = ImageProcessing.gaussian(originalImage, image1, image2);
		
		updateResultImageView();
	}
	
	@FXML
	public void findAndDrawContours() {
		resultImage = ImageProcessing.findAndDrawContours(originalImage, image1, image2);
		
		updateResultImageView();
	}
	
	private void updateResultImageView() {
		resultImageView.setImage(resultImage);
		resultImageView.setFitWidth(resultImage.getWidth());
		resultImageView.setFitHeight(resultImage.getHeight());
	}
	
	private void updateImage1() {
		imageView1.setImage(image1);
		imageView1.setFitWidth(image1.getWidth());
		imageView1.setFitHeight(image1.getHeight());
	}
	
	private void showAlert(String titulo, String cabecalho, String msg, AlertType tipo) {
		Alert alert = new Alert(tipo);
		alert.setTitle(titulo);
		alert.setHeaderText(cabecalho);
		alert.setContentText(msg);
		alert.showAndWait();
	}
	
	private void checkColor(Image img, int x, int y){
		try {
			Color color = img.getPixelReader().getColor(x-1, y-1);
			labelR.setText(""+(int) (color.getRed()*255));
			labelG.setText(""+(int) (color.getGreen()*255));
			labelB.setText(""+(int) (color.getBlue()*255));
			colorPane.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	private File selectImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
			"Imagens", "*.jpg", "*.JPG", 
			"*.JPEG", "*.jpeg",
			"*.png", "*.PNG", "*.gif", "*.GIF", 
			"*.bmp", "*.BMP"));

		fileChooser.setInitialDirectory(new File("C:\\Users\\Eric\\Pictures\\img"));

		return fileChooser.showOpenDialog(null);
	}
}

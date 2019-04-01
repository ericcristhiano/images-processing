package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import core.ImageProcessing;
import core.NeighborType;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

public class MainController {
	@FXML
	private ImageView imageView1;

	@FXML
	private ImageView imageView2;
	
	@FXML
	private ImageView resultImageView;
	
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
	public void grayScaleAverage() {
		resultImage = ImageProcessing.grayScale(image1, Integer.parseInt(grayscaleRed.getText()), Integer.parseInt(grayscaleGreen.getText()), Integer.parseInt(grayscaleBlue.getText()));
		updateImage3();
	}
	
	@FXML
	public void limiarizacao() {
		resultImage = ImageProcessing.limiarizacao(image1, Integer.parseInt(grayscaleRed.getText()), Integer.parseInt(grayscaleGreen.getText()), Integer.parseInt(grayscaleBlue.getText()), sliderLimiarizacao.getValue());
		updateImage3();
	}
	
	@FXML
	public void negative() {
		resultImage = ImageProcessing.negative(image1);
		updateImage3();
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
		
		updateImage3();
	}
	
	@FXML
	public void challenge01() {
		resultImage = ImageProcessing.challenge01(image1);
		updateImage3();
	}
	
	@FXML
	public void rasterImage(MouseEvent mouseEvent) {
		ImageView imageView = (ImageView)mouseEvent.getTarget();

		if (imageView.getImage() != null) {
			checkColor(imageView.getImage(), (int) mouseEvent.getX(), (int) mouseEvent.getY());
		}
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
	public void openImageSelector1() {
		file1 = selectImage();
		
		if (file1 != null) {
			image1 = new Image(file1.toURI().toString());
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
	
	private void updateImage3() {
		resultImageView.setImage(resultImage);
		resultImageView.setFitWidth(resultImage.getWidth());
		resultImageView.setFitHeight(resultImage.getHeight());
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

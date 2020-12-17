
import java.io.File;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class GUI 
{
	private static Text textArea = new Text();
	
	public GUI(Stage stage)
	{
		stage.setTitle("Trame decoder V1.0");
		
		Label label = new Label("Please select the file you wish to decode! (.txt only)");
		label.setMaxWidth(Double.MAX_VALUE);
		label.setAlignment(Pos.BASELINE_CENTER);
		
		
		Button openFileButton = new Button("Open file");
		openFileButton.setOnAction(e -> {
									FileChooser fileChooser = new FileChooser();
									File selectedFile = fileChooser.showOpenDialog(stage);
									String fileName = selectedFile.getPath();
									
									try {
										textArea.setText("");
										Analyser.readFrame(fileName);
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									
									});
		
		
		ScrollPane sp = new ScrollPane();
		sp.setContent(textArea);
		
		BorderPane root= new BorderPane();
		Scene scene = new Scene(root, 960, 600);
		root.setTop(openFileButton);
		root.setCenter(sp);
		root.setBottom(label);
		
		BorderPane.setAlignment(openFileButton, Pos.CENTER);
		BorderPane.setAlignment(label, Pos.CENTER);
		BorderPane.setAlignment(textArea, Pos.TOP_CENTER);

		Insets insets = new Insets(10);
		BorderPane.setMargin(openFileButton, insets);
		BorderPane.setMargin(label, insets);
		BorderPane.setMargin(textArea, insets);
		
		stage.setScene(scene);
		stage.show();
	}
	
	
	
	public static void println(String s)
	{
	    Platform.runLater(new Runnable() 
	    {//in case you call from other thread
	    	
	        @Override
	        public void run() 
	        {
	            textArea.setText(textArea.getText()+s+"\n");
	            System.out.println(s);//for echo if you want
	        }
	    });
	}
}

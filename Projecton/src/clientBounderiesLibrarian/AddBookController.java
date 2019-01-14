package clientBounderiesLibrarian;

import com.jfoenix.controls.JFXButton;
import java.net.*;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;

import Common.Book;
import Common.IGUIController;
import Common.ObjectMessage;
import clientCommonBounderies.AClientCommonUtilities;
import clientCommonBounderies.StartPanelController;
import clientConrollers.AValidationInput;
import clientConrollers.OBLClient;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class AddBookController implements IGUIController
{
	OBLClient client;
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

    @FXML // fx:id="TopicTextField"
    private JFXTextField TopicTextField; 
    @FXML
    private JFXTextField BookTitleTextField;

    @FXML 
    private JFXTextField numberOfCopies; 

	@FXML
	private JFXTextField BookAuthorTextField;

	@FXML
	private JFXTextField PublishedYearTextField;

	@FXML
	private JFXTextField EditionTextField;


	@FXML
	private JFXCheckBox DesiredCheckBox;

	@FXML
	private ChoiceBox<String> TopicChoiceBox;

	@FXML
	private DatePicker PurchaseDateDatePicker;

	@FXML
	private Button UploadBtn;

	@FXML
	private JFXButton CancelBtn;

	@FXML
	private JFXButton SaveBtn;

    @FXML
    private Label fileLabel;
	
	//function for cancel button. if librarian doesn't want  continue to add a book
	@FXML
	void cancelBtnClicked(ActionEvent event) 
	{
		AClientCommonUtilities.backToStartPanel();
	}
	
  

	//add new book or only copy
	@FXML
	void saveAddNewBook(ActionEvent event) 
	{ 
		/*
		if(validationFields().equals("correct"))//if all fields correctly
		{
			System.out.println("all right)))))");
			boolean isDesired= DesiredCheckBox.isSelected();
			Book book=new Book(BookTitleTextField.getText(), BookAuthorTextField.getText(),PublishedYearTextField.getText(),TopicTextField.getText(),String.valueOf(isDesired),EditionTextField.getText(),numberOfCopies.getText());
			System.out.println(String.valueOf(isDesired));
			ObjectMessage msg= new ObjectMessage(book,"addBook","Book");
			client.handleMessageFromClient(msg);
		}
		else
		{
			System.out.println("Errorrrrrrr!");
			ObjectMessage massage=new ObjectMessage("The fields you filled are not correctly.","Wrong") ;
			AClientCommonUtilities.alertErrorWithOption(massage.getMessage(), massage.getNote(),"Back");
			
			
		}*/
		
		boolean isDesired= DesiredCheckBox.isSelected();
		Book book=new Book(BookTitleTextField.getText(), BookAuthorTextField.getText(),PublishedYearTextField.getText(),TopicTextField.getText(),String.valueOf(isDesired),EditionTextField.getText(),numberOfCopies.getText());
		System.out.println(String.valueOf(isDesired));
		ObjectMessage msg= new ObjectMessage(book,"addBook","Book");
		client.handleMessageFromClient(msg);
		
		
	}
	
	
	//function upload file and send it to server/ sending- still not writing
	@FXML
	void uploadTableOfContents(ActionEvent event) 
	{
		FileChooser fc=new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter("PDF Files","*.pdf"));
		File f=fc.showOpenDialog(null);
		if(f!=null)
		{
			fileLabel.setText("Selected File ::" + f.getAbsolutePath());
		}
		
		
		
		
		
	}

	
	@FXML
	void initialize() 
	{
		client=StartPanelController.connToClientController;
		client.setClientUI(this);
		

	}
	
	//check validation
	 private String validationFields()
	 {
		 String result,finalResult="";

		 result=AValidationInput.checkValidationBook("bookName",BookTitleTextField.getText());
		 if(!result.equals("correct"))
		 {
			 finalResult+=result+"\n";
		 }
			 

		 result=AValidationInput.checkValidationBook("authorName",BookAuthorTextField.getText());
		 if(!result.equals("correct"))
		 {
			 finalResult+=result+"\n";
		 }

		 result=AValidationInput.checkValidationBook("dateOfBook",PublishedYearTextField.getText());
		 if(!result.equals("correct"))
		 {
			 finalResult+=result+"\n";
		 }

		 result=AValidationInput.checkValidationBook("topic",TopicChoiceBox.getAccessibleText());
		 if(!result.equals("correct"))
		 {
			 finalResult+=result+"\n";
		 }
	    	if (fileLabel.getText().equals(null))//check if client upload file 
	    	{
	    		finalResult+=result+"\n";
	    	}
	
	    	if(finalResult.equals(""))
	    	{
	    		return "correct";
	    	}
	    		
	    	else
	    	{
	    		return finalResult;
	    	}
	    		
	    }

	
	@Override
	public void display(ObjectMessage msg)
	{
		if (msg.getNote().equals("Successfull"))
		{
			AClientCommonUtilities.infoAlert(msg.getMessage(), msg.getNote());
			Platform.runLater(()->
			{   
				AClientCommonUtilities.backToStartPanel(); 
			});
		
			
		}
		
		if (msg.getNote().equals("Unsuccessfull"))
		{
			AClientCommonUtilities.alertErrorWithExit(msg.getMessage(), msg.getNote());
		}
		if (msg.getNote().equals("Wrong"))
		{
			AClientCommonUtilities.alertErrorWithOption(msg.getMessage(), msg.getNote(),"Back");
		}
		
	}
}

package clientBounderiesLibrarian;

import com.jfoenix.controls.JFXTextField;

import Common.IEntity;
import Common.IGUIController;
import Common.ObjectMessage;
import Common.Report;
import clientCommonBounderies.StartPanelController;
import clientConrollers.OBLClient;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class ReportsController implements IGUIController
{

	OBLClient client;
	
    @FXML
    private TabPane TabPaneSelect;
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField activeReaderAccounts;

    @FXML
    private TextField freezedReaderAccounts;

    @FXML
    private TextField lockedReaderAccounts;

    @FXML
    private TextField totalNumOfCopies;

    @FXML
    private TextField numOfdelayedReaderAccounts;

    @FXML
    private ComboBox<?> monthComboBox;

    @FXML
    private ComboBox<?> yearComboBox;

    @FXML
    private ComboBox<?> chooseFromPreviousComboBox;

    @FXML
    private TextField avgForRegular;

    @FXML
    private TextField medianForRegular;

    @FXML
    private BarChart<?, ?> diagramForRegular;

    @FXML
    private TextField avgForDesired;

    @FXML
    private TextField medianForDesired;

    @FXML
    private BarChart<?, ?> diagramForDesired;

    @FXML
    private TextField avgForAll;

    @FXML
    private TextField medianForAll;

    @FXML
    private BarChart<?, ?> diagramForAll;

    @FXML
    private TextField avgForNumLateReturns;

    @FXML
    private TextField medianForNumLateReturns;

    @FXML
    private BarChart<?, ?> diagramForNumLateReturns;

    @FXML
    private TextField avgForDurationLateReturns;

    @FXML
    private TextField medianForDurationLateReturns;

    @FXML
    private JFXTextField BookIDReport3;

    @FXML
    void initialize() 
    {
    	client=StartPanelController.connToClientController;
    	client.setClientUI(this);
    	ObjectMessage sendToServer=new ObjectMessage("Ask for report2","History");
    	client.setClientUI(this);
		client.handleMessageFromClient(sendToServer); 
    	
    	

    }

	@Override
	public void display(ObjectMessage msg) 
	{
		
			if(msg.getNote().equals("Report number 2"))
			{
				ArrayList <IEntity> result=msg.getObjectList(); //get the array list received from the server
				
				float avgRegular=((Report)result.get(0)).getAverage();
				float medianRegular=((Report)result.get(0)).getMedian();
				avgForRegular.setText(String.valueOf(avgRegular));
				medianForRegular.setText(String.valueOf(medianRegular));
				
				float averageDesired=((Report)result.get(1)).getAverage();
				float medianDesired=((Report)result.get(1)).getMedian();
				avgForDesired.setText(String.valueOf(averageDesired));
				medianForDesired.setText(String.valueOf(medianDesired));
			
				float averageAll=((Report)result.get(2)).getAverage();
				float medianAll=((Report)result.get(1)).getMedian();
				avgForAll.setText(String.valueOf(averageAll));
				medianForAll.setText(String.valueOf(medianAll));
			}

		
		
		

	}
}

package com.ontariotechu.sofe3980U;


import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
    public static void main( String[] args )
    {
		String filePath="model_1.csv";
		FileReader filereader;
		List<String[]> allData;
		try{
			filereader = new FileReader(filePath); 
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
			allData = csvReader.readAll();
		}
		catch(Exception e){
			System.out.println( "Error reading the CSV file" );
			return;
		}
		
		int count=0;
		for (String[] row : allData) { 
			int y_true=Integer.parseInt(row[0]);
			float y_predicted=Float.parseFloat(row[1]);
			System.out.print(y_true + "  \t  "+y_predicted); 
			System.out.println(); 
			count++;
			if (count==10){
				break;
			}
		} 

	}

	public static double bce(float[][] model) {
    	double epsilon = 1e-10;
    	double bce = 0.0;

    	for (int i = 0; i < model.length; i++) {
        	double yTrue = model[i][0];
        	double yPred = model[i][1];

        	// Keep prediction away from 0 and 1 so log() is valid
        	yPred = Math.max(epsilon, Math.min(1.0 - epsilon, yPred));

        	bce += yTrue * Math.log(yPred) + (1 - yTrue) * Math.log(1 - yPred);
    	}

    	return -bce / model.length;
	}
}

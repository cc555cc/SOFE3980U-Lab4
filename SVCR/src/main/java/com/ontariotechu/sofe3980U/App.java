package com.ontariotechu.sofe3980U;


import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;
import java.util.ArrayList;



/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
	static double epsilon = 1e-10;
	static float[][] model1;
	static float[][] model2;
	static float[][] model3;
	static ArrayList<float[][]> models = new ArrayList<>();
	static double[] mse_list = new double[3];
	static double[] mae_list = new double[3];
	static double[] mare_list = new double[3];

    public static void main( String[] args )
    {
		String[] paths = new String[]{"model_1.csv","model_2.csv","model_3.csv"};
		FileReader filereader;
		List<String[]> allData;

		models.add(model1);
		models.add(model2);
		models.add(model3);

		for(int i = 0; i < models.size(); i++){
			double mse, mae, mare;

			try{
				filereader = new FileReader(paths[i]); 
				CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
				allData = csvReader.readAll();
			}
			catch(Exception e){
				System.out.println( "Error reading the CSV file" );
					return;
			}

			models.set(i, create_model(allData));
			mse = mse(models.get(i));
			mae = mae(models.get(i));
			mare = mare(models.get(i));

			mse_list[i] = mse;
			mae_list[i] = mae;
			mare_list[i] = mare;

			System.out.println("Model"+(i+1)+"---------------------------------------------------------------------");
			System.out.println("MSE: " + mse);
			System.out.println("MAE: " + mae);
			System.out.println("MARE: " + mare);
		}

		System.out.println("According to MSE, The best model is model_"+smallest(mse_list)+".csv");
		System.out.println("According to MAE, The best model is model_"+smallest(mae_list)+".csv");
		System.out.println("According to MARE, The best model is model_"+smallest(mare_list)+".csv");

		
    }

	public static double mse(float[][] model){
		double mean_square_error = 0;
		for(int i = 0; i < model.length; i++){
			double iteration_res = 0;
			double x = model[i][0];
			double y = model[i][1];

			iteration_res = Math.pow((x-y),2);
			mean_square_error += iteration_res;
		}
		
		return mean_square_error / model.length;
	}

	public static double mae(float[][] model){
		double mean_absolute_error = 0;
		for(int i = 0; i < model.length; i++){
			double iteration_res = 0;
			double x = model[i][0];
			double y = model[i][1];

			iteration_res = Math.abs((x-y));
			mean_absolute_error += iteration_res;
		}
		return mean_absolute_error / model.length;
	}

	public static double mare(float[][] model){
		double mare = 0;
		for(int i = 0; i < model.length; i++){
			double iteration_res = 0;
			double x = model[i][0];
			double y = model[i][1];

			iteration_res = Math.abs(x-y)/(Math.abs(x)+epsilon);
			mare += iteration_res;
		}
		return (mare / model.length) * 100;
	}

	public static float[][] create_model(List<String[]> allData){
		float[][] model = new float[allData.size()][2];
		int count = 0;
		
		for (String[] row : allData) { 
			float y_true=Float.parseFloat(row[0]);
			float y_predicted=Float.parseFloat(row[1]);
			//System.out.print(y_true + "  \t  "+y_predicted); 
			//System.out.println(); 
			model[count][0] = y_true;
			model[count][1] = y_predicted;
			count++;
		} 
		return model;
	}

	public static int smallest(double[] values){
		int min = 0;
		for(int i = 1; i < 3; i++){
			if(values[i] < values[min]){
				min = i;
			}
		}

		return min+1;
	}
}

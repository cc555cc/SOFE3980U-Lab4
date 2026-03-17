package com.ontariotechu.sofe3980U;


import java.io.FileReader; 
import java.util.ArrayList;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
	static float[][] model1;
	static float[][] model2;
	static float[][] model3;
	static ArrayList<float[][]> models = new ArrayList<>();
	static double[] bceList = new double[3];
	static double[] accuracyList = new double[3];
	static double[] precisionList = new double[3];
	static double[] recallList = new double[3];
	static double[] f1List = new double[3];
	static double[] aucList = new double[3];
	static int[][] confusionList = new int[3][4];


    public static void main( String[] args )
    {
		String[] paths = new String[]{"model_1.csv","model_2.csv","model_3.csv"};
		FileReader filereader;
		List<String[]> allData;

		models.add(model1);
		models.add(model2);
		models.add(model3);

		for(int i = 0; i < models.size(); i++){
			double bce;
			double accuracy;
			double precision;
			double recall;
			double f1_score;
			double auc_roc;

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
			float[][] model = models.get(i);

			bceList[i] = bce(model);
			accuracyList[i] = accuracy(model);
			precisionList[i] = precision(model);
			recallList[i] = recall(model);
			f1List[i] = f1Score(model);
			aucList[i] = aucRoc(model);

			int[] counts = confusionCounts(model);
			confusionList[i][0] = counts[0]; 
			confusionList[i][1] = counts[1]; 
			confusionList[i][2] = counts[2]; 
			confusionList[i][3] = counts[3]; 

			bce = bceList[i];
			accuracy = accuracyList[i];
			precision = precisionList[i];
			recall = recallList[i];
			f1_score = f1List[i];
			auc_roc = aucList[i];

			System.out.println("Model"+(i+1)+"---------------------------------------------------------------------");
			System.out.println("BCE: " + bce);
			System.out.println("Confusion matrix");
			System.out.println();
			System.out.println("\t\t\ty=1\t\ty=0");
			System.out.println("\ty^=1\t\t" + counts[0] + "\t\t" + counts[2]);
			System.out.println("\ty^=0\t\t" + counts[3] + "\t\t" + counts[1]);
			System.out.println("Accuracy: " + accuracy);
			System.out.println("Precision: " + precision);
			System.out.println("Recall: " + recall);
			System.out.println("F1 Score: " + f1_score);
			System.out.println("AUC-ROC: " + auc_roc);
		}

		System.out.println("According to BCE, The best model is model_" + smallest(bceList) + ".csv");
		System.out.println("According to Accuracy, The best model is model_" + largest(accuracyList) + ".csv");
		System.out.println("According to Precision, The best model is model_" + largest(precisionList) + ".csv");
		System.out.println("According to Recall, The best model is model_" + largest(recallList) + ".csv");
		System.out.println("According to F1 score, The best model is model_" + largest(f1List) + ".csv");
		System.out.println("According to AUC ROC, The best model is model_" + largest(aucList) + ".csv");

			
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

	public static int[][] confusionMatrix(float[][] model) {
    	int tp = 0;
    	int tn = 0;
    	int fp = 0;
    	int fn = 0;

    	for (int i = 0; i < model.length; i++) {
        	int actual = (int) model[i][0];
        	int predicted = (int) model[i][1];

        	if (actual == 1 && predicted == 1) {
            	tp++;
        	} else if (actual == 0 && predicted == 0) {
            	tn++;
        	} else if (actual == 0 && predicted == 1) {
            	fp++;
        	} else if (actual == 1 && predicted == 0) {
         		fn++;
        	}
 		}

			return new int[][] {
				{tp, fp},
				{fn, tn}
			};
		}	

		public static int[] confusionCounts(float[][] model) {
			int tp = 0;
			int tn = 0;
			int fp = 0;
			int fn = 0;

			for (int i = 0; i < model.length; i++) {
				int actual = (int) model[i][0];
				int predicted = model[i][1] >= 0.5 ? 1 : 0;

				if (actual == 1 && predicted == 1) {
					tp++;
				} else if (actual == 0 && predicted == 0) {
					tn++;
				} else if (actual == 0 && predicted == 1) {
					fp++;
				} else if (actual == 1 && predicted == 0) {
					fn++;
				}
			}

			return new int[]{tp, tn, fp, fn};
		}

		public static double accuracy(float[][] model) {
		int[] c = confusionCounts(model);
		int tp = c[0], tn = c[1], fp = c[2], fn = c[3];

		return (double) (tp + tn) / (tp + tn + fp + fn);
	}

	public static double precision(float[][] model) {
		int[] c = confusionCounts(model);
		int tp = c[0], fp = c[2];

		if (tp + fp == 0) return 0.0;
		return (double) tp / (tp + fp);
	}

	public static double recall(float[][] model) {
		int[] c = confusionCounts(model);
		int tp = c[0], fn = c[3];

		if (tp + fn == 0) return 0.0;
		return (double) tp / (tp + fn);
	}

	public static double f1Score(float[][] model) {
		double p = precision(model);
		double r = recall(model);

		if (p + r == 0) return 0.0;
		return 2 * p * r / (p + r);
	}

	public static double aucRoc(float[][] model) {
    double[][] roc = rocCurve(model);
    double[] x = roc[0];
    double[] y = roc[1];

    double auc = 0.0;

		for (int i = 1; i <= 100; i++) {
			auc += ((y[i - 1] + y[i]) * Math.abs(x[i - 1] - x[i])) / 2.0;
		}

			return auc;
		}

	public static float[][] create_model(List<String[]> allData) {
		float[][] model = new float[allData.size()][2];

			for (int i = 0; i < allData.size(); i++) {
				String[] row = allData.get(i);
				model[i][0] = Float.parseFloat(row[0]);
				model[i][1] = Float.parseFloat(row[1]);
		}

			return model;
		}

		public static int smallest(double[] values) {
			int min = 0;
			for (int i = 1; i < values.length; i++) {
				if (values[i] < values[min]) {
					min = i;
				}
			}
			return min + 1;
		}

		public static int largest(double[] values) {
			int max = 0;
			for (int i = 1; i < values.length; i++) {
				if (values[i] > values[max]) {
					max = i;
				}
			}
			return max + 1;
		}

		public static double[][] rocCurve(float[][] model) {
			double[] x = new double[101]; 
			double[] y = new double[101]; 

		int nPositive = 0;
		int nNegative = 0;

		for (int i = 0; i < model.length; i++) {
			if ((int) model[i][0] == 1) {
				nPositive++;
			} else if ((int) model[i][0] == 0) {
				nNegative++;
			}
		}

		for (int i = 0; i <= 100; i++) {
			double th = i / 100.0;
			int tp = 0;
			int fp = 0;

			for (int j = 0; j < model.length; j++) {
				int actual = (int) model[j][0];
				double predicted = model[j][1];

				if (actual == 1 && predicted >= th) {
					tp++;
				}
				if (actual == 0 && predicted >= th) {
					fp++;
				}
			}

			double tpr = (nPositive == 0) ? 0.0 : (double) tp / nPositive;
			double fpr = (nNegative == 0) ? 0.0 : (double) fp / nNegative;

			x[i] = fpr;
			y[i] = tpr;
		}

		return new double[][]{x, y};
	}
}

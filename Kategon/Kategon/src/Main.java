

import java.text.SimpleDateFormat;
import java.util.Calendar;

import runner.Evaluation;
import runner.Train;

public class Main {

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println("引数に誤りがあります。");
			return;
		}
		
		String csvFilePath = args[0];

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String serFilePath = "nb" + "_" + sdf.format(cal.getTime());
		
		Train learner = new Train();
		learner.train(csvFilePath, serFilePath);
		
		Evaluation eval = new Evaluation();
		eval.test(csvFilePath, serFilePath);
		
	}
}

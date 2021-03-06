package etmo.metaheuristics.moead;

import etmo.core.Algorithm;
import etmo.core.Operator;
import etmo.core.ProblemSet;
import etmo.core.SolutionSet;
import etmo.operators.crossover.CrossoverFactory;
import etmo.operators.mutation.MutationFactory;
import etmo.qualityIndicator.QualityIndicator;
import etmo.util.JMException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.HashMap;

public class MOEAD_main {
	public static void main(String[] args) throws JMException, SecurityException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		ProblemSet problemSet; // The problem to solve
		Algorithm algorithm; // The algorithm to use
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator

		HashMap parameters; // Operator parameters

		int taskStart = 3;
		int taskEnd = 40;

		int times = 1;

		DecimalFormat form = new DecimalFormat("#.####E0");

		for (int pCase = taskStart; pCase <= taskEnd; pCase++){
//			problemSet = ETMOF7.getProblem();
			problemSet = (ProblemSet) Class
					.forName("etmo.problems.benchmarks_ETMO.ETMOF" + pCase)
					.getMethod("getProblem")
					.invoke(null, null);

			int taskNum = problemSet.size();

			String[] pf = new String[taskNum];
			double[] ave = new double[taskNum];

			System.out.println("taskNum = "+taskNum);

			for (int tsk = 0; tsk < taskNum; tsk++){
				ProblemSet pS = problemSet.getTask(tsk);
				algorithm = new MOEAD(pS);

				algorithm.setInputParameter("populationSize", 100 * taskNum);
				algorithm.setInputParameter("maxEvaluations", 100 * 1000 * taskNum);

				algorithm.setInputParameter("dataDirectory", "D:\\_r\\EA\\ETMO\\MTO-cec2021-\\resources\\weightVectorFiles\\moead");

				algorithm.setInputParameter("T", 20);
				algorithm.setInputParameter("delta", 0.9);
				algorithm.setInputParameter("nr", 2);

				parameters = new HashMap();
				parameters.put("CR", 1.0);
				parameters.put("F", 0.5);
				crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover",parameters);

				// Mutation operator
				parameters = new HashMap();
				parameters.put("probability", 1.0 / pS.get(0).getNumberOfVariables());
				parameters.put("distributionIndex", 20.0);
				mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);


				algorithm.addOperator("crossover", crossover);
				algorithm.addOperator("mutation", mutation);

				System.out.println("RunID\t" + "IGD for " + problemSet.get(tsk).getName() + " for " + times + " times.");

				pf[tsk] = "PF/StaticPF/" + problemSet.get(tsk).getHType() + "_" + problemSet.get(tsk).getNumberOfObjectives() + "D.pf";
				QualityIndicator indicator = new QualityIndicator(problemSet.get(tsk), pf[tsk]);

				for (int t = 1; t <= times; t++){
					SolutionSet population = algorithm.execute();
					population.printObjectivesToFile("MOEAD_"+problemSet.get(tsk).getNumberOfObjectives()+"Obj_"+
							problemSet.get(tsk).getName()+ "_" + problemSet.get(tsk).getNumberOfVariables() + "D_run"+t+".txt");
					double igd =  indicator.getIGD(population);
					ave[tsk] += igd;
				}
				System.out.println("Average IGD for " + problemSet.get(tsk).getName() + ": " + form.format(ave[tsk] / times));
			}
			System.out.println();
			// for briefly summarization
//			for(int i=0;i<taskNum;i++) {
//				System.out.println("Average IGD for " + problemSet.get(i).getName() + ": " + form.format(ave[i] / times));
//			}
		}
	} // main
} // MOEAD_main

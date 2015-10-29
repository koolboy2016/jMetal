package org.uma.jmetal.algorithm.multiobjective.gawasfga;

import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.gawasfga.util.GAWASFGARanking;
import org.uma.jmetal.algorithm.multiobjective.mombi.util.ASFWASFGA;
import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.WeightVector;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;

public class GAWASFGA<S extends Solution<?>> extends WASFGA<S> {
	//AchievementScalarizingFunction<S> achievementScalarizingFunction ;
	final AbstractUtilityFunctionsSet<S> achievementScalarizingUtopia;
	final AbstractUtilityFunctionsSet<S> achievementScalarizingNadir;
	private static final long serialVersionUID = 1L;
	
	public GAWASFGA(Problem<S> problem, int populationSize, int maxIterations, CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator,
			SolutionListEvaluator<S> evaluator) {
		super(problem, populationSize, maxIterations, crossoverOperator, mutationOperator, selectionOperator, evaluator,
				null);
		this.populationSize			 		= populationSize ;
	    
		double [][] weights =  WeightVector.initUniformWeights2D(0.005, this.populationSize);
		
		int halfVectorSize = weights.length  / 2;
		int evenVectorsSize    = (weights.length%2==0) ? halfVectorSize : (halfVectorSize+1);
		int oddVectorsSize     = halfVectorSize;
		
		double [][] evenVectors = new double[evenVectorsSize][getProblem().getNumberOfObjectives()];
		double [][] oddVectors = new double[oddVectorsSize][getProblem().getNumberOfObjectives()];
		
		int index = 0;
		for (int i = 0; i < weights.length; i = i + 2) 
			evenVectors[index++] = weights[i];
		
		index = 0;
		for (int i = 1; i < weights.length; i = i + 2) 
			oddVectors[index++] = weights[i];
				
		
	    //this.achievementScalarizingUtopia =  createUtilityFunction(this.getUtopiaPoint(), evenVectors);
	    this.achievementScalarizingNadir  =  createUtilityFunction(this.getNadirPoint(), evenVectors);
	    this.achievementScalarizingUtopia =  createUtilityFunction(this.getUtopiaPoint(), oddVectors);
	    
	}
	


	public AbstractUtilityFunctionsSet<S> createUtilityFunction(List<Double> referencePoint, double [][] weights) {	  		  	
		  	weights = WeightVector.invertWeights(weights,true);	    				
			ASFWASFGA<S> aux = new ASFWASFGA<>(weights,referencePoint);
			//aux.setNadir(this.getNadirPoint());
			//aux.setUtopia(this.getUtopiaPoint());
		  	//ASFUtilityFunctionSet<S> aux = new ASFUtilityFunctionSet<>(weights,this.referencePoint);
			return aux;
	  }

	protected Ranking<S> computeRanking(List<S> solutionList) {
		Ranking<S> ranking = new GAWASFGARanking<>(this.achievementScalarizingUtopia, this.achievementScalarizingNadir);
		ranking.computeRanking(solutionList);		
		return ranking;
	}

	

	

}
package cern.ais.gridwars.MachineLearning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kavan on 05/04/17.
 */
public class GeneticAlgorithm {

    // Holds the entire population of chromosomes
    private List<Genome> population = new ArrayList<>();

    // Size of population
    private int populationSize;

    // Amount of weights per chromosome
    private int chromosomeLength;

    // Total fitness of population
    private double totalFitness;

    // Best fitness of population
    private double bestFitness;

    // Average fitness of population
    private double averageFitness;

    // Worst fitness of population
    private double worstFitness;

    // Keeps track of the best genome
    private int fittestGenome;

    // Probability that a chromosome's bits will mutate.
    // Should be figures around 0.05 and 0.3ish.
    private double mutationRate;

    // Probability of chromosomes crossing over bits
    private double crossoverRate;

    // Generation counter
    private int generation;

    public GeneticAlgorithm(int populationSize, double mutationRate,
                            double crossoverRate, int numWeights) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.chromosomeLength = numWeights;
        this.totalFitness = 0;
        this.generation = 0;
        this.fittestGenome = 0;
        this.bestFitness = 0;
        this.worstFitness = 99999999;
        this.averageFitness = 0;

        // Initialize the population with chromosomes consisting of random
        // weights and all fitnesses set to zero
        for (int i = 0; i < populationSize; ++i) {
            population.add(new Genome());
            for (int j = 0; j < chromosomeLength; ++j) {
                population.get(i).weights.add(Utils.randomClamped());
            }
        }
    }

    // Runs the genetic algorithm for one generation. Returns a new population
    // of chromosomes
    public List<Genome> epoch(List<Genome> oldPopulation) {
        // Assign the given population to the classes population
        population = oldPopulation;

        // Reset the appropriate variables
        reset();

        // Sort the population (for scaling and elitism)
        Collections.sort(population);

        // Calculate best, worst, average and total fitness
        calculateBestWorstAverageTotal();

        // Create a temporary list to store new chromosomes
        List<Genome> newPopulation = new ArrayList<>();

        // Add in some copies of the fittest genome to add elitism. Make sure
        // that an EVEN number is added or the roulette wheel sampling will
        // crash.
        if ((Params.NUM_ELITE_COPIES * Params.NUM_ELITE % 2) == 0) {
            grabNBest(Params.NUM_ELITE, Params.NUM_ELITE_COPIES, newPopulation);
        }

        // Enter genetic algorithm loop

        // Repeat until a new population is generated
        while (newPopulation.size() < populationSize) {
            // Grab two chromosomes
            Genome mum = getChromosomeRoulette();
            Genome dad = getChromosomeRoulette();

            // Create some offspring via crossover
            List<Double> baby1 = new ArrayList<>();
            List<Double> baby2 = new ArrayList<>();
            crossover(mum.weights, dad.weights, baby1, baby2);

            // Now mutate
            mutate(baby1);
            mutate(baby2);

            // Now copy into the new population
            newPopulation.add(new Genome(baby1, 0));
            newPopulation.add(new Genome(baby2, 0));
        }

        // Finished so assign new population back into original population
        population = newPopulation;

        return population;
    }

    // Accessor method
    public List<Genome> getChromosomes() {
        return population;
    }

    // Accessor method
    public double getAverageFitness() {
        return totalFitness / populationSize;
    }

    // Accessor method
    public double getBestFitness() {
        return bestFitness;
    }

    // Given parents and storage for the offspring, this method performs
    // crossover according to the genetic algorithm's crossover rate.
    // TODO: Do I need to return the updated version or will this update the original?
    private void crossover(final List<Double> mum, final List<Double> dad,
                           List<Double> baby1, List<Double> baby2) {
        // Just return parents as offspring depending on the rate or if parents
        // are the same
        if ((Utils.randFloat() > crossoverRate) || (mum == dad)) {
            baby1 = mum;
            baby2 = dad;
            return;
        }

        // Determine a crossover point
        int cp = Utils.randInt(0, chromosomeLength - 1);

        // Create the offspring
        for (int i = 0; i < cp; ++i) {
            baby1.add(mum.get(i));
            baby2.add(dad.get(i));
        }
        for (int i = cp; i < mum.size(); ++i) {
            baby1.add(dad.get(i));
            baby2.add(mum.get(i));
        }
    }

    // Mutates a chromosome by perturbing its weights by an amount not greater
    // than Params.MAX_PERTURBATION
    // TODO: Do I need to return the updated version or will this update the original?
    private void mutate(List<Double> chromosome) {
        // Traverse the chromosome and mutate each weight dependent on the
        // mutation rate
        for (int i = 0; i < chromosome.size(); ++i) {
            // Do we perturb this weight?
            if (Utils.randFloat() < mutationRate) {
                // Add or subtract a small value to the weight
                chromosome.set(i, chromosome.get(i) +
                        (Utils.randomClamped() * Params.MAX_PERTURBATION));
            }
        }
    }

    // Returns a chromosome based on roulette wheel sampling
    private Genome getChromosomeRoulette() {
        // Generate a random number between 0 and total fitness count
        double slice = (double)(Utils.randFloat() * totalFitness);

        // This will be set to the chosen chromosome
        Genome chosen = new Genome(); // TODO: Seems strange that this is initialized in C++

        // Go through the chromosomes adding up the fitness so far
        double fitnessSoFar = 0;

        for (int i = 0; i < populationSize; ++i) {
            fitnessSoFar += population.get(i).fitness;

            // If the fitness so far > random number, return the chromosome at
            // this point
            if (fitnessSoFar >= slice) {
                chosen = population.get(i);
                break;
            }
        }

        return chosen;
    }

    // Works like an advanced form of elitism by inserting numCopies of the N
    // best most fittest genomes into a population.
    // TODO: Do I need to return the updated version or will this update the original?
    private void grabNBest(int nBest, final int numCopies, List<Genome> pop) {
        // Add the required amount of copies of the N most fittest to the population
        while (nBest-- > 0) {
            for (int i = 0; i < numCopies; ++i) {
                pop.add(population.get((populationSize - 1) - nBest));
            }
        }
    }

    // Calculates the fittest and weakest genome and the average/total fitness
    // scores for all genomes in the population
    private void calculateBestWorstAverageTotal() {
        totalFitness = 0;

        double highestSoFar = 0;
        double lowestSoFar = 9999999;

        for (int i = 0; i < populationSize; ++i) {
            // Update fittest if necessary
            if (population.get(i).fitness > highestSoFar) {
                highestSoFar = population.get(i).fitness;
                fittestGenome = i;
                bestFitness = highestSoFar;
            }
            // Update worst if necessary
            if (population.get(i).fitness < lowestSoFar) {
                lowestSoFar = population.get(i).fitness;
                worstFitness = lowestSoFar;
            }
            totalFitness += population.get(i).fitness;
        }
        averageFitness = totalFitness / populationSize;
    }

    // Resets all the relevant variables, ready for a new generation
    private void reset() {
        totalFitness = 0;
        bestFitness = 0;
        worstFitness = 9999999;
        averageFitness = 0;
    }
}

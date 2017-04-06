package cern.ais.gridwars.MachineLearning;

import cern.ais.gridwars.*;
import cern.ais.gridwars.Cell;
import cern.ais.gridwars.bot.PlayerBot;
import cern.ais.gridwars.cell.*;
import cern.ais.gridwars.command.MovementCommand;
import cern.ais.gridwars.universe.Universe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kavan on 05/04/17.
 */
public class MachineLearningTrainer {

    private static final String FITTEST_FILENAME = "MachineLearningTrainer.weights";

    private static final int POPULATION_SIZE = 10;

    private List<MachineLearningBot> bots;
    private GeneticAlgorithm algorithm;
    private List<Genome> population;

    private int currentlyTrainingIndex;
    private int currentGeneration;

    public MachineLearningTrainer() {
        algorithm = new GeneticAlgorithm(
                POPULATION_SIZE,
                Params.MUTATION_RATE,
                Params.CROSSOVER_RATE,
                10009);// (2501 * 4) + 4 + 1

        bots = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            bots.add(new MachineLearningBot());
        }

        population = algorithm.getChromosomes();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            bots.get(i).putWeights(population.get(i).weights);
        }

        // Insert the most evolved genome multiple times if it is available
        // to support elitism from a previous training session
        List<Double> mostEvolvedWeights = loadWeights();
        if (mostEvolvedWeights != null) {
            for (int i = 0; i < Params.NUM_ELITE; i++) {
                bots.get(i).putWeights(mostEvolvedWeights);
            }
        }

        currentlyTrainingIndex = 0;

        // Run the game
        try {
            runGame(bots.get(currentlyTrainingIndex), new TeamBot(true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        List<Coordinates> coords = null;
        try {
            coords = game.getUniverse().getCellsForPlayer(
                    new Player(0, bots.get(currentlyTrainingIndex), new File("player1.log"), 0));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // If the ML bot was defeated or the game is over
        if (coords == null || coords.size() == 0 || game.done()) {
            if (currentlyTrainingIndex == POPULATION_SIZE - 1) {
                MachineLearningBot fittestBot = getBestBot();
                saveWeights(fittestBot.fitness, fittestBot.getWeights());
                nextGen();
            }
            else {
                nextGenIteration();
            }

            // Run the game
            try {
                createGame(bots.get(currentlyTrainingIndex), new TeamBot(true));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // Update the fitness by counting the number of our troops
            int totalTroopCount = getTotalTroopCount();
            if (totalTroopCount > bots.get(currentlyTrainingIndex).fitness) {
                bots.get(currentlyTrainingIndex).fitness = totalTroopCount;
                population.get(currentlyTrainingIndex).fitness = totalTroopCount;
            }
        }
    }

    private MachineLearningBot getBestBot() {
        MachineLearningBot fittestBot = null;
        for (MachineLearningBot bot : bots) {
            if (fittestBot == null || bot.fitness > fittestBot.fitness) {
                fittestBot = bot;
            }
        }
        return fittestBot;
    }

    private void nextGenIteration() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            bots.get(i).putWeights(population.get(i).weights);
        }
        currentlyTrainingIndex++;
    }

    private void nextGen() {
        population = algorithm.epoch(population);

        for (int i = 0; i < POPULATION_SIZE; i++) {
            bots.get(i).putWeights(population.get(i).weights);
        }

        currentGeneration++;
        currentlyTrainingIndex = 0;
    }

    private void printStats() {
        System.out.println("####################################################\n################### GENERATION " + currentGeneration + " ###################\n####################################################");
        for (int i = 0; i < population.size(); i++)
        {
            System.out.println("=================== GENOME " + i + " ===================");
            System.out.println("Fitness: " + population.get(i).fitness);
            System.out.println("Weights:");
            for (int j = 0; j < population.get(i).weights.size(); j++)
            {
                System.out.println(j + ": " + population.get(i).weights.get(j));
            }
        }
    }

    private boolean saveWeights(double fitness, List<Double> weights) {
        List<String> vals = new ArrayList<>();
        for (double w : weights) {
            vals.add(String.valueOf(w));
        }

        // Insert the fitness value at the top
        vals.add(0, "Fitness: " + String.valueOf(fitness) + "/" + GlobalContext.NUM_CELLS * Cell.MAX_TROOPS);

        Path file = Paths.get(FITTEST_FILENAME);
        try {
            Files.write(file, vals, Charset.forName("UTF-8"));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private List<Double> loadWeights() {
        List<String> lines;
        Path file = Paths.get(FITTEST_FILENAME);
        try {
            lines = Files.readAllLines(file, Charset.forName("UTF-8"));
        } catch (IOException e) {
            return null;
        }

        List<Double> weights = new ArrayList<>();
        // Ignore the line declaring the fitness
        for (int i = 1; i < lines.size(); i++) {
            weights.add(Double.valueOf(lines.get(i)));
        }
        return weights;
    }

    private int getTotalTroopCount() {
        MachineLearningBot activeBot = bots.get(currentlyTrainingIndex);
        int sum = 0;
        try {
            for (Cell cell : activeBot.context.myCells()) {
                sum += cell.myTroops();
            }
        } catch (NullPointerException e) {
            System.out.println("UV not yet initialized -- should only be called on first move!");
            return -1;
        }
        return sum;
    }

    ////////////////////////////////////////////////////////////////////////////
    // THIS IS A COPY OF VISUALIZER.JAVA BELOW THIS POINT
    ////////////////////////////////////////////////////////////////////////////

    private JFrame frame;
    private boolean m_Running = !false;
    private Label m_StatusLabel;
    private int[] speeds = new int[] { 1, 10, 20, 40, 80, 160, 320, 500 };
    private int m_TimerSpeedId = 3;
    private Game game;

    private void createGame(PlayerBot bot1, PlayerBot bot2) throws FileNotFoundException {
        game = new Game(Arrays.asList(new Player(0, bot1, new File("player1.log"), 0), new Player(1, bot2, new File("player2.log"), 1)), new Game.TurnCallback() {
            @Override public void onPlayerResponse(Player player, int turn, List<MovementCommand> movementCommands, ByteBuffer binaryGameStatus) {
                frame.setTitle("Machine Learning Trainer: Turn " + turn);
                frame.repaint();
            }
        }, true);

        game.startUp();
    }

    private void createAndShowGUI() throws FileNotFoundException
    {
        frame = new JFrame("GridWars Emulator");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel drawPanel = new JPanel(new BorderLayout()) {
            @Override public void paint(Graphics graphics) {
                Universe universe = game.getUniverse();
                for (int my = 0; my < GameConstants.UNIVERSE_SIZE; my++)
                    for (int mx = 0; mx < GameConstants.UNIVERSE_SIZE; mx++)
                    {
                        cern.ais.gridwars.cell.Cell cell = universe.getCell(mx, my);
                        long population = cell.getPopulation();
                        if (population == 0)
                            continue;

                        Color color = cell.getOwner().getColorIndex() == 0 ? Color.red : Color.blue;
                        int alpha = (int)(Math.abs(population) / (double)GameConstants.MAXIMUM_POPULATION * 255);
                        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);

                        graphics.setColor(color);
                        graphics.fillRect(mx * 10, 50 + my * 10, 10, 10);
                    }
            }
        };

        m_StatusLabel = new Label("IDLE");
        mainPanel.add(m_StatusLabel, BorderLayout.NORTH);
        mainPanel.add(drawPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        final JToggleButton pauseButton = new JToggleButton("Pause");
        pauseButton.setSelected(false);
        pauseButton.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent itemEvent) {
                m_Running = itemEvent.getStateChange() != ItemEvent.SELECTED;
            }
        });

        controlPanel.add(pauseButton);
        controlPanel.add(createSpeedControlButton("+"), BorderLayout.EAST);
        controlPanel.add(createSpeedControlButton("-"), BorderLayout.WEST);

        frame.setPreferredSize(new Dimension(500, 600));
        frame.getContentPane().add(mainPanel);

        frame.pack();
        frame.setVisible(true);

        final int[] delta = { 0 };
        new Timer(10, new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if (!m_Running) return;

                if (delta[0] < speeds[m_TimerSpeedId]) {
                    delta[0] += 10;
                    return;
                }
                delta[0] = 0;

                if (!game.done()) {
                    game.nextTurn();

                    // TODO: This was added so that an update to neural networks
                    // TODO: became possible
                    update();
                }
            }
        }).start();
    }

    private Component createSpeedControlButton(final String caption) {
        JButton control = new JButton(caption);
        control.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if (caption.equals("-") && m_TimerSpeedId < speeds.length - 2)
                    m_TimerSpeedId += 1;
                if (caption.equals("+") && m_TimerSpeedId > 0)
                    m_TimerSpeedId -= 1;
            }
        });
        return control;
    }

    public void runGame(PlayerBot bot1, PlayerBot bot2) throws FileNotFoundException
    {
        createGame(bot1, bot2);
        createAndShowGUI();
    }
}

import java.util.*;
import javax.swing.*;
import java.text.*;

class Population extends Thread {

    private int populationSize;
    private int mutationRate;
    private LinkedList Solutions;
    private Routine BestSolution;
    private GAPanel OutputPanel;
    private JLabel OutputLabel;
    private String OutputPrefix;

    double baseline;
    double last;

    public boolean IsRunning = true;

    public Population(int populationSize, int mutationRate, Routine FirstSolution) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        Solutions = new LinkedList();
        BestSolution = FirstSolution;

        for (int i = 0; i < populationSize; i++) {
            Solutions.add(BestSolution.copyRoutine());
        }

    }

    public void iterate() {
        //step 1: Rank the population by fitness
        Collections.sort(Solutions);
        BestSolution = (Routine) Solutions.get(0);

        //step 2: Kill of some of the population
        Random r = new Random();
        int deadSolutions = 0;
        for (int i = 1; i < populationSize; i++) {
            if (r.nextFloat() < (float) (i) / populationSize) {
                Solutions.remove(i - deadSolutions);
                deadSolutions++;
            }
        }

        //step 3: repopulate with through mating
        r = new Random();
        for (int i = 0; i < deadSolutions; i++) {
            int Mom = r.nextInt(populationSize - deadSolutions);
            Routine Mama = (Routine) Solutions.get(Mom);
            Routine Baby = Mama.copyRoutine();

            int thisTime = r.nextInt(mutationRate);
            for (int j = 0; j < thisTime; j++) {
                switch (r.nextInt(4)) {
                    case 0:
                        Baby.mutate_insertion();
                        break;
                    case 1:
                        Baby.mutate();
                        break;
                    case 2:
                        Baby.mutate_branch_swap();
                        break;
                    case 3:
                        Baby.mutate_branch_swap();
                        break;
                }
            }
            Solutions.add(Baby);
        }
    }

    public Routine getBestRoutine() {
        return BestSolution;
    }

    public void setPanel(GAPanel output) {
        OutputPanel = output;
    }

    public void setLabel(JLabel label, String Prefix) {
        OutputLabel = label;
        OutputPrefix = Prefix;
    }

    public void setBaseline(double baseline) {
        this.baseline = baseline;
    }

    public void setLast(double last) {
        this.last = last;
    }

    public void run() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        double percentage = 0;
        while (IsRunning) {
            iterate();
            Routine thisBest = this.getBestRoutine();
            if (thisBest.getSumLargerDistances() < last) {
                OutputPanel.setRoutine(thisBest.copyRoutine());
                last = thisBest.getSumLargerDistances();
                percentage = (baseline - last) / baseline * 100.0;
                OutputLabel.setText(OutputPrefix + nf.format(percentage) + "%");
                OutputPanel.repaint();
            }
        }
    }
}
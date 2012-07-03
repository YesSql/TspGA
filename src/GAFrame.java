import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

class GAFrame extends JFrame implements ActionListener {
    String title = "Tool Path Optimization";
    JMenuBar mainMenuBar;
    JMenu fileMenu;
    JMenuItem openFile;
    JMenuItem fileProperties;
    JMenuItem saveFile;
    JMenuItem saveFileAs;
    JMenu optimizeMenu;
    JMenuItem Randomize;
    JMenuItem Heuristic;
    JMenuItem HardHeuristic;
    JMenuItem StartGA;
    JMenuItem StopGA;
    JMenu viewMenu;
    JMenuItem viewOriginal;
    JMenuItem viewBest;
    JMenu helpMenu;
    JMenuItem About;
    GAPanel panel;
    JTabbedPane tabbedPane;
    JPanel statusBarPanel;
    JLabel running;
    JLabel percentageImprovement;
    Routine originalLocations;
    Routine Locations;
    Routine bestLocations;
    double baseline;
    Population population;
    Thread thread;
    PunchFile currentFile;

    public GAFrame() {
        super.setTitle(title);

        mainMenuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        openFile = new JMenuItem("Open File");
        openFile.setMnemonic(KeyEvent.VK_O);
        fileProperties = new JMenuItem("View File Properties");
        fileProperties.setMnemonic(KeyEvent.VK_V);

        saveFile = new JMenuItem("Save File");
        saveFile.setMnemonic(KeyEvent.VK_S);
        saveFileAs = new JMenuItem("Save File As");
        saveFileAs.setMnemonic(KeyEvent.VK_A);

        fileMenu.add(openFile);
        fileMenu.add(fileProperties);
        fileMenu.add(saveFileAs);

        optimizeMenu = new JMenu("Optimize");
        optimizeMenu.setMnemonic(KeyEvent.VK_O);

        Randomize = new JMenuItem("Randomize");
        Randomize.setMnemonic(KeyEvent.VK_R);
        HardHeuristic = new JMenuItem("Thorough Heuristic");
        HardHeuristic.setMnemonic(KeyEvent.VK_T);
        Heuristic = new JMenuItem("Quick Heuristic");
        Heuristic.setMnemonic(KeyEvent.VK_H);
        StartGA = new JMenuItem("Start Genetic Algorithm");
        StartGA.setMnemonic(KeyEvent.VK_S);
        StopGA = new JMenuItem("Stop Genetic Algorithm");
        StopGA.setMnemonic(KeyEvent.VK_T);

        optimizeMenu.add(Randomize);
        optimizeMenu.add(HardHeuristic);
        optimizeMenu.add(Heuristic);
        optimizeMenu.add(StartGA);
        optimizeMenu.add(StopGA);

        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        viewOriginal = new JMenuItem("Original Sequence");
        viewOriginal.setMnemonic(KeyEvent.VK_O);
        viewBest = new JMenuItem("Shortest Sequence");
        viewBest.setMnemonic(KeyEvent.VK_S);

        viewMenu.add(viewOriginal);
        viewMenu.add(viewBest);

        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        About = new JMenuItem("About");
        About.setMnemonic(KeyEvent.VK_A);

        helpMenu.add(About);

        mainMenuBar.add(fileMenu);
        mainMenuBar.add(optimizeMenu);
        mainMenuBar.add(viewMenu);
        mainMenuBar.add(helpMenu);

        this.setJMenuBar(mainMenuBar);

        panel = new GAPanel();
        panel.setPreferredSize(new Dimension(600, 400));
        tabbedPane = new JTabbedPane();

        tabbedPane.add(panel, " Locations");
        statusBarPanel = new JPanel();

        running = new JLabel("");
        percentageImprovement = new JLabel("");

        statusBarPanel.setLayout(new GridLayout(1, 4));
        statusBarPanel.setBorder(BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        statusBarPanel.add(running);
        statusBarPanel.add(percentageImprovement);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        this.getContentPane().add(statusBarPanel, BorderLayout.SOUTH);

        openFile.addActionListener(this);
        fileProperties.addActionListener(this);
        saveFileAs.addActionListener(this);
        About.addActionListener(this);
        Randomize.addActionListener(this);
        HardHeuristic.addActionListener(this);
        Heuristic.addActionListener(this);
        StartGA.addActionListener(this);
        StopGA.addActionListener(this);
        viewOriginal.addActionListener(this);
        viewBest.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String command = source.getText();

        if(command.equals("Open File")) {
            JFileChooser fc = new JFileChooser();

            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try  {
                    File file = fc.getSelectedFile();
                    currentFile = new PunchFile(file.getPath());
                    Locations = new Routine(currentFile.getLocations());
                    originalLocations = Locations.copyRoutine();
                    bestLocations = Locations.copyRoutine();
                    panel.setOriginalRoutine(Locations);
                    panel.setRoutine(Locations);
                    baseline = Locations.getSumLargerDistances();
                    super.setTitle(title + " - " + file.getName());
                    running.setText("Func. GA Stopped");
                    percentageImprovement.setText("Improvement: 0%");

                }
                catch(Exception ex) {
                    ex.printStackTrace(System.out);
                }
            }
        }

        if(command.equals("View File Properties")) {
            if(currentFile != null) {
                int f = currentFile.getLocationCount();
                int f_length = (int) (originalLocations.getSumLargerDistances()/10000);

                JOptionPane.showMessageDialog(this,
                        "Properties of: "+ currentFile.FilePath + "\n\n" +
                                f + "  Locations \n\n" +
                                "Path Length: " + f_length + " mm",
                        "Tool Path File Information...", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(this,"Please open valid tool path file.","Error!",JOptionPane.ERROR_MESSAGE);
            }
        }

        if(command.equals("Save File As")) {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if(running.getText().equals("GA running")) {
                    thread.stop();
                    running.setText("GA stopped");
                    Locations = population.getBestRoutine();
                    if(Locations.getSumLargerDistances() < bestLocations.getSumLargerDistances())
                        bestLocations = Locations.copyRoutine();
                }

                currentFile.setLocations(bestLocations.getLocations());
                currentFile.SaveFile(fc.getSelectedFile().getPath());
            }
        }

        if(command.equals("Randomize")) {
            Locations.randomize(10000);
            panel.setRoutine(Locations);
            panel.repaint();
        }

        if(command.equals("Quick Heuristic")) {
            Locations.easyHeuristic();
            panel.setRoutine(Locations);
            panel.repaint();
            if(Locations.getSumLargerDistances() < bestLocations.getSumLargerDistances()) {
                bestLocations = Locations;
            }
        }

        if(command.equals("Thorough Heuristic")) {

            Locations.thoroughHeuristic();
            panel.setRoutine(Locations);
            panel.repaint();
            if(Locations.getSumLargerDistances() < bestLocations.getSumLargerDistances())
                bestLocations = Locations;
        }

        if(command.equals("Start Genetic Algorithm")) {
            if(!running.getText().equals("GA running")) {
                population = new Population(30,2, Locations);
                population.setPanel(panel);
                population.setLabel(percentageImprovement, "Improvement: ");
                population.setBaseline(baseline);
                population.setLast(Locations.getSumLargerDistances());
                thread = new Thread(population, "GA");
                thread.start();
                running.setText("GA running");
            }
        }

        if(command.equals("Stop Genetic Algorithm")) {
            if(running.getText().equals("GA running")) {
                thread.stop();
                running.setText("GA stopped");
                Locations = population.getBestRoutine();
                if(Locations.getSumLargerDistances() < bestLocations.getSumLargerDistances())
                    bestLocations = Locations.copyRoutine();
            }
        }

        if(command.equals("Original Sequence")) {
            panel.setRoutine(originalLocations);
            panel.repaint();
        }

        if(command.equals("Shortest Sequence") ){
            panel.setRoutine(bestLocations);
            panel.repaint();
        }

        if(command.equals("About")) {
            JOptionPane.showMessageDialog(this,"Genetic Algorithm for Optimizing Tool Path with Two Independent Axes\n\n By John Madison \n\n john.c.madison@gmail.com", "About this Program...",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void createAndShowGUI() {
        GAFrame frame = new GAFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
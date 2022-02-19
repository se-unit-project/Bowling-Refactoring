
/*
 *  constructs a prototype Lane View
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


import java.util.*;

public class LaneView implements ActionListener, Observer {

	public static final int NUM_OF_TRIES = 23;
	public static final int NUM_OF_ROUNDS = 10;
	public static final int LAST_ROUND = 9;
	private boolean initDone = false;
	JFrame frame;
	Container cpanel;
	Vector bowlers;
	JPanel[][] balls;
	JLabel[][] ballLabel;
	JPanel[][] scores;
	JLabel[][] scoreLabel;
	JPanel[][] ballGrid;
	JPanel[] pins;
	JButton maintenance;
	Lane lane;

	public LaneView(Lane lane, int laneNum) {

		this.lane = lane;
		frame = new JFrame("Lane " + laneNum + ":");
		cpanel = frame.getContentPane();
		cpanel.setLayout(new BorderLayout());
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.hide();
			}
		});
		cpanel.add(new JPanel());
	}

	public void show() {
		frame.show();
	}

	public void hide() {
		frame.hide();
	}

	private JPanel makeFrame(Party party) {
		bowlers = party.getMembers();
		int numBowlers = bowlers.size();
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		balls = new JPanel[numBowlers][NUM_OF_TRIES];
		ballLabel = new JLabel[numBowlers][NUM_OF_TRIES];

		scores = new JPanel[numBowlers][NUM_OF_ROUNDS];
		scoreLabel = new JLabel[numBowlers][NUM_OF_ROUNDS];
		ballGrid = new JPanel[numBowlers][NUM_OF_ROUNDS];
		pins = new JPanel[numBowlers];

		for (int i = 0; i != numBowlers; i++) {
			for (int j = 0; j != NUM_OF_TRIES; j++) {
				ballLabel[i][j] = new JLabel(" ");
				balls[i][j] = new JPanel();
				balls[i][j].setBorder(
						BorderFactory.createLineBorder(Color.BLACK));
				balls[i][j].add(ballLabel[i][j]);
			}
		}

		for (int i = 0; i != numBowlers; i++) {
			for (int j = 0; j != LAST_ROUND; j++) {
				ballGrid[i][j] = new JPanel();
				ballGrid[i][j].setLayout(new GridLayout(0, 3));
				ballGrid[i][j].add(new JLabel("  "), BorderLayout.EAST);
				ballGrid[i][j].add(balls[i][2 * j], BorderLayout.EAST);
				ballGrid[i][j].add(balls[i][2 * j + 1], BorderLayout.EAST);
			}

			ballGrid[i][LAST_ROUND] = new JPanel();
			ballGrid[i][LAST_ROUND].setLayout(new GridLayout(0, 3));
			ballGrid[i][LAST_ROUND].add(balls[i][2 * LAST_ROUND]);
			ballGrid[i][LAST_ROUND].add(balls[i][2 * LAST_ROUND + 1]);
			ballGrid[i][LAST_ROUND].add(balls[i][2 * LAST_ROUND + 2]);
		}

		for (int i = 0; i != numBowlers; i++) {
			pins[i] = new JPanel();
			pins[i].setBorder(
					BorderFactory.createTitledBorder(
							((Bowler) bowlers.get(i)).getNick()));
			pins[i].setLayout(new GridLayout(0, NUM_OF_ROUNDS));
			for (int k = 0; k != NUM_OF_ROUNDS; k++) {
				scores[i][k] = new JPanel();
				scoreLabel[i][k] = new JLabel("  ", SwingConstants.CENTER);
				scores[i][k].setBorder(
						BorderFactory.createLineBorder(Color.BLACK));
				scores[i][k].setLayout(new GridLayout(0, 1));
				scores[i][k].add(ballGrid[i][k], BorderLayout.EAST);
				scores[i][k].add(scoreLabel[i][k], BorderLayout.SOUTH);
				pins[i].add(scores[i][k], BorderLayout.EAST);
			}
			panel.add(pins[i]);
		}

		initDone = true;
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(maintenance)) lane.pauseGame();
	}

	@Override
	public void update(Observable o, Object arg) {		
		if(!(o instanceof Lane)) return;
		Lane le = (Lane)o;

		if (lane.isPartyAssigned() && !lane.isGameFinished()) {
			
			if(!initDone){
				cpanel.removeAll();
				cpanel.add(makeFrame(le.getParty()), "Center");
			}
			
			int numBowlers = le.getParty().getMembers().size();
		
			if (le.getFrameNumber() == 0 && le.getBall() == 0 && le.getBowlIndex() == 0) {
				System.out.println("Making the frame.");
				cpanel.removeAll();
				cpanel.add(makeFrame(le.getParty()), "Center");

				// Button Panel
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new FlowLayout());

				maintenance = new JButton("Maintenance Call");
				JPanel maintenancePanel = new JPanel();
				maintenancePanel.setLayout(new FlowLayout());
				maintenance.addActionListener(this);
				maintenancePanel.add(maintenance);

				buttonPanel.add(maintenancePanel);

				cpanel.add(buttonPanel, "South");

				frame.pack();
				cpanel.setVisible(true);
			}
			
			int[][] lescores = le.getCumulScores();
			HashMap scores = le.getScores();
			this.bowlers = le.getParty().getMembers();
			for (int k = 0; k < numBowlers; k++) {
				for (int i = 0; i <= le.getFrameNumber() - 1; i++) {
					if (lescores[k][i] != 0){
						this.scoreLabel[k][i].setText((new Integer(lescores[k][i])).toString());
					}
				}

				for (int i = 0; i < 21; i++) {
					if (((int[]) (scores.get(bowlers.get(k))))[i] != -1){
						if (((int[]) scores.get(bowlers.get(k)))[i] == NUM_OF_ROUNDS && (i % 2 == 0 || i == 19)){
							ballLabel[k][i].setText("X");
						}					
						else if (i > 0 && ((int[]) scores.get(bowlers.get(k)))[i] + ((int[]) scores.get(bowlers.get(k)))[i - 1] == NUM_OF_ROUNDS && i % 2 == 1){
							ballLabel[k][i].setText("/");
						}
						else if ( ((int[])scores.get(bowlers.get(k)))[i] == -2 ){
							ballLabel[k][i].setText("F");
						} 
						else{
							ballLabel[k][i].setText((new Integer(((int[]) scores.get(bowlers.get(k)))[i])).toString());
						}
					}
				}

			}
		}
		else{
			initDone = false;
		}
	}
}

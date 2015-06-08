package com.github.blaskovicz;

import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.JDialog;

public class Main {
	private static final int CHECK_INTERVAL_MILLIS = 1000 * 60 * 10;
	public static void main(String[] args) throws Exception {
		Robot robot = new Robot();
		//Main.gatherMouseCoordinates(robot);
		Main.holdWakelock(robot);
	}

	// move the mouse a couple pixels every CHECK_INTERVAL_MILLIS if it hasn't changed position
	private static void holdWakelock(Robot robot) throws Exception {
		JDialog runningDialog = new JDialog();
		runningDialog.addWindowListener(new WindowListener() {
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("User requested window close, exitting app");
				System.exit(0);
			}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}
		});
		
		// TODO make check interval configurable via dialog
		Container container = runningDialog.getContentPane();
		BoxLayout box = new BoxLayout(container, BoxLayout.Y_AXIS);
		container.setLayout(box);
		runningDialog.setTitle("ScreenSavr");
		runningDialog.setVisible(true);
		runningDialog.setModalityType(ModalityType.MODELESS);
		
		int cycle = 0;
		Point lastPoint = MouseInfo.getPointerInfo().getLocation();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		while (true) {
			cycle++;
			Point currentPoint = MouseInfo.getPointerInfo().getLocation();
			// mouse hasn't moved since last cycle, update coordinates within
			// the bounds of the screen
			if(currentPoint.equals(lastPoint)){
				Dimension screenDim = toolkit.getScreenSize();
				int maxX = (int) screenDim.getWidth();
				int maxY = (int) screenDim.getHeight();
				int currentX = (int) currentPoint.getX();
				int currentY = (int) currentPoint.getY();
				int newX = currentX + 2 > maxX ? currentX - 2 : currentX + 2;
				int newY = currentY + 2 > maxY ? currentY - 2 : currentY + 2;
				robot.mouseMove(newX, newY);
				Point newPoint = new Point(newX, newY);
				System.out.println(
					"Coordinates auto-changed on cycle " + cycle + " from " +
					lastPoint + " to " + newPoint +
					" at " + nowTime()
				);
				lastPoint = newPoint;
			}
			// otherwise, update our tracking position
			else {
				System.out.println(
					"Coordinates manually-changed on cycle " + cycle + " from " +
					lastPoint + " to " + currentPoint + 
					" at " + nowTime()
				);
				lastPoint = currentPoint;
			}
			Thread.sleep(CHECK_INTERVAL_MILLIS);
		}
	}
	
	public static String nowTime(){
		Calendar now = Calendar.getInstance();
		return now.getTime().toString();
	}
}

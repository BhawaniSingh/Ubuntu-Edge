package org.bhawanisingh.ubuntuedge;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

/**
 *
 */
public class MainUI extends JFrame {
	/**
 * 
 */
	private String ubuntuEdgeUrlString = "http://www.indiegogo.com/projects/ubuntu-edge";
	private String amountCollected = "<span class=\"amount medium clearfix\">";
	private String amountCollectedEnd = "</span>";
	private String amountToCollect = "<p class=\"money-raised goal\">";
	private String amountToCollectPart = "Raised of";
	private String amountToCollectEnd = " Goal";
	private String daysLeft = "<p class=\"days-left\">";
	private String daysLeftPart = "<span class=\"amount bold\">";
	private String daysLeftEnd = "</span>";
	private Font font = new Font("Ubuntu", Font.BOLD, 16);

	private int xPosition;
	private int yPosition;
	private JPanel mainPanel;
	private MyLabel ubuntuFundingLabel;
	private MyLabel amountCollectedLabel;
	private MyLabel amountToCollectLabel;
	private MyLabel daysLeftLabel;
	private MyLabel statusLabel;

	// Popup menu
	private JPopupMenu popupMenu;
	private JMenuItem refreshMenuItem;
	private JMenuItem exitMenuItem;

	private Thread fetchDataThread;
	private FetchData fetchData;
	private TimedFetch timedFetch;
	private Thread timerThread;

	private boolean fetching;

	public MainUI() {
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");

		initialize();
		addComponents();
		addListeners();
		theming();
		buildPopUpMenu();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setUndecorated(true);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		timerThread.start();
	}

	private void initialize() {
		mainPanel = new JPanel(new GridLayout(5, 1, 5, 5));
		ubuntuFundingLabel = new MyLabel("Ubuntu Funding Stats");
		amountCollectedLabel = new MyLabel("Amount Collected : ");
		amountToCollectLabel = new MyLabel("Amount To Collect :  $32,000,000");
		daysLeftLabel = new MyLabel("Days Left :");
		// refreshButton = new JButton("Refresh");
		// exitbutton = new JButton("Exit");
		statusLabel = new MyLabel();

		timedFetch = new TimedFetch();
		timerThread = new Thread(timedFetch);
	}

	private void addComponents() {
		mainPanel.add(ubuntuFundingLabel);
		mainPanel.add(amountCollectedLabel);
		mainPanel.add(amountToCollectLabel);
		mainPanel.add(daysLeftLabel);
		mainPanel.add(statusLabel);

		add(mainPanel);
	}

	private void theming() {
		// TODO Add transparency
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		ubuntuFundingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		ubuntuFundingLabel.setFont(new Font("Ubuntu Mono", Font.TRUETYPE_FONT, 30));
		ubuntuFundingLabel.setForeground(Color.BLACK);
		amountCollectedLabel.setForeground(Color.BLACK);
		amountCollectedLabel.setFont(font);
		amountToCollectLabel.setForeground(Color.BLACK);
		amountToCollectLabel.setFont(font);
		daysLeftLabel.setForeground(Color.BLACK);
		daysLeftLabel.setFont(font);
		statusLabel.setForeground(Color.BLACK);
		statusLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 0));
	}

	private void addListeners() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				xPosition = mouseEvent.getX();
				yPosition = mouseEvent.getY();
				if (mouseEvent.isPopupTrigger()) {
					popupMenu.show(MainUI.this, mouseEvent.getX(), mouseEvent.getY());
				}
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent evt) {
				setLocation(evt.getXOnScreen() - xPosition, evt.getYOnScreen() - yPosition);
			}
		});
	}

	// Complete PopupMenuCode
	private void buildPopUpMenu() {
		popupMenu = new JPopupMenu();
		refreshMenuItem = new JMenuItem("Refresh");
		popupMenu.add(refreshMenuItem);
		exitMenuItem = new JMenuItem("Exit");
		popupMenu.add(exitMenuItem);
		refreshMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!fetching) {
					fetchData = new FetchData();
					fetchDataThread = new Thread(fetchData);
					fetchDataThread.start();
				}
			}
		});
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		refreshMenuItem.setOpaque(true);
		refreshMenuItem.setBackground(Color.WHITE);
		exitMenuItem.setOpaque(true);
		exitMenuItem.setBackground(Color.WHITE);

	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException classNotFoundException) {

		} catch (InstantiationException instantiationException) {

		} catch (IllegalAccessException illegalAccessException) {

		} catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {

		}
		new MainUI().setVisible(true);
	}

	private class FetchData implements Runnable {
		@Override
		public void run() {
			fetching = true;
			statusLabel.setText("Updating...");
			URL ubuntuEdgeURL;
			InputStream inputStream = null;
			BufferedReader bufferedReader;
			long contentLength;
			String line;
			String print;
			try {
				HttpURLConnection content = (HttpURLConnection) new URL(ubuntuEdgeUrlString).openConnection();
				contentLength = content.getContentLength();
				ubuntuEdgeURL = new URL(ubuntuEdgeUrlString);
				inputStream = ubuntuEdgeURL.openStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

				while ((line = bufferedReader.readLine()) != null) {
					if (line.contains(amountCollected)) {
						print = line.substring(line.indexOf(amountCollected) + amountCollected.length());
						print = print.substring(0, print.indexOf(amountCollectedEnd));
						System.out.println("Amount Collected : " + print);
						amountCollectedLabel.setText("Amount Collected : " + print);
					}

					if (line.contains(amountToCollect)) {
						if (contentLength == -1) {
							print = line.substring(line.indexOf(amountToCollect) + amountToCollect.length() + amountToCollectPart.length()) + 1;
							print = print.substring(0, print.indexOf(amountToCollectEnd));
						} else {
							line = bufferedReader.readLine();
							print = line.substring(line.indexOf(amountToCollectPart) + amountToCollectPart.length());
							print = print.substring(0, print.indexOf(amountToCollectEnd));
						}
						System.out.println("Amount To Collect :" + print);
						amountToCollectLabel.setText(amountToCollectLabel.getText());
					}

					if (line.contains(daysLeft)) {
						if (contentLength == -1) {
							print = line.substring(line.indexOf(daysLeft) + daysLeft.length() + daysLeftPart.length()) + 1;
							print = print.substring(0, print.indexOf(daysLeftEnd));
						} else {
							line = bufferedReader.readLine();
							print = line.substring(line.indexOf(daysLeftPart) + daysLeftPart.length());
							print = print.substring(0, print.indexOf(daysLeftEnd));
						}
						System.out.println("Days Left : " + print);
						daysLeftLabel.setText("Days Left : " + print);
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			} finally {
				timedFetch.wakeMeUp();
				try {
					inputStream.close();
				} catch (IOException exception) {
				}
				statusLabel.setText("");
				fetching = false;
			}

		}
	}

	class TimedFetch implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (!fetching) {
					new Thread(new FetchData()).start();
					putThreadToWait();
				}
				try {
					TimeUnit.MINUTES.sleep(10);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		public void putThreadToWait() {
			synchronized (this) {
				try {
					wait();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		public void wakeMeUp() {
			synchronized (this) {
				try {
					notify();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	class MyLabel extends JLabel {

		public MyLabel() {
			super();
		}

		public MyLabel(String text) {
			super(text);
		}

		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			super.paint(g2);
		}
	}
}

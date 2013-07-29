package org.bhawanisingh.ubuntuedge;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

public class MainUI extends JFrame {

	private String ubuntuEdgeUrlString = "http://www.indiegogo.com/projects/ubuntu-edge";
	private String amountCollected = "<span class=\"amount medium clearfix\">";
	private String amountCollectedEnd = "</span>";
	private String amountToCollect = "<p class=\"money-raised goal\">";
	private String amountToCollectPart = "Raised of";
	private String amountToCollectEnd = " Goal";
	private String daysLeft = "<p class=\"days-left\">";
	private String daysLeftPart = "<span class=\"amount bold\">";
	private String daysLeftEnd = "</span>";

	private int size = 11;
	private GridLayout gridLayout = new GridLayout(1, 12, 3, 0);

	private int xPosition;
	private int yPosition;

	private EdgeLabel[] fundLabels;
	private EdgeLabel[] totalFundLabels;
	private EdgeLabel[] daysLeftLabels;
	private EdgePanel fundPanel;
	private EdgePanel totalFundPanel;
	private EdgePanel daysLeftPanel;
	private EdgePanel mainPanel;

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
		super("Ubuntu Funding Stats");
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");

		initialize();
		addComponents();
		addListeners();
		theming();
		buildPopUpMenu();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		timerThread.start();
	}

	private void initialize() {
		// setLayout(new GridLayout(3, 1, 0, 10));

		mainPanel = new EdgePanel(new GridLayout(3, 1, 0, 10));

		fundPanel = new EdgePanel(gridLayout);
		fundLabels = new EdgeLabel[size];
		String label = "";
		for (int i = 0; i < fundLabels.length; ++i) {
			if (i == 0) {
				label = "$";
			} else {
				label = " ";
			}
			fundLabels[i] = new EdgeLabel(label);
		}

		totalFundPanel = new EdgePanel(gridLayout);
		totalFundLabels = new EdgeLabel[size];
		for (int i = 0; i < totalFundLabels.length; ++i) {
			if (i == 0) {
				label = "$";
			} else {
				label = " ";
			}
			totalFundLabels[i] = new EdgeLabel(label);
		}
		updateTotalAmount("$32,000,000");

		daysLeftPanel = new EdgePanel(gridLayout);
		daysLeftLabels = new EdgeLabel[size];
		for (int i = 0; i < daysLeftLabels.length; ++i) {
			daysLeftLabels[i] = new EdgeLabel(" ");
		}
		updateDaysLeft("DaysLeft:--");

		timedFetch = new TimedFetch();
		timerThread = new Thread(timedFetch);
	}

	private void addComponents() {

		for (EdgeLabel fundLabel : fundLabels) {
			fundPanel.add(fundLabel);
		}
		for (EdgeLabel fundLabel : totalFundLabels) {
			totalFundPanel.add(fundLabel);
		}
		for (EdgeLabel fundLabel : daysLeftLabels) {
			daysLeftPanel.add(fundLabel);
		}
		mainPanel.add(fundPanel);
		mainPanel.add(totalFundPanel);
		mainPanel.add(daysLeftPanel);

		add(mainPanel);
	}

	private void theming() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 1));
	}

	private void addListeners() {
		fundLabels[0].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					try {
						Desktop.getDesktop().browse(new URI("http://www.ubuntu-edge.info"));
					} catch (IOException ioException) {
					} catch (URISyntaxException uriSyntaxException) {
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				openPopUp(mouseEvent);
			}
		});

		totalFundLabels[0].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					try {
						Desktop.getDesktop().browse(new URI(ubuntuEdgeUrlString));
					} catch (IOException ioException) {
					} catch (URISyntaxException uriSyntaxException) {
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				openPopUp(mouseEvent);
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				openPopUp(mouseEvent);
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
				repaint();
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
				repaint();
				System.exit(0);
			}
		});

		refreshMenuItem.setOpaque(true);
		refreshMenuItem.setBackground(Color.WHITE);
		exitMenuItem.setOpaque(true);
		exitMenuItem.setBackground(Color.WHITE);

	}

	private void openPopUp(MouseEvent mouseEvent) {
		xPosition = mouseEvent.getX();
		yPosition = mouseEvent.getY();
		if (mouseEvent.isPopupTrigger()) {
			popupMenu.show(MainUI.this, mouseEvent.getX(), mouseEvent.getY());
			MainUI.this.revalidate();
		}
	}

	private void updateAmount(String amount) {
		amount = amount.replace("$", "").replace(",", "");
		System.out.println(amount + "\t this is the amount");
		char[] amt = amount.toCharArray();
		int diff = fundLabels.length - amt.length;
		for (int i = amt.length - 1; i >= 0; --i) {
			fundLabels[i + diff].setText(amt[i] + "");
		}

	}

	private void updateTotalAmount(String amount) {
		amount = amount.replace("$", "").replace(",", "");
		System.out.println(amount + "\t this is the total amount");
		char[] amt = amount.toCharArray();
		int diff = totalFundLabels.length - amt.length;
		for (int i = amt.length - 1; i >= 0; --i) {
			totalFundLabels[i + diff].setText(amt[i] + "");
		}
	}

	private void updateDaysLeft(String daysLeft) {
		System.out.println(daysLeft + "\t this is the total days left");
		char[] amt = daysLeft.toCharArray();
		int diff = daysLeftLabels.length - amt.length;
		for (int i = amt.length - 1; i >= 0; --i) {
			daysLeftLabels[i + diff].setText(amt[i] + "");
		}
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
						updateAmount(print);
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
						updateTotalAmount(print);
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
						updateDaysLeft(print);
						System.out.println("Days Left : " + print);
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
}

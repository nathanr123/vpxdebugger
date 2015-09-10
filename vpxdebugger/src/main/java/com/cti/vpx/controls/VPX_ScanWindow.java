package com.cti.vpx.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.cti.vpx.command.ATPCommand;
import com.cti.vpx.listener.VPXTCPConnector;
import com.cti.vpx.model.VPXSystem;
import com.cti.vpx.util.VPXComponentFactory;
import com.cti.vpx.util.VPXUtilities;
import com.cti.vpx.view.VPX_ETHWindow;

public class VPX_ScanWindow extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6540961732678062286L;

	private final JPanel contentPanel = new JPanel();

	private JTextField txt_From_IP;

	private JTextField txt_To_IP;

	/**
	 * Create the dialog.
	 */
	public VPX_ScanWindow() {

		init();

		loadComponents();

		centerFrame();
	}

	private void centerFrame() {

		Dimension windowSize = getSize();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		Point centerPoint = ge.getCenterPoint();

		int dx = centerPoint.x - windowSize.width / 2;

		int dy = centerPoint.y - windowSize.height / 2;

		setLocation(dx, dy);
	}

	private void init() {

		setTitle("Processor Scanning Window");
		
		setIconImage(VPXUtilities.getAppIcon());
		
		setBounds(100, 100, 305, 165);

		setIconImage(VPXUtilities.getAppIcon());

		getContentPane().setLayout(new BorderLayout());

		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		contentPanel.setLayout(null);
	}

	private void loadComponents() {

		JLabel lbl_From_IP = VPXComponentFactory.createJLabel("From");

		lbl_From_IP.setBounds(20, 11, 61, 29);

		contentPanel.add(lbl_From_IP);

		JLabel lbl_To_IP = VPXComponentFactory.createJLabel("To");

		lbl_To_IP.setBounds(20, 51, 61, 29);

		contentPanel.add(lbl_To_IP);

		txt_From_IP = VPXComponentFactory.createJTextField("172.17.1.27");

		txt_From_IP.setBounds(89, 11, 191, 23);

		contentPanel.add(txt_From_IP);

		txt_From_IP.setColumns(10);

		txt_To_IP = VPXComponentFactory.createJTextField("172.17.1.30");

		txt_To_IP.setColumns(10);

		txt_To_IP.setBounds(89, 51, 191, 23);

		contentPanel.add(txt_To_IP);

		JSeparator separator = VPXComponentFactory.createJSeparator();

		separator.setBounds(0, 85, 329, 2);

		contentPanel.add(separator);

		JPanel buttonPane = VPXComponentFactory.createJPanel();

		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton(new ScanAction("Scan"));

		buttonPane.add(okButton);

		JButton cancelButton = VPXComponentFactory.createJButton(new CancelAction("Cancel"));

		buttonPane.add(cancelButton);
	}

	class ScanAction extends AbstractAction {

		/**
		 * 
		 */

		public ScanAction(String name) {
			putValue(Action.NAME, name);
		}

		private static final long serialVersionUID = -780929428772240491L;

		@Override
		public void actionPerformed(ActionEvent e) {

			VPX_ScanWindow.this.setVisible(false);

			new VPX_ScanStatusWindow(txt_From_IP.getText(), txt_To_IP.getText());

		}
	}

	class CancelAction extends AbstractAction {

		/**
		 * 
		 */

		public CancelAction(String name) {
			putValue(Action.NAME, name);
		}

		private static final long serialVersionUID = -780929428772240491L;

		@Override
		public void actionPerformed(ActionEvent e) {

			VPX_ScanWindow.this.dispose();

		}

	}

	class VPX_ScanStatusWindow extends JDialog {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8556421629676554896L;

		private final JPanel contentPanel = new JPanel();

		private String fromIP, toIP;

		private JLabel lbl_Current_Scanning_IP;

		private PingerThread scan;

		private JProgressBar progressBar;

		private JLabel lbl_Detecting_Processor;

		/**
		 * Create the dialog.
		 */
		public VPX_ScanStatusWindow(String from_ip, String to_ip) {

			this.fromIP = from_ip;

			this.toIP = to_ip;

			init();

			loadComponents();

			centerFrame();

			scan = new PingerThread();

			scan.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent event) {

					switch (event.getPropertyName()) {
					case "progress":

						progressBar.setIndeterminate(false);

						progressBar.setValue((Integer) event.getNewValue());

						break;
					case "state":

						switch ((StateValue) event.getNewValue()) {

						case DONE:

							try {
								VPXSystem vpx = (VPXSystem) scan.get();

								// parent.reloadProcessorTree(vpx);

								VPX_ScanStatusWindow.this.dispose();

							} catch (InterruptedException | ExecutionException e) {

								e.printStackTrace();

							}

							break;

						case STARTED:

						case PENDING:

							break;
						}
						break;
					}
				}
			});

			scan.execute();

			setVisible(true);
		}

		private void init() {

			setTitle("Processing on Scan");

			setType(Type.NORMAL);

			setIconImage(VPXUtilities.getAppIcon());

			setAlwaysOnTop(true);

			setModal(true);

			setBounds(100, 100, 431, 166);

			getContentPane().setLayout(new BorderLayout());

			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

			getContentPane().add(contentPanel, BorderLayout.CENTER);
		}

		private void loadComponents() {

			contentPanel.setLayout(new MigLayout("", "[523.00px]", "[14px][][][][][]"));

			lbl_Detecting_Processor = VPXComponentFactory.createJLabel("Detecting Processors");

			contentPanel.add(lbl_Detecting_Processor, "cell 0 0 1 2");

			lbl_Current_Scanning_IP = VPXComponentFactory.createJLabel("");

			contentPanel.add(lbl_Current_Scanning_IP, "cell 0 4,alignx left,aligny top");

			progressBar = new JProgressBar(0, 100);

			progressBar.setValue(0);

			progressBar.setStringPainted(true);

			contentPanel.add(progressBar, "cell 0 5,growx");

			JPanel buttonPane = VPXComponentFactory.createJPanel();

			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

			getContentPane().add(buttonPane, BorderLayout.SOUTH);

			JButton cancelButton = VPXComponentFactory.createJButton("Cancel");

			cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					VPX_ScanStatusWindow.this.dispose();
				}
			});

			cancelButton.setActionCommand("Cancel");

			buttonPane.add(cancelButton);

		}

		private void centerFrame() {

			Dimension windowSize = getSize();

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

			Point centerPoint = ge.getCenterPoint();

			int dx = centerPoint.x - windowSize.width / 2;

			int dy = centerPoint.y - windowSize.height / 2;

			setLocation(dx, dy);
		}

		class PingerThread extends SwingWorker<VPXSystem, Long> {

			VPXSystem vpxSystem = new VPXSystem();

			@Override
			protected VPXSystem doInBackground() throws Exception {

				Thread.sleep(500);

				long st = VPXUtilities.getLongFromIP(fromIP);

				long en = VPXUtilities.getLongFromIP(toIP);

				long size = en - st;

				int ii = 1;

				String ip;

				for (long i = st; i <= en; i++) {

					publish(i);

					ip = VPXUtilities.getIPFromLong(i);

					ATPCommand processorInfo = VPXTCPConnector.identifyProcessor(ip);

					if (processorInfo != null) {

						parseCMD(ip, processorInfo);

					}

					long scanning_IPs_Completed_Percentage = (ii + 1) * 100 / size;

					if (scanning_IPs_Completed_Percentage <= 100) {

						setProgress(Integer.parseInt("" + scanning_IPs_Completed_Percentage));

						Thread.sleep(150);

						lbl_Detecting_Processor.setVisible(!lbl_Detecting_Processor.isVisible());

						ii++;
					}
				}

				return vpxSystem;
			}

			@Override
			protected void done() {

			}

			@Override
			protected void process(List<Long> chunks) {

				for (final Long string : chunks) {

					lbl_Current_Scanning_IP.setText("IP Address : " + VPXUtilities.getIPFromLong(string));
				}
			}

			private void parseCMD(String ip, ATPCommand cmd) {

				// vpxSystem.addProcessor(new Processor(ip,
				// VPXUtilities.getProcessor(cmd.params.proccesorInfo.processorTYPE)));

			}

		}
	}
	
	public static void main(String[] args) {
		new VPX_ScanWindow().setVisible(true);
	}

}

package com.cti.vpx.controls.hex;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.cti.vpx.controls.tab.VPX_TabbedPane;
import com.cti.vpx.model.VPX;
import com.cti.vpx.util.VPXComponentFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class VPX_MemoryBrowser extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 988169319963990373L;

	private JButton btn_Addr_Go;

	private JButton btn_Addr_NewTab;

	private JLabel lbl_Addr_View;

	private VPX_TabbedPane memoryBrowser;

	private JPanel panel;

	private JComboBox<String> comboBox;

	private HexEditorPanel hexPanel;

	/**
	 * Create the panel.
	 */
	public VPX_MemoryBrowser() {
		init();

		loadComponents();

	}

	private void loadComponents() {
		add(createAddressPanel(), BorderLayout.NORTH);

		createMemoryTab();

		add(memoryBrowser, BorderLayout.CENTER);
	}

	private void init() {
		setLayout(new BorderLayout());

	}

	private JPanel createAddressPanel() {

		JPanel base_Panel = VPXComponentFactory.createJPanel();

		add(base_Panel, BorderLayout.SOUTH);

		base_Panel.setLayout(new BorderLayout(10, 0));

		JPanel btn_Panel = VPXComponentFactory.createJPanel();

		base_Panel.add(btn_Panel, BorderLayout.EAST);

		btn_Addr_Go = VPXComponentFactory.createJButton("Go");
		
		btn_Addr_Go.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				try {
				      String host = "172.17.1.170";
				      
				      int port = VPX.COMM_PORTNO;

				      byte[] message = "Java Source and Support".getBytes();

				      // Get the internet address of the specified host
				      InetAddress address = InetAddress.getByName(host);

				      // Initialize a datagram packet with data and address
				      DatagramPacket packet = new DatagramPacket(message, message.length,
				          address, port);

				      // Create a datagram socket, send the packet through it, close it.
				      DatagramSocket dsocket = new DatagramSocket();
				      dsocket.send(packet);
				      dsocket.close();
				    } catch (Exception e) {
				      System.err.println(e);
				    }
				
			}
		});

		btn_Panel.add(btn_Addr_Go);

		btn_Addr_NewTab = VPXComponentFactory.createJButton(new NewTabAction("New Tab"));

		btn_Panel.add(btn_Addr_NewTab);

		lbl_Addr_View = VPXComponentFactory.createJLabel("Address");

		base_Panel.add(lbl_Addr_View, BorderLayout.WEST);

		base_Panel.setPreferredSize(new Dimension(500, 35));

		panel = new JPanel();
		base_Panel.add(panel, BorderLayout.CENTER);
		panel.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.GROWING_BUTTON_COLSPEC, }, new RowSpec[] { RowSpec
				.decode("35px"), }));

		comboBox = VPXComponentFactory.createJComboBox();
		panel.add(comboBox, "1, 1, fill, center");

		return base_Panel;
	}

	private void createMemoryTab() {

		memoryBrowser = new VPX_TabbedPane(false, true);

		createHexPanel();

		memoryBrowser.add(hexPanel, "Address " + (memoryBrowser.getTabCount() + 1));

	}

	private void addNewTab() {

		memoryBrowser.add(getHexPanel(), "Address " + (memoryBrowser.getTabCount() + 1));

	}

	public void updateMemory(byte[] b){
		hexPanel.handleRecievedBuffer(b);
	}
	
	private void createHexPanel() {
		hexPanel = new HexEditorPanel();
	}

	private HexEditorPanel getHexPanel() {
		return new HexEditorPanel();
	}

	private class NewTabAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3995220949383326443L;

		public NewTabAction(String name) {
			putValue(NAME, name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			addNewTab();

		}

	}
}

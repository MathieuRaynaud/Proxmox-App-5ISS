package org.ctlv.proxmox.manager;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManagerMain {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Start Program");
		ProxmoxAPI api = new ProxmoxAPI();		
		api.checkLoginTicket();
		Controller controller = new Controller(api);
		
		Analyzer analyzer = new Analyzer(api,controller);
		Monitor monitor = new Monitor(api,analyzer);
		
		//monitor.run();
		
		/*System.out.println("Creating a container on server " + Constants.SERVER1 +"...");
		api.createCT(Constants.SERVER1, "126", "ct-tpiss-virt-C5-ct2", Constants.RAM_SIZE[1]);
		System.out.println("Created!");*/
		System.out.println("Status: " +api.getCT(Constants.SERVER1, "3500").getStatus());
		System.out.println("Lock: " + api.getCT(Constants.SERVER1, "3500").getLock());

		System.out.println("Starting container ct-tpiss-virt-C5-ct1");
		api.startCT(Constants.SERVER1, "3500");
		System.out.println("Started!");
		
	}

}
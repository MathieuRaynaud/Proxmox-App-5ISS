package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import java.util.Map.Entry;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.ctlv.proxmox.api.data.Node;
import org.json.JSONException;

public class Analyzer {
	ProxmoxAPI api;
	Controller controller;
	
	public Analyzer(ProxmoxAPI api, Controller controller) {
		this.api = api;
		this.controller = controller;
	}
	
	public void analyzeServer(String serverName) {
		try {
			Node server = api.getNode(serverName);
			
			System.out.println(serverName + " server usage:");
			System.out.println("\t"+"CPU usage: " + server.getCpu()*100 + "%");
			System.out.println("\t"+"Memory usage: " + server.getMemory_used()*100/server.getMemory_total() + "%");
		} catch (LoginException | JSONException | IOException e) {
			e.printStackTrace();}
	}
	
	public long getMemoryOfServer(String serverName) {
		long mem = -1;
		
		try {
			Node server = api.getNode(serverName);
			mem = server.getMemory_used()*100/server.getMemory_total();
		} catch (LoginException | JSONException | IOException e) {
			e.printStackTrace();
		}
		
		return mem;
	}
	
	public ArrayList<String> getIds(){
		ArrayList<String> Ids = new ArrayList<String>();
		try {
			for (String serverName : api.getNodes()) {
				for(LXC lxc : api.getCTs(serverName)) {
					Ids.add(lxc.getVmid());
				}
			}
		} catch (LoginException | JSONException | IOException e) {
			e.printStackTrace();
		}
		return Ids;
	}
	
	
	
	public void analyze(HashMap<String, ArrayList<LXC>> myCTsPerServer)  {
		
		// Mémoire autorisée sur chaque serveur
		
		try {
			long MaxMemCTonServer1 = api.getNode(Constants.SERVER1).getMemory_total()*8/100;
			long MaxMemCTonServer2 = api.getNode(Constants.SERVER2).getMemory_total()*8/100;
		

			// Calculer la quantité de RAM utilisée par mes CTs sur chaque serveur
			for (Entry<String, ArrayList<LXC>> e : myCTsPerServer.entrySet()) {
				analyzeServer(e.getKey());
				
				for(LXC lxc : e.getValue()) {
					if (lxc.getVmid().substring(0, 2).equals("35")) {
						System.out.println("\tContainer " + lxc.getName() +":");
						System.out.println("\t\t"+"CPU usage: " + lxc.getCpu()*100 + "%");
						System.out.println("\t\t"+"Disk usage: " + lxc.getDisk()*100/lxc.getMaxdisk()+ "%");
						System.out.println("\t\t"+"RAM usage: " + lxc.getMem()*100/lxc.getMaxmem()+ "%");
						System.out.println("");
						long ramusedCT = lxc.getMem()*100/api.getNode(e.getKey()).getMemory_total();
						
						if(e.getKey().equals(Constants.SERVER1)) {
							if(ramusedCT >= MaxMemCTonServer1) {
								api.migrateCT(Constants.SERVER1, lxc.getVmid(), Constants.SERVER2);
								System.out.println("CT " + lxc.getVmid() + " migrated to the server " + Constants.SERVER2);
							}
						}
						else {
							if(ramusedCT >= MaxMemCTonServer2) {
								api.migrateCT(Constants.SERVER2, lxc.getVmid(), Constants.SERVER1);
								System.out.println("CT " + lxc.getVmid() + " migrated to the server " + Constants.SERVER1);
							}
						}
					}
				}
				long load = api.getNode(e.getKey()).getMemory_used()*100/api.getNode(e.getKey()).getMemory_total();
				if(load > 12/100) {
					ArrayList<String> existing_ids = getIds();
					long min = (long) Integer.MAX_VALUE;
					LXC toDelete = null;
					for (LXC lxc : e.getValue()) {
						if (lxc.getVmid().substring(0, 2).equals("35")) {
							if (lxc.getUptime() < min) {
								toDelete = lxc;
								min = lxc.getUptime();
							}
						}
					}
					if (toDelete != null) {
						while (api.getCT(e.getKey(), toDelete.getVmid()).getStatus().equals("running")) {
							try {
								System.out.println("Stopping container " + toDelete.getName());
								api.stopCT(e.getKey(), toDelete.getVmid());
								System.out.println("Stopped!");
							} catch (Exception ignore ) {}
						}
					}
				}
			}
		
		} catch (LoginException | JSONException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		
		
		

		
		// Analyse et Actions
		// ...
		
	}

}

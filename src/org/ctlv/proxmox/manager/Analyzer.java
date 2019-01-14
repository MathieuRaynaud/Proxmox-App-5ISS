package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import java.util.Map.Entry;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Ids;
	}
	
	public void analyze(HashMap<String, ArrayList<LXC>> myCTsPerServer)  {

		// Calculer la quantit� de RAM utilis�e par mes CTs sur chaque serveur
		for (Entry<String, ArrayList<LXC>> e : myCTsPerServer.entrySet()) {
			analyzeServer(e.getKey());
			
			for(LXC lxc : e.getValue()) {
				System.out.println("\tContainer " + lxc.getName() +":");
				System.out.println("\t\t"+"CPU usage: " + lxc.getCpu()*100 + "%");
				System.out.println("\t\t"+"Disk usage: " + lxc.getDisk()*100/lxc.getMaxdisk()+ "%");	
				System.out.println("");
			}				
		}

		
		// M�moire autoris�e sur chaque serveur
		// ...
		//getName();
		//getCpu();
		//getDisk();
		
		
		/*
		try {
			for (int i=1; i<=10; i++) {
				String srv ="srv-px"+i;
				System.out.println("CTs sous "+srv);
				
				
				List<LXC> cts = api.getCTs(srv);
				
				for (LXC lxc : cts) {
					System.out.println("\t" + lxc.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		
		// Analyse et Actions
		// ...
		
	}

}

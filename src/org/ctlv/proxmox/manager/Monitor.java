package org.ctlv.proxmox.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;

public class Monitor implements Runnable {

	Analyzer analyzer;
	ProxmoxAPI api;
	
	public Monitor(ProxmoxAPI api, Analyzer analyzer) {
		this.api = api;
		this.analyzer = analyzer;
	}
	
	public void deleteAllCTs () {
		try {
			for (int i=1; i<=10; i++) {
				String srv ="srv-px"+i;
				
				if (srv.equals(Constants.SERVER1)) {
					List<LXC> cts = api.getCTs(srv);
					System.out.println("Delete CTs on Server " + Constants.SERVER1);
					for (LXC lxc : cts) {
						if(lxc.getVmid().substring(0, 2).equals("35")) {
							while (api.getCT(Constants.SERVER1, lxc.getVmid()).getStatus().equals("running")) {
								try {
									System.out.println("Stopping container " + lxc.getName());
									api.stopCT(Constants.SERVER1, lxc.getVmid());
									System.out.println("Stopped!");
								} catch (Exception ignore ) {}
							}
							boolean deleted = false;
							while (!deleted) {
								try {
									System.out.println("Deleting..." + lxc.getVmid());
									api.deleteCT(Constants.SERVER1, lxc.getVmid());
									System.out.println("Deleted!");
									deleted = true;
								} catch (Exception ignore ) {}
							}
						}
					}
				}
				if(srv.equals(Constants.SERVER2)) {
					List<LXC> cts = api.getCTs(srv);
					System.out.println("Delete CTs on Server " + Constants.SERVER2);
					for (LXC lxc : cts) {
						if(lxc.getVmid().substring(0, 2).equals("35")) {
							while (api.getCT(Constants.SERVER2, lxc.getVmid()).getStatus().equals("running")) {
								try {
									System.out.println("Stopping container " + lxc.getName());
									api.stopCT(Constants.SERVER2, lxc.getVmid());
									System.out.println("Stopped!");
								} catch (Exception ignore ) {}
							}
							boolean deleted = false;
							while (!deleted) {
								try {
									System.out.println("Deleting... " + lxc.getVmid());
									api.deleteCT(Constants.SERVER2, lxc.getVmid());
									System.out.println("Deleted!");
									deleted = true;
								} catch (Exception ignore ) {}
							}
						}
					}
				}
					
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void run() {
		
		//while(true) {
			
			// R�cup�rer les donn�es sur les serveurs
			ArrayList<LXC> LXCList = new ArrayList<LXC>();
			ArrayList<LXC> LXCList2 = new ArrayList<LXC>();
			
			try {
				for (int i=1; i<=10; i++) {
					String srv ="srv-px"+i;
					
					if (srv.equals(Constants.SERVER1)) {
						List<LXC> cts = api.getCTs(srv);
						System.out.println("Creation LXC List");
						for (LXC lxc : cts) {
							LXCList.add(lxc);
						}
					}
					if(srv.equals(Constants.SERVER2)) {
						List<LXC> cts = api.getCTs(srv);
						System.out.println("Creation LXC List");
						for (LXC lxc : cts) {
							LXCList2.add(lxc);
						}
					}
						
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("Creation HasMap");
			HashMap<String, ArrayList<LXC>> ctMap = new HashMap<String, ArrayList<LXC>>();		
			ctMap.put(Constants.SERVER1, LXCList);
			ctMap.put(Constants.SERVER2, LXCList2);
			
			/*
			for (Entry<String, ArrayList<LXC>> e : ctMap.entrySet()) {
				System.out.println(e.getKey() + ":");
				for(LXC lxc : e.getValue()) {
					System.out.println("\t"+ lxc.getName());
				}
						
			}*/
			
			// Lancer l'analyse
			System.out.println("Start analyze");
			this.analyzer.analyze(ctMap);
			
			

			
			// attendre une certaine p�riode
			try {
				Thread.sleep(Constants.MONITOR_PERIOD * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		//}
		
	}
}

package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.List;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;


public class Monitor implements Runnable {

	Analyzer analyzer;
	ProxmoxAPI api;
	
	public Monitor(ProxmoxAPI api, Analyzer analyzer) {
		this.api = api;
		this.analyzer = analyzer;
	}
	

	@Override
	public void run() {
		
		while(true) {
			
			// R�cup�rer les donn�es sur les serveurs
			
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
			}

			
			// Lancer l'analyse
			// ...

			
			// attendre une certaine p�riode
			try {
				Thread.sleep(Constants.MONITOR_PERIOD * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}

package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Analyzer {
	ProxmoxAPI api;
	Controller controller;
	
	public Analyzer(ProxmoxAPI api, Controller controller) {
		this.api = api;
		this.controller = controller;
	}
	
	public void analyze(HashMap<String, ArrayList<LXC>> myCTsPerServer)  {
		
		try {
			api.login();
		} catch (LoginException | JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Calculer la quantit� de RAM utilis�e par mes CTs sur chaque serveur
		
		Iterator<String> sv = myCTsPerServer.keySet().iterator();
		while(sv.hasNext()) {
			String server = sv.next();
			System.out.println("Server " + server + ": ");
			Iterator<LXC> ct = myCTsPerServer.get(sv).iterator();
			while(ct.hasNext()) {
				LXC CT = ct.next();
				System.out.println("	CT " + CT.getName() + ": ");
				System.out.println("		RAM used: " + CT.getMem());
				System.out.println("		Disk used: " + CT.getDisk());
				System.out.println("		CPU used: " + CT.getCpu());
			}
		}
		
		// M�moire autoris�e sur chaque serveur
		// ...

		
		// Analyse et Actions
		// ...
		
	}

}

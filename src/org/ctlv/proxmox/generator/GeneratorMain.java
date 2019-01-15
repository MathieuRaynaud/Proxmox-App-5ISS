package org.ctlv.proxmox.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.ctlv.proxmox.manager.Analyzer;
import org.ctlv.proxmox.manager.Controller;
import org.ctlv.proxmox.manager.Monitor;
import org.json.JSONException;

public class GeneratorMain {
	
	static Random rndTime = new Random(new Date().getTime());
	public static int getNextEventPeriodic(int period) {
		return period;
	}
	public static int getNextEventUniform(int max) {
		return rndTime.nextInt(max);
	}
	public static int getNextEventExponential(int inv_lambda) {
		float next = (float) (- Math.log(rndTime.nextFloat()) * inv_lambda);
		return (int)next;
	}
	
	public static void main(String[] args) throws InterruptedException, LoginException, JSONException, IOException {
	
		long baseID = Constants.CT_BASE_ID;
		int lambda = 30;
		
		Integer vmid = 3500;
		Integer indice = 2;
		
		Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();

		ProxmoxAPI api = new ProxmoxAPI();
		Random rndServer = new Random(new Date().getTime());
		Random rndRAM = new Random(new Date().getTime()); 
		
		long memAllowedOnServer1 = (long) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.MAX_THRESHOLD);
		long memAllowedOnServer2 = (long) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.MAX_THRESHOLD);
		
		api.checkLoginTicket();
		Controller controller = new Controller(api);
		
		Analyzer analyzer = new Analyzer(api,controller);
		Monitor monitor = new Monitor(api,analyzer);
		
		monitor.deleteAllCTs();
		
		while (true) {
			
			// 1. Calculer la quantit� de RAM utilis�e par mes CTs sur chaque serveur
			long memOnServer1 = analyzer.getMemoryOfServer(Constants.SERVER1);			
			long memOnServer2 = analyzer.getMemoryOfServer(Constants.SERVER2);
			
			// M�moire autoris�e sur chaque serveur
			float memRatioOnServer1 = api.getNode(Constants.SERVER1).getMemory_total()*16/100;
			float memRatioOnServer2 = api.getNode(Constants.SERVER2).getMemory_total()*16/100;
			
			if (memOnServer1 < memRatioOnServer1 && memOnServer2 < memRatioOnServer2) {  // Exemple de condition de l'arr�t de la g�n�ration de CTs
				
				// choisir un serveur al�atoirement avec les ratios sp�cifi�s 66% vs 33%
				String serverName;
				if (rndServer.nextFloat() < Constants.CT_CREATION_RATIO_ON_SERVER1)
					serverName = Constants.SERVER1;
				else
					serverName = Constants.SERVER2;
				
				ArrayList<String> existing_ids = analyzer.getIds();
				while (existing_ids.contains(vmid.toString()))
					vmid++;
				// cr�er un contenaire sur ce serveur
				System.out.println("Creating a container on server " + serverName +"...");
				api.createCT(serverName, vmid.toString(), "ct-tpiss-virt-C5-ct"+indice, Constants.RAM_SIZE[1]);
				System.out.println("Created!");

				while (api.getCT(serverName, vmid.toString()).getStatus().equals("stopped")) {
					try {
						System.out.println("Starting container ct-tpiss-virt-C5-ct"+indice);
						api.startCT(serverName, vmid.toString());
						System.out.println("Started!");
					} catch (Exception ignore ) {}
				}

				indice++;
				
				// planifier la prochaine cr�ation
				int timeToWait = getNextEventExponential(lambda); // par exemple une loi expo d'une moyenne de 30sec
				
				// attendre jusqu'au prochain �v�nement
				Thread.sleep(500 * timeToWait);
			}
			else {
				System.out.println("Servers are loaded, waiting ...");
				Thread.sleep(Constants.GENERATION_WAIT_TIME* 1000);
			}
			
			// Monitor 
			monitor.run();
		}
		
	}

}

package org.ctlv.proxmox.manager;

import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.ctlv.proxmox.api.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerMain {

	
	public static void main(String[] args) throws Exception {
		ProxmoxAPI api = new ProxmoxAPI();
		
		ArrayList<LXC> ctsList = new ArrayList<LXC>();
		
		HashMap<String, ArrayList<LXC>> ctsMap = new HashMap<String, ArrayList<LXC>>();
		
		ctsList.add(api.getCT(Constants.CT_BASE_NAME, "124"));
		
		ctsMap.put(Constants.SERVER2, ctsList);
				
		Controller controller = new Controller(api);
		Analyzer analyzer = new Analyzer(api, controller);
		Monitor monitor = new Monitor(api, analyzer);
		
		//monitor.run();
		analyzer.analyze(ctsMap);
	}

}
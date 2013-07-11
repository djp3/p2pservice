package edu.uci.ics.luci.p2p4java.p2pchat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;
import edu.uci.ics.luci.p2p4java.exception.PeerGroupException;
import edu.uci.ics.luci.p2p4java.peergroup.PeerGroup;
import edu.uci.ics.luci.p2p4java.platform.NetworkConfigurator;
import edu.uci.ics.luci.p2p4java.platform.NetworkManager;
import edu.uci.ics.luci.p2p4java.util.luci.P2P4Java;


public class MakePipeListener extends AsyncTask<Void, String, Exception> {

	private MainActivity main;
	private AsyncTask<Void,String,Exception> previousTask;
	private ProgressDialog progressBar;
	private Integer progress = null;
	private Random random = new Random();
	
	Integer incrementProgress(){
		if(progress == null){
			progress = 0;
		}
		else{
			if(random.nextInt(100) > progress){
				if(progress < 99){
					progress++;
				}
			}
		}
		return progress;
	}
    
	public MakePipeListener(AsyncTask<Void,String,Exception> previousTask, MainActivity mainActivity, ProgressDialog progressBar) {
		super();
		this.previousTask = previousTask;
		this.main = mainActivity;
		this.progressBar = progressBar;
	}
	
	@Override
	protected void onPreExecute(){
		progressBar.setMessage("Initializing Peer to Peer Network");
		progressBar.setProgress(incrementProgress());
	}
    
	@Override
	protected Exception doInBackground(Void... arg0) {
		try{
			if(previousTask != null){
				try {
					while(previousTask.getStatus() != AsyncTask.Status.FINISHED){
						Exception result = previousTask.get();
						if(result != null){
							return result;
						}
					}
				} catch (InterruptedException e) {
					return e;
				} catch (ExecutionException e) {
					return e;
				}
			}
			
			File file = P2P4Java.getCacheDirectory();
			File file2 = new File(file, "SinkServer");
			URI uri = file2.toURI();
		
			publishProgress("Initializing P2P Network Manager");
			NetworkManager localManager = null;
			localManager = new NetworkManager(NetworkManager.ConfigMode.EDGE, "SinkServer",uri);
			
			publishProgress("Initializing P2P Network Configuration");
			NetworkConfigurator configurator = null;
			configurator = localManager.getConfigurator();
		
			URI TheSeed = URI.create(Globals.SUPER_URI);
			configurator.addSeedRendezvous(TheSeed);
			configurator.addSeedRelay(TheSeed);
		
			publishProgress("Starting P2P Network");
			localManager.startNetwork();
		
		
			// Get the NetPeerGroup
			publishProgress("Getting Peer Group");
			PeerGroup netPeerGroup = localManager.getNetPeerGroup();
			main.setNetPeerGroup(netPeerGroup);
			
			// get the pipe service, and discovery
			publishProgress("Getting Pipe Service");
			main.setPipeService(netPeerGroup.getPipeService());
			
			// create the pipe advertisement
			publishProgress("Getting Pipe Advertisement");
			main.setPipeAdv(SourceServer.getPipeAdvertisement());
			
			main.setManager(localManager);
		
		} catch (PeerGroupException e) {
			return e;
		} catch (IOException e) {
			return e;
		} catch (Exception e) {
			return e;
		}
		return null;
    }
	
	@Override
	protected void onProgressUpdate(String... progress){
		super.onProgressUpdate(progress);
		progressBar.setMessage(progress[0]);
	}

    protected void onPostExecute(Exception exception) {
		super.onPostExecute(exception);

   		TextView myTextView = (TextView) main.findViewById(R.id.editText1);
    	if(exception != null){
    		myTextView.append("I can't start peer to peer \n");
    		myTextView.append(exception.toString()+"\n");
    	}
    	else{
    		myTextView.append("Peer to Peer running!\n");
			progressBar.setMessage("Peer to Peer running");
    		progressBar.dismiss();
    	}
    }
 }
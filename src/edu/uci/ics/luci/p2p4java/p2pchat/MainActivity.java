package edu.uci.ics.luci.p2p4java.p2pchat;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import edu.uci.ics.luci.p2p4java.exception.PeerGroupException;
import edu.uci.ics.luci.p2p4java.peergroup.PeerGroup;
import edu.uci.ics.luci.p2p4java.pipe.InputPipe;
import edu.uci.ics.luci.p2p4java.pipe.PipeService;
import edu.uci.ics.luci.p2p4java.platform.NetworkConfigurator;
import edu.uci.ics.luci.p2p4java.platform.NetworkManager;
import edu.uci.ics.luci.p2p4java.protocol.PipeAdvertisement;
import edu.uci.ics.luci.p2p4java.util.luci.P2P4Java;

public class MainActivity extends Activity {
	
	transient NetworkManager manager = null;
    
    class CheckInternetTask extends AsyncTask<Void, Void, StringBuilder> {

        private Exception exception = null;
        
		@Override
		protected StringBuilder doInBackground(Void... arg0) {
            try {
            	URL url = null;
        		try {
        			url = new URL("http://www.google.com");
        		} catch (MalformedURLException e) {
        			exception = e;
        			return null;
        		}
        		
        	    BufferedReader reader = null;
        	    StringBuilder builder = new StringBuilder();
        	    try {
        	        reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        	        for (String line; (line = reader.readLine()) != null;) {
        	            builder.append(line.trim());
        	        }
        	    } catch (UnsupportedEncodingException e) {
        			exception = e;
        			return null;
        		} catch (IOException e) {
        			exception = e;
        			return null;
        		} catch (RuntimeException e){
        			exception = e;
        			return null;
        		} finally {
        	        if (reader != null){
        	        	try {
        	        		reader.close();
        	        	} catch (IOException e) {
                		}
        	        }
        	    }
        	    return builder;
            } catch (Exception e) {
       			exception = e;
       			return null;
            }
        }

        protected void onPostExecute(StringBuilder result) {
       		TextView myTextView = (TextView) findViewById(R.id.editText1);
        	if(result == null){
        		myTextView.append("I can't anything from the Internet. Permission problem?\n");
        		if(exception != null){
        			myTextView.append(exception.toString()+"\n");
        		}
        	}
        	else{
        		myTextView.append("Internet okay\n");
        		new MakePipeListenerTask().execute();
        	}
        }
    }
    
    class MakePipeListenerTask extends AsyncTask<Void, Void, NetworkManager> {

        private Exception exception = null;
        
		@Override
		protected NetworkManager doInBackground(Void... arg0) {
            try {
            	File file = P2P4Java.getCacheDirectory();
    			File file2 = new File(file, "SinkServer");
    			URI uri = file2.toURI();
    			
    			NetworkManager localManager = null;
    			localManager = new NetworkManager(NetworkManager.ConfigMode.EDGE, "SinkServer",uri);
    			NetworkConfigurator configurator = null;
    			configurator = localManager.getConfigurator();
    			
    			URI TheSeed = URI.create(Globals.SUPER_URI);
    			configurator.addSeedRendezvous(TheSeed);
    			configurator.addSeedRelay(TheSeed);
    			
    			localManager.startNetwork();
    			
    			PeerGroup netPeerGroup = null;
    			PipeService pipeService = null;
    		    PipeAdvertisement pipeAdv = null;
    		    InputPipe inputPipe = null;
    			 // Get the NetPeerGroup
                netPeerGroup = localManager.getNetPeerGroup();
                // get the pipe service, and discovery
                pipeService = netPeerGroup.getPipeService();
                // create the pipe advertisement
                pipeAdv = SourceServer.getPipeAdvertisement();
    			
    			return localManager;
    			
			} catch (PeerGroupException e) {
				exception = e;
				return null;
            } catch (IOException e) {
            	exception = e;
            	return null;
            } catch (RuntimeException e) {
            	exception = e;
            	return null;
			}
        }

        protected void onPostExecute(NetworkManager result) {
       		TextView myTextView = (TextView) findViewById(R.id.editText1);
        	if(result == null){
        		myTextView.append("I can't start peer to peer \n");
        		if(exception != null){
        			myTextView.append(exception.toString()+"\n");
        		}
        	}
        	else{
        		myTextView.append("Peer To Peer started\n");
        		manager = result;
        	}
        	
        }

     }
    
	@Override
	protected void onStart() {
		super.onStart();
	}
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView myTextView = (TextView) findViewById(R.id.editText1);
		myTextView.setText("");
		
		//Set up compatibility layer to handle Android
		P2P4Java.setContext(this.getApplicationContext());
		
		new CheckInternetTask().execute();
		myTextView.setText("Background processes started\n");
		
		//myPipeMsgListener = new MyPipeMsgListener(myTextView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	

}

package edu.uci.ics.luci.p2p4java.p2pchat;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import edu.uci.ics.luci.p2p4java.peergroup.PeerGroup;
import edu.uci.ics.luci.p2p4java.pipe.InputPipe;
import edu.uci.ics.luci.p2p4java.pipe.PipeService;
import edu.uci.ics.luci.p2p4java.platform.NetworkManager;
import edu.uci.ics.luci.p2p4java.protocol.PipeAdvertisement;
import edu.uci.ics.luci.p2p4java.util.luci.P2P4Java;

public class MainActivity extends Activity {
	
	transient private NetworkManager manager = null;
	
	transient private PeerGroup netPeerGroup = null;
	transient private PipeService pipeService = null;
    transient private PipeAdvertisement pipeAdv = null;
    transient private InputPipe inputPipe = null;
  
    
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
		
		ProgressDialog progressBar = makeProgressBar();
		progressBar.setMessage("Starting background processes");
		
		AsyncTask<Void, String, Exception> task = null;
		task = new CheckCharSet(task,this,progressBar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
		task = new CheckInternet(task,this,progressBar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(Void) null);
		task = new MakePipeListener(task, this,progressBar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
		
	}

	private ProgressDialog makeProgressBar() {
        ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Initializing ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        return progressBar;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public NetworkManager getManager() {
		return manager;
	}

	public void setManager(NetworkManager manager) {
		this.manager = manager;
	}
	
	public synchronized void setNetPeerGroup(PeerGroup netPeerGroup) {
		this.netPeerGroup = netPeerGroup;
	}
	
	public synchronized PeerGroup getNetPeerGroup(){
		return this.netPeerGroup;
	}


	public synchronized void setPipeService(PipeService pipeService) {
		this.pipeService = pipeService;
	}
	
	public synchronized PipeService getPipeService(){
		return(this.pipeService);
	}


	public synchronized void setPipeAdv(PipeAdvertisement pipeAdvertisement) {
		this.pipeAdv = pipeAdvertisement;
	}
	
	public synchronized PipeAdvertisement getPipeAdv(){
		return(this.pipeAdv);
	}


}

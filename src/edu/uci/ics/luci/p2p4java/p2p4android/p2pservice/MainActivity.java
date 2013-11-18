package edu.uci.ics.luci.p2p4java.p2p4android.p2pservice;


import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import edu.uci.ics.luci.p2p4java.endpoint.StringMessageElement;
import edu.uci.ics.luci.p2p4java.p2p4android.lib.P2PService;
import edu.uci.ics.luci.p2p4java.p2p4android.lib.P2PStatus;
import edu.uci.ics.luci.p2p4java.p2p4android.lib.ServiceManager;

public class MainActivity extends Activity implements ControlFragment.P2PEngine {
	
	/****************************************************************/
	
	/*
	 *  Code for handling UI events generated off the UI thread
	 *
	 */
	
	/**
	 * Tell the UI fragment to enable the right combo of start and stop buttons based on whether the 
	 * service appears to be running or not
	 */
	public void resetUI() {
		ControlFragment fragment = (ControlFragment) getFragmentManager().findFragmentById(R.id.p2p_control_fragment);
		if (fragment != null && fragment.isInLayout()) {
			fragment.setUIView();
		}
	}
	
	/**
	 * Tasks the UI can do from off the main thread
	 *
	 */
	public enum UITask {
		UPDATE_BUTTONS, 	// Adjust UI reflect the status of the service
		SERVICE_HAS_STOPPED //Adjust text fields to reflect that the P2P service has stopped
							//sending heartbeats
	};
	
    private static class UIHandler extends Handler {
    	
    	private MainActivity parent;

		public UIHandler(MainActivity parent,Looper looper) {
    		super(looper);
    		this.parent=parent;
		}

		@Override
        public void handleMessage(Message inputMessage) {
            if(inputMessage.what == UITask.UPDATE_BUTTONS.ordinal()){
            	parent.resetUI();
            }
            else if(inputMessage.what == UITask.SERVICE_HAS_STOPPED.ordinal()){
            	TextView myTextView = (TextView) parent.findViewById(R.id.statusText);
          		
          		long x = System.currentTimeMillis();
                TextView myTextView2 = (TextView) parent.findViewById(R.id.heartbeatText);
                myTextView2.setTextColor(Color.parseColor("#BBBBBB"));
        		myTextView2.setText("Last Update: "+myTextView.getText()+"\nReceived at: "+DateFormat.getDateTimeInstance().format(new Date(x)));
        		
          		myTextView.setText("Updates have stopped");

            }
            else{
            	super.handleMessage(inputMessage);
            }
    	}
    };
    
    UIHandler uihandler = new UIHandler(this,Looper.getMainLooper());
    
	/****************************************************************/
	
	/*
	 *  Code for handling messages from the P2P Service, passed to Service Manager for managing 
	 *
	 */
    
	private ServiceManager service;
    Messenger serviceMessenger = null;

	private boolean directIntent = false;

	static class P2PServiceIncomingHandler extends Handler {
		
		private MainActivity parent;
		private Timer watchdog;
		
		P2PServiceIncomingHandler(MainActivity activity){
			this.parent = activity;
		}
		
        @Override
        public void handleMessage(Message msg) {
        	
			switch (msg.what) {
            case P2PService.MSG_FROM_SET_STATUS_VALUE:{
            	P2PStatus x = (P2PStatus) msg.obj;
        		Log.i(this.getClass().getCanonicalName(),"Received a P2PStatus Value: "+x.translate());
        		
                TextView myTextView = (TextView) parent.findViewById(R.id.statusText);
        		myTextView.setText(x.translate());
        		
        		/* If the list fragment is going, then update the UI to reflect the new state */
        		if(x.equals(P2PStatus.P2P_FAILURE) || x.equals(P2PStatus.P2P_SUCCESS) || x.equals(P2PStatus.OFF)){
        			parent.uihandler.obtainMessage(UITask.UPDATE_BUTTONS.ordinal(),null).sendToTarget();
        		}
        		//Notice that this flows into the next condition so that any message is considered a heartbeat
            }
            case P2PService.MSG_FROM_HEARTBEAT:{
            	long x = System.currentTimeMillis();
        		Log.i(this.getClass().getCanonicalName(),"Received a heartbeat: "+x);
        		
                TextView myTextView = (TextView) parent.findViewById(R.id.heartbeatText);
                myTextView.setTextColor(Color.parseColor("#555555"));
        		myTextView.setText("Alive at: "+DateFormat.getDateTimeInstance().format(new Date(x)));
        		
        		if(watchdog != null){
        			watchdog.cancel();
        			watchdog = null;
        		}
        		watchdog = new Timer();
        		watchdog.schedule(new TimerTask(){
					@Override
					public void run() {
                        parent.uihandler.obtainMessage(UITask.UPDATE_BUTTONS.ordinal(),null).sendToTarget();
                        parent.uihandler.obtainMessage(UITask.SERVICE_HAS_STOPPED.ordinal(),null).sendToTarget();

					}}, (long) (P2PService.HEARTBEAT_FREQUENCY * 2));
                break;
            }
            
            default:
                super.handleMessage(msg);
            }
        }

    }
	
	
	/****************************************************************/
	
	/*
	 *  Lifecyle 
	 *
	 */
	
	//I'm started or reoriented
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.i(this.getClass().getCanonicalName(), "onCreate - mark");

		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
		String action = intent.getAction();
        //Log.i(this.getClass().getCanonicalName(), "onCreate - mark: "+action);
		
        if(action == null){//Direct intent call
        	directIntent = true;
        }
        else if(action.equals("android.intent.action.MAIN")){
        	//Launched as an app, hide goback button
			//Button goBack = (Button) findViewById(R.id.buttonGoBack);
			//goBack.setVisibility(View.GONE);
		}

		
		if(service == null){
			TextView myTextView = (TextView) findViewById(R.id.textView1);
			myTextView.setFocusableInTouchMode(true);
			myTextView.requestFocus();
			
			myTextView = (TextView) findViewById(R.id.statusText);
			myTextView.setText("");
			
			myTextView = (TextView) findViewById(R.id.heartbeatText);
			myTextView.setText("");
		
			try{
				service = new ServiceManager(this,P2PService.class, new P2PServiceIncomingHandler(this));
			}
			catch(NoClassDefFoundError e){
				Log.e(this.getClass().getCanonicalName(), "Unable to find a class. Probably a library is not installed: "+e);
			}
		}
		
		if(directIntent){
			if(!service.isRunning()){
				service.start();
			}
			if(!service.isBound()){
				service.bind();
			}
		}
	}
	
	
	//I'm foreground
	@Override
	protected void onResume() {
		super.onResume();
        Log.i(this.getClass().getCanonicalName(), "onResume - mark");
		resetUI();
		
	}
	
	//I'm obscured
	@Override
	protected void onPause() {
		super.onPause();
        Log.i(this.getClass().getCanonicalName(), "onPause - mark");
	}
	
	//I'm dying
	@Override
	protected void onDestroy(){
		super.onDestroy();
        Log.i(this.getClass().getCanonicalName(), "onDestroy - mark");
        service.unbind();
	}
	
	/****************************************************************/
	
	/*
	 *  Handle buttons from UI fragment 
	 *
	 */
	
	
	@Override
	public void onP2PStart() {
		if(service != null){
			service.start();
		}
	}
	

	@Override
	public void onP2PBind(){
		if(service != null){
			service.bind();
		}
	}
	
	
	@Override
	public void onP2PUnbind(){
		if(service != null){
			service.unbind();
		}
	}
	
	
	int count = 0;
	@Override
	public void onP2PStartSource(){
		
		edu.uci.ics.luci.p2p4java.endpoint.Message packet = new edu.uci.ics.luci.p2p4java.endpoint.Message();
		Date date = new Date(System.currentTimeMillis());
		String data = date.toString();
		StringMessageElement sme = new StringMessageElement(examples.SourceServer.MESSAGE_NAME_SPACE,"Hello World, This is message #"+(count++)+", time:"+data , null);
		
		packet.addMessageElement(null,sme);

		/* Send the data to the service for forwarding to the network */
		Message message = Message.obtain(null, P2PService.MSG_TO_SEND_DATA,packet);
		
		try {
			service.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onP2PStopSource(){
		//TODO: Put code here
	}
	
	@Override
	public void onP2PStop(){
		if(service != null){
			service.stop();
		}
	}
	
	@Override
	public int isP2PServiceRunning() {
		if(service != null){
			 boolean x = service.isRunning();
			 if(x){
				 return 1;
			 }
			 else{
				 return 0;
			 }
		}
		else{
			return -1;
		}
	}
	
	@Override
	public int isP2PServiceBound() {
		if(service != null){
			 boolean x = service.isBound();
			 if(x){
				 return 1;
			 }
			 else{
				 return 0;
			 }
		}
		else{
			return 0;
		}
	}

}

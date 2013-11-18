package edu.uci.ics.luci.p2p4java.p2p4android.p2pservice;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ControlFragment extends Fragment {
	
	/**
	 *  Interface that any activity that uses Control Fragment must implement
	 *
	 */
	public interface P2PEngine {
		public void onP2PStart();
		public void onP2PStop();
		public void onP2PBind();
		public void onP2PUnbind();
		public void onP2PStartSource();
		public void onP2PStopSource();
		public int isP2PServiceRunning();
		public int isP2PServiceBound();
	}
	
	private P2PEngine p2pEngineListener = null;
	
	private Button buttonStart = null;
	private Button buttonStop = null;
	private Button buttonBind = null;
	private Button buttonUnbind = null;
	private Button buttonStartSource = null;
	private Button buttonStopSource = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		try{
		view = inflater.inflate(R.layout.fragment_p2pcontrol, container, false);
		
		buttonStart = (Button) view.findViewById(R.id.buttonStart);
		buttonStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(p2pEngineListener != null){
					p2pEngineListener.onP2PStart();
				}
				setUIView();
			}
		});
		
		buttonStop = (Button) view.findViewById(R.id.buttonStop);
		buttonStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(p2pEngineListener != null){
					p2pEngineListener.onP2PStop();
				}
				setUIView();
			}
		});
		
		buttonBind = (Button) view.findViewById(R.id.buttonBind);
		buttonBind.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(p2pEngineListener != null){
					p2pEngineListener.onP2PBind();
				}
				setUIView();
			}
		});
		
		buttonUnbind = (Button) view.findViewById(R.id.buttonUnbind);
		buttonUnbind.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(p2pEngineListener != null){
					p2pEngineListener.onP2PUnbind();
				}
				setUIView();
			}
		});
		
		buttonStartSource = (Button) view.findViewById(R.id.buttonStartSource);
		buttonStartSource.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(p2pEngineListener != null){
					p2pEngineListener.onP2PStartSource();
				}
				setUIView();
			}
		});
		
		buttonStopSource = (Button) view.findViewById(R.id.buttonStopSource);
		buttonStopSource.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(p2pEngineListener != null){
					p2pEngineListener.onP2PStopSource();
				}
				setUIView();
			}
		});
		
		setUIView();
		
		}
		catch(RuntimeException e){
			Log.e(this.getClass().getCanonicalName(),"Here is the problem: "+e);
		}
		return view;
	}

	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		if (activity instanceof P2PEngine) {
			p2pEngineListener = (P2PEngine) activity;
		} else {
			throw new ClassCastException(activity.toString() + " must implement "+P2PEngine.class.getCanonicalName());
		}
		
		setUIView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		p2pEngineListener = null;
	}
	
	
	/**
	 * Set the buttons on the fragment to reflect the state of the service
	 */
	public void setUIView() {
		int running = p2pEngineListener.isP2PServiceRunning();
		int bound = p2pEngineListener.isP2PServiceBound();
		setUIView(running,bound);
	}
	
	public void setUIView(int running,int bound) {
		if(running == 1){
			if(buttonStart != null){
				buttonStart.setEnabled(false);
			}
			if(buttonStop != null){
				buttonStop.setEnabled(true);
			}
		}
		else if(running == 0){
			if(buttonStart != null){
				buttonStart.setEnabled(true);
			}
			if(buttonStop != null){
				buttonStop.setEnabled(false);
			}
		}
		else {
			if(buttonStart != null){
				buttonStart.setEnabled(false);
			}
			if(buttonStop != null){
				buttonStop.setEnabled(false);
			}
		}
		
		
		if(bound == 1){
			if(buttonBind != null){
				buttonBind.setEnabled(false);
			}
			if(buttonUnbind != null){
				buttonUnbind.setEnabled(true);
			}
			if(buttonStartSource != null){
				buttonStartSource.setEnabled(true);
			}
			if(buttonStopSource != null){
				buttonStopSource.setEnabled(true);
			}
		}
		else if(bound == 0){
			if(running == 1){
				if(buttonBind != null){
					buttonBind.setEnabled(true);
				}
			}
			else{
				if(buttonBind != null){
					buttonBind.setEnabled(false);
				}
			}
			if(buttonUnbind != null){
				buttonUnbind.setEnabled(false);
			}
			
			if(buttonStartSource != null){
				buttonStartSource.setEnabled(false);
			}
			if(buttonStopSource != null){
				buttonStopSource.setEnabled(false);
			}
		}
		else {
			if(buttonBind != null){
				buttonBind.setEnabled(false);
			}
			if(buttonUnbind != null){
				buttonUnbind.setEnabled(false);
			}
			if(buttonStartSource != null){
				buttonStartSource.setEnabled(false);
			}
			if(buttonStopSource != null){
				buttonStopSource.setEnabled(false);
			}
		}
	}

}

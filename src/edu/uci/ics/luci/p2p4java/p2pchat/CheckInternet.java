package edu.uci.ics.luci.p2p4java.p2pchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

public class CheckInternet extends AsyncTask<Void, String, Exception> {

		private MainActivity main;
		private AsyncTask<Void,String,Exception> previousTask;
		private ProgressDialog progressBar;
		
        
		public CheckInternet(AsyncTask<Void,String,Exception> previousTask, MainActivity mainActivity,ProgressDialog progressBar) {
			super();
			this.previousTask = previousTask;
			this.main = mainActivity;
			this.progressBar = progressBar;
		}
		
		@Override
		protected void onPreExecute(){
			progressBar.setMessage("Checking Internet Connection");
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
			
				URL url = null;
				try {
					url = new URL("http://www.google.com");
				} catch (MalformedURLException e) {
					return e;
				}
      		
				BufferedReader reader = null;
				StringBuilder builder = new StringBuilder();
				try {
					reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
					for (String line; (line = reader.readLine()) != null;) {
						builder.append(line.trim());
					}
				} catch (UnsupportedEncodingException e) {
					return e;
				} catch (IOException e) {
					return e;
				} catch (RuntimeException e){
					return e;
				} finally {
					if (reader != null){
						try {
							reader.close();
						} catch (IOException e) {
						}
					}
				}
			}
			catch(Exception e){
				return e;
			}
			return null;
        }
		
		@Override
		protected void onProgressUpdate(String... progress){
			super.onProgressUpdate(progress);
			progressBar.setMessage(progress[0]);
		}

		@Override
        protected void onPostExecute(Exception exception) {
			super.onPostExecute(exception);
        	
       		if(exception != null){
       			TextView myTextView = (TextView) main.findViewById(R.id.editText1);
        		myTextView.append("I can't anything from the Internet. Permission problem?\n");
        		myTextView.append(exception.toString()+"\n");
        	}
        	else{
        		progressBar.setMessage("Internet OK");
        	}
        }
}

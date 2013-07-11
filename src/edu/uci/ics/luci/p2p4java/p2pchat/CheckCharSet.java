package edu.uci.ics.luci.p2p4java.p2pchat;

import java.util.concurrent.ExecutionException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

public class CheckCharSet extends AsyncTask<Void, String, Exception> {

		private MainActivity main;
		private AsyncTask<Void,String,Exception> previousTask;
		private ProgressDialog progressBar;
        
		public CheckCharSet(AsyncTask<Void,String,Exception> previousTask, MainActivity mainActivity, ProgressDialog progressBar) {
			super();
			this.previousTask = previousTask;
			this.main = mainActivity;
			this.progressBar = progressBar;
		}
		
		@Override
		protected void onPreExecute(){
			progressBar.setMessage("Checking Character Set is UTF-8");
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
				
				String c = java.nio.charset.Charset.defaultCharset().name();
				if(!c.equals("UTF-8")){
					return new IllegalArgumentException("The character set is not UTF-8:"+c);
				}
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

		@Override
        protected void onPostExecute(Exception exception) {
			super.onPostExecute(exception);
			
       		if(exception != null){
       			TextView myTextView = (TextView) main.findViewById(R.id.editText1);
       			myTextView.append("Chararacter Set is not ok\n");
       			myTextView.append(exception.toString()+"\n");
        	}
        	else{
        		progressBar.setMessage("Character Set is UTF-8");
        	}
        }
}

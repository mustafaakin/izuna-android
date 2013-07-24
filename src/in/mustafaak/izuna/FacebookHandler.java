package in.mustafaak.izuna;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SharedPreferencesTokenCachingStrategy;
import com.facebook.TokenCachingStrategy;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;

public class FacebookHandler {
	// Screw you Facebook SDK for limiting me to use only android views etc. 
	// I want to use OpenGL for all of my game, f*ck me right? 

	private static final String SESSION_BUNDLE_SAVE_KEY = "com.facebook.sdk.Session.saveSessionKey";
    private static final String AUTH_BUNDLE_SAVE_KEY = "com.facebook.sdk.Session.authBundleKey";
    // bundle.putByteArray(SESSION_BUNDLE_SAVE_KEY, outputStream.toByteArray());
    // bundle.putBundle(AUTH_BUNDLE_SAVE_KEY, session.authorizationBundle); => is always null, 
	private MainActivity activity;

	private Bundle facebookBundle;

	public Bundle getFacebookBundle() {
		return facebookBundle;
	}

	public void login() {
		if (facebookBundle != null) {
			Log.d("Facebookbundle", facebookBundle.toString());
			Session s = Session.restoreSession(activity, new SharedPreferencesTokenCachingStrategy(activity),
					new StatusCallback() {
						@Override
						public void call(Session session, SessionState state, Exception exception) {
							if (exception != null) {
								exception.printStackTrace();
							} else {
							}							
						}
					}, facebookBundle);
			Session.setActiveSession(s);
			if ( s.isOpened()){
				putScoreActually(6000,s);
				getScores(null);
			} 
			
		} else {
			openActiveSession(activity, true, new StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					if (exception != null) {
						exception.printStackTrace();
					}
					if (session.isOpened()) {
						facebookBundle = new Bundle();
						Session.saveSession(session, facebookBundle);						
						writeBundle(facebookBundle);
						// Bundle b = readBundle(); => for testing
					}
				}
			}, Arrays.asList(new String[] { "user_games_activity, friends_games_activity" }));

		}
	}

	public static Session openActiveSession(Activity activity, boolean allowLoginUI, StatusCallback callback,
			List<String> permissions) {
		OpenRequest openRequest = new OpenRequest(activity).setPermissions(permissions).setCallback(callback);
		Session session = new Session.Builder(activity).build();
		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
			Session.setActiveSession(session);
			session.openForRead(openRequest);
			return session;
		}
		return null;
	}

	public FacebookHandler(MainActivity activity) {
		this.activity = activity;
		facebookBundle = readBundle();
	}

	private void writeBundle(Bundle b) {
		try {
			FileOutputStream fos = activity.openFileOutput("facebook", Context.MODE_PRIVATE);
			fos.write(b.getByteArray(SESSION_BUNDLE_SAVE_KEY));
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Bundle readBundle(){
		try {
			FileInputStream fos = activity.openFileInput("facebook");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int bytesRead;
			while ((bytesRead = fos.read(b)) != -1) {
			   bos.write(b, 0, bytesRead);
			}
			byte[] data = bos.toByteArray();
			
			
			Bundle bundle = new Bundle(2);
			bundle.putBundle(AUTH_BUNDLE_SAVE_KEY, null);
			bundle.putByteArray(SESSION_BUNDLE_SAVE_KEY, data);			
			return bundle;
		} catch ( Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private void putScoreActually(int score, Session s) {
		Bundle params = new Bundle();
		params.putInt("score", score);
		final Request request = new Request(s, "me/scores", params, HttpMethod.POST,
				new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						if (response.getError() != null) {
							Log.d("Score post error", response.getError().getErrorMessage());
						} else {
							Log.d("Score post Response", response.getGraphObject().asMap().toString());
						}
					}
				});
		activity.runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				request.executeAsync();				
			}
		});
	}
	
	public void getScores(ScoreReadyCallback c) {
		Bundle params = new Bundle();
		Log.d("put score actually", "actually score");
		final Request request = new Request(Session.getActiveSession(), Constants.APP_ID + "/scores", params, HttpMethod.GET,
				new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						if (response.getError() != null) {
							Log.d("Score post error", response.getError().getErrorMessage());
						} else {
							Log.d("Score post Response", response.getGraphObject().asMap().toString());
						}
					}
				});
		activity.runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				request.executeAsync();				
			}
		});
	}

	public interface ScoreReadyCallback {
		public void onReady(List<ScoreElement> scores);
	}

	public static class ScoreElement {
		String username;
		int score;

		public ScoreElement(String username, int score) {
			this.username = username;
			this.score = score;
		}
	}
}

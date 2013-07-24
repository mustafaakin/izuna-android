package in.mustafaak.izuna;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.facebook.model.GraphObject;

public class FacebookScoreHandler {
	// Screw you Facebook SDK for limiting me to use only android views etc.
	// I want to use OpenGL for all of my game, f*ck me right?
	public List<ScoreElement> getLocalScores() {
		ArrayList<ScoreElement> list = new ArrayList<ScoreElement>();
		SharedPreferences sp = activity.getSharedPreferences("scores", 0);
		Editor e = sp.edit();

		return list;
	}

	public static class ScoreElement {
		String username;
		int score;

		public ScoreElement(String username, int score) {
			this.username = username;
			this.score = score;
		}
		
		@Override
		public String toString() {
			return username + "  " + score;
		} 
	}

	public static interface ScoreReadyCallback {
		public void onReady(List<ScoreElement> scores);
	}

	private static final String SESSION_BUNDLE_SAVE_KEY = "com.facebook.sdk.Session.saveSessionKey";

	private static final String AUTH_BUNDLE_SAVE_KEY = "com.facebook.sdk.Session.authBundleKey";

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

	// bundle.putByteArray(SESSION_BUNDLE_SAVE_KEY, outputStream.toByteArray());
	// bundle.putBundle(AUTH_BUNDLE_SAVE_KEY, session.authorizationBundle); =>
	// is always null,
	private MainActivity activity;

	private Bundle facebookBundle;

	public FacebookScoreHandler(MainActivity activity) {
		this.activity = activity;
		// facebookBundle = readBundle();
		// intializeOldLogin();
	}

	public Bundle getFacebookBundle() {
		return facebookBundle;
	}

	public void getScores(final ScoreReadyCallback c) {
		Bundle params = new Bundle();
		Log.d("get scoes", "called");
		final Request request = new Request(Session.getActiveSession(), Constants.APP_ID + "/scores", params,
				HttpMethod.GET, new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						if (response.getError() != null) {
							Log.d("Score post error", response.getError().getErrorMessage());
						} else {
							ArrayList<ScoreElement> allScores = new ArrayList<ScoreElement>();
							JSONObject data =	response.getGraphObject().getInnerJSONObject();
							try {
								JSONArray scoresJSON = data.getJSONArray("data");
								for( int i = 0; i < scoresJSON.length(); i++){
									JSONObject scoreJSON = scoresJSON.getJSONObject(i);
									// ScoreElement s 
									int scoreValue = scoreJSON.getInt("score");
									String username = scoreJSON.getJSONObject("user").getString("name");
									ScoreElement scoreElement = new ScoreElement(username, scoreValue);
									allScores.add(scoreElement);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							c.onReady(allScores);
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

	public void intializeOldLogin() {
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
		}
	}

	public boolean isLoggedIn() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			return true;
		} else {
			return false;
		}
	}

	public void login(StatusCallback statusCallback) {
		openActiveSession(activity, true, statusCallback, Arrays.asList(new String[] { "friends_games_activity" }));
	}

	private void putScoreActually(int score, Session s) {
		Bundle params = new Bundle();
		params.putInt("score", score);
		final Request request = new Request(s, "me/scores", params, HttpMethod.POST, new Request.Callback() {
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

	private Bundle readBundle() {
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
}

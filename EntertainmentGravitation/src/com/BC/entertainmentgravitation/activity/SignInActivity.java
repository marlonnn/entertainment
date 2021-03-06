package com.BC.entertainmentgravitation.activity;

import java.util.HashMap;
import java.util.List;

import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.BC.androidtool.BaseActivity;
import com.BC.androidtool.HttpThread.WorkerManager;
import com.BC.androidtool.JSON.BaseEntity;
import com.BC.androidtool.utils.Audio;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.HttpThread.InfoSource;
import com.BC.entertainmentgravitation.HttpThread.SimpleHttpTask;
import com.BC.entertainmentgravitation.fragment.CalendarFragemt;
import com.BC.entertainmentgravitation.json.SignTime;
import com.BC.entertainmentgravitation.utl.HttpUtil;
import com.BC.entertainmentgravitation.utl.TimestampTool;
import com.BC.entertainmentgravitation.utl.ToastUtil;
import com.BC.entertainmentgravitation.view.EmotionsView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 签到
 * 
 * @author shuzhi
 * 
 */
public class SignInActivity extends BaseActivity {

	Button signInButton;

	CalendarFragemt calendarFragemt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		signInButton = (Button) findViewById(R.id.signInButton);
		calendarFragemt = (CalendarFragemt) getSupportFragmentManager()
				.findFragmentById(R.id.fragment1);
		signInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendReqStarInformation();
			}
		});
		// showEmotionsView();
		String[] strings = TimestampTool.getCurrentDate().split("-");
		setText(R.id.month, strings[1] + "月签到");
		sendReq();
	}

	private RefreshHandler mRedrawHandler = new RefreshHandler();
	EmotionsView ev;
	AudioTrack audioTrack;

	private void showEmotionsView() {
		// 获得表情雨视图,加载icon到内存(在布局文件中置入自定义EmotionsView)
		ev = (EmotionsView) findViewById(R.id.emotion_view); //
		// 此处可实现表情图片的更替，具体判断来自发送的文本内容
		int intDrawable = R.drawable.memoney;

		audioTrack = Audio.palyAudio(this, R.raw.g5293);

		ev.LoadEmotionImage(intDrawable);
		ev.setVisibility(View.VISIBLE); // 获取当前屏幕的高和宽
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		ev.setView(dm.heightPixels, dm.widthPixels);
		updateEmotions();
		sendReq();
	}

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (ev == null || ev.isEnd()) {
				if (audioTrack != null) {
					audioTrack.stop();
					audioTrack.release();
				}
				return;
			}
			ev.addRandomEmotion();
			ev.invalidate();
			sleep(50);
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	public void updateEmotions() {
		ev.setEnd(false);
		ev.clearAllEmotions();
		ev.addRandomEmotion();
		mRedrawHandler.removeMessages(0);
		mRedrawHandler.sleep(100);
	}

	@Override
	public void requestSuccessful(String jsonString, int taskType) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		switch (taskType) {
		case InfoSource.sign_in:
			BaseEntity<Reward> baseEntity3 = gson.fromJson(jsonString,
					new TypeToken<BaseEntity<Reward>>() {
					}.getType());
			Reward Reward_type = baseEntity3.getData();
			Toast.makeText(this, "签到成功,获得：" + Reward_type.Reward_type + "个娱币",
					Toast.LENGTH_LONG).show();
			showEmotionsView();
			break;
		case InfoSource.continuous:
			BaseEntity<Continuous> baseEntity4 = gson.fromJson(jsonString,
					new TypeToken<BaseEntity<Continuous>>() {
					}.getType());
			if (baseEntity4.getData().getContinuous() != null
					&& !baseEntity4.getData().getContinuous().equals("")) {
				setText(R.id.continuous, baseEntity4.getData().getContinuous());
				calendarFragemt.setSign(baseEntity4.getData().getSigns());
				int continuous = Integer.parseInt(baseEntity4.getData()
						.getContinuous().trim());
				switch (continuous) {
				case 0:
					setText(R.id.total, "累计签到天数\n还有额外奖励");
					break;
				case 1:
					setText(R.id.total, "明日签到获得150娱币");
					break;
				case 2:
					setText(R.id.total, "明日签到获得200娱币");
					break;
				case 3:
					setText(R.id.total, "明日签到获得250娱币");
					break;
				case 4:
					setText(R.id.total, "明日签到获得300娱币");
					break;
				case 5:
					setText(R.id.total, "明日签到获得350娱币");
					break;
				case 6:
					setText(R.id.total, "明日签到获得400娱币");
					break;
				case 7:
					setText(R.id.total, "明日签到获得450娱币");
					break;
				case 8:
					setText(R.id.total, "明日签到获得500娱币");
					break;
				case 9:
					setText(R.id.total, "明日签到获得550娱币");
					break;
				case 10:
					setText(R.id.total, "明日签到获得600娱币");
					break;
				default:
					setText(R.id.total, "明日签到获得600娱币");
					break;
				}
			} else {
				ToastUtil.show(this, "获取数据失败");
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 签到
	 */
	private void sendReqStarInformation() {
		if (MainActivity.user == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", MainActivity.user.getClientID());
		entity.put("The_date_of", TimestampTool.getCurrentDate());

		SimpleHttpTask httpTask = HttpUtil.packagingHttpTask(entity,
				InfoSource.sign_in);
		showProgressDialog("签到中...");
		WorkerManager.addTask(httpTask, this);
	}

	/**
	 * 获取签到天数
	 */
	private void sendReq() {
		if (MainActivity.user == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", MainActivity.user.getClientID());

		SimpleHttpTask httpTask = HttpUtil.packagingHttpTask(entity,
				InfoSource.continuous);
		showProgressDialog("获取信息...");
		WorkerManager.addTask(httpTask, this);
	}

	class Continuous {
		String continuous;

		List<SignTime> signs;

		public List<SignTime> getSigns() {
			return signs;
		}

		public void setSigns(List<SignTime> signs) {
			this.signs = signs;
		}

		public String getContinuous() {
			return continuous;
		}

		public void setContinuous(String continuous) {
			this.continuous = continuous;
		}

	}

	

	class Reward {
		public String Reward_type;
	}
}

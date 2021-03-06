package com.BC.entertainmentgravitation.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.BC.androidtool.BaseActivity;
import com.BC.androidtool.HttpThread.WorkerManager;
import com.BC.androidtool.JSON.BaseEntity;
import com.BC.androidtool.adapter.CommonAdapter;
import com.BC.androidtool.view.CircularImage;
import com.BC.androidtool.views.pull.PullToRefreshBase;
import com.BC.androidtool.views.pull.PullToRefreshBase.OnRefreshListener2;
import com.BC.androidtool.views.pull.PullToRefreshListView;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.HttpThread.InfoSource;
import com.BC.entertainmentgravitation.HttpThread.SimpleHttpTask;
import com.BC.entertainmentgravitation.json.Contribution;
import com.BC.entertainmentgravitation.utl.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.utl.HttpUtil;
import com.BC.entertainmentgravitation.utl.ToastUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LevelAcitivity extends BaseActivity implements OnItemClickListener {
	PullToRefreshListView pullToRefreshListView1;

	List<Contribution> ranking;

	private CommonAdapter<Contribution> adapter;
	// private RadioGroupLayout radio;
	private int pageIndex = 1;
	private CircularImage imageView2, imageView3, imageView4;
	ApplauseGiveConcern applauseGC;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pai_hang_bang);
		init();
		sendReqConnect();
	}

	private void init() {
		// TODO Auto-generated method stub
		imageView2 = (CircularImage) findViewById(R.id.imageView2);
		imageView3 = (CircularImage) findViewById(R.id.imageView3);
		imageView4 = (CircularImage) findViewById(R.id.imageView4);

		if (MainActivity.starInformation.getHead_portrait() != null) {
			Glide.with(LevelAcitivity.this)
					.load(MainActivity.starInformation.getHead_portrait())
					.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.home_image).into(imageView3);
		}
		if (MainActivity.starInformation.getStage_name() != null) {
			setText(R.id.name3, MainActivity.starInformation.getStage_name());
		}

		pullToRefreshListView1 = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView1);
		pullToRefreshListView1.setOnRefreshListener(refreshListener);
		adapter = new CommonAdapter<Contribution>(this,
				R.layout.item_list_ranking_2, new ArrayList<Contribution>()) {

			@Override
			public void convert(ViewHolder helper, final Contribution item) {
				// helper.setText(R.id.The_picture, item.getThe_picture() + "");
				helper.setText(R.id.Star_names, item.getName());
				helper.setText(R.id.contribution, item.getContribution() + "");
				helper.setText(R.id.highest, item.getHighest() + "");
				helper.setText(R.id.acquire, item.getAcquire() + "");

				ImageView imageView = helper.getView(R.id.Head_portrait);
				Glide.with(LevelAcitivity.this).load(item.getHand())
						.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_image)
						.into(imageView);
				ImageView imageView2 = helper.getView(R.id.rankingImage);
				TextView textView = helper.getView(R.id.rankingText);
				// imageView.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				// if (item.getPermission().equals("2")) {
				// Intent intent = new Intent(v.getContext(),
				// DetailsActivity.class);
				// intent.putExtra("userID", item.getUserID());
				// startActivity(intent);
				// } else {
				// Intent intent = new Intent(v.getContext(),
				// Details4UserActivity.class);
				// intent.putExtra("userID", item.getUserID());
				// startActivity(intent);
				// }
				// }
				// });
				switch (helper.getPosition()) {
				case 0:
					imageView2.setVisibility(View.VISIBLE);
					textView.setVisibility(View.GONE);
					imageView2.setImageResource(R.drawable.icon_1);
					break;
				case 1:
					imageView2.setVisibility(View.VISIBLE);
					textView.setVisibility(View.GONE);
					imageView2.setImageResource(R.drawable.icon_2);
					break;
				case 2:
					imageView2.setVisibility(View.VISIBLE);
					textView.setVisibility(View.GONE);
					imageView2.setImageResource(R.drawable.icon_3);
					break;

				default:
					imageView2.setVisibility(View.GONE);
					textView.setVisibility(View.VISIBLE);
					textView.setText((helper.getPosition() + 1) + "");
					break;
				}

			}
		};
		pullToRefreshListView1.setAdapter(adapter);
		pullToRefreshListView1.setOnItemClickListener(this);
	}

	public void initPersonalInformation() {
		// TODO Auto-generated method stub
		if (ranking == null) {
			// ToastUtil.show(activity, "获取数据失败");
			return;
		}
		// if (arrayList != null && arrayList.size() > 0) {
		if (pageIndex == 1) {// 第一页时，先清空数据集
			adapter.clearAll();
			if (ranking.size() > 0) {
				String hand = ranking.get(0).getHand();
				String name = ranking.get(0).getName();
				Glide.with(LevelAcitivity.this).load(hand).centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_image)
						.into(imageView2);
				setText(R.id.name2, name);

			}
			if (ranking.size() > 1) {
				String hand = ranking.get(1).getHand();
				String name = ranking.get(1).getName();
				Glide.with(LevelAcitivity.this).load(hand).centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_image)
						.into(imageView4);
				setText(R.id.name4, name);
			}
		}
		pageIndex++;
		adapter.add(ranking);

		// findViewById(R.id.noContent).setVisibility(View.GONE);
		// } else if (pageIndex == 1) {
		// findViewById(R.id.noContent).setVisibility(View.VISIBLE);
		// }
	}

	OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(LevelAcitivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView1.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pullToRefreshListView1.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pullToRefreshListView1.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);
			pageIndex = 1;
			// 调用数据
			// sendReq(pageIndex);
			sendReqConnect();

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(LevelAcitivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView1.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pullToRefreshListView1.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pullToRefreshListView1.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendReqConnect();
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// ToastUtil.show(LevelAcitivity.this, "准备删除");
		Contribution item = (Contribution) parent.getAdapter()
				.getItem(position);
		if (item.getPermission().equals("2")) {
			Intent intent = new Intent(this, DetailsActivity.class);
			intent.putExtra("userID", item.getUserID());
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, Details4UserActivity.class);
			intent.putExtra("userID", item.getUserID());
			startActivity(intent);
		}
	}

	/**
	 * 获取信息
	 */
	private void sendReqConnect() {
		if (MainActivity.user == null) {
			ToastUtil.show(LevelAcitivity.this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("star_id", MainActivity.starInformation.getStar_ID());
		entity.put("pageIndex", "" + pageIndex);

		SimpleHttpTask httpTask = HttpUtil.packagingHttpTask(entity,
				InfoSource.contribution);
		showProgressDialog("获取信息...");
		WorkerManager.addTask(httpTask, this);
	}

	@Override
	public void onInfoReceived(int errcode, HashMap<String, Object> items) {
		// TODO Auto-generated method stub
		super.onInfoReceived(errcode, items);
		pullToRefreshListView1.onRefreshComplete();
	}

	@Override
	public void requestSuccessful(String jsonString, int taskType) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		switch (taskType) {
		case InfoSource.contribution:
			BaseEntity<List<Contribution>> baseEntity4 = gson.fromJson(
					jsonString,
					new TypeToken<BaseEntity<List<Contribution>>>() {
					}.getType());
			ranking = baseEntity4.getData();
			if (ranking != null && ranking.size() > 0) {
				initPersonalInformation();
			} else {
				ToastUtil.show(LevelAcitivity.this, "没有更多数据了");
			}
			break;
		case InfoSource.give_applause_booed:
			ToastUtil.show(this, "提交成功");
			if (applauseGC == null) {
				return;
			}
			switch (applauseGC.getType()) {
			case 1:
				applauseGC.showAnimationDialog(R.drawable.circle4,
						R.raw.applaud);
				break;
			case 2:
				applauseGC.showAnimationDialog(R.drawable.circle5,
						R.raw.give_back);
				break;
			default:
				applauseGC.showAnimationDialog(R.drawable.circle4,
						R.raw.applaud);
				break;
			}

			break;
		case InfoSource.and_attention:
			ToastUtil.show(this, "提交成功");
			if (applauseGC == null) {
				return;
			}
			applauseGC.showAnimationDialog(R.drawable.circle6, R.raw.concern);
			break;
		}
	}
}

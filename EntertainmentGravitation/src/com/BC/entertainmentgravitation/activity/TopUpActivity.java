package com.BC.entertainmentgravitation.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.BC.androidtool.BaseActivity;
import com.BC.androidtool.HttpThread.WorkerManager;
import com.BC.androidtool.JSON.JsonRequest;
import com.BC.androidtool.utils.Audio;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.HttpThread.InfoSource;
import com.BC.entertainmentgravitation.HttpThread.SimpleHttpTask;
import com.BC.entertainmentgravitation.util.PayResult;
import com.BC.entertainmentgravitation.util.SignUtils;
import com.BC.entertainmentgravitation.utl.HttpUtil;
import com.BC.entertainmentgravitation.utl.ToastUtil;
import com.BC.entertainmentgravitation.view.EmotionsView;
import com.BC.entertainmentgravitation.view.dialog.ChoosePayDialog;
import com.BC.entertainmentgravitation.view.dialog.ChoosePayDialog.ChoosePayCallback;
import com.BC.entertainmentgravitation.view.dialog.PayDialog;
import com.BC.entertainmentgravitation.wxapi.entity.PayWays;
import com.BC.entertainmentgravitation.wxapi.entity.WxCheckOrder;
import com.BC.entertainmentgravitation.wxapi.entity.WxOrder;
import com.BC.entertainmentgravitation.wxapi.entity.WxPrePayOrder;
import com.BC.entertainmentgravitation.wxapi.util.Constants;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 充值
 * 
 * @author shuzhi
 * 
 */
public class TopUpActivity extends BaseActivity implements OnClickListener {
	// 商户PID
	public static final String PARTNER = "2088711408262791";
	// 商户收款账号
	public static final String SELLER = "13911533774@163.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM083JmW+LUJYG90UVsEBaZH1TvIFZmE8odQBMltupc4kXAZQppBh4DMhoQ8BpJRibZMnR2lqB+OwEP6ZfdKGGRW41XbEF1mXCKFwNZXyDoArlOFb8P/OPw22WbQAbtlZJndM15PGMONReje149FMoOD7SCdfTslTSyvVisI8atnAgMBAAECgYEAxvV1NT9xoq6QWft80qq3f1ark+SHa+fB5QLhYROKiwY/l1glhLx5y5Z54L7/7+AzjBIBTbhFnzIXmM6pAm1F7mMJS1nxELHqytikxpyynHvmUJj976ZL2rrG3RsDaQXF/mrM1+Y6Ajps1hwt+uOtnBDJRnPM9mBD0wGAH9OKBCECQQDoDYEgxePw8LgmmseNhET00VHdE8CL6pcZ46ttivYYpR9mckRy2C9VlQiOYDGbk10H1chXih1erruOEDCd/bWNAkEA4mrxnS4tTk25LJak36pApSYlt1KdOuEtG+wDbIT4a78ffdEgnhP2gKTmBBVXPbHImspSFGO9OZQpYk/7Zv8lwwJAWXC7EJK1pKxjjh2iRJ1yppn3X6q5UDR/QO9Lp9EjwaQDUk1ArLM+q1HiFl5lQH2wIdD4gyUs5M2cZMlAs+SSEQJBAJeeIbmtqG0dIvk2z6VvLuboiq0eR2ecTka6XviWenw8eewY1IzGtXUj91uYptkLalgtT5WTzKz4CFZrVOB9z10CQFD3aoS4/4ya4H6/TvxL254wz+qtL6pxUc7mI5HWpd17lZFfB2aFwQ8UaNjDZfr5v5fzM6oFWF11JcoyBlLgGhM=";
	// 支付宝公钥
	// public static final String RSA_PUBLIC =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	/**
	 * 支付人民币金额
	 */
	String price = "";
	/**
	 * 娱币数量
	 */
	String amount = "";
	/**
	 * 订单ID
	 */
	String orderID = "";
	
	private ChoosePayDialog choosePayDialog;
	
	private IWXAPI api;
	
	//微信支付，预支付
	private WxPrePayOrder wxPrePayOrder;
	
	private WxCheckOrder wxCheckOrder = new WxCheckOrder();
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(TopUpActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					// sendReqUp(amount);
					showEmotionsView();
					sendReqUpOK();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(TopUpActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(TopUpActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(TopUpActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			case Constants.WX_EXCEPTION_ERROR:
				Toast.makeText(TopUpActivity.this, "微信支付失败，支付异常",
						Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.PAY_WX:
//				startWxPay();
				createPrePay();
				break;
			case Constants.PAY_ALI:
				pay();
				break;
			default:
				break;
			}
		};
	};
	
	BroadcastReceiver mMessageReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
		    int errorCode = intent.getIntExtra("errorCode", 1);
		    Log.d("test", "Got message: " + errorCode);
		    switch(errorCode)
		    {
		    case -2:
		    	//用户取消微信支付
		    	Log.i("test", "cancel");
				Toast.makeText(TopUpActivity.this, "支付已取消，支付失败",
						Toast.LENGTH_LONG).show();
		    	break;
		    case -1:
		    	Log.i("test", "error");
				Toast.makeText(TopUpActivity.this, "微信支付失败，支付异常",
						Toast.LENGTH_LONG).show();
		    	//错误，异常
		    	break;
		    case 0:
		    	//支付成功
		    	Log.i("test", "success");
				checkOrder();

		    	break;
		    case 1:
		    	//默认值
		    	Log.i("test", "default");
		    	break;
		    }
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_up);

		findViewById(R.id.up6).setOnClickListener(this);
		findViewById(R.id.up18).setOnClickListener(this);
		findViewById(R.id.up30).setOnClickListener(this);
		findViewById(R.id.up45).setOnClickListener(this);
		findViewById(R.id.up98).setOnClickListener(this);
		findViewById(R.id.up298).setOnClickListener(this);
		// sendReqUpOK();
		api = WXAPIFactory.createWXAPI(this, null);
		api.registerApp(Constants.APP_ID);
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter("wxPayEvent"));
	}
	
	private void createChoosePayDialog(Context context, Handler handler)
	{
		choosePayDialog = new ChoosePayDialog(context, handler);
		choosePayDialog.SetChoosePayCallback(choosePayCallback);
		choosePayDialog.Show();
	}
	
	//支付选择对话框回调函数
	ChoosePayCallback choosePayCallback = new ChoosePayCallback()
	{

		@Override
		public void pay(Handler handler, PayWays payWays) {
			switch(payWays.getPayId())
			{
			case Constants.PAY_WX:
				//微信支付
				Message wxMsg = new Message();
				wxMsg.what = Constants.PAY_WX;
				mHandler.sendMessage(wxMsg);
				break;
			case Constants.PAY_ALI:
				//支付宝支付
				Message aLiMsg = new Message();
				aLiMsg.what = Constants.PAY_ALI;
				mHandler.sendMessage(aLiMsg);
				break;
			}
		}
		
	};

	/**
	 * 检查订单
	 */
	private void checkOrder()
	{
//		HashMap<String, String> entity = new HashMap<String, String>();
//
//		entity.put("preorder", wxCheckOrder.getPreOrder());
		
		String content = JsonRequest.getJSONObject(wxCheckOrder);
		Log.i("test", "---wxCheckOrder---" + wxCheckOrder.getRechargesn()
				+ "-----ClientID----" + wxCheckOrder.getClientID());
		SimpleHttpTask httpTask = new SimpleHttpTask(InfoSource.wx_pay, content, Constants.URLQUERY);
		WorkerManager.addTask(httpTask, this);
	}
	
	/**
	 * 创建预支付订单
	 */
	private void createPrePay()
	{
		wxPrePayOrder = new WxPrePayOrder();
		wxPrePayOrder.setClientID(MainActivity.user.getClientID());
		wxPrePayOrder.setProductname(Constants.BODY);
		wxPrePayOrder.setPrice(Integer.parseInt(price) * 100);
		wxPrePayOrder.setAmount(Integer.parseInt(amount));
		
		String content = JsonRequest.getJSONObject(wxPrePayOrder);
		SimpleHttpTask httpTask = new SimpleHttpTask(InfoSource.wx_pre_pay, content, Constants.URLSTRING);
		httpTask.setRequestType(true);
		WorkerManager.addTask(httpTask, this);
	}
	
	/**
	 * 充值
	 */
	private void sendReqUp() {
		if (MainActivity.user == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", MainActivity.user.getClientID());
		entity.put("amount", "" + amount);
		entity.put("price", price);
		entity.put("order_sn", orderID);

		SimpleHttpTask httpTask = HttpUtil.packagingHttpTask(entity,
				InfoSource.top_up);
		WorkerManager.addTask(httpTask, this);
	}

	/**
	 * 充值成功
	 */
	private void sendReqUpOK() {
		if (MainActivity.user == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("order_sn", orderID);
		// entity.put("order_sn", "1129145833-1074");

		SimpleHttpTask httpTask = HttpUtil.packagingHttpTask(entity,
				InfoSource.recharge_success);
		WorkerManager.addTask(httpTask, this);
	}

	@Override
	public void requestSuccessful(String jsonString, int taskType) {
		Log.i("test", "taskTpye :" + taskType + "\n");
		Log.i("test", "jsonString :" + jsonString + "\n");
		switch(taskType)
		{
		case InfoSource.wx_pre_pay:
			//接受到微信预支付订单信息
			Toast.makeText(TopUpActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
//			startOrder(jsonString);
			startPayThread(jsonString);
			break;
		case InfoSource.wx_pay:
			//接受到微信支付完成订单信息
			showEmotionsView();
			break;
		}
		
	}
	
    public WxOrder GetWxOrder(String jsonString)
    {
    	WxOrder wxOrder = new WxOrder();
    	JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonString).getJSONObject("data");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Message message = new Message();
        	message.what = Constants.WX_EXCEPTION_ERROR;
        	mHandler.sendMessage(message);
			e.printStackTrace();
		} 
    	if(jsonObject != null)
    	{
			try {
				wxOrder.setPrepayid(jsonObject.getString("prepayid"));
				wxOrder.setNoncestr(jsonObject.getString("noncestr"));
				wxOrder.setTimeStamp(jsonObject.getString("timestamp"));
				wxOrder.setSign(jsonObject.getString("sign"));
				wxCheckOrder.setClientID(MainActivity.user.getClientID());
				wxCheckOrder.setRechargesn(jsonObject.getString("preorder"));
			} catch (JSONException e) {
	        	Message message = new Message();
	        	message.what = Constants.WX_EXCEPTION_ERROR;
	        	mHandler.sendMessage(message);
				e.printStackTrace();
			}
    	}
    	return wxOrder;
    }
    
    private void startPayThread(final String jsonString)
    {
    	
    	Runnable payRunnable = new Runnable(){

			@Override
			public void run() {
				startOrder(jsonString);
			}
    	};
		Thread payThread = new Thread(payRunnable);
		payThread.start();
    }
    
    private void startOrder(String jsonString)
    {
    	WxOrder wxOrder = GetWxOrder(jsonString);
		PayReq req = new PayReq();
		req.appId			= wxOrder.getAppid();
		req.partnerId		= wxOrder.getPartnerid();
		req.prepayId		= wxOrder.getPrepayid();
		req.nonceStr		= wxOrder.getNoncestr();
		req.timeStamp		= wxOrder.getTimestamp();
		req.packageValue	= wxOrder.getWxPackage();
		req.sign			= wxOrder.getSign();
//		Log.i("test", "---appid---" + wxOrder.getAppid() + "\n");
//		Log.i("test", "---partnerId---" + wxOrder.getPartnerid() + "\n");
//		Log.i("test", "---prepayId---" + wxOrder.getPrepayid() + "\n");
//		Log.i("test", "---nonceStr---" + wxOrder.getNoncestr() + "\n");
//		Log.i("test", "---timeStamp---" + wxOrder.getTimestamp() + "\n");
//		Log.i("test", "---packageValue---" + wxOrder.getWxPackage() + "\n");
//		Log.i("test", "---sign---" + wxOrder.getSign() + "\n");
		api.sendReq(req);
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.up6:
			// sendReqUp(18);
			price = "6";
			amount = "600";
			createChoosePayDialog(TopUpActivity.this, mHandler);
//			pay();
//			test();
//			startWxPay();
			break;
		case R.id.up18:
			// sendReqUp(18);
			price = "18";
			amount = "2200";
			createChoosePayDialog(TopUpActivity.this, mHandler);
//			pay();
			break;
		case R.id.up30:
			// sendReqUp(30);
			price = "30";
			amount = "3600";
			createChoosePayDialog(TopUpActivity.this, mHandler);
//			pay();
			break;
		case R.id.up45:
			// sendReqUp(45);
			price = "45";
			amount = "5400";
			createChoosePayDialog(TopUpActivity.this, mHandler);
//			pay();
			break;
		case R.id.up98:
			// sendReqUp(98);
			price = "98";
			amount = "12000";
			createChoosePayDialog(TopUpActivity.this, mHandler);
//			pay();
			break;
		case R.id.up298:
			// sendReqUp(298);
			// pay(v);
			showAlertDialog();
			break;
		}
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

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay() {
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
				|| TextUtils.isEmpty(SELLER)) {
			new AlertDialog.Builder(this)
					.setTitle("警告")
					.setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									//
									finish();
								}
							}).show();
			return;
		}
		// 订单
		String orderInfo = getOrderInfo("购买" + amount + "个娱币",
				MainActivity.user.getNickName() + "购买价值" + price + "的娱币",
				price);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				sendReqUp();
				PayTask alipay = new PayTask(TopUpActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(TopUpActivity.this);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 */
	public String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号

		orderID = getOutTradeNo();
		orderInfo += "&out_trade_no=" + "\"" + orderID + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\""
				+ "http://120.25.107.200/php/alipay/notify_url.php" + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	public void showAlertDialog() {
		// TODO Auto-generated method stub
		final PayDialog.Builder builder = new PayDialog.Builder(this);
		builder.setTitle("请输入充值人民币金额");
		builder.setMessage("请输入金额");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				EditText message = (EditText) builder
						.findViewById(R.id.message);
				if (message != null && !message.getText().toString().equals("")) {
					createChoosePayDialog(TopUpActivity.this, mHandler);
//					pay();
				} else {
					ToastUtil.show(TopUpActivity.this, "抱歉没有您充值的金额");
				}
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		PayDialog animationDialog = builder.create();
		EditText editText = (EditText) builder.findViewById(R.id.message);
		final TextView textView1 = (TextView) builder
				.findViewById(R.id.textView1);
		final TextView textView2 = (TextView) builder
				.findViewById(R.id.textView2);
		final TextView textView3 = (TextView) builder
				.findViewById(R.id.TextView03);
		final TextView textView4 = (TextView) builder
				.findViewById(R.id.TextView04);
		editText.setText("20");
		textView1.setText("兑换娱币数量：");
		textView2.setText("" + 2000);
		textView4.setText("赠送的娱币数量：");
		textView3.setText(2000 * 0.02 + "");
		price = "20";
		amount = "2040";
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String ts = s.toString();
				if (!ts.equals("")) {
					int v = Integer.valueOf(ts);
					price = ts;
					textView2.setText("" + v * 100);
					textView4.setText("赠送的娱币数量：");
					if (v >= 20) {
						amount = "" + (v * 2 + v * 100);
						textView3.setText(v * 2 + "");
					} else {
						amount = "" + (v * 100);
						textView3.setText("0");
					}
				}
			}
		});
		animationDialog.show();
	}

}

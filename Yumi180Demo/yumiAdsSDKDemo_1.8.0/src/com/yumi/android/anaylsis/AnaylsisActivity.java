package com.yumi.android.anaylsis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AnaylsisActivity extends ExpandableListActivity {

	public static final String ACTION_REPORT = "yumi_action_report";
	public static final String ACTION_REPORT_CLICK= "click";
	public static final String ACTION_REPORT_REQUEST = "request";
	public static final String ACTION_REPORT_RESPONSE = "response";
	public static final String ACTION_REPORT_OPPORT = "opport";
	public static final String ACTION_REPORT_EXPOSURE = "exposure";
	public static final String ACTION_REPORT_REWARD = "reward";
	public static final String ACTION_REPORT_ROUND = "round";
    public static final String ACTION_REPORT_START = "start";
    public static final String ACTION_REPORT_END = "end";
	private AlertDialog dialog;
	private AnaylsisDBHelper helper;
	private ProgressDialog progress;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helper = AnaylsisDBHelper.getHelper(this);
		createDialog();
		AsyncTask<Object, Object, Map<String, ArrayList<String>>> asyncTask = new AsyncTask<Object, Object, Map<String, ArrayList<String>>>() {

			@Override
			protected Map<String, ArrayList<String>> doInBackground(Object... params) {
				Map<String, ArrayList<String>> maps = new HashMap<String, ArrayList<String>>();
				maps.put("banner", getInfoList("2"));
				maps.put("interstitial", getInfoList("3"));
				maps.put("media", getInfoList("5"));
				return maps;
			}
			
			@Override
			protected void onPostExecute(Map<String, ArrayList<String>> result) {
				if (result != null) {
					setListAdapter(new AnaylsisAdapter(result));
					if (progress != null) {
						progress.dismiss();
					}
				}else {
					Toast.makeText(AnaylsisActivity.this, "no data", Toast.LENGTH_SHORT).show();
				}
				super.onPostExecute(result);
			}
		};
		asyncTask.execute();
	}

	private void createDialog() {
		progress = new ProgressDialog(this);
		progress.setMessage("loading...");
		progress.show();
		dialog = new AlertDialog.Builder(this).setPositiveButton("清数据", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (helper != null) {
					helper.clearDB();
					AnaylsisActivity.this.finish();
				}
			}
		}).setNegativeButton("退出", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AnaylsisActivity.this.finish();
			}
		}).create();
	}

	private ArrayList<String> getInfoList(String format) {
		ArrayList<String> providers = helper.getProviders(format);
		ArrayList<String> banners = null;
		if (providers != null && providers.size() > 0) {
			banners = new ArrayList<String>();
			for (String provider : providers) {
				int request = helper.getCountByAction(provider, format, ACTION_REPORT_REQUEST);
				int success = helper.getCountByResponse(provider, format, true);
				int response = helper.getCountByResponse(provider, format, false);
				int click = helper.getCountByAction(provider, format, ACTION_REPORT_CLICK);
				int oppt = helper.getCountByAction(provider, format, ACTION_REPORT_OPPORT);
				int exposure = helper.getCountByAction(provider, format, ACTION_REPORT_EXPOSURE);
				int round = helper.getCountByAction(null, format, ACTION_REPORT_ROUND);
				int mediaSrart = helper.getCountByAction(provider, format, ACTION_REPORT_START);
				int mediaEnd = helper.getCountByAction(provider, format, ACTION_REPORT_END);
				int mediaReward = helper.getCountByAction(provider, format, ACTION_REPORT_REWARD);
				String item = new StringBuilder().append(provider+":"+provider).append("\n")
//						String item = new StringBuilder().append(provider+":"+ProviderID.getProviderNameByID(provider)).append("\n")
					.append("\t").append("request       = ").append(request+ "\n")
					.append("\t").append("success     = ").append(success+ "\n")
					.append("\t").append("failed          = ").append(response+ "\n")
					.append("\t").append("oppt            = ").append(oppt +  "\n")
					.append("\t").append("exposure    = ").append(exposure+ "\n")
					.append("\t").append("click             = ").append(click + "\n")
					.append("\t").append("round           = ").append(round+ "\n")
                    .append("\t").append("start           = ").append(mediaSrart+ "\n")
                    .append("\t").append("end           = ").append(mediaEnd+ "\n")
                    .append("\t").append("reward           = ").append(mediaReward).toString();
				banners.add(item);
			}
		}
		return banners;
	}
	
	@Override
	protected void onDestroy() {
		helper.closeDB();
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		super.onDestroy();
	}
	
	
	@Override
	public void onBackPressed() {
		if (dialog != null) {
			dialog.show();
		}
		return;
	}
	
	
	
	private class AnaylsisAdapter extends BaseExpandableListAdapter{

		private final String[] group = new String[]{"banner", "interstitial", "media"};
		private Map<String, ArrayList<String>> childs ;
		
		private AnaylsisAdapter(Map<String, ArrayList<String>> maps){
			this.childs = maps;
		}
		
		@Override
		public int getGroupCount() {
			return group.length;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (childs != null && childs.get(group[groupPosition]) != null) {
				ArrayList<String> arrayList = childs.get(group[groupPosition]);
				return arrayList.size();
			}
			return 0;
		}

		@Override
		public String getGroup(int groupPosition) {
			return group[groupPosition];
		}

		@Override
		public String getChild(int groupPosition, int childPosition) {
			if (childs != null && childs.size() > 0) {
				ArrayList<String> items = childs.get(group[groupPosition]);
				if (items != null && items.size() > 0) {
					return items.get(childPosition);
				}
			}
			return "-";
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(AnaylsisActivity.this);
				convertView.setPadding(100, 10, 100, 10);
			}
			((TextView)convertView).setText(getGroup(groupPosition));
			((TextView)convertView).setTextSize(22);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(AnaylsisActivity.this);
				convertView.setPadding(100, 10, 100, 10);
			}
			((TextView)convertView).setText(getChild(groupPosition, childPosition));
			((TextView)convertView).setTextSize(22);
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
		
	}

}

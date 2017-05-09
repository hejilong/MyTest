package com.yumi.android.sdk.ads.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public final class Frequency{

	private static final String TAG = "Frequency";
	private static final int STATUS_PRIORITY = 0x123;
	private static final int STATUS_ROLL = 0x124;
	private static final int STATUS_ENSURE = 0x125;
	private int currentStatus = 0;
	private YumiProviderBean priority;
	private YumiProviderBean ensure;
	private Set<Integer> rollPool;
	private ArrayList<YumiProviderBean> order;
	private YumiProviderBean currentProvider;
	private int index;
	private final boolean isAutoOptimization;
	private static final boolean onoff = true;
	private final Comparator<YumiProviderBean> comparator = new Comparator<YumiProviderBean>() {
		@Override
		public int compare(YumiProviderBean o1, YumiProviderBean o2) {
			if (o1.getPriority() > o2.getPriority()) {
				return 1;
			}
			if (o1.getPriority() == o2.getPriority()) {
				return 0;
			}
			return -1;
		}
	};

	/**
	 * 根据返回比例 和 优先级 .随即生成
	 * 
	 */
	public Frequency(List<YumiProviderBean> beans, boolean isAutoOptimization) {
		this.isAutoOptimization = isAutoOptimization;
		if (isAutoOptimization) {
			currentStatus = STATUS_ROLL;
		}
		YumiProviderBean[] array = new YumiProviderBean[beans.size()];
		beans.toArray(array);
		Arrays.sort(array, comparator);
		order = new ArrayList<YumiProviderBean>();
		for (YumiProviderBean providerBean : array) {
			if (providerBean.getPriority() == 0) {
				priority = providerBean;
			} else if (providerBean.getPriority() == -1) {
				ensure = providerBean;
			} else {
				order.add(providerBean);
			}
		}
		createRollPool();
	}

	private void createRollPool() {
		if (!isAutoOptimization) {
			int index = 1;
			if (rollPool == null) {
				rollPool = new HashSet<Integer>();
			} else {
				rollPool.clear();
			}
			for (int i = 0; i < order.size(); i++) {
				for (int j = 0; j < order.get(i).getRatio(); j++) {
					index++;
					int number = index * order.size() + i;
					rollPool.add(number);
				}
			}
		}
//		toNextRound();
	}

	private YumiProviderBean calculateRandomProvider() {
		if (NullCheckUtils.isNotEmptyCollection(rollPool)) {
			if (NullCheckUtils.isNotEmptyCollection(order)) {
				int nextRoll = rollPool.iterator().next();
				currentProvider = order.get(nextRoll % order.size());
				rollPool.remove(nextRoll);
				if (currentProvider != null) {
					return currentProvider;
				}
			}
		}else {
			createRollPool();
		}
		return getNextProvider();
	}

	public final YumiProviderBean getNextProvider() {
		// 判断状态
		String name = null;
		switch (currentStatus) {
		case 0: // 初始
			currentStatus = STATUS_PRIORITY;
			if (priority != null) {// 最优先不是空 直接返回
				currentProvider = priority;
			} else {
				currentProvider = getNextProvider();
			}
			if (currentProvider!=null)
			{
				name = currentProvider.getProviderName();
				ZplayDebug.v(TAG, "return by PRIORITY:"+name, onoff);
			}else{
				ZplayDebug.v(TAG, "return by PRIORITY", onoff);
			}
			break;
		case STATUS_PRIORITY: // 返回过最优先
			currentStatus = STATUS_ROLL;
			currentProvider = calculateRandomProvider();
			if (currentProvider!=null)
			{
				name = currentProvider.getProviderName();
				ZplayDebug.v(TAG, "return by RANDOM:"+name, onoff);
			}else{
				ZplayDebug.v(TAG, "return by RANDOM", onoff);
			}
			break;
		case STATUS_ROLL:// 返回过roll
			YumiProviderBean provider = getProviderByOrder();
			if (provider!=null)
			{
				name = provider.getProviderName();
				ZplayDebug.v(TAG, "return by ORDER:"+name, onoff);
			}else{
				ZplayDebug.v(TAG, "return by ORDER", onoff);
			}
			return provider;
		case STATUS_ENSURE: // 返回过 ensure
			ZplayDebug.v(TAG, "return by ensure", onoff);
			return null;
		}
		return currentProvider;
	}

	private YumiProviderBean getProviderByOrder() {
		if (NullCheckUtils.isNotEmptyCollection(order)) {
			if (index >= order.size()) {
				return getEnsureProvider();
			}
			YumiProviderBean provider = order.get(index);
			index++;
			if (!provider.equals(currentProvider)) {
				return provider;
			} else {
				return getProviderByOrder();
			}
		}
		return getEnsureProvider();
	}

	private YumiProviderBean getEnsureProvider() {
		currentStatus = STATUS_ENSURE;
		return ensure;
	}

	public final void toNextRound() {
		index = 0;
		if (isAutoOptimization) {
			currentStatus = STATUS_ROLL;
		} else {
			currentStatus = 0;
		}
	}

	public final void cutDownProvider(YumiProviderBean provider) {
		if (provider.equals(priority)) {
			priority = null;
		} else if (provider.equals(ensure)) {
			ensure = null;
		}else{
			if (NullCheckUtils.isNotEmptyCollection(order)) {
				order.remove(provider);
			}
		}
		// 创建新的 rollpool
		createRollPool();
	}

	
	public final boolean isCutDownAll() {
		return priority == null && !NullCheckUtils.isNotEmptyCollection(order) && ensure == null;
	}
}

package com.yumi.android.sdk.ads.animation;

import java.util.HashMap;
import java.util.Map;

import android.view.animation.TranslateAnimation;

public final class AnimationHolder {

	public static final String KEY_INANIM = "inanim";
	public static final String KEY_OUTANIM = "outanim";
	
	private static AnimationHolder anim;
	
	private AnimationHolder() {
	}

	public final static AnimationHolder getAnimManager(){
		if (anim == null) {
			anim = new AnimationHolder();
		}
		return anim;
	}
	
	public final Map<String, TranslateAnimation> buildHorizontalAnimation(int widthPix, long duration){
			HashMap<String, TranslateAnimation> r_in_l_out = new HashMap<String, TranslateAnimation>();
			TranslateAnimation inAnim = new TranslateAnimation(widthPix, 0, 0, 0);
			inAnim.setDuration(duration);
			r_in_l_out.put(KEY_INANIM, inAnim);
			TranslateAnimation outAnim = new TranslateAnimation(0, (-1) * widthPix, 0, 0);
			outAnim.setDuration(duration);
			r_in_l_out.put(KEY_OUTANIM, outAnim);
		return r_in_l_out;
	}
 	
	
	
}

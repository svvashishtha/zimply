package com.application.zimply.utils;

import java.util.Random;

import com.application.zimply.R;
import com.application.zimply.extras.AppConstants;

import android.content.Context;

public class UiUtils {

	public static int getColorValue(int position) {
		int color = 0;
		System.out.println("Color Position == " + position);
		switch (position % 6) {
		case 0:
			color = R.color.color_pallete_1;
			break;
		case 1:
			color = R.color.color_pallete_2;
			break;
		case 2:
			color = R.color.color_pallete_4;
			break;
		case 3:
			color = R.color.color_pallete_5;
			break;
		case 4:
			color = R.color.color_pallete_6;
			break;
		case 5:
			color = R.color.color_pallete_7;
			break;

		}
		System.out.println("Color Value == " + color);
		return color;
	}

	public static final String getTextFromRes(Context context) {

		Random r = new Random();
		int i = r.nextInt(24 - 0 + 1) + 0;
		return AppConstants.quotes[i];
	}

	public static String getId(String proSlug) {
		String finalUrl = null;
		int slashCount = 0;

		for (int i = (proSlug.length() - 1); i >= 0; i--) {

			if (proSlug.charAt(i) == '/') {
				slashCount++;
			}

			if (slashCount == 2) {
				finalUrl = proSlug.substring(i + 1, proSlug.length() - 1);
				break;
			}

		}

		return finalUrl;
	}
}

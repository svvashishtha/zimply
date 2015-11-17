/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.application.zimplyshop.firstRunOverlay;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import com.application.zimply.R;

/**
 * Created by curraa01 on 13/10/2013.
 */
class NewShowcaseDrawer extends StandardShowcaseDrawer {

	private static final int ALPHA_60_PERCENT = 153;
	private final float outerRadius;
	private final float innerRadius;
	private final float innerRadiusrectangle;
	private final int type;

	private Resources resources;
	private int customMeasure;
	private int extraMargin;
	private int leftMargin;
	private int rightMargin; 


	public NewShowcaseDrawer(Resources resources,int type, int customMeasurecounter ) {
		super(resources);
		this.type = type;
		customMeasure=customMeasurecounter;
		if(customMeasurecounter == ShowcaseView.CUSTOMMEASURE_CIRCLE_SMALL) {
			innerRadiusrectangle = resources.getDimension(R.dimen.showcase_radius_inner_small);
			innerRadius = resources.getDimension(R.dimen.showcase_radius_showcase_small);
			outerRadius = resources.getDimension(R.dimen.showcase_radius_outer_small);
			customMeasure = customMeasurecounter;
		} else if(customMeasurecounter == ShowcaseView.CUSTOMMEASURE_CIRCLE_LARGE) {
			innerRadiusrectangle = resources.getDimension(R.dimen.showcase_radius_inner_large);
			innerRadius = resources.getDimension(R.dimen.showcase_radius_showcase_large);
			outerRadius = resources.getDimension(R.dimen.showcase_radius_outer_large);
			customMeasure = customMeasurecounter;
		} else if (customMeasurecounter == ShowcaseView.CUSTOMMEASURE_CIRCLE_SMALL) {
			innerRadiusrectangle = resources.getDimension(R.dimen.showcase_radius_inner);
			innerRadius = resources.getDimension(R.dimen.showcase_radius_showcase);
			outerRadius = resources.getDimension(R.dimen.showcase_radius_outer);
		} else if(customMeasurecounter == ShowcaseView.CUSTOMMEASURE_CIRCLE_X_LARGE) {
			innerRadiusrectangle = resources.getDimension(R.dimen.showcase_radius_inner_large);
			innerRadius = resources.getDimension(R.dimen.showcase_radius_showcase_large);
			outerRadius = resources.getDimension(R.dimen.showcase_radius_outer_large);
			customMeasure = customMeasurecounter;
		} else {
			innerRadiusrectangle = resources.getDimension(R.dimen.showcase_radius_inner);
			innerRadius = resources.getDimension(R.dimen.showcase_radius_showcase);
			outerRadius = resources.getDimension(R.dimen.showcase_radius_outer);
		}
		this.resources = resources;

	}

	@Override
	public void setShowcaseColour(int color) {
		eraserPaint.setColor(color);
	}

	@Override
	public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier, ViewTarget view) {

		/* isRectangle == false shows the circle will be drawn over the view*/

        try{

            if(type == ShowcaseView.TYPE_CIRCLE) {

                if(customMeasure==ShowcaseView.CUSTOMMEASURE_CIRCLE) {
                    Canvas bufferCanvas = new Canvas(buffer);
                    eraserPaint.setShader(new RadialGradient(x, y, x, Color.TRANSPARENT, Color.DKGRAY,Shader.TileMode.MIRROR));
                    bufferCanvas.drawCircle(x, y, (x/(3f)), eraserPaint);
                } else if(customMeasure==ShowcaseView.CUSTOMMEASURE_CIRCLE_LARGE) {
					Canvas bufferCanvas = new Canvas(buffer);
					eraserPaint.setShader(new RadialGradient(x, y, x, Color.TRANSPARENT, Color.DKGRAY,Shader.TileMode.MIRROR));
					bufferCanvas.drawCircle(x, y, (x/(1.5f)), eraserPaint);
				} else if(customMeasure==ShowcaseView.CUSTOMMEASURE_CIRCLE_SMALL) {
					Canvas bufferCanvas = new Canvas(buffer);
					eraserPaint.setShader(new RadialGradient(x, y, x, Color.TRANSPARENT, Color.DKGRAY,Shader.TileMode.MIRROR));
					bufferCanvas.drawCircle(x, y, (x/(5f)), eraserPaint);
				} else if(customMeasure==ShowcaseView.CUSTOMMEASURE_CIRCLE_X_LARGE) {
					Canvas bufferCanvas = new Canvas(buffer);
					eraserPaint.setShader(new RadialGradient(x, y, x, Color.TRANSPARENT, Color.DKGRAY,Shader.TileMode.MIRROR));
					bufferCanvas.drawCircle(x, y, (x/(5f)), eraserPaint);
				}

			} else if(type == ShowcaseView.TYPE_RECTANGLE) {

                if(customMeasure==ShowcaseView.CUSTOMMEASURE_RECTANGLE){
                    Canvas bufferCanvas = new Canvas(buffer);
                    eraserPaint.setAlpha(0);
                    rightMargin=bufferCanvas.getWidth()-(bufferCanvas.getWidth()/40);
                    leftMargin=bufferCanvas.getWidth()/40;
                    bufferCanvas.drawRect(leftMargin,
                            y-innerRadiusrectangle/5,
                            rightMargin,
                            y+innerRadiusrectangle/5,
                            eraserPaint);

                    eraserPaint.setAlpha(ALPHA_60_PERCENT);
                    bufferCanvas.drawRect(leftMargin-5, y-(innerRadiusrectangle/5)-5 , rightMargin+5, y+(innerRadiusrectangle/5)+5, eraserPaint);

                    // rectangle over action_draw view
                } else if(customMeasure==ShowcaseView.CUSTOMMEASURE_MAPRECTANGLE) {

                    Canvas bufferCanvas = new Canvas(buffer);
                    eraserPaint.setAlpha(0);
                    extraMargin=bufferCanvas.getWidth() / 10 + bufferCanvas.getWidth() / 30;
                    leftMargin=bufferCanvas.getWidth()/40;
                    bufferCanvas.drawRect(leftMargin,
                            y-innerRadiusrectangle/5,
                            leftMargin+extraMargin,
                            y+innerRadiusrectangle/5,
                            eraserPaint);

                    eraserPaint.setAlpha(ALPHA_60_PERCENT);
                    bufferCanvas.drawRect(leftMargin-5, y-(innerRadiusrectangle/5)-5 , leftMargin+extraMargin+5, y+(innerRadiusrectangle/5)+5, eraserPaint);
                } else if(customMeasure==ShowcaseView.CUSTOMMEASURE_RECTANGLE_SMALL) {
					Canvas bufferCanvas = new Canvas(buffer);
					eraserPaint.setAlpha(0);
					extraMargin=bufferCanvas.getWidth() / 10 + bufferCanvas.getWidth() / 30;
					leftMargin=bufferCanvas.getWidth()/40;
					bufferCanvas.drawRect(leftMargin,
							y-innerRadiusrectangle/5,
							leftMargin+extraMargin,
							y+innerRadiusrectangle/5,
							eraserPaint);

					eraserPaint.setAlpha(ALPHA_60_PERCENT);
					bufferCanvas.drawRect(leftMargin-5, y-(innerRadiusrectangle/5)-5 , leftMargin+extraMargin+5, y+(innerRadiusrectangle/5)+5, eraserPaint);
				}
            }

        } catch(Exception e){

        }
	}

	@Override
	public int getShowcaseWidth() {		
		return (int) (outerRadius * 2);
	}

	@Override
	public int getShowcaseHeight() {
		return (int) (outerRadius * 2);
	}

	@Override
	public float getBlockedRadius() {
		return innerRadius;
	}

	@Override
	public void setBackgroundColour(int backgroundColor) {
		this.backgroundColour = backgroundColor;
	}
}
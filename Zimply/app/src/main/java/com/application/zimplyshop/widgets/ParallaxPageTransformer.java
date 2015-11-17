package com.application.zimplyshop.widgets;

import android.support.v4.view.ViewPager;
import android.view.View;

public class ParallaxPageTransformer implements ViewPager.PageTransformer
{
    float parallaxCoefficient;
    float distanceCoefficient;
    int layer;

    public ParallaxPageTransformer (float parallaxCoefficient,
                                    float distanceCoefficient,
                                    int layer)
    {
        this.parallaxCoefficient = parallaxCoefficient;
        this.distanceCoefficient = distanceCoefficient;
        this.layer = layer;
    }

    public void transformPage (View page, float position)
    {

        float coefficient = page.getWidth() * parallaxCoefficient;


        View v = page.findViewById(layer);
        if (v != null)
        {
            v.setTranslationX((coefficient * position)*-1);
        }

        coefficient *= distanceCoefficient;
    }
}


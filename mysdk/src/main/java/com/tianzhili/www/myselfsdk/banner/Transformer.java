package com.tianzhili.www.myselfsdk.banner;


import android.support.v4.view.ViewPager.PageTransformer;

import com.tianzhili.www.myselfsdk.banner.transformer.AccordionTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.BackgroundToForegroundTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.CubeInTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.CubeOutTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.DefaultTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.DepthPageTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.FlipHorizontalTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.FlipVerticalTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.ForegroundToBackgroundTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.RotateDownTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.RotateUpTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.ScaleInOutTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.StackTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.TabletTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.ZoomInTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.ZoomOutSlideTransformer;
import com.tianzhili.www.myselfsdk.banner.transformer.ZoomOutTranformer;

public class Transformer {
    public static Class<? extends PageTransformer> Default = DefaultTransformer.class;
    public static Class<? extends PageTransformer> Accordion = AccordionTransformer.class;
    public static Class<? extends PageTransformer> BackgroundToForeground = BackgroundToForegroundTransformer.class;
    public static Class<? extends PageTransformer> ForegroundToBackground = ForegroundToBackgroundTransformer.class;
    public static Class<? extends PageTransformer> CubeIn = CubeInTransformer.class;
    public static Class<? extends PageTransformer> CubeOut = CubeOutTransformer.class;
    public static Class<? extends PageTransformer> DepthPage = DepthPageTransformer.class;
    public static Class<? extends PageTransformer> FlipHorizontal = FlipHorizontalTransformer.class;
    public static Class<? extends PageTransformer> FlipVertical = FlipVerticalTransformer.class;
    public static Class<? extends PageTransformer> RotateDown = RotateDownTransformer.class;
    public static Class<? extends PageTransformer> RotateUp = RotateUpTransformer.class;
    public static Class<? extends PageTransformer> ScaleInOut = ScaleInOutTransformer.class;
    public static Class<? extends PageTransformer> Stack = StackTransformer.class;
    public static Class<? extends PageTransformer> Tablet = TabletTransformer.class;
    public static Class<? extends PageTransformer> ZoomIn = ZoomInTransformer.class;
    public static Class<? extends PageTransformer> ZoomOut = ZoomOutTranformer.class;
    public static Class<? extends PageTransformer> ZoomOutSlide = ZoomOutSlideTransformer.class;
}

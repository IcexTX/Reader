package org.geometerplus.android.fbreader.bookmenu;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Describe：view适配器
 * Author：Icex
 * CreationTime：2017/11/17
 */

public class ViewPagerAdapter extends PagerAdapter {

    private List<View> viewList;
    private String[] strings;


    public ViewPagerAdapter(List<View> viewList, String[] strings) {
        super();
        this.viewList = viewList;
        this.strings = strings;
    }

    @Override
    public int getCount() {
        return viewList.isEmpty() ? 0 : viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        //根据传来的key，找到view,判断与传来的参数View arg0是不是同一个视图
        return (view == object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        container.removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
        container.addView(viewList.get(position));
        return viewList.get(position);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return strings[position];
    }
}

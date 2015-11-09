package ramola.com.ramola;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;


public class MainScreen extends ActionBarActivity implements Book.OnBookClickListener {
TabLayout tabLayout;
    ViewPager viewPager;
    AdapterFragment adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainactivity);
        tabLayout= (TabLayout) findViewById(R.id.tabs);
        viewPager= (ViewPager) findViewById(R.id.viewpager);
        adapter=new AdapterFragment(getSupportFragmentManager());
        adapter.addFragment(new Main(), "NEWS");
        adapter.addFragment(new Book(), "SEARCH");
        adapter.addFragment(new Bookmark(),"Bookmark");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
            if(adapter.getPageTitle(i)=="Bookmark")
                viewPager.setOffscreenPageLimit(2);
                else
                viewPager.setOffscreenPageLimit(1);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setOffscreenPageLimit(2);
    }

    @Override
    public void getId(String id) {
        Intent intent=new Intent("ramola.com.ramola.detail");
        intent.putExtra("ID",id);
        startActivity(intent);
    }
}

package com.jason.microstream.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jason.microstream.R;
import com.jason.microstream.ui.base.BasicActivity;
import com.jason.microstream.ui.contact.MsContactFragment;
import com.jason.microstream.ui.conversation.MsConversationListFragment;
import com.jason.microstream.ui.mine.MsMineFragment;

import java.util.ArrayList;

import butterknife.BindView;

public class MainActivity extends BasicActivity<MainPresenter> implements MainPresenter.View {

    @BindView(R.id.main_tab)
    TabLayout main_tab;
    @BindView(R.id.main_pager)
    ViewPager2 main_pager;


    public static final String KEY_LAUNCH = "launch_type";
    public static final int LAUNCH_TYPE_CHAT = 1;
    public static final int LAUNCH_TYPE_CHAT_SINGLE = 3;
    public static final int LAUNCH_TYPE_SCHEDULE = 4;
    public static final int LAUNCH_TYPE_MAIN = 5;
    public static final int LAUNCH_TYPE_MAIL = 12;
    public static final int LAUNCH_TYPE_NEWFRIEND = 13;
    public static final int LAUNCH_TYPE_GROUPJOIN = 14;
    public static final int OVERLAY_PERMISSION_REQ_CODE = 1234;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preInit();
        initView();
        initData();
    }

    private void preInit() {
//        ServiceManager.getInstance().init(AccountManager.getInstance().getUserId());
//        ServiceManager.getInstance().getConversationManager().sycSingleNotifivation().onErrorComplete().subscribe();

        supportPostponeEnterTransition();

    }

    @Override
    protected MainPresenter onCreatePresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_;
    }






    private void initView() {
        getToolBarArea().setVisibility(View.GONE);

        main_pager = findViewById(R.id.main_pager);
        main_tab = findViewById(R.id.main_tab);

    }

    MainPagerAdapter pagerAdapter;
    private void initData() {

        conversionFragment = new MsConversationListFragment();
        conversionFragment.setTitle(getString(R.string.ms_message));
        conversionFragment.setTabView(this,R.layout.tab_conversation);
        contactFragment = new MsContactFragment();
        contactFragment.setTitle(getString(R.string.ms_contact));
        contactFragment.setTabView(this,R.layout.tab_contact);
        mineFragment = new MsMineFragment();
        mineFragment.setTitle(getString(R.string.ms_mine));
        mineFragment.setTabView(this,R.layout.tab_mine);
        fragments.add(conversionFragment);
        fragments.add(contactFragment);
        fragments.add(mineFragment);

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(),getLifecycle());
        main_pager.setAdapter(pagerAdapter);
        main_pager.setOffscreenPageLimit(fragments.size());
//        main_tab.setupWithViewPager(main_pager);
        new TabLayoutMediator(main_tab, main_pager, (tab, position) -> {}).attach();
        main_tab.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < main_tab.getTabCount(); i++) {
            TabLayout.Tab tab = main_tab.getTabAt(i);
            View viewTab = fragments.get(i).getTabView();
            tab.setCustomView(viewTab);
        }
        main_tab.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                for (int i = 0; i < fragments.size(); i++) {
                    if (i == tab.getPosition()) {
                        fragments.get(i).setSelected(true);
                    } else {
                        fragments.get(i).setSelected(false);
                    }
                }

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        main_pager.setCurrentItem(0);
        fragments.get(0).setSelected(true);


//        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
//        main_pager.setAdapter(pagerAdapter);
//        main_tab.setupWithViewPager(main_pager);
//        main_tab.setTabMode(TabLayout.MODE_FIXED);
//        for (int i = 0; i < main_tab.getTabCount(); i++) {
//            TabLayout.Tab tab = main_tab.getTabAt(i);
//            View viewTab = fragments.get(i).getTabView();
//            tab.setCustomView(viewTab);
//        }
//        main_tab.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                for (int i = 0; i < fragments.size(); i++) {
//                    if (i == tab.getPosition()) {
//                        fragments.get(i).setSelected(true);
//                    } else {
//                        fragments.get(i).setSelected(false);
//                    }
//                }
//
//            }
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) { }
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) { }
//        });
//
//        main_pager.setCurrentItem(0);
//        fragments.get(0).setSelected(true);

    }
    @Override
    public void onBackPressed() {
        finish();
    }








    ArrayList<MainFragment> fragments = new ArrayList<>();
    MsConversationListFragment conversionFragment;
    MsContactFragment contactFragment;
    MsMineFragment mineFragment;
    public class MainPagerAdapter extends FragmentStateAdapter {

        public MainPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }


//    public class MainPagerAdapter extends FragmentPagerAdapter {
//
//        public MainPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//        @Override
//        public Fragment getItem(int position) {
//            return fragments.get(position);
//        }
//        @Override
//        public int getCount() {
//            return fragments.size();
//        }
//        @Override
//        public String getPageTitle(int position) {
//            return fragments.get(position).getTitle();
//        }
//    }




}

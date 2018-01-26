package im.tox.toktok.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import im.tox.toktok.R;
import im.tox.toktok.app.main.HomeSearch;
import im.tox.toktok.app.main.MainFragment;
import im.tox.toktok.app.main.friends.SlideInContactsLayout;
import im.tox.toktok.app.profile.ProfileFragment;
import scala.Function0;
import scala.runtime.BoxedUnit;

public final class MainActivityHolder extends AppCompatActivity {

    private LinearLayout activeTab = null;
    private SlideInContactsLayout activeContacts = null;
    private HomeSearch activeSearch = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LinearLayout chatsDrawerItem = this.findViewById(R.id.home_drawer_chats);
        final LinearLayout profileDrawerItem = this.findViewById(R.id.home_drawer_profile);

        chatsDrawerItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activeTab = changeTab(activeTab, chatsDrawerItem, "Chats", "", new FragmentFactory() {
                    @Override
                    public Fragment get() {
                        return new MainFragment();
                    }
                });
            }
        });

        profileDrawerItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activeTab = changeTab(activeTab, profileDrawerItem, "Profile", "Activity", new FragmentFactory() {
                    @Override
                    public Fragment get() {
                        return new ProfileFragment();
                    }
                });
            }
        });

        chatsDrawerItem.callOnClick();
    }

    protected void onDestroy() {
        if (activeSearch != null) {
            getWindowManager().removeView(activeSearch);
            activeSearch = null;
        }

        if (activeContacts != null) {
            getWindowManager().removeView(activeContacts);
            activeContacts = null;
        }

        super.onDestroy();
    }

    private <T extends Fragment> LinearLayout changeTab(
            LinearLayout oldTab,
            LinearLayout newTab,
            String tag,
            String stackName,
            FragmentFactory<T> newFragment
    ) {
        if (newTab != oldTab) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_frame, newFragment.get(), tag)
                    .addToBackStack(stackName)
                    .commit();
        }

        if (oldTab != null) {
            oldTab.setBackgroundResource(R.drawable.background_ripple);
        }

        newTab.setBackgroundResource(R.color.drawerBackgroundSelected);
        DrawerLayout homeLayout = this.findViewById(R.id.home_layout);
        homeLayout.closeDrawers();

        return newTab;
    }

    public void setAddContactPopup(SlideInContactsLayout contact) {
        activeContacts = contact;
    }

    public void setSearch(HomeSearch homeSearch) {
        activeSearch = homeSearch;
    }

    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("Profile") != null) {
            getSupportFragmentManager().popBackStack("Activity", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if (activeContacts != null) {
            activeContacts.finish(new SlideInContactsLayout.AfterFinish() {
                @Override
                public void run() {
                    getWindowManager().removeView(activeContacts);
                    activeContacts = null;
                }
            });
        }

        if (activeSearch != null) {
            activeSearch.finish(new SlideInContactsLayout.AfterFinish() {
                @Override
                public void run() {
                    getWindowManager().removeView(activeSearch);
                    activeSearch = null;
                }
            });
        }
    }

    private interface FragmentFactory<T extends Fragment> {
        T get();
    }
}

//////////////////////////////////////////////////
//Nathaniel Josephs ////////////////////////////
//Leftovers App - MapsStatePagerAdapter.cs/////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MapsStatePagerAdapter extends FragmentStatePagerAdapter {

    //In case the amount of fragments implemented was a greater amount, the below help manage this
    //i.e if I wanted to use it for my Menu (e.g. a swiping one) it would need better management
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public MapsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    //Method for adding a fragment to our list of fragments
    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}

/*
 * Z-Way for Android is a UI for Z-Way server
 *
 * Created by Ivan Platonov on 15.06.14 15:01.
 * Copyright (c) 2014 Z-Wave.Me
 *
 * All rights reserved
 * info@z-wave.me
 * Z-Way for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Z-Way for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Z-Way for Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.z_wave.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import me.z_wave.android.R;
import me.z_wave.android.dataModel.Device;
import me.z_wave.android.dataModel.Filter;
import me.z_wave.android.network.ApiClient;
import me.z_wave.android.otto.events.CommitFragmentEvent;
import me.z_wave.android.otto.events.OnDataUpdatedEvent;
import me.z_wave.android.ui.activity.CameraActivity;
import me.z_wave.android.ui.adapters.DevicesGridAdapter;
import me.z_wave.android.ui.fragments.dashboard.EditDashboardFragment;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DevicesFragment extends BaseFragment implements DevicesGridAdapter.DeviceStateUpdatedListener {

    public static final String FILTER_KEY = "filter_key";
    public static final String FILTER_NAME_KEY = "filter_name_key";

    @InjectView(R.id.devices_widgets)
    GridView widgetsGridView;

    @InjectView(R.id.devices_msg_empty)
    View emptyListMsg;

    @Inject
    ApiClient apiClient;

    private List<Device> mDevices;
    private DevicesGridAdapter mAdapter;
    private Filter mFilter;
    private String mFilterValue;

    public static DevicesFragment newInstance(Filter filter, String filterValue){
        final DevicesFragment devicesFragment = new DevicesFragment();
        final Bundle args = new Bundle();
        args.putInt(FILTER_KEY, filter.ordinal());
        args.putString(FILTER_NAME_KEY, filterValue);
        devicesFragment.setArguments(args);
        return devicesFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareDevicesView();
        changeEmptyMsgVisibility();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_devices, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.devices_edit:
                bus.post(new CommitFragmentEvent(EditDevicesFragment.newInstance(mFilter, mFilterValue), true));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwitchStateChanged(Device updatedDevice) {
        apiClient.updateDevicesState(updatedDevice, new ApiClient.EmptyApiCallback<Device>() {
            @Override
            public void onSuccess() {
                showToast("Device state changed!");
            }

            @Override
            public void onFailure(Device request, boolean isNetworkError) {
                if(isAdded()){
                    if(isNetworkError){
                        showToast(R.string.request_network_problem);
                    } else {
                        showToast(R.string.request_server_problem_msg);
                    }
                }
            }
        });
    }

    @Override
    public void onSeekBarStateChanged(final Device updatedDevice) {
        apiClient.updateDevicesLevel(updatedDevice, new ApiClient.EmptyApiCallback<Device>() {
            @Override
            public void onSuccess() {
                showToast("Seek changed " + updatedDevice.metrics.level);
            }

            @Override
            public void onFailure(Device request, boolean isNetworkError) {
                if(isAdded()){
                    if(isNetworkError){
                        showToast(R.string.request_network_problem);
                    } else {
                        showToast(R.string.request_server_problem_msg);
                    }
                }
            }
        });
    }

    @Override
    public void onToggleClicked(Device updatedDevice) {
        apiClient.updateToggle(updatedDevice, new ApiClient.EmptyApiCallback<Device>() {
            @Override
            public void onSuccess() {
                showToast("Toggle clicked");
            }

            @Override
            public void onFailure(Device request, boolean isNetworkError) {
                if (isAdded()) {
                    if (isNetworkError) {
                        showToast(R.string.request_network_problem);
                    } else {
                        showToast(R.string.request_server_problem_msg);
                    }
                }
            }
        });
    }

    @Override
    public void onColorViewClicked(Device updatedDevice) {
        showToast("Coming soon");
    }

    @Override
    public void onOpenCameraView(Device updatedDevice) {
        showToast("Coming soon");
//        final Intent intent = new Intent(getActivity(), CameraActivity.class);
//        intent.putExtra(CameraActivity.KEY_DEVICE, updatedDevice);
//        startActivity(intent);
    }

//    @Override
//    public void onAddRemoveClicked(Device updatedDevice) {
//        final Profile profile = dataContext.getActiveProfile();
//        if(profile != null){
//            if(profile.positions == null)
//                profile.positions = new ArrayList<String>();
//
//            widgetsGridView.closeOpenedItems();
//            if(profile.positions.contains(updatedDevice.id)){
//                profile.positions.remove(updatedDevice.id);
//            } else {
//                profile.positions.add(updatedDevice.id);
//            }
//            apiClient.updateProfile(profile, new ApiClient.ApiCallback<List<Profile>, String>() {
//                @Override
//                public void onSuccess(List<Profile> result) {
//                    mAdapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onFailure(String request, boolean isNetworkError) {
//
//                }
//            });
//        }
//    }

    @Subscribe
    public void onDataUpdated(OnDataUpdatedEvent event){
        Timber.v("Device list updated!");
        mAdapter.setProfile(dataContext.getActiveProfile());
        mDevices = getFilteredDeviceList();
        mAdapter.notifyDataSetChanged();
        changeEmptyMsgVisibility();
    }

    private void changeEmptyMsgVisibility(){
        final int msgVisibility = mAdapter == null || mAdapter.getCount() == 0 ? View.VISIBLE : View.GONE;
        if(emptyListMsg.getVisibility() != msgVisibility){
            emptyListMsg.setVisibility(msgVisibility);
        }
    }

    private void prepareDevicesView(){
        mDevices =  getFilteredDeviceList();
        mAdapter = new DevicesGridAdapter(getActivity(), mDevices,
                dataContext.getActiveProfile(), this);
        widgetsGridView.setAdapter(mAdapter);
    }

    private List<Device> getFilteredDeviceList(){
        mFilter = Filter.values()[getArguments().getInt(FILTER_KEY, 0)];
        mFilterValue = getArguments().getString(FILTER_NAME_KEY, Filter.DEFAULT_FILTER);
        switch (mFilter){
            case LOCATION:
                return dataContext.getDevicesForLocation(mFilterValue);
            case TYPE:
                return dataContext.getDevicesWithType(mFilterValue);
            case TAG:
                return dataContext.getDevicesWithTag(mFilterValue);
        }
        return new ArrayList<Device>();
    }
}

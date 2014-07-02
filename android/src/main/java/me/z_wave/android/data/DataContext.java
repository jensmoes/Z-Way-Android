/*
 * Z-Way for Android is a UI for Z-Way server
 *
 * Created by Ivan Platonov on 14.06.14 17:39.
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

package me.z_wave.android.data;

import me.z_wave.android.dataModel.Device;
import me.z_wave.android.dataModel.Filter;
import me.z_wave.android.dataModel.Location;
import me.z_wave.android.dataModel.Notification;
import me.z_wave.android.dataModel.Profile;
import me.z_wave.android.dataModel.SimpleDevice;

import java.util.ArrayList;
import java.util.List;

public class DataContext {

    private List<Device> mDevices = new ArrayList<Device>();
    private List<Location> mLocation = new ArrayList<Location>();
    private List<Notification> mNotifications = new ArrayList<Notification>();
    private List<Profile> mProfiles = new ArrayList<Profile>();

    public void setLocations(List<Location> locations) {
        mLocation = locations;
    }

    public List<Device> getDevices() {
        if (mDevices == null)
            mDevices = new ArrayList<Device>();
        return mDevices;
    }

    public void setDevices(List<Device> devices) {
        mDevices = devices;
    }

    public void addNotifications(List<Notification> notifications){
        for(Notification notification : notifications){
            final int i = mNotifications.indexOf(notification);
            if (i >= 0) {
                mNotifications.remove(i);
                mNotifications.add(i, notification);
            } else {
                mNotifications.add(notification);
            }
        }
    }

    public void addDevices(List<Device> devices) {
        for (Device device : devices) {
            final int i = mDevices.indexOf(device);
            if (i >= 0) {
                mDevices.remove(i);
                mDevices.add(i, device);
            } else {
                mDevices.add(device);
            }
        }
    }

    public List<String> getDeviceTypes() {
        final List<String> result = new ArrayList<String>();
        for (Device device : mDevices) {
            final String deviceType = device.deviceType;
            if (!result.contains(deviceType))
                result.add(deviceType);
        }
        return result;
    }

    public List<String> getDeviceTags() {
        final List<String> result = new ArrayList<String>();
        for (Device device : mDevices) {
            for (String tag : device.tags) {
                if (!result.contains(tag))
                    result.add(tag);
            }
        }
        return result;
    }

    public List<String> getLocations() {
        final List<String> result = new ArrayList<String>();
        for (Location location : mLocation) {
            if (!result.contains(location.title))
                result.add(location.title);
        }
        return result;
    }

    public List<Notification> getNotifications(){
        return mNotifications;
    }

    public List<Device> getDevicesWithType(String deviceType){
        if(deviceType.equalsIgnoreCase(Filter.DEFAULT_FILTER))
            return mDevices;

        final ArrayList<Device> result = new ArrayList<Device>();
        for(Device device : mDevices){
            if(device.deviceType.equalsIgnoreCase(deviceType))
                result.add(device);
        }
        return result;
    }

    public List<Device> getDevicesWithTag(String deviceTag){
        if(deviceTag.equalsIgnoreCase(Filter.DEFAULT_FILTER))
            return mDevices;

        final ArrayList<Device> result = new ArrayList<Device>();
        for(Device device : mDevices){
            if(device.tags.contains(deviceTag))
                result.add(device);
        }
        return result;
    }

    public List<Device> getDevicesForLocation(String location){
        if(location.equalsIgnoreCase(Filter.DEFAULT_FILTER))
            return mDevices;

        final ArrayList<Device> result = new ArrayList<Device>();
        for(Device device : mDevices){
            if(device.location != null &&  device.location.equalsIgnoreCase(location))
                result.add(device);
        }
        return result;
    }

    public void addProfiles(List<Profile> profiles) {
        for (Profile profile : profiles) {
            final int i = mProfiles.indexOf(profile);
            if (i >= 0) {
                mProfiles.remove(i);
                mProfiles.add(i, profile);
            } else {
                mProfiles.add(profile);
            }
        }
    }

    public List<Profile> getProfiles(){
        return mProfiles;
    }

    public Profile getProfileWithId(int id){
        for(Profile profile : mProfiles){
            if(profile.id == id)
                return profile;
        }
        return null;
    }

    public Profile getActiveProfile(){
        for(Profile profile : mProfiles){
            if(profile.active)
                return profile;
        }
        return null;
    }

    public List<Device> getDashboardDevices(){
        final Profile profile = getActiveProfile();
        final List<Device> result = new ArrayList<Device>();
        //TODO uncnown widgets instead strings
        if(profile != null && profile.widgets != null){
            for(SimpleDevice simpleDevice :profile.widgets){
                for(Device device : mDevices){
                    if(simpleDevice.id.equalsIgnoreCase(device.id)){
                        result.add(device);
                    }
                }
            }
        }
        return result;
    }

}

/*
 * Z-Way for Android is a UI for Z-Way server
 *
 * Created by Ivan Platonov on 04.09.14 20:44.
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

package me.z_wave.android.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import me.z_wave.android.R;
import me.z_wave.android.network.ApiClient;
import me.z_wave.android.otto.MainThreadBus;
import me.z_wave.android.otto.events.ShowReconnectionProgressEvent;

/**
 * Created by Ivan PL on 04.09.2014.
 */
public class ReconnectionProgressDialog extends BaseDialogFragment {

    public static final String KEY_PROFILE_NAME = "profile_name";

    @Inject
    ApiClient apiClient;

    @Inject
    public MainThreadBus bus;

    public static ReconnectionProgressDialog newInstance(String profileName) {
        final ReconnectionProgressDialog dialog = new ReconnectionProgressDialog();
        final Bundle args = new Bundle();
        args.putString(KEY_PROFILE_NAME, profileName);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final String profileName = getArguments().getString(KEY_PROFILE_NAME);
        setDialogBody(initAlertDialogBody(profileName));
        showTitle(false);
        setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.cancel_connection) {
            apiClient.cancelConnection();
            bus.post(new ShowReconnectionProgressEvent(false, false, ""));
        } else {
            super.onClick(view);
        }
    }

    private View initAlertDialogBody(String profileName) {
        final View dialogBody = LayoutInflater.from(getActivity()).inflate(
                R.layout.layout_reconnection_dialog_progress, null);
        dialogBody.findViewById(R.id.cancel_connection).setOnClickListener(this);
        final TextView textView = (TextView) dialogBody.findViewById(R.id.connecting_to);
        textView.setText(String.format(getString(R.string.connecting_to), profileName));
        return dialogBody;
    }
}

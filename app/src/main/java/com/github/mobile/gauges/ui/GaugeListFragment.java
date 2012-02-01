package com.github.mobile.gauges.ui;

import android.R;
import android.accounts.AccountsException;
import android.os.Bundle;
import android.support.v4.app.SupportActivity;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.Gauge;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Fragment to display a list of gauges
 */
public class GaugeListFragment extends ListLoadingFragment<Gauge> {

    private final static String TAG = "GLF";

    @Inject
    private GaugesServiceProvider serviceProvider;

    private OnGaugeSelectedListener containerCallback;

    @Override
    public void onAttach(SupportActivity activity) {
        super.onAttach(activity);
        if (activity instanceof OnGaugeSelectedListener)
            containerCallback = (OnGaugeSelectedListener) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setCacheColorHint(getResources().getColor(R.color.transparent));
        getListView().setFastScrollEnabled(true);
    }

    public Loader<List<Gauge>> onCreateLoader(int id, Bundle args) {
        return new AsyncLoader<List<Gauge>>(getActivity()) {
            public List<Gauge> loadInBackground() {
                try {
                    return serviceProvider.getService().getGauges();
                } catch (IOException e) {
                    Log.d(TAG, "Exception getting gauges", e);
                } catch (AccountsException e) {
                    Log.d(TAG, "Exception getting gauges", e);
                }
                return Collections.emptyList();
            }
        };
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (containerCallback != null)
            containerCallback.onGaugeSelected((Gauge) l.getItemAtPosition(position));
    }

    protected ListAdapter adapterFor(List<Gauge> items) {
        return new ViewHoldingListAdapter<Gauge>(items, ViewInflator.viewInflatorFor(getActivity(),
                layout.gauge_list_item), ReflectiveHolderFactory.reflectiveFactoryFor(GaugeViewHolder.class,
                getActivity().getResources()));
    }
}

package com.tdam2013.grupo13.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tdam2013.grupo13.R;
import com.tdam2013.grupo13.model.ConnectionHistory;
import com.tdam2013.grupo13.model.ConnectionHistory.ConnectionHistoryStatus;

import java.util.ArrayList;
import java.util.List;

public class ConnectionHistoryAdapter extends BaseAdapter {

	private List<ConnectionHistory> connections;
	private Activity activity;
	
	public ConnectionHistoryAdapter(Activity activity,List<ConnectionHistory> connections){
		super();
		this.activity = activity;
		this.connections = connections;		
	}
	
	@Override
	public int getCount() {
		return connections.size();
	}

	@Override
	public Object getItem(int index) {		
		return connections.get(index);
	}

	@Override
	public long getItemId(int index) {		
		return index;
	}

	@Override
	public View getView(int index, View connectioView, ViewGroup parent) {
		ConnectionHistory connection = connections.get(index);
		if(connectioView == null){
			connectioView = activity.getLayoutInflater().inflate(R.layout.listview_connectivity, null);			
		}			
		
		ImageView imageStatus = (ImageView) connectioView.findViewById(R.id.image_connection);
		TextView date = (TextView) connectioView.findViewById(R.id.connection_date);
		TextView connectionStatus = (TextView) connectioView.findViewById(R.id.connection_status);
		
		ConnectionHistoryStatus status = connection.getConnectionStatus();		
		switch (status) {
		case CONNECTED: imageStatus.setImageResource(R.drawable.connection);
						connectionStatus.setText("Conexion");
						break;
		case LOST : imageStatus.setImageResource(R.drawable.connection);
					connectionStatus.setText("Desconexion");
					break;
		}
				
		date.setText(connection.getDate());
		return connectioView;
	}

	public void update(ArrayList<ConnectionHistory> connectivityLog) {
		this.connections = connectivityLog;
		notifyDataSetChanged();
	}

}

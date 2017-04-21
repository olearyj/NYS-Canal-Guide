package com.AYC.canalguide;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutFragment extends Fragment implements OnClickListener {
	
	private View view;
	
	private ImageButton ibShare;
	private TextView tvShare;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
	    view = inflater.inflate(R.layout.layout_about, container, false);
	    
	    ibShare = (ImageButton) view.findViewById(R.id.ibShare);
	    ibShare.setOnClickListener(this);
	    tvShare = (TextView) view.findViewById(R.id.tvShare);
	    tvShare.setOnClickListener(this);
	    
	    return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.ibShare:
		case R.id.tvShare:
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "\nhttps://play.google.com/store/apps/details?id=com.AYC.canalguide&hl=en");
			sendIntent.setType("text/plain");
			
			// Create intent to show chooser
			Intent chooser = Intent.createChooser(sendIntent, "Share NYS Canal Guide with: ");

			// Verify the intent will resolve to at least one activity
			if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null)
			    startActivity(chooser);
			else
				startActivity(sendIntent);
		}
	}
	
}

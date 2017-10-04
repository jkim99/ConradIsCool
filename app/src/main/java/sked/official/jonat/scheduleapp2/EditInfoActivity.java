package sked.official.jonat.scheduleapp2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class EditInfoActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_info_view);
		final EditText[] classNames = {(EditText)findViewById(R.id.period1_input),
				 				 (EditText)findViewById(R.id.period2_input),
								 (EditText)findViewById(R.id.period3_input),
								 (EditText)findViewById(R.id.period4_input),
								 (EditText)findViewById(R.id.period5_input)};
		int x = 0;
		for(EditText e : classNames) {
			e.setHint(Utility.oneLineClassNames(MainActivity.swipeDirectionOffset)[x].substring(2));
			x++;
		}

		final String info;

//		Button submit = (Button)findViewById(R.id.button);
//		submit.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				for(EditText e : classNames) {
////					info = (String)e.getText();
//				}
//			}
//		});
	}
}
